package com.kev1n.spring4demo.test.config;


import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.neo4j.Neo4jContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers 测试配置类
 * 用于集成测试中提供容器化的数据库服务
 *
 * @author kev1n
 * @since 1.0.0
 */
@TestConfiguration
@Profile("test")
public class TestContainersConfig {

    private TestContainersConfig() {
        // fix pmd
    }

    /**
     * MySQL 容器配置
     */
    public static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer(
            DockerImageName.parse("mysql:9.5.0"))
            .withDatabaseName("spring4demo_test")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(3306);

    /**
     * MongoDB 容器配置
     */
    public static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(
            DockerImageName.parse("mongo:7.0"))
            .withExposedPorts(27017);

    /**
     * Redis 容器配置
     */
    public static final RedisContainer REDIS_CONTAINER = new RedisContainer(
            DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379);

    /**
     * Elasticsearch 容器配置
     */
    public static final ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(
            DockerImageName.parse("elasticsearch:8.11.4"))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withExposedPorts(9200, 9300);

    /**
     * Neo4j 容器配置
     */
    public static final Neo4jContainer NEO4J_CONTAINER = new Neo4jContainer(
            DockerImageName.parse("neo4j:5.12"))
            .withAdminPassword("password")
            .withExposedPorts(7474, 7687);

