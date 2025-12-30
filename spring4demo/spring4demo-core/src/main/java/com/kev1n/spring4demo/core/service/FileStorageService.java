package com.kev1n.spring4demo.core.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件存储服务接口
 * 基于 RustFS (兼容 S3 协议) 的文件存储服务
 *
 * @author kev1n
 * @since 1.0.0
 */
public interface FileStorageService {

    /**
     * 上传文件（使用输入流）
     *
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 内容类型
     * @param contentLength 内容长度
     * @return 文件名
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength);

    /**
     * 下载文件
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    byte[] downloadFile(String fileName);

    /**
     * 下载文件（返回输入流）
     *
     * @param fileName 文件名
     * @return 文件输入流
     */
    InputStream downloadFileStream(String fileName);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    void deleteFile(String fileName);

    /**
     * 批量删除文件
     *
     * @param fileNames 文件名列表
     */
    void deleteFiles(List<String> fileNames);

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 是否存在
     */
    boolean fileExists(String fileName);

    /**
     * 获取文件信息
     *
     * @param fileName 文件名
     * @return 文件信息
     */
    Map<String, Object> getFileInfo(String fileName);

    /**
     * 列出所有文件
     *
     * @return 文件列表
     */
    List<Map<String, Object>> listFiles();

    /**
     * 列出指定前缀的文件
     *
     * @param prefix 文件名前缀
     * @return 文件列表
     */
    List<Map<String, Object>> listFiles(String prefix);

    /**
     * 获取文件访问 URL
     *
     * @param fileName 文件名
     * @return 文件访问 URL
     */
    String getFileUrl(String fileName);

    /**
     * 复制文件
     *
     * @param sourceFileName 源文件名
     * @param targetFileName 目标文件名
     */
    void copyFile(String sourceFileName, String targetFileName);

    /**
     * 移动文件
     *
     * @param sourceFileName 源文件名
     * @param targetFileName 目标文件名
     */
    void moveFile(String sourceFileName, String targetFileName);
}