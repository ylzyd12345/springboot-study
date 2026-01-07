package com.kev1n.spring4demo.core.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.kev1n.spring4demo.core.document.UserDocument;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.elasticsearch.UserDocumentRepository;
import com.kev1n.spring4demo.core.service.UserSearchService;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户搜索服务实现类
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final UserDocumentRepository userDocumentRepository;
    private final UserService userService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void indexUser(Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            UserDocument document = UserDocument.fromUser(user);
            userDocumentRepository.save(document);
            log.info("索引用户成功: userId={}", userId);
        } else {
            log.warn("用户不存在，无法索引: userId={}", userId);
        }
    }

    @Override
    public void indexUsers(List<Long> userIds) {
        List<User> users = userService.listByIds(userIds);
        List<UserDocument> documents = users.stream()
                .map(UserDocument::fromUser)
                .collect(Collectors.toList());
        userDocumentRepository.saveAll(documents);
        log.info("批量索引用户成功: count={}", documents.size());
    }

    @Override
    public void rebuildIndex() {
        log.info("开始重建用户索引");

        // 删除所有索引
        userDocumentRepository.deleteAll();

        // 重新索引所有用户
        List<User> users = userService.list();
        List<UserDocument> documents = users.stream()
                .map(UserDocument::fromUser)
                .collect(Collectors.toList());
        userDocumentRepository.saveAll(documents);

        log.info("重建用户索引完成: count={}", documents.size());
    }

    @Override
    public void deleteUserIndex(Long userId) {
        UserDocument document = userDocumentRepository.findByUserId(userId);
        if (document != null) {
            userDocumentRepository.delete(document);
            log.info("删除用户索引成功: userId={}", userId);
        }
    }

    @Override
    public void deleteUserIndexes(List<Long> userIds) {
        List<UserDocument> documents = new ArrayList<>();
        for (Long userId : userIds) {
            UserDocument document = userDocumentRepository.findByUserId(userId);
            if (document != null) {
                documents.add(document);
            }
        }
        userDocumentRepository.deleteAll(documents);
        log.info("批量删除用户索引成功: count={}", documents.size());
    }

    @Override
    public Page<UserDocument> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userDocumentRepository.findAll(pageable);
        }
        return userDocumentRepository.findByUsernameOrRealName(keyword, keyword, pageable);
    }

    @Override
    public Page<UserDocument> advancedSearch(String keyword, Integer status, String deptId, Pageable pageable) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            MatchQuery usernameQuery = MatchQuery.of(m -> m
                    .field("username")
                    .query(keyword)
                    .fuzziness("AUTO")
            );
            MatchQuery realNameQuery = MatchQuery.of(m -> m
                    .field("realName")
                    .query(keyword)
                    .fuzziness("AUTO")
            );
            MatchQuery emailQuery = MatchQuery.of(m -> m
                    .field("email")
                    .query(keyword)
                    .fuzziness("AUTO")
            );

            boolQueryBuilder.should(Query.of(q -> q.match(usernameQuery)))
                    .should(Query.of(q -> q.match(realNameQuery)))
                    .should(Query.of(q -> q.match(emailQuery)))
                    .minimumShouldMatch("1");
        }

        // 状态过滤
        if (status != null) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t
                    .field("status")
                    .value(status)
            )));
        }

        // 部门过滤
        if (deptId != null && !deptId.isEmpty()) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t
                    .field("deptId")
                    .value(deptId)
            )));
        }

        // 只查询未删除的用户
        boolQueryBuilder.filter(Query.of(q -> q.term(t -> t
                .field("deleted")
                .value(0)
        )));

        // 构建查询
        org.springframework.data.elasticsearch.core.query.Query searchQuery = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .withPageable(pageable)
                .build();

        // 执行查询
        SearchHits<UserDocument> searchHits = elasticsearchOperations.search(searchQuery, UserDocument.class);

        // 转换结果
        List<UserDocument> documents = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<UserDocument> searchByUsername(String username, Pageable pageable) {
        return userDocumentRepository.searchByUsername(username, pageable);
    }

    @Override
    public Page<UserDocument> searchByRealName(String realName, Pageable pageable) {
        return userDocumentRepository.searchByRealName(realName, pageable);
    }

    @Override
    public Page<UserDocument> searchByEmail(String email, Pageable pageable) {
        return userDocumentRepository.findByEmail(email, pageable);
    }

    @Override
    public Page<UserDocument> searchByStatus(Integer status, Pageable pageable) {
        return userDocumentRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<UserDocument> searchByDeptId(String deptId, Pageable pageable) {
        return userDocumentRepository.findByDeptId(deptId, pageable);
    }

    @Override
    public UserDocument getUserDocumentById(Long userId) {
        return userDocumentRepository.findByUserId(userId);
    }

    @Override
    public void syncUserToEs(Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            UserDocument document = UserDocument.fromUser(user);
            userDocumentRepository.save(document);
            log.info("同步用户到ES成功: userId={}", userId);
        } else {
            log.warn("用户不存在，无法同步: userId={}", userId);
        }
    }

    @Override
    public void syncUsersToEs(List<Long> userIds) {
        List<User> users = userService.listByIds(userIds);
        List<UserDocument> documents = users.stream()
                .map(UserDocument::fromUser)
                .collect(Collectors.toList());
        userDocumentRepository.saveAll(documents);
        log.info("批量同步用户到ES成功: count={}", documents.size());
    }
}