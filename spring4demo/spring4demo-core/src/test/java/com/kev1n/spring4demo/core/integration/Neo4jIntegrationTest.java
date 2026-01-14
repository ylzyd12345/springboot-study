package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.bolt.connection.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Neo4j 集成测试
 * 测试 Neo4j 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("Neo4j 集成测试")
class Neo4jIntegrationTest {

    private Driver neo4jDriver;

    @BeforeEach
    void setUp() {
        log.info("初始化 Neo4j 连接...");
        log.info("Neo4j Bolt URL: {}", TestContainersConfig.getNeo4jBoltUrl());
        log.info("Neo4j HTTP URL: {}", TestContainersConfig.getNeo4jHttpUrl());

        // 创建 Neo4j 驱动连接
        neo4jDriver = GraphDatabase.driver(
                TestContainersConfig.getNeo4jBoltUrl()
        );

        log.info("Neo4j 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        try (Session session = neo4jDriver.session()) {
            // 清理所有测试数据
            session.run("MATCH (n) DETACH DELETE n");
            log.info("Neo4j 测试数据已清理");
        } catch (org.neo4j.driver.exceptions.Neo4jException e) {
            log.error("清理 Neo4j 测试数据失败: Neo4j异常", e);
        } catch (Exception e) {
            log.error("清理 Neo4j 测试数据失败: 未知异常", e);
        } finally {
            if (neo4jDriver != null) {
                neo4jDriver.close();
                log.info("Neo4j 连接已关闭");
            }
        }
    }

    @Test
    @DisplayName("应该成功连接到 Neo4j 容器")
    void shouldConnectToNeo4jContainer_whenContainerIsRunning() {
        // Given & When
        try (Session session = neo4jDriver.session()) {
            Result result = session.run("RETURN 1 AS number");

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            assertThat(record.get("number").asInt()).isEqualTo(1);
            log.info("Neo4j 连接测试通过: {}", record.get("number").asInt());
        }
    }

