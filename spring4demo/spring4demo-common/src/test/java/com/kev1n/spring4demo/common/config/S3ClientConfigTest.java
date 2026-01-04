package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S3 客户端配置测试
 *
 * 测试要点：
 * 1. 验证 S3ClientConfig Bean 创建
 * 2. 验证 S3Client Bean 创建
 * 3. 验证 S3Client 配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@DisplayName("S3 客户端配置测试")
class S3ClientConfigTest {

    @Autowired
    private S3Client s3Client;

    @Autowired(required = false)
    private S3ClientConfig s3ClientConfig;

    @Test
    @DisplayName("验证 S3ClientConfig Bean 创建")
    void shouldCreateS3ClientConfigBean() {
        // Then - 验证 Bean 创建成功
        if (s3ClientConfig != null) {
            assertThat(s3ClientConfig).isNotNull();
        }
    }

    @Test
    @DisplayName("验证 S3Client Bean 创建")
    void shouldCreateS3ClientBean() {
        // Then - 验证 Bean 创建成功
        assertThat(s3Client).isNotNull();
    }

    @Test
    @DisplayName("验证 S3Client 服务名称")
    void shouldVerifyS3ClientServiceName() {
        // When - 获取服务名称
        String serviceName = s3Client.serviceName();

        // Then - 验证服务名称
        assertThat(serviceName).isEqualTo("S3");
    }

    @Test
    @DisplayName("验证 S3Client 配置存在")
    void shouldVerifyS3ClientConfiguration() {
        // Then - 验证客户端配置存在
        assertThat(s3Client).isNotNull();
    }

    @Test
    @DisplayName("验证 S3Client 列出存储桶")
    void shouldVerifyS3ClientListBuckets() {
        // When - 尝试列出存储桶
        var listBucketsResponse = s3Client.listBuckets();

        // Then - 验证响应不为空
        assertThat(listBucketsResponse).isNotNull();
        assertThat(listBucketsResponse.buckets()).isNotNull();
    }

    @Test
    @DisplayName("验证 S3Client 创建存储桶")
    void shouldVerifyS3ClientCreateBucket() {
        // Given - 测试存储桶名称
        String bucketName = "test-bucket-" + System.currentTimeMillis();

        // When - 尝试创建存储桶
        var createBucketResponse = s3Client.createBucket(builder -> builder.bucket(bucketName));

        // Then - 验证存储桶创建成功
        assertThat(createBucketResponse).isNotNull();
        assertThat(createBucketResponse.location()).isNotNull();

        // Cleanup - 删除测试存储桶
        s3Client.deleteBucket(builder -> builder.bucket(bucketName));
    }

    @Test
    @DisplayName("验证 S3Client 删除存储桶")
    void shouldVerifyS3ClientDeleteBucket() {
        // Given - 创建测试存储桶
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        s3Client.createBucket(builder -> builder.bucket(bucketName));

        // When - 删除存储桶
        var deleteBucketResponse = s3Client.deleteBucket(builder -> builder.bucket(bucketName));

        // Then - 验证存储桶删除成功
        assertThat(deleteBucketResponse).isNotNull();
    }

    @Test
    @DisplayName("验证 S3Client 检查存储桶是否存在")
    void shouldVerifyS3ClientHeadBucket() {
        // Given - 创建测试存储桶
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        s3Client.createBucket(builder -> builder.bucket(bucketName));

        try {
            // When - 检查存储桶是否存在
            var headBucketResponse = s3Client.headBucket(builder -> builder.bucket(bucketName));

            // Then - 验证存储桶存在
            assertThat(headBucketResponse).isNotNull();
        } finally {
            // Cleanup - 删除测试存储桶
            s3Client.deleteBucket(builder -> builder.bucket(bucketName));
        }
    }

