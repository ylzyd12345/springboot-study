package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.core.service.DocumentPreviewService;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档预览控制器
 *
 * <p>提供文档在线预览功能的 REST API。基于 KKFileView 实现各类文档的在线预览，
 * 支持 Office 文档、PDF、图片、视频、音频等多种格式。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>生成文件预览 URL</li>
 *   <li>生成带水印的预览 URL</li>
 *   <li>检查文件是否支持预览</li>
 *   <li>获取支持的文件类型列表</li>
 * </ul>
 *
 * @author kev1n
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/preview")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文档预览", description = "文档预览相关接口，提供各类文档的在线预览功能")
public class DocumentPreviewController {

    private final DocumentPreviewService documentPreviewService;

    /**
     * 生成文件预览 URL
     *
     * <p>根据文件 URL 生成预览 URL，用户可以通过该 URL 在浏览器中预览文件。</p>
     *
     * @param fileName 文件名
     * @return 预览 URL
     */
    @GetMapping("/{fileName}")
    @SaCheckLogin
    @Operation(summary = "生成文件预览URL", description = "根据文件名生成预览 URL")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generatePreviewUrl(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.info("生成文件预览 URL: fileName={}", fileName);

        try {
            // 检查文件是否支持预览
            if (!documentPreviewService.isSupportPreview(fileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(), "该文件类型不支持预览"));
            }

            // 获取用户 Token
            String userToken = StpUtil.getTokenValue().toString();

            // 生成预览 URL（带用户 Token）
            String previewUrl = documentPreviewService.generatePreviewUrlWithToken(
                    fileName, userToken);

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("previewUrl", previewUrl);
            result.put("supportPreview", true);

            log.info("生成文件预览 URL 成功: fileName={}", fileName);
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("生成文件预览 URL 失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("生成预览 URL 失败: " + e.getMessage()));
        }
    }

    /**
     * 生成带水印的文件预览 URL
     *
     * <p>根据文件 URL 生成带水印的预览 URL，水印文字将显示在预览页面上。</p>
     *
     * @param fileName 文件名
     * @param watermark 水印文字
     * @return 预览 URL
     */
    @GetMapping("/{fileName}/watermark")
    @SaCheckLogin
    @Operation(summary = "生成带水印的预览URL", description = "生成带水印的文件预览 URL")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generatePreviewUrlWithWatermark(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName,
            @Parameter(description = "水印文字", required = true)
            @RequestParam("watermark") String watermark) {

        log.info("生成带水印的文件预览 URL: fileName={}, watermark={}", fileName, watermark);

        try {
            // 检查文件是否支持预览
            if (!documentPreviewService.isSupportPreview(fileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(), "该文件类型不支持预览"));
            }

            // 获取用户 Token
            String userToken = StpUtil.getTokenValue().toString();

            // 生成预览 URL（带水印和用户 Token）
            String previewUrl = documentPreviewService.generatePreviewUrlWithWatermark(
                    fileName, watermark);

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("previewUrl", previewUrl);
            result.put("watermark", watermark);
            result.put("supportPreview", true);

            log.info("生成带水印的文件预览 URL 成功: fileName={}", fileName);
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("生成带水印的文件预览 URL 失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("生成预览 URL 失败: " + e.getMessage()));
        }
    }

    /**
     * 检查文件是否支持预览
     *
     * @param fileName 文件名
     * @return 是否支持预览
     */
    @GetMapping("/{fileName}/check")
    @SaCheckLogin
    @Operation(summary = "检查文件是否支持预览", description = "检查指定文件类型是否支持在线预览")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSupportPreview(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.debug("检查文件是否支持预览: fileName={}", fileName);

        try {
            boolean supportPreview = documentPreviewService.isSupportPreview(fileName);

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("supportPreview", supportPreview);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("检查文件预览支持失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("检查失败: " + e.getMessage()));
        }
    }

    /**
     * 获取支持的文件类型列表
     *
     * @return 支持的文件类型列表
     */
    @GetMapping("/supported-types")
    @SaCheckLogin
    @Operation(summary = "获取支持的文件类型", description = "获取所有支持在线预览的文件类型列表")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupportedFileTypes() {

        log.debug("获取支持的文件类型列表");

        try {
            String[] supportedTypes = documentPreviewService.getSupportedFileTypes();

            Map<String, Object> result = new HashMap<>();
            result.put("supportedTypes", supportedTypes);
            result.put("count", supportedTypes.length);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("获取支持的文件类型失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取失败: " + e.getMessage()));
        }
    }

    /**
     * 重定向到预览页面
     *
     * <p>直接跳转到 KKFileView 的预览页面，方便前端直接使用。</p>
     *
     * @param fileName 文件名
     * @return 重定向到预览页面
     */
    @GetMapping("/{fileName}/redirect")
    @SaCheckLogin
    @Operation(summary = "重定向到预览页面", description = "直接跳转到文件预览页面")
    public ResponseEntity<Void> redirectToPreview(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.info("重定向到预览页面: fileName={}", fileName);

        try {
            // 检查文件是否支持预览
            if (!documentPreviewService.isSupportPreview(fileName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // 获取用户 Token
            String userToken = StpUtil.getTokenValue().toString();

            // 生成预览 URL
            String previewUrl = documentPreviewService.generatePreviewUrlWithToken(
                    fileName, userToken);

            log.info("重定向到预览页面成功: fileName={}, previewUrl={}", fileName, previewUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", previewUrl)
                    .build();

        } catch (Exception e) {
            log.error("重定向到预览页面失败: fileName={}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}