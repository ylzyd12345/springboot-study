package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.common.config.KKFileViewProperties;
import com.kev1n.spring4demo.core.service.DocumentPreviewService;
import com.kev1n.spring4demo.core.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 文档预览服务实现类
 * 基于 KKFileView 实现各类文档的在线预览
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPreviewServiceImpl implements DocumentPreviewService {

    private final KKFileViewProperties kkFileViewProperties;
    private final FileStorageService fileStorageService;

    /**
     * 支持预览的文件类型
     */
    private static final String[] SUPPORTED_FILE_TYPES = {
            // Office 文档
            ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
            // PDF
            ".pdf",
            // 文本文件
            ".txt", ".xml", ".html", ".htm", ".md", ".json", ".csv",
            // 图片
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".svg", ".webp",
            // 压缩文件
            ".zip", ".rar", ".7z", ".tar", ".gz",
            // 视频文件
            ".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv",
            // 音频文件
            ".mp3", ".wav", ".ogg", ".flac", ".aac"
    };

    @Override
    public String generatePreviewUrl(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }

        try {
            // 对文件 URL 进行 Base64 编码
            String encodedUrl = Base64.getEncoder().encodeToString(fileUrl.getBytes(StandardCharsets.UTF_8));

            // 构建预览 URL
            StringBuilder previewUrl = new StringBuilder(kkFileViewProperties.getServerUrl());
            previewUrl.append(kkFileViewProperties.getPreviewPath());
            previewUrl.append("?url=").append(encodedUrl);

            // 添加缓存控制参数
            if (!kkFileViewProperties.getUseCache() || kkFileViewProperties.getForceUpdateCache()) {
                previewUrl.append("&full=").append(Boolean.FALSE);
            }

            log.debug("生成预览 URL: {} -> {}", fileUrl, previewUrl);
            return previewUrl.toString();

        } catch (IllegalArgumentException e) {
            log.error("生成预览 URL 失败(参数异常): {}", fileUrl, e);
            throw new IllegalArgumentException("生成预览 URL 失败: 参数错误 - " + e.getMessage());
        } catch (Exception e) {
            log.error("生成预览 URL 失败(未知异常): {}", fileUrl, e);
            throw new RuntimeException("生成预览 URL 失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePreviewUrlByFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取文件访问 URL
        String fileUrl = fileStorageService.getFileUrl(fileName);
        return generatePreviewUrl(fileUrl);
    }

    @Override
    public String generatePreviewUrlWithWatermark(String fileUrl, String watermark) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }

        try {
            // 对文件 URL 进行 Base64 编码
            String encodedUrl = Base64.getEncoder().encodeToString(fileUrl.getBytes(StandardCharsets.UTF_8));

            // 对水印进行 Base64 编码
            String encodedWatermark = Base64.getEncoder().encodeToString(
                    watermark.getBytes(StandardCharsets.UTF_8));

            // 构建预览 URL
            StringBuilder previewUrl = new StringBuilder(kkFileViewProperties.getServerUrl());
            previewUrl.append(kkFileViewProperties.getPreviewPath());
            previewUrl.append("?url=").append(encodedUrl);
            previewUrl.append("&watermarkTxt=").append(encodedWatermark);

            // 添加缓存控制参数
            if (!kkFileViewProperties.getUseCache() || kkFileViewProperties.getForceUpdateCache()) {
                previewUrl.append("&full=").append(Boolean.FALSE);
            }

            log.debug("生成带水印的预览 URL: {} -> {}", fileUrl, previewUrl);
            return previewUrl.toString();

        } catch (IllegalArgumentException e) {
            log.error("生成带水印的预览 URL 失败(参数异常): {}", fileUrl, e);
            throw new IllegalArgumentException("生成带水印的预览 URL 失败: 参数错误 - " + e.getMessage());
        } catch (Exception e) {
            log.error("生成带水印的预览 URL 失败(未知异常): {}", fileUrl, e);
            throw new RuntimeException("生成带水印的预览 URL 失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePreviewUrlWithToken(String fileUrl, String userToken) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }

        try {
            // 对文件 URL 进行 Base64 编码
            String encodedUrl = Base64.getEncoder().encodeToString(fileUrl.getBytes(StandardCharsets.UTF_8));

            // 构建预览 URL
            StringBuilder previewUrl = new StringBuilder(kkFileViewProperties.getServerUrl());
            previewUrl.append(kkFileViewProperties.getPreviewPath());
            previewUrl.append("?url=").append(encodedUrl);

            // 添加用户 Token
            if (StringUtils.hasText(userToken)) {
                previewUrl.append("&userToken=").append(URLEncoder.encode(userToken, StandardCharsets.UTF_8));
            }

            // 添加缓存控制参数
            if (!kkFileViewProperties.getUseCache() || kkFileViewProperties.getForceUpdateCache()) {
                previewUrl.append("&full=").append(Boolean.FALSE);
            }

            log.debug("生成带 Token 的预览 URL: {} -> {}", fileUrl, previewUrl);
            return previewUrl.toString();

        } catch (IllegalArgumentException e) {
            log.error("生成带 Token 的预览 URL 失败(参数异常): {}", fileUrl, e);
            throw new IllegalArgumentException("生成带 Token 的预览 URL 失败: 参数错误 - " + e.getMessage());
        } catch (Exception e) {
            log.error("生成带 Token 的预览 URL 失败(未知异常): {}", fileUrl, e);
            throw new RuntimeException("生成带 Token 的预览 URL 失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isSupportPreview(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }

        String lowerFileName = fileName.toLowerCase();
        for (String fileType : SUPPORTED_FILE_TYPES) {
            if (lowerFileName.endsWith(fileType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String[] getSupportedFileTypes() {
        return SUPPORTED_FILE_TYPES.clone();
    }
}