    @Test
    @DisplayName("应该成功创建节点")
    void shouldCreateNode_whenNodePropertiesAreProvided() {
        // Given & When
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "CREATE (p:Person {name: $name, age: $age}) RETURN p",
                    Values.parameters("name", "张三", "age", 30)
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            Node node = record.get("p").asNode();
            assertThat(node.get("name").asString()).isEqualTo("张三");
            assertThat(node.get("age").asInt()).isEqualTo(30);
            log.info("创建节点测试通过: 节点ID = {}, 名称 = {}", node.id(), node.get("name").asString());
        }
    }

    @Test
    @DisplayName("应该成功创建关系")
    void shouldCreateRelationship_whenNodesAndRelationshipTypeAreProvided() {
        // Given
        try (Session session = neo4jDriver.session()) {
            // 创建两个节点
            session.run("CREATE (p1:Person {name: '李四'})");
            session.run("CREATE (p2:Person {name: '王五'})");

            // When
            Result result = session.run(
                    "MATCH (p1:Person {name: '李四'}), (p2:Person {name: '王五'}) " +
                            "CREATE (p1)-[r:FRIENDS_WITH]->(p2) " +
                            "RETURN r"
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            Relationship relationship = record.get("r").asRelationship();
            assertThat(relationship.type()).isEqualTo("FRIENDS_WITH");
            log.info("创建关系测试通过: 关系类型 = {}", relationship.type());
        }
    }

    @Test
    @DisplayName("应该成功查询节点")
    void shouldQueryNodes_whenQueryConditionsAreProvided() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (:Person {name: '赵六', age: 25})");
            session.run("CREATE (:Person {name: '钱七', age: 35})");
            session.run("CREATE (:Person {name: '孙八', age: 28})");

            // When
            Result result = session.run(
                    "MATCH (p:Person) WHERE p.age > $minAge RETURN p ORDER BY p.age",
                    Values.parameters("minAge", 27)
            );

            // Then
            List<org.neo4j.driver.Record> records = result.list();
            assertThat(records).hasSize(2);
            log.info("查询节点测试通过: 找到 {} 个节点", records.size());
            records.forEach(record -> {
                Node node = record.get("p").asNode();
                log.info("  - 节点: {}, 年龄: {}", node.get("name").asString(), node.get("age").asInt());
            });
        }
    }

    @Test
    @DisplayName("应该成功查询关系")
    void shouldQueryRelationships_whenRelationshipTypeIsProvided() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (a:Person {name: 'A'})-[:KNOWS]->(b:Person {name: 'B'})");
            session.run("CREATE (b:Person {name: 'B'})-[:KNOWS]->(c:Person {name: 'C'})");
            session.run("CREATE (c:Person {name: 'C'})-[:WORKS_WITH]->(d:Person {name: 'D'})");

            // When
            Result result = session.run(
                    "MATCH (a:Person)-[r:KNOWS]->(b:Person) RETURN a.name AS from, b.name AS to"
            );

            // Then
            List<org.neo4j.driver.Record> records = result.list();
            assertThat(records).hasSize(2);
            log.info("查询关系测试通过: 找到 {} 条 KNOWS 关系", records.size());
            records.forEach(record -> {
                log.info("  - {} KNOWS {}", record.get("from").asString(), record.get("to").asString());
            });
        }
    }

    @Test
    @DisplayName("应该成功更新节点")
    void shouldUpdateNode_whenNodePropertiesAreUpdated() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (:Person {name: '周九', age: 40})");

            // When
            Result result = session.run(
                    "MATCH (p:Person {name: '周九'}) SET p.age = $newAge RETURN p",
                    Values.parameters("newAge", 41)
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            Node node = record.get("p").asNode();
            assertThat(node.get("age").asInt()).isEqualTo(41);
            log.info("更新节点测试通过: 节点年龄已更新为 {}", node.get("age").asInt());
        }
    }

    @Test
    @DisplayName("应该成功删除节点")
    void shouldDeleteNode_whenNodeExists() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (:Person {name: '吴十'})");

            // When
            Result result = session.run(
                    "MATCH (p:Person {name: '吴十'}) DELETE p RETURN count(p) AS deleted"
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            assertThat(record.get("deleted").asInt()).isEqualTo(1);
            log.info("删除节点测试通过: 已删除 {} 个节点", record.get("deleted").asInt());
        }
    }

    @Test
    @DisplayName("应该成功删除关系")
    void shouldDeleteRelationship_whenRelationshipExists() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (a:Person {name: 'A'})-[:FRIEND]->(b:Person {name: 'B'})");

            // When
            Result result = session.run(
                    "MATCH (a:Person {name: 'A'})-[r:FRIEND]->(b:Person {name: 'B'}) DELETE r RETURN count(r) AS deleted"
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            assertThat(record.get("deleted").asInt()).isEqualTo(1);
            log.info("删除关系测试通过: 已删除 {} 条关系", record.get("deleted").asInt());
        }
    }

    @Test
    @DisplayName("应该成功执行复杂查询")
    void shouldExecuteComplexQuery_whenMultipleNodesAndRelationshipsAreInvolved() {
        // Given
        try (Session session = neo4jDriver.session()) {
            // 创建社交网络
            session.run("CREATE (a:Person {name: 'Alice'})");
            session.run("CREATE (b:Person {name: 'Bob'})");
            session.run("CREATE (c:Person {name: 'Charlie'})");
            session.run("CREATE (d:Person {name: 'David'})");

            session.run("MATCH (a:Person {name: 'Alice'}), (b:Person {name: 'Bob'}) CREATE (a)-[:FRIEND]->(b)");
            session.run("MATCH (b:Person {name: 'Bob'}), (c:Person {name: 'Charlie'}) CREATE (b)-[:FRIEND]->(c)");
            session.run("MATCH (c:Person {name: 'Charlie'}), (d:Person {name: 'David'}) CREATE (c)-[:FRIEND]->(d)");

            // When
            Result result = session.run(
                    "MATCH (start:Person {name: 'Alice'})-[:FRIEND*1..2]->(friend:Person) " +
                            "RETURN DISTINCT friend.name AS name " +
                            "ORDER BY name"
            );

// Then
            List<org.neo4j.driver.Record> records = result.list();
            assertThat(records).hasSize(3); // Bob, Charlie, David
            log.info("复杂查询测试通过: Alice 的朋友和朋友的朋友有 {} 人", records.size());
            records.forEach(record -> {
                log.info("  - {}", record.get("name").asString());
            });
        }
    }

    @Test
    @DisplayName("应该成功执行聚合查询")
    void shouldExecuteAggregationQuery_whenAggregationFunctionsAreUsed() {
        // Given
        try (Session session = neo4jDriver.session()) {
            session.run("CREATE (:Person {name: 'P1', age: 20})");
            session.run("CREATE (:Person {name: 'P2', age: 25})");
            session.run("CREATE (:Person {name: 'P3', age: 30})");
            session.run("CREATE (:Person {name: 'P4', age: 35})");

            // When
            Result result = session.run(
                    "MATCH (p:Person) RETURN count(p) AS count, avg(p.age) AS avgAge, max(p.age) AS maxAge"
            );

            // Then
            assertThat(result.hasNext()).isTrue();
            org.neo4j.driver.Record record = result.next();
            assertThat(record.get("count").asInt()).isEqualTo(4);
            assertThat(record.get("avgAge").asDouble()).isEqualTo(27.5);
            assertThat(record.get("maxAge").asInt()).isEqualTo(35);
            log.info("聚合查询测试通过: count = {}, avgAge = {}, maxAge = {}",
                    record.get("count").asInt(),
                    record.get("avgAge").asDouble(),
                    record.get("maxAge").asInt());
        }
    }

    @Test
    @DisplayName("应该成功执行事务")
    void shouldExecuteTransaction_whenMultipleOperationsArePerformed() {
        // Given & When
        try (Session session = neo4jDriver.session()) {
            Integer count = session.executeWrite(tx -> {
                // 创建节点
                tx.run("CREATE (:Person {name: '事务测试1'})");
                tx.run("CREATE (:Person {name: '事务测试2'})");

                // 查询验证
                Result result = tx.run("MATCH (p:Person) WHERE p.name STARTS WITH '事务测试' RETURN count(p) AS count");
                org.neo4j.driver.Record record = result.next();
                return record.get("count").asInt();
            });

            // Then
            assertThat(count).isEqualTo(2);

            Result verifyResult = session.run(
                    "MATCH (p:Person) WHERE p.name STARTS WITH '事务测试' RETURN count(p) AS count"
            );
            org.neo4j.driver.Record verifyRecord = verifyResult.next();
            assertThat(verifyRecord.get("count").asInt()).isEqualTo(2);
            log.info("事务测试通过: 事务中成功创建 {} 个节点", verifyRecord.get("count").asInt());
        }
    }
}