    /**
     * InfluxDB 3.0 容器配置
     * 使用 GenericContainer 而不是 InfluxDBContainer，因为 InfluxDBContainer 是针对 2.x 版本的
     */
    public static final GenericContainer<?> INFLUXDB_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("influxdb:3.0"))
            .withEnv("INFLUXDB_MODE", "dev")
            .withEnv("INFLUXDB_USERNAME", "admin")
            .withEnv("INFLUXDB_PASSWORD", "password")
            .withEnv("INFLUXDB_ORG", "spring4demo")
            .withEnv("INFLUXDB_BUCKET", "test")
            .withExposedPorts(8086, 8088);

    /**
     * RabbitMQ 容器配置
     */
    public static final RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.12-management-alpine"))
            .withAdminUser("admin")
            .withAdminPassword("admin")
            .withExposedPorts(5672, 15672);

    /**
     * Kafka 容器配置
     */
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:3.6.1"))
            .withExposedPorts(9092);

    static {
        // 启动所有容器
        MYSQL_CONTAINER.start();
        MONGODB_CONTAINER.start();
        REDIS_CONTAINER.start();
        ELASTICSEARCH_CONTAINER.start();
        NEO4J_CONTAINER.start();
        INFLUXDB_CONTAINER.start();
        RABBITMQ_CONTAINER.start();
        KAFKA_CONTAINER.start();
    }

    /**
     * 获取MySQL容器JDBC URL
     *
     * @return JDBC URL
     */
    public static String getMySQLJdbcUrl() {
        return MYSQL_CONTAINER.getJdbcUrl();
    }

    /**
     * 获取MySQL容器用户名
     *
     * @return 用户名
     */
    public static String getMySQLUsername() {
        return MYSQL_CONTAINER.getUsername();
    }

    /**
     * 获取MySQL容器密码
     *
     * @return 密码
     */
    public static String getMySQLPassword() {
        return MYSQL_CONTAINER.getPassword();
    }

    /**
     * 获取MongoDB容器连接字符串
     *
     * @return MongoDB连接字符串
     */
    public static String getMongoDBConnectionString() {
        return MONGODB_CONTAINER.getConnectionString();
    }

    /**
     * 获取Redis容器主机地址
     *
     * @return Redis主机地址
     */
    public static String getRedisHost() {
        return REDIS_CONTAINER.getHost();
    }

    /**
     * 获取Redis容器端口
     *
     * @return Redis端口
     */
    public static Integer getRedisPort() {
        return REDIS_CONTAINER.getFirstMappedPort();
    }

    /**
     * 获取Redis容器连接字符串
     *
     * @return Redis连接字符串
     */
    public static String getRedisConnectionString() {
        return "redis://" + getRedisHost() + ":" + getRedisPort();
    }

    /**
     * 获取Elasticsearch容器HTTP URL
     *
     * @return Elasticsearch HTTP URL
     */
    public static String getElasticsearchHttpUrl() {
        return "http://" + ELASTICSEARCH_CONTAINER.getHost() + ":" + ELASTICSEARCH_CONTAINER.getMappedPort(9200);
    }

    /**
     * 获取Elasticsearch容器主机地址
     *
     * @return Elasticsearch主机地址
     */
    public static String getElasticsearchHost() {
        return ELASTICSEARCH_CONTAINER.getHost();
    }

    /**
     * 获取Elasticsearch容器HTTP端口
     *
     * @return Elasticsearch HTTP端口
     */
    public static Integer getElasticsearchHttpPort() {
        return ELASTICSEARCH_CONTAINER.getMappedPort(9200);
    }

    /**
     * 获取Neo4j容器Bolt URL
     *
     * @return Neo4j Bolt URL
     */
    public static String getNeo4jBoltUrl() {
        return NEO4J_CONTAINER.getBoltUrl();
    }

    /**
     * 获取Neo4j容器HTTP URL
     *
     * @return Neo4j HTTP URL
     */
    public static String getNeo4jHttpUrl() {
        return NEO4J_CONTAINER.getHttpUrl();
    }

    /**
     * 获取Neo4j容器密码
     *
     * @return Neo4j密码
     */
    public static String getNeo4jPassword() {
        return NEO4J_CONTAINER.getAdminPassword();
    }

    /**
     * 获取InfluxDB容器URL
     *
     * @return InfluxDB URL
     */
    public static String getInfluxDbUrl() {
        return "http://" + INFLUXDB_CONTAINER.getHost() + ":" + INFLUXDB_CONTAINER.getFirstMappedPort();
    }

    /**
     * 获取InfluxDB容器主机地址
     *
     * @return InfluxDB主机地址
     */
    public static String getInfluxDbHost() {
        return INFLUXDB_CONTAINER.getHost();
    }

    /**
     * 获取InfluxDB容器端口
     *
     * @return InfluxDB端口
     */
    public static Integer getInfluxDbPort() {
        return INFLUXDB_CONTAINER.getFirstMappedPort();
    }

    /**
     * 获取RabbitMQ容器连接地址
     *
     * @return RabbitMQ连接地址
     */
    public static String getRabbitMQAmqpUrl() {
        return RABBITMQ_CONTAINER.getAmqpUrl();
    }

    /**
     * 获取RabbitMQ容器主机地址
     *
     * @return RabbitMQ主机地址
     */
    public static String getRabbitMQHost() {
        return RABBITMQ_CONTAINER.getHost();
    }

    /**
     * 获取RabbitMQ容器AMQP端口
     *
     * @return RabbitMQ AMQP端口
     */
    public static Integer getRabbitMQAmqpPort() {
        return RABBITMQ_CONTAINER.getMappedPort(5672);
    }

    /**
     * 获取RabbitMQ容器管理界面URL
     *
     * @return RabbitMQ管理界面URL
     */
    public static String getRabbitMQManagementUrl() {
        return "http://" + RABBITMQ_CONTAINER.getHost() + ":" + RABBITMQ_CONTAINER.getMappedPort(15672);
    }

    /**
     * 获取RabbitMQ容器用户名
     *
     * @return RabbitMQ用户名
     */
    public static String getRabbitMQUsername() {
        return RABBITMQ_CONTAINER.getAdminUsername();
    }

    /**
     * 获取RabbitMQ容器密码
     *
     * @return RabbitMQ密码
     */
    public static String getRabbitMQPassword() {
        return RABBITMQ_CONTAINER.getAdminPassword();
    }

    /**
     * 获取Kafka容器Bootstrap Servers
     *
     * @return Kafka Bootstrap Servers
     */
    public static String getKafkaBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }

    /**
     * 获取Kafka容器主机地址
     *
     * @return Kafka主机地址
     */
    public static String getKafkaHost() {
        return KAFKA_CONTAINER.getHost();
    }

    /**
     * 获取Kafka容器端口
     *
     * @return Kafka端口
     */
    public static Integer getKafkaPort() {
        return KAFKA_CONTAINER.getFirstMappedPort();
    }
}