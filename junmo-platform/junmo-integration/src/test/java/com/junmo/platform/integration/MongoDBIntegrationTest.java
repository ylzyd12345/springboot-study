package com.junmo.platform.integration;

import com.junmo.platform.test.config.TestContainersConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MongoDB 集成测试
 * 测试 MongoDB 容器的启动和基本操作
 *
 * @author junmo
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("MongoDB 集成测试")
class MongoDBIntegrationTest {

    private static final String DATABASE_NAME = "test";
    private static final String COLLECTION_NAME = "products";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @BeforeEach
    void setUp() {
        log.info("初始化 MongoDB 连接...");
        log.info("MongoDB 连接字符串: {}", TestContainersConfig.getMongoDBConnectionString());

        // 创建 MongoDB 客户端
        mongoClient = MongoClients.create(TestContainersConfig.getMongoDBConnectionString());
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);

        log.info("MongoDB 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        if (collection != null) {
            collection.drop();
            log.info("删除集合: {}", COLLECTION_NAME);
        }
        if (mongoClient != null) {
            mongoClient.close();
            log.info("MongoDB 连接已关闭");
        }
    }

    @Test
    @DisplayName("应该成功连接到 MongoDB 容器")
    void shouldConnectToMongoDBContainer_whenContainerIsRunning() {
        // Given & When
        String databaseName = database.getName();

        // Then
        assertThat(databaseName).isEqualTo(DATABASE_NAME);
        log.info("MongoDB 连接测试通过: 数据库名称 = {}", databaseName);
    }

    @Test
    @DisplayName("应该成功插入单条文档")
    void shouldInsertSingleDocument_whenDocumentIsProvided() {
        // Given
        Document document = new Document("name", "iPhone 15")
                .append("price", 999.99)
                .append("stock", 100)
                .append("category", "Electronics");

        // When
        collection.insertOne(document);
        log.info("插入单条文档: name = {}, price = {}", document.getString("name"), document.getDouble("price"));

        // Then - 验证文档已插入
        long count = collection.countDocuments();
        assertThat(count).isEqualTo(1);
        log.info("插入单条文档测试通过: 集合中有 {} 条文档", count);
    }

    @Test
    @DisplayName("应该成功插入多条文档")
    void shouldInsertMultipleDocuments_whenMultipleDocumentsAreProvided() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60));

        // When
        collection.insertMany(documents);
        log.info("插入多条文档: {} 条文档", documents.size());

        // Then - 验证文档已插入
        long count = collection.countDocuments();
        assertThat(count).isEqualTo(3);
        log.info("插入多条文档测试通过: 集合中有 {} 条文档", count);
    }

    @Test
    @DisplayName("应该成功查询文档")
    void shouldQueryDocuments_whenQueryIsExecuted() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60));
        collection.insertMany(documents);

        // When
        Document query = new Document("price", new Document("$gt", 850.0));
        List<Document> results = new ArrayList<>();
        collection.find(query).into(results);

        // Then
        assertThat(results).hasSize(2);
        for (Document doc : results) {
            log.info("  - 名称: {}, 价格: {}", doc.getString("name"), doc.getDouble("price"));
        }
        log.info("查询文档测试通过: 查询返回 {} 条文档", results.size());
    }

    @Test
    @DisplayName("应该成功更新文档")
    void shouldUpdateDocument_whenUpdateQueryIsExecuted() {
        // Given
        Document document = new Document("name", "iPhone 15")
                .append("price", 999.99)
                .append("stock", 100);
        collection.insertOne(document);

        // When
        Document update = new Document("$set", new Document("price", 899.99).append("stock", 150));
        Document filter = new Document("name", "iPhone 15");
        long modifiedCount = collection.updateOne(filter, update).getModifiedCount();

        // Then
        assertThat(modifiedCount).isEqualTo(1);

        // 验证更新后的数据
        Document updatedDoc = collection.find(filter).first();
        assertThat(updatedDoc.getDouble("price")).isEqualTo(899.99);
        assertThat(updatedDoc.getInteger("stock")).isEqualTo(150);

        log.info("更新文档测试通过: 影响行数 = {}", modifiedCount);
    }

    @Test
    @DisplayName("应该成功删除文档")
    void shouldDeleteDocument_whenDeleteQueryIsExecuted() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60));
        collection.insertMany(documents);

        // When
        Document filter = new Document("price", new Document("$lt", 850.0));
        long deletedCount = collection.deleteOne(filter).getDeletedCount();

        // Then
        assertThat(deletedCount).isEqualTo(1);

        // 验证删除后的数据
        long remainingCount = collection.countDocuments();
        assertThat(remainingCount).isEqualTo(2);

        log.info("删除文档测试通过: 删除行数 = {}, 剩余行数 = {}", deletedCount, remainingCount);
    }

    @Test
    @DisplayName("应该成功执行聚合查询")
    void shouldExecuteAggregationQuery_whenAggregationPipelineIsProvided() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100).append("category", "Electronics"));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80).append("category", "Electronics"));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60).append("category", "Electronics"));
        documents.add(new Document("name", "MacBook Pro").append("price", 1999.99).append("stock", 50).append("category", "Electronics"));
        documents.add(new Document("name", "iPad Pro").append("price", 1099.99).append("stock", 70).append("category", "Electronics"));
        collection.insertMany(documents);

        // When
        List<Document> aggregationPipeline = List.of(
                new Document("$match", new Document("category", "Electronics")),
                new Document("$group", new Document("_id", "$category")
                        .append("avgPrice", new Document("$avg", "$price"))
                        .append("totalStock", new Document("$sum", "$stock"))
                        .append("count", new Document("$sum", 1)))
        );

        List<Document> results = new ArrayList<>();
        collection.aggregate(aggregationPipeline).into(results);

        // Then
        assertThat(results).hasSize(1);
        Document result = results.get(0);
        assertThat(result.getDouble("avgPrice")).isGreaterThan(0);
        assertThat(result.getInteger("totalStock")).isEqualTo(360);
        assertThat(result.getInteger("count")).isEqualTo(5);

        log.info("聚合查询测试通过: 平均价格 = {}, 总库存 = {}, 数量 = {}",
                result.getDouble("avgPrice"),
                result.getInteger("totalStock"),
                result.getInteger("count"));
    }

    @Test
    @DisplayName("应该成功执行排序查询")
    void shouldExecuteSortedQuery_whenSortIsSpecified() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60));
        collection.insertMany(documents);

        // When
        Document sort = new Document("price", 1); // 1 表示升序
        List<Document> results = new ArrayList<>();
        collection.find().sort(sort).into(results);

        // Then
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getString("name")).isEqualTo("Google Pixel");
        assertThat(results.get(1).getString("name")).isEqualTo("Samsung Galaxy");
        assertThat(results.get(2).getString("name")).isEqualTo("iPhone 15");

        log.info("排序查询测试通过: 按价格升序排列");
    }

    @Test
    @DisplayName("应该成功执行分页查询")
    void shouldExecutePaginatedQuery_whenSkipAndLimitAreSpecified() {
        // Given
        List<Document> documents = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            documents.add(new Document("name", "Product " + i).append("price", 100.0 + i).append("stock", 10 + i));
        }
        collection.insertMany(documents);

        // When - 查询第2页，每页3条记录
        int page = 2;
        int pageSize = 3;
        int skip = (page - 1) * pageSize;

        List<Document> results = new ArrayList<>();
        collection.find().skip(skip).limit(pageSize).into(results);

        // Then
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getString("name")).isEqualTo("Product 4");
        assertThat(results.get(1).getString("name")).isEqualTo("Product 5");
        assertThat(results.get(2).getString("name")).isEqualTo("Product 6");

        log.info("分页查询测试通过: 第 {} 页，每页 {} 条记录", page, pageSize);
    }

    @Test
    @DisplayName("应该成功执行条件查询")
    void shouldExecuteConditionalQuery_whenMultipleConditionsAreProvided() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100).append("category", "Electronics"));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80).append("category", "Electronics"));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60).append("category", "Electronics"));
        documents.add(new Document("name", "MacBook Pro").append("price", 1999.99).append("stock", 50).append("category", "Electronics"));
        documents.add(new Document("name", "iPad Pro").append("price", 1099.99).append("stock", 70).append("category", "Electronics"));
        collection.insertMany(documents);

        // When - 查询价格在 800 到 1200 之间且库存大于 60 的产品
        Document query = new Document("price", new Document("$gte", 800.0).append("$lte", 1200.0))
                .append("stock", new Document("$gt", 60));
        List<Document> results = new ArrayList<>();
        collection.find(query).into(results);

        // Then
        assertThat(results).hasSize(2);
        for (Document doc : results) {
            log.info("  - 名称: {}, 价格: {}, 库存: {}",
                    doc.getString("name"),
                    doc.getDouble("price"),
                    doc.getInteger("stock"));
        }
        log.info("条件查询测试通过: 查询返回 {} 条文档", results.size());
    }

    @Test
    @DisplayName("应该成功执行批量更新")
    void shouldExecuteBatchUpdate_whenMultipleDocumentsAreUpdated() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100).append("category", "Electronics"));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80).append("category", "Electronics"));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60).append("category", "Electronics"));
        collection.insertMany(documents);

        // When - 将所有 Electronics 类别的产品库存增加 20
        Document filter = new Document("category", "Electronics");
        Document update = new Document("$inc", new Document("stock", 20));
        long modifiedCount = collection.updateMany(filter, update).getModifiedCount();

        // Then
        assertThat(modifiedCount).isEqualTo(3);

        // 验证更新后的数据
        List<Document> results = new ArrayList<>();
        collection.find(filter).into(results);
        for (Document doc : results) {
            assertThat(doc.getInteger("stock")).isGreaterThan(70);
        }

        log.info("批量更新测试通过: 影响行数 = {}", modifiedCount);
    }

    @Test
    @DisplayName("应该成功执行批量删除")
    void shouldExecuteBatchDelete_whenMultipleDocumentsAreDeleted() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100).append("category", "Electronics"));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80).append("category", "Electronics"));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60).append("category", "Electronics"));
        documents.add(new Document("name", "MacBook Pro").append("price", 1999.99).append("stock", 50).append("category", "Electronics"));
        documents.add(new Document("name", "iPad Pro").append("price", 1099.99).append("stock", 70).append("category", "Electronics"));
        collection.insertMany(documents);

        // When - 删除价格大于 1000 的产品
        Document filter = new Document("price", new Document("$gt", 1000.0));
        long deletedCount = collection.deleteMany(filter).getDeletedCount();

        // Then
        assertThat(deletedCount).isEqualTo(3);

        // 验证删除后的数据
        long remainingCount = collection.countDocuments();
        assertThat(remainingCount).isEqualTo(2);

        log.info("批量删除测试通过: 删除行数 = {}, 剩余行数 = {}", deletedCount, remainingCount);
    }

    @Test
    @DisplayName("应该成功创建索引")
    void shouldCreateIndex_whenIndexDefinitionIsProvided() {
        // Given
        Document indexKeys = new Document("name", 1);
        String indexName = "name_1";

        // When
        collection.createIndex(indexKeys);
        log.info("创建索引: {}", indexName);

        // Then - 验证索引已创建
        List<Document> indexes = new ArrayList<>();
        for (Document index : collection.listIndexes()) {
            indexes.add(index);
        }

        assertThat(indexes).anyMatch(index -> index.getString("name").equals(indexName));
        log.info("创建索引测试通过: 索引 {} 已创建", indexName);
    }

    @Test
    @DisplayName("应该成功执行复杂聚合查询")
    void shouldExecuteComplexAggregation_whenMultipleStagesAreProvided() {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("name", "iPhone 15").append("price", 999.99).append("stock", 100).append("category", "Electronics"));
        documents.add(new Document("name", "Samsung Galaxy").append("price", 899.99).append("stock", 80).append("category", "Electronics"));
        documents.add(new Document("name", "Google Pixel").append("price", 799.99).append("stock", 60).append("category", "Electronics"));
        documents.add(new Document("name", "MacBook Pro").append("price", 1999.99).append("stock", 50).append("category", "Electronics"));
        documents.add(new Document("name", "iPad Pro").append("price", 1099.99).append("stock", 70).append("category", "Electronics"));
        documents.add(new Document("name", "AirPods").append("price", 199.99).append("stock", 200).append("category", "Accessories"));
        documents.add(new Document("name", "Magic Keyboard").append("price", 299.99).append("stock", 150).append("category", "Accessories"));
        collection.insertMany(documents);

        // When - 按类别分组，计算平均价格和总库存，并按平均价格降序排列
        List<Document> aggregationPipeline = List.of(
                new Document("$group", new Document("_id", "$category")
                        .append("avgPrice", new Document("$avg", "$price"))
                        .append("totalStock", new Document("$sum", "$stock"))
                        .append("count", new Document("$sum", 1))),
                new Document("$sort", new Document("avgPrice", -1))
        );

        List<Document> results = new ArrayList<>();
        collection.aggregate(aggregationPipeline).into(results);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getString("_id")).isEqualTo("Electronics");
        assertThat(results.get(1).getString("_id")).isEqualTo("Accessories");

        for (Document result : results) {
            log.info("  - 类别: {}, 平均价格: {}, 总库存: {}, 数量: {}",
                    result.getString("_id"),
                    result.getDouble("avgPrice"),
                    result.getInteger("totalStock"),
                    result.getInteger("count"));
        }

        log.info("复杂聚合查询测试通过: 返回 {} 个分组", results.size());
    }
}