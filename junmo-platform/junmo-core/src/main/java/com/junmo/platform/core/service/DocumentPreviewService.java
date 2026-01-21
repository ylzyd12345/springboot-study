package com.junmo.platform.core.service;

/**
 * 文档预览服务接口
 * 基于 KKFileView 实现各类文档的在线预览
 *
 * @author junmo-platform
 * @since 1.0.0
 */
public interface DocumentPreviewService {

    /**
     * 生成文件预览 URL
     *
     * @param fileUrl 文件访问 URL
     * @return 预览 URL
     */
    String generatePreviewUrl(String fileUrl);

    /**
     * 生成文件预览 URL（使用文件名）
     *
     * @param fileName 文件名
     * @return 预览 URL
     */
    String generatePreviewUrlByFileName(String fileName);

    /**
     * 生成文件预览 URL（带水印）
     *
     * @param fileUrl 文件访问 URL
     * @param watermark 水印文字
     * @return 预览 URL
     */
    String generatePreviewUrlWithWatermark(String fileUrl, String watermark);

    /**
     * 生成文件预览 URL（带用户 Token）
     *
     * @param fileUrl 文件访问 URL
     * @param userToken 用户 Token
     * @return 预览 URL
     */
    String generatePreviewUrlWithToken(String fileUrl, String userToken);

    /**
     * 检查文件是否支持预览
     *
     * @param fileName 文件名
     * @return 是否支持预览
     */
    boolean isSupportPreview(String fileName);

    /**
     * 获取支持的文件类型
     *
     * @return 支持的文件类型列表
     */
    String[] getSupportedFileTypes();
}