package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Elasticsearch 集成测试
 * 测试 Elasticsearch 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("Elasticsearch 集成测试")
class ElasticsearchIntegrationTest {

    private static final String TEST_INDEX = "test-index";

    private ElasticsearchClient elasticsearchClient;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        log.info("初始化 Elasticsearch 连接...");
        log.info("Elasticsearch HTTP URL: {}", TestContainersConfig.getElasticsearchHttpUrl());
        log.info("Elasticsearch 主机: {}", TestContainersConfig.getElasticsearchHost());
        log.info("Elasticsearch HTTP 端口: {}", TestContainersConfig.getElasticsearchHttpPort());

        // 创建 RestClient
        restClient = RestClient.builder(
                new HttpHost(
                        TestContainersConfig.getElasticsearchHost(),
                        TestContainersConfig.getElasticsearchHttpPort(),
                        "http"
                )
        ).build();

        // 创建 ElasticsearchClient
        RestClientTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        elasticsearchClient = new ElasticsearchClient(transport);

        log.info("Elasticsearch 连接初始化完成");
    }

    @AfterEach
    void tearDown() throws IOException {
        try {
            // 删除测试索引
            if (elasticsearchClient.indices().exists(e -> e.index(TEST_INDEX)).value()) {
                elasticsearchClient.indices().delete(d -> d.index(TEST_INDEX));
                log.info("测试索引 {} 已删除", TEST_INDEX);
            }
        } catch (co.elastic.clients.elasticsearch._types.ElasticsearchException e) {
            log.error("删除测试索引失败: Elasticsearch异常", e);
        } catch (IOException e) {
            log.error("删除测试索引失败: IO异常", e);
        } catch (Exception e) {
            log.error("删除测试索引失败: 未知异常", e);
        } finally {
            if (restClient != null) {
                restClient.close();
                log.info("Elasticsearch 连接已关闭");
            }
        }
    }

    @Test
    @DisplayName("应该成功连接到 Elasticsearch 容器")
    void shouldConnectToElasticsearchContainer_whenContainerIsRunning() throws IOException {
        // Given & When
        InfoResponse info = elasticsearchClient.info();

        // Then
        assertThat(info).isNotNull();
        assertThat(info.version().number()).isNotNull();
        log.info("Elasticsearch 连接测试通过, 版本: {}", info.version().number());
    }

    @Test
    @DisplayName("应该成功创建索引")
    void shouldCreateIndex_whenIndexDoesNotExist() throws IOException {
        // Given
        CreateIndexResponse response = elasticsearchClient.indices().create(c -> c
                .index(TEST_INDEX)
                .mappings(m -> m
                        .properties("title", p -> p.text(t -> t))
                        .properties("content", p -> p.text(t -> t))
                        .properties("author", p -> p.keyword(k -> k))
                )
        );

        // Then
        assertThat(response.acknowledged()).isTrue();
        assertThat(response.index()).isEqualTo(TEST_INDEX);
        log.info("创建索引测试通过: {}", response.index());
    }

    @Test
    @DisplayName("应该成功索引文档")
    void shouldIndexDocument_whenDocumentIsProvided() throws IOException {
        // Given
        createTestIndex();

        Map<String, Object> document = Map.of(
                "title", "测试文档标题",
                "content", "这是一篇测试文档的内容",
                "author", "test-author"
        );

        // When
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(TEST_INDEX)
                .id("1")
                .document(document)
        );

        // Then
        assertThat(response.id()).isEqualTo("1");
        assertThat(response.result().jsonValue()).isEqualTo("created");
        log.info("索引文档测试通过: 文档ID = {}, 结果 = {}", response.id(), response.result());
    }

    @Test
    @DisplayName("应该成功获取文档")
    void shouldGetDocument_whenDocumentIdIsProvided() throws IOException {
        // Given
        createTestIndex();
        Map<String, Object> document = Map.of(
                "title", "获取测试文档",
                "content", "这是用于获取测试的文档",
                "author", "test-author"
        );
        elasticsearchClient.index(i -> i
                .index(TEST_INDEX)
                .id("2")
                .document(document)
        );

        // When
        GetResponse<Map> response = elasticsearchClient.get(g -> g
                        .index(TEST_INDEX)
                        .id("2"),
                Map.class
        );

        // Then
        assertThat(response.found()).isTrue();
        assertThat(response.source()).isNotNull();
        assertThat(response.source().get("title")).isEqualTo("获取测试文档");
        log.info("获取文档测试通过: 文档ID = {}, 找到 = {}", response.id(), response.found());
    }

    @Test
    @DisplayName("应该成功搜索文档")
    void shouldSearchDocuments_whenSearchQueryIsProvided() throws IOException {
        // Given
        createTestIndex();

        // 索引多个文档
        elasticsearchClient.index(i -> i.index(TEST_INDEX).id("3").document(Map.of(
                "title", "搜索测试文档1",
                "content", "这是第一篇搜索测试文档",
                "author", "author1"
        )));

        elasticsearchClient.index(i -> i.index(TEST_INDEX).id("4").document(Map.of(
                "title", "搜索测试文档2",
                "content", "这是第二篇搜索测试文档",
                "author", "author2"
        )));

        // 刷新索引以确保文档可搜索
        elasticsearchClient.indices().refresh(r -> r.index(TEST_INDEX));

        // When
        SearchResponse<Map> response = elasticsearchClient.search(s -> s
                        .index(TEST_INDEX)
                        .query(Query.of(q -> q.match(m -> m
                                .field("content")
                                .query("搜索测试")
                        ))),
                Map.class
        );

        // Then
        assertThat(response.hits().total().value()).isGreaterThanOrEqualTo(2);
        List<Hit<Map>> hits = response.hits().hits();
        log.info("搜索文档测试通过: 找到 {} 个文档", hits.size());
        hits.forEach(hit -> log.info("文档ID: {}, 标题: {}", hit.id(), hit.source().get("title")));
    }

    @Test
    @DisplayName("应该成功更新文档")
    void shouldUpdateDocument_whenDocumentIdIsProvided() throws IOException {
        // Given
        createTestIndex();
        Map<String, Object> document = Map.of(
                "title", "更新前标题",
                "content", "更新前内容",
                "author", "test-author"
        );
        elasticsearchClient.index(i -> i.index(TEST_INDEX).id("5").document(document));

        Map<String, Object> updateDoc = Map.of(
                "title", "更新后标题",
                "content", "更新后内容"
        );

        // When
        UpdateResponse<Map> response = elasticsearchClient.update(u -> u
                        .index(TEST_INDEX)
                        .id("5")
                        .doc(updateDoc),
                Map.class
        );

        // Then
        assertThat(response.result().jsonValue()).isEqualTo("updated");
        log.info("更新文档测试通过: 文档ID = {}, 结果 = {}", response.id(), response.result());

        // 验证更新
        GetResponse<Map> updatedDoc = elasticsearchClient.get(g -> g.index(TEST_INDEX).id("5"), Map.class);
        assertThat(updatedDoc.source().get("title")).isEqualTo("更新后标题");
    }

    @Test
    @DisplayName("应该成功删除文档")
    void shouldDeleteDocument_whenDocumentIdIsProvided() throws IOException {
        // Given
        createTestIndex();
        Map<String, Object> document = Map.of(
                "title", "删除测试文档",
                "content", "这是用于删除测试的文档",
                "author", "test-author"
        );
        elasticsearchClient.index(i -> i.index(TEST_INDEX).id("6").document(document));

        // When
        DeleteResponse response = elasticsearchClient.delete(d -> d
                .index(TEST_INDEX)
                .id("6")
        );

        // Then
        assertThat(response.result().jsonValue()).isEqualTo("deleted");
        log.info("删除文档测试通过: 文档ID = {}, 结果 = {}", response.id(), response.result());

        // 验证删除
        GetResponse<Map> deletedDoc = elasticsearchClient.get(g -> g.index(TEST_INDEX).id("6"), Map.class);
        assertThat(deletedDoc.found()).isFalse();
    }

    @Test
    @DisplayName("应该成功删除索引")
    void shouldDeleteIndex_whenIndexExists() throws IOException {
        // Given
        createTestIndex();
        Boolean existsBefore = elasticsearchClient.indices().exists(e -> e.index(TEST_INDEX)).value();
        assertThat(existsBefore).isTrue();

        // When
        DeleteIndexResponse response = elasticsearchClient.indices().delete(d -> d.index(TEST_INDEX));

        // Then
        assertThat(response.acknowledged()).isTrue();
        Boolean existsAfter = elasticsearchClient.indices().exists(e -> e.index(TEST_INDEX)).value();
        assertThat(existsAfter).isFalse();
        log.info("删除索引测试通过: 索引 {} 已删除", TEST_INDEX);
    }

    @Test
    @DisplayName("应该成功执行批量操作")
    void shouldPerformBulkOperations_whenMultipleDocumentsAreProvided() throws IOException {
        // Given
        createTestIndex();

        // When
        BulkResponse response = elasticsearchClient.bulk(b -> b
                .index(TEST_INDEX)
                .operations(op -> op
                        .create(c -> c
                                .index(TEST_INDEX)
                                .id("7")
                                .document(Map.of(
                                        "title", "批量文档1",
                                        "content", "批量操作测试文档1",
                                        "author", "author1"
                                ))
                        )
                )
                .operations(op -> op
                        .create(c -> c
                                .index(TEST_INDEX)
                                .id("8")
                                .document(Map.of(
                                        "title", "批量文档2",
                                        "content", "批量操作测试文档2",
                                        "author", "author2"
                                ))
                        )
                )
        );

        // Then
        assertThat(response.errors()).isFalse();
        log.info("批量操作测试通过: 成功创建 {} 个文档", response.items().size());
    }

    /**
     * 创建测试索引
     */
    private void createTestIndex() throws IOException {
        elasticsearchClient.indices().create(c -> c
                .index(TEST_INDEX)
                .mappings(m -> m
                        .properties("title", p -> p.text(t -> t))
                        .properties("content", p -> p.text(t -> t))
                        .properties("author", p -> p.keyword(k -> k))
                )
        );
    }
}