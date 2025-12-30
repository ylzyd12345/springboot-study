package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.common.config.RustFSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * FileStorageServiceImpl 单元测试
 *
 * <p>测试 FileStorageServiceImpl 的所有文件存储相关方法</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("文件存储服务单元测试")
class FileStorageServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private RustFSProperties rustFSProperties;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String ENDPOINT = "http://localhost:9000";

    @BeforeEach
    void setUp() {
        when(rustFSProperties.getBucketName()).thenReturn(BUCKET_NAME);
        when(rustFSProperties.getEndpoint()).thenReturn(ENDPOINT);
    }

    @Test
    @DisplayName("上传文件 - 成功")
    void uploadFile_Success() throws Exception {
        // Given
        String fileName = "test.txt";
        String contentType = "text/plain";
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        long contentLength = content.length();

        when(s3Client.headBucket(any(HeadBucketRequest.class))).thenReturn(HeadBucketResponse.builder().build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        // When
        String result = fileStorageService.uploadFile(inputStream, fileName, contentType, contentLength);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).endsWith(".txt");
        verify(s3Client, times(1)).headBucket(any(HeadBucketRequest.class));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("上传文件 - 存储桶不存在自动创建")
    void uploadFile_CreateBucket() throws Exception {
        // Given
        String fileName = "test.txt";
        String contentType = "text/plain";
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        long contentLength = content.length();

        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.class);
        when(s3Client.createBucket(any(CreateBucketRequest.class)))
                .thenReturn(CreateBucketResponse.builder().build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = fileStorageService.uploadFile(inputStream, fileName, contentType, contentLength);

        // Then
        assertThat(result).isNotNull();
        verify(s3Client, times(1)).headBucket(any(HeadBucketRequest.class));
        verify(s3Client, times(1)).createBucket(any(CreateBucketRequest.class));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("下载文件 - 成功")
    void downloadFile_Success() throws Exception {
        // Given
        String fileName = "test.txt";
        String content = "Hello, World!";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // 创建一个 mock 的 ResponseInputStream
        ResponseInputStream<GetObjectResponse> responseStream =
                mock(ResponseInputStream.class);
        when(responseStream.readAllBytes()).thenReturn(contentBytes);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        // When
        byte[] result = fileStorageService.downloadFile(fileName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(contentBytes);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("下载文件流 - 成功")
    void downloadFileStream_Success() throws Exception {
        // Given
        String fileName = "test.txt";
        String content = "Hello, World!";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // 创建一个 mock 的 ResponseInputStream
        ResponseInputStream<GetObjectResponse> responseStream =
                mock(ResponseInputStream.class);
        when(responseStream.readAllBytes()).thenReturn(contentBytes);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        // When
        InputStream result = fileStorageService.downloadFileStream(fileName);

        // Then
        assertThat(result).isNotNull();
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("删除文件 - 成功")
    void deleteFile_Success() {
        // Given
        String fileName = "test.txt";
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        // When
        fileStorageService.deleteFile(fileName);

        // Then
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("批量删除文件 - 成功")
    void deleteFiles_Success() {
        // Given
        List<String> fileNames = List.of("file1.txt", "file2.txt", "file3.txt");
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        // When
        fileStorageService.deleteFiles(fileNames);

        // Then
        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("检查文件是否存在 - 存在")
    void fileExists_True() {
        // Given
        String fileName = "test.txt";
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        // When
        boolean result = fileStorageService.fileExists(fileName);

        // Then
        assertThat(result).isTrue();
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("检查文件是否存在 - 不存在")
    void fileExists_False() {
        // Given
        String fileName = "nonexistent.txt";
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.class);

        // When
        boolean result = fileStorageService.fileExists(fileName);

        // Then
        assertThat(result).isFalse();
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("获取文件信息 - 成功")
    void getFileInfo_Success() {
        // Given
        String fileName = "test.txt";
        long contentLength = 1024L;
        String contentType = "text/plain";
        String eTag = "abc123";
        Instant lastModified = Instant.now();

        HeadObjectResponse response = HeadObjectResponse.builder()
                .contentLength(contentLength)
                .contentType(contentType)
                .eTag(eTag)
                .lastModified(lastModified)
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        // When
        Map<String, Object> result = fileStorageService.getFileInfo(fileName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("fileName")).isEqualTo(fileName);
        assertThat(result.get("contentLength")).isEqualTo(contentLength);
        assertThat(result.get("contentType")).isEqualTo(contentType);
        assertThat(result.get("eTag")).isEqualTo(eTag);
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("获取文件信息 - 文件不存在")
    void getFileInfo_NotFound() {
        // Given
        String fileName = "nonexistent.txt";
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.class);

        // When & Then
        assertThatThrownBy(() -> fileStorageService.getFileInfo(fileName))
                .isInstanceOf(com.kev1n.spring4demo.common.exception.BusinessException.class);

        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("列出所有文件 - 成功")
    void listFiles_Success() {
        // Given
        S3Object s3Object1 = S3Object.builder()
                .key("file1.txt")
                .size(1024L)
                .lastModified(Instant.now())
                .build();

        S3Object s3Object2 = S3Object.builder()
                .key("file2.txt")
                .size(2048L)
                .lastModified(Instant.now())
                .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(s3Object1, s3Object2)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        // When
        List<Map<String, Object>> result = fileStorageService.listFiles();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("fileName")).isEqualTo("file1.txt");
        assertThat(result.get(1).get("fileName")).isEqualTo("file2.txt");
        verify(s3Client, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    @DisplayName("列出文件 - 带前缀")
    void listFiles_WithPrefix() {
        // Given
        String prefix = "documents/";
        S3Object s3Object = S3Object.builder()
                .key("documents/file1.txt")
                .size(1024L)
                .lastModified(Instant.now())
                .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(s3Object)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        // When
        List<Map<String, Object>> result = fileStorageService.listFiles(prefix);

        // Then
        assertThat(result).hasSize(1);
        verify(s3Client, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    @DisplayName("获取文件URL - 成功")
    void getFileUrl_Success() {
        // Given
        String fileName = "test.txt";
        String expectedUrl = ENDPOINT + "/" + BUCKET_NAME + "/" + fileName;

        // When
        String result = fileStorageService.getFileUrl(fileName);

        // Then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("复制文件 - 成功")
    void copyFile_Success() {
        // Given
        String sourceFileName = "source.txt";
        String targetFileName = "target.txt";
        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenReturn(CopyObjectResponse.builder().build());

        // When
        fileStorageService.copyFile(sourceFileName, targetFileName);

        // Then
        verify(s3Client, times(1)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("移动文件 - 成功")
    void moveFile_Success() {
        // Given
        String sourceFileName = "source.txt";
        String targetFileName = "target.txt";
        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenReturn(CopyObjectResponse.builder().build());
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        // When
        fileStorageService.moveFile(sourceFileName, targetFileName);

        // Then
        verify(s3Client, times(1)).copyObject(any(CopyObjectRequest.class));
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("上传文件 - 无扩展名生成UUID文件名")
    void uploadFile_GenerateUUIDForNoExtension() throws Exception {
        // Given
        String fileName = "noextension";
        String contentType = "text/plain";
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        long contentLength = content.length();

        when(s3Client.headBucket(any(HeadBucketRequest.class))).thenReturn(HeadBucketResponse.builder().build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        // When
        String result = fileStorageService.uploadFile(inputStream, fileName, contentType, contentLength);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).doesNotContain(".");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}