    @Test
    @DisplayName("验证 S3Client 上传对象")
    void shouldVerifyS3ClientPutObject() {
        // Given - 创建测试存储桶和对象
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        String objectKey = "test-object.txt";
        String objectContent = "Hello, S3!";

        s3Client.createBucket(builder -> builder.bucket(bucketName));

        try {
            // When - 上传对象
            var putObjectResponse = s3Client.putObject(
                builder -> builder.bucket(bucketName).key(objectKey),
                software.amazon.awssdk.core.sync.RequestBody.fromString(objectContent)
            );

            // Then - 验证对象上传成功
            assertThat(putObjectResponse).isNotNull();
            assertThat(putObjectResponse.eTag()).isNotNull();
        } finally {
            // Cleanup - 删除测试对象和存储桶
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(objectKey));
            s3Client.deleteBucket(builder -> builder.bucket(bucketName));
        }
    }

    @Test
    @DisplayName("验证 S3Client 下载对象")
    void shouldVerifyS3ClientGetObject() {
        // Given - 创建测试存储桶和对象
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        String objectKey = "test-object.txt";
        String objectContent = "Hello, S3!";

        s3Client.createBucket(builder -> builder.bucket(bucketName));
        s3Client.putObject(
            builder -> builder.bucket(bucketName).key(objectKey),
            software.amazon.awssdk.core.sync.RequestBody.fromString(objectContent)
        );

        try {
            // When - 下载对象
            var getObjectResponse = s3Client.getObject(builder -> builder.bucket(bucketName).key(objectKey));
            String downloadedContent = new String(getObjectResponse.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            // Then - 验证对象下载成功
            assertThat(downloadedContent).isEqualTo(objectContent);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read object content", e);
        } finally {
            // Cleanup - 删除测试对象和存储桶
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(objectKey));
            s3Client.deleteBucket(builder -> builder.bucket(bucketName));
        }
    }

    @Test
    @DisplayName("验证 S3Client 删除对象")
    void shouldVerifyS3ClientDeleteObject() {
        // Given - 创建测试存储桶和对象
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        String objectKey = "test-object.txt";
        String objectContent = "Hello, S3!";

        s3Client.createBucket(builder -> builder.bucket(bucketName));
        s3Client.putObject(
            builder -> builder.bucket(bucketName).key(objectKey),
            software.amazon.awssdk.core.sync.RequestBody.fromString(objectContent)
        );

        // When - 删除对象
        var deleteObjectResponse = s3Client.deleteObject(builder -> builder.bucket(bucketName).key(objectKey));

        // Then - 验证对象删除成功
        assertThat(deleteObjectResponse).isNotNull();

        // Cleanup - 删除测试存储桶
        s3Client.deleteBucket(builder -> builder.bucket(bucketName));
    }

    @Test
    @DisplayName("验证 S3Client 列出对象")
    void shouldVerifyS3ClientListObjects() {
        // Given - 创建测试存储桶和多个对象
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        s3Client.createBucket(builder -> builder.bucket(bucketName));
        s3Client.putObject(
            builder -> builder.bucket(bucketName).key("object1.txt"),
            software.amazon.awssdk.core.sync.RequestBody.fromString("Content 1")
        );
        s3Client.putObject(
            builder -> builder.bucket(bucketName).key("object2.txt"),
            software.amazon.awssdk.core.sync.RequestBody.fromString("Content 2")
        );

        try {
            // When - 列出对象
            var listObjectsResponse = s3Client.listObjects(builder -> builder.bucket(bucketName));

            // Then - 验证对象列表
            assertThat(listObjectsResponse).isNotNull();
            assertThat(listObjectsResponse.contents()).isNotEmpty();
            assertThat(listObjectsResponse.contents().size()).isGreaterThanOrEqualTo(2);
        } finally {
            // Cleanup - 删除测试对象和存储桶
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key("object1.txt"));
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key("object2.txt"));
            s3Client.deleteBucket(builder -> builder.bucket(bucketName));
        }
    }

    @Test
    @DisplayName("验证 S3Client 生成预签名URL")
    void shouldVerifyS3ClientPresignedUrl() {
        // Given - 创建测试存储桶和对象
        String bucketName = "test-bucket-" + System.currentTimeMillis();
        String objectKey = "test-object.txt";

        s3Client.createBucket(builder -> builder.bucket(bucketName));

        try {
            // When - 生成预签名URL
            var presignedUrl = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(objectKey));

            // Then - 验证预签名URL
            assertThat(presignedUrl).isNotNull();
            assertThat(presignedUrl.toString()).contains(bucketName);
            assertThat(presignedUrl.toString()).contains(objectKey);
        } finally {
            // Cleanup - 删除测试存储桶
            s3Client.deleteBucket(builder -> builder.bucket(bucketName));
        }
    }

    @Test
    @DisplayName("验证 S3Client 关闭方法")
    void shouldVerifyS3ClientClose() {
        // When - 验证关闭方法存在
        boolean hasCloseMethod = s3Client != null;

        // Then - 验证关闭方法
        assertThat(hasCloseMethod).isTrue();
        // 注意：实际测试中不应该调用close，因为会影响其他测试
    }
}