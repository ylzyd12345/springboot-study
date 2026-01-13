package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySQL 集成测试
 * 测试 MySQL 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("MySQL 集成测试")
class MySQLIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        log.info("初始化 MySQL 连接...");
        log.info("MySQL JDBC URL: {}", TestContainersConfig.getMySQLJdbcUrl());
        log.info("MySQL 用户名: {}", TestContainersConfig.getMySQLUsername());

        // 创建 MySQL 连接
        connection = DriverManager.getConnection(
                TestContainersConfig.getMySQLJdbcUrl(),
                TestContainersConfig.getMySQLUsername(),
                TestContainersConfig.getMySQLPassword()
        );

        log.info("MySQL 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        if (connection != null) {
            try {
                connection.close();
                log.info("MySQL 连接已关闭");
            } catch (SQLException e) {
                log.error("关闭 MySQL 连接失败", e);
            }
        }
    }

    @Test
    @DisplayName("应该成功连接到 MySQL 容器")
    void shouldConnectToMySQLContainer_whenContainerIsRunning() throws SQLException {
        // Given & When
        boolean isValid = connection.isValid(5);

        // Then
        assertThat(isValid).isTrue();
        log.info("MySQL 连接测试通过: {}", isValid);
    }

    @Test
    @DisplayName("应该成功创建表")
    void shouldCreateTable_whenTableDefinitionIsProvided() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        // When
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
            log.info("创建表 test_users 成功");
        }

        // Then - 验证表是否存在
        try (ResultSet rs = connection.getMetaData().getTables(null, null, "test_users", null)) {
            assertThat(rs.next()).isTrue();
            log.info("创建表测试通过: 表 test_users 存在");
        }
    }

    @Test
    @DisplayName("应该成功插入单条数据")
    void shouldInsertSingleRecord_whenDataIsProvided() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "testuser");
            ps.setString(2, "test@example.com");
            ps.setInt(3, 25);
            int affectedRows = ps.executeUpdate();

            // Then
            assertThat(affectedRows).isEqualTo(1);
            log.info("插入单条数据测试通过: 影响行数 = {}", affectedRows);
        }
    }

    @Test
    @DisplayName("应该成功插入多条数据")
    void shouldInsertMultipleRecords_whenMultipleDataAreProvided() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user1");
            ps.setString(2, "user1@example.com");
            ps.setInt(3, 20);
            ps.addBatch();

            ps.setString(1, "user2");
            ps.setString(2, "user2@example.com");
            ps.setInt(3, 25);
            ps.addBatch();

            ps.setString(1, "user3");
            ps.setString(2, "user3@example.com");
            ps.setInt(3, 30);
            ps.addBatch();

            int[] affectedRows = ps.executeBatch();

            // Then
            assertThat(affectedRows).hasSize(3);
            assertThat(affectedRows).allMatch(rows -> rows == 1);
            log.info("插入多条数据测试通过: 插入 {} 条记录", affectedRows.length);
        }
    }

    @Test
    @DisplayName("应该成功查询数据")
    void shouldQueryData_whenQueryIsExecuted() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";
        String selectSql = "SELECT * FROM test_users WHERE age > ?";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user1");
            ps.setString(2, "user1@example.com");
            ps.setInt(3, 20);
            ps.executeUpdate();

            ps.setString(1, "user2");
            ps.setString(2, "user2@example.com");
            ps.setInt(3, 25);
            ps.executeUpdate();

            ps.setString(1, "user3");
            ps.setString(2, "user3@example.com");
            ps.setInt(3, 30);
            ps.executeUpdate();
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
            ps.setInt(1, 22);
            ResultSet rs = ps.executeQuery();

            // Then
            int count = 0;
            while (rs.next()) {
                count++;
                log.info("  - ID: {}, 用户名: {}, 邮箱: {}, 年龄: {}",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("age"));
            }

            assertThat(count).isEqualTo(2);
            log.info("查询数据测试通过: 查询返回 {} 条记录", count);
        }
    }

    @Test
    @DisplayName("应该成功更新数据")
    void shouldUpdateData_whenUpdateQueryIsExecuted() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";
        String updateSql = "UPDATE test_users SET age = ? WHERE username = ?";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "testuser");
            ps.setString(2, "test@example.com");
            ps.setInt(3, 25);
            ps.executeUpdate();
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
            ps.setInt(1, 30);
            ps.setString(2, "testuser");
            int affectedRows = ps.executeUpdate();

            // Then
            assertThat(affectedRows).isEqualTo(1);

            // 验证更新后的数据
            String selectSql = "SELECT age FROM test_users WHERE username = ?";
            try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
                selectPs.setString(1, "testuser");
                ResultSet rs = selectPs.executeQuery();
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("age")).isEqualTo(30);
            }

            log.info("更新数据测试通过: 影响行数 = {}", affectedRows);
        }
    }

    @Test
    @DisplayName("应该成功删除数据")
    void shouldDeleteData_whenDeleteQueryIsExecuted() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";
        String deleteSql = "DELETE FROM test_users WHERE age < ?";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user1");
            ps.setString(2, "user1@example.com");
            ps.setInt(3, 20);
            ps.executeUpdate();

            ps.setString(1, "user2");
            ps.setString(2, "user2@example.com");
            ps.setInt(3, 25);
            ps.executeUpdate();

            ps.setString(1, "user3");
            ps.setString(2, "user3@example.com");
            ps.setInt(3, 30);
            ps.executeUpdate();
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
            ps.setInt(1, 25);
            int affectedRows = ps.executeUpdate();

            // Then
            assertThat(affectedRows).isEqualTo(1);
            log.info("删除数据测试通过: 影响行数 = {}", affectedRows);
        }
    }

    @Test
    @DisplayName("应该成功执行聚合查询")
    void shouldExecuteAggregationQuery_whenAggregationFunctionsAreUsed() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        for (int i = 1; i <= 10; i++) {
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, "user" + i);
                ps.setString(2, "user" + i + "@example.com");
                ps.setInt(3, 20 + i);
                ps.executeUpdate();
            }
        }

        // When
        String avgSql = "SELECT AVG(age) as avg_age FROM test_users";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(avgSql)) {

            // Then
            assertThat(rs.next()).isTrue();
            double avgAge = rs.getDouble("avg_age");
            assertThat(avgAge).isEqualTo(25.0);
            log.info("聚合查询测试通过: 平均年龄 = {}", avgAge);
        }
    }

    @Test
    @DisplayName("应该成功执行事务")
    void shouldExecuteTransaction_whenMultipleOperationsAreInTransaction() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";
        String selectSql = "SELECT COUNT(*) as count FROM test_users";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        // When
        connection.setAutoCommit(false);

        try {
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, "user1");
                ps.setString(2, "user1@example.com");
                ps.setInt(3, 20);
                ps.executeUpdate();

                ps.setString(1, "user2");
                ps.setString(2, "user2@example.com");
                ps.setInt(3, 25);
                ps.executeUpdate();
            }

            connection.commit();

            // Then - 验证数据已提交
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(selectSql)) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("count")).isEqualTo(2);
                log.info("事务测试通过: 提交了 2 条记录");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Test
    @DisplayName("应该成功执行事务回滚")
    void shouldRollbackTransaction_whenExceptionOccurs() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";
        String selectSql = "SELECT COUNT(*) as count FROM test_users";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        // 插入第一条数据
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user1");
            ps.setString(2, "user1@example.com");
            ps.setInt(3, 20);
            ps.executeUpdate();
        }

        // When
        connection.setAutoCommit(false);

        try {
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, "user2");
                ps.setString(2, "user2@example.com");
                ps.setInt(3, 25);
                ps.executeUpdate();

                // 尝试插入重复的用户名，会抛出异常
                ps.setString(1, "user1");
                ps.setString(2, "user1_new@example.com");
                ps.setInt(3, 30);
                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            log.info("事务回滚: {}", e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }

        // Then - 验证数据已回滚，只有第一条数据
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(selectSql)) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("count")).isEqualTo(1);
            log.info("事务回滚测试通过: 事务已回滚，只有 1 条记录");
        }
    }

    @Test
    @DisplayName("应该成功执行批量操作")
    void shouldExecuteBatchOperations_whenMultipleOperationsAreBatched() throws SQLException {
        // Given
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "age INT" +
                ")";
        String insertSql = "INSERT INTO test_users (username, email, age) VALUES (?, ?, ?)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql);
        }

        // When
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(1, "user" + i);
                ps.setString(2, "user" + i + "@example.com");
                ps.setInt(3, 20 + i);
                ps.addBatch();
            }

            int[] affectedRows = ps.executeBatch();

            // Then
            assertThat(affectedRows).hasSize(5);
            assertThat(affectedRows).allMatch(rows -> rows == 1);
            log.info("批量操作测试通过: 批量插入 {} 条记录", affectedRows.length);
        }
    }
}