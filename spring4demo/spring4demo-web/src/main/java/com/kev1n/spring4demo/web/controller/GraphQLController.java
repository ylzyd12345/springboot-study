package com.kev1n.spring4demo.web.controller;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * GraphQL控制器
 * 提供GraphQL查询和变更端点
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/graphql")
@Tag(name = "GraphQL", description = "GraphQL API端点")
public class GraphQLController {

    @Autowired
    private GraphQL graphQL;

    @Autowired
    private GraphQLSchema graphQLSchema;

    /**
     * GraphQL查询端点（GET）
     * 用于执行GraphQL查询
     *
     * @param query GraphQL查询语句
     * @param variables 变量
     * @return 执行结果
     */
    @Operation(summary = "GraphQL查询", description = "执行GraphQL查询")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> executeQuery(
            @RequestParam String query,
            @RequestParam(required = false) String variables) {
        log.info("GraphQL查询: query={}", query);
        
        ExecutionResult executionResult = graphQL.execute(query);
        return executionResult.toSpecification();
    }

    /**
     * GraphQL查询端点（POST）
     * 用于执行GraphQL查询和变更
     *
     * @param body 请求体，包含query、variables、operationName
     * @return 执行结果
     */
    @Operation(summary = "GraphQL查询和变更", description = "执行GraphQL查询和变更")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> executeQuery(@RequestBody Map<String, Object> body) {
        String query = (String) body.get("query");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        String operationName = (String) body.get("operationName");
        
        log.info("GraphQL查询: query={}, operation={}", query, operationName);
        
        ExecutionResult executionResult = graphQL.execute(
            ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variables)
                .operationName(operationName)
                .build()
        );
        
        return executionResult.toSpecification();
    }

    /**
     * GraphQL Schema端点
     * 返回GraphQL Schema
     *
     * @return GraphQL Schema字符串
     */
    @Operation(summary = "获取GraphQL Schema", description = "返回GraphQL Schema定义")
    @GetMapping(value = "/schema", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSchema() {
        return graphQLSchema.toString();
    }
}