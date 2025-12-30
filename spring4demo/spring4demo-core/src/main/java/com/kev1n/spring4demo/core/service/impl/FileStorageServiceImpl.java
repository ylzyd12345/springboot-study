package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.common.config.RustFSProperties;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件存储服务实现类
 * 基于 RustFS (兼容 S3 协议) 的文件存储服务
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final RustFSProperties rustFSProperties;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength) {
        try {
            ensureBucketExists();
            String objectKey = generateFileName(fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

            log.info("文件上传成功: {}", objectKey);
            return objectKey;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(fileName)
                    .build();

            return s3Client.getObject(getObjectRequest).readAllBytes();
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileName, e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadFileStream(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(fileName)
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileName, e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("文件删除成功: {}", fileName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileName, e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFiles(List<String> fileNames) {
        try {
            List<ObjectIdentifier> objects = fileNames.stream()
                    .map(fileName -> ObjectIdentifier.builder().key(fileName).build())
                    .toList();

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .delete(d -> d.objects(objects))
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
            log.info("批量删除文件成功，数量: {}", fileNames.size());
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            throw new BusinessException("批量删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(fileName)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", fileName, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getFileInfo(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .key(fileName)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", fileName);
            fileInfo.put("contentLength", response.contentLength());
            fileInfo.put("contentType", response.contentType());
            fileInfo.put("lastModified", formatInstant(response.lastModified()));
            fileInfo.put("eTag", response.eTag());

            return fileInfo;
        } catch (NoSuchKeyException e) {
            log.warn("文件不存在: {}", fileName);
            throw new BusinessException("文件不存在: {}", fileName);
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileName, e);
            throw new BusinessException("获取文件信息失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> listFiles() {
        return listFiles("");
    }

    @Override
    public List<Map<String, Object>> listFiles(String prefix) {
        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsV2Request);

            List<Map<String, Object>> files = new ArrayList<>();
            for (S3Object s3Object : response.contents()) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileName", s3Object.key());
                fileInfo.put("size", s3Object.size());
                fileInfo.put("lastModified", formatInstant(s3Object.lastModified()));
                files.add(fileInfo);
            }

            return files;
        } catch (Exception e) {
            log.error("列出文件失败", e);
            throw new BusinessException("列出文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return rustFSProperties.getEndpoint() + "/" + rustFSProperties.getBucketName() + "/" + fileName;
    }

    @Override
    public void copyFile(String sourceFileName, String targetFileName) {
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(rustFSProperties.getBucketName())
                    .sourceKey(sourceFileName)
                    .destinationBucket(rustFSProperties.getBucketName())
                    .destinationKey(targetFileName)
                    .build();

            s3Client.copyObject(copyObjectRequest);
            log.info("文件复制成功: {} -> {}", sourceFileName, targetFileName);
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourceFileName, targetFileName, e);
            throw new BusinessException("文件复制失败: " + e.getMessage());
        }
    }

    @Override
    public void moveFile(String sourceFileName, String targetFileName) {
        try {
            copyFile(sourceFileName, targetFileName);
            deleteFile(sourceFileName);
            log.info("文件移动成功: {} -> {}", sourceFileName, targetFileName);
        } catch (Exception e) {
            log.error("文件移动失败: {} -> {}", sourceFileName, targetFileName, e);
            throw new BusinessException("文件移动失败: " + e.getMessage());
        }
    }

    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .build();

            s3Client.headBucket(headBucketRequest);
        } catch (NoSuchBucketException e) {
            log.info("存储桶不存在，创建存储桶: {}", rustFSProperties.getBucketName());
            createBucket();
        } catch (Exception e) {
            log.error("检查存储桶失败", e);
            throw new BusinessException("检查存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 创建存储桶
     */
    private void createBucket() {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(rustFSProperties.getBucketName())
                    .build();

            s3Client.createBucket(createBucketRequest);
            log.info("存储桶创建成功: {}", rustFSProperties.getBucketName());
        } catch (Exception e) {
            log.error("创建存储桶失败", e);
            throw new BusinessException("创建存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return java.util.UUID.randomUUID().toString();
        }

        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        return java.util.UUID.randomUUID() + extension;
    }

    /**
     * 格式化时间
     */
    private String formatInstant(Instant instant) {
        return DATE_FORMATTER.format(instant);
    }
}