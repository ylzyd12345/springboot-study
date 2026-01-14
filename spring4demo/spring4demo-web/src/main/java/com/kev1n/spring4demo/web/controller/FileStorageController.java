package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.common.exception.SystemException;
import com.kev1n.spring4demo.core.service.FileStorageService;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件存储控制器
 *
 * <p>提供文件上传、下载、删除、查询等功能的 REST API。
 * 基于 RustFS (兼容 S3 协议) 的文件存储服务。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>文件上传（单文件）</li>
 *   <li>文件下载</li>
 *   <li>文件删除（单文件、批量）</li>
 *   <li>文件列表查询</li>
 *   <li>文件信息查询</li>
 *   <li>文件复制、移动</li>
 * </ul>
 *
 * @author kev1n
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文件存储", description = "文件存储相关接口，提供文件上传、下载、删除等功能")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * <p>上传单个文件到 RustFS 存储服务。文件会自动生成唯一的文件名，
     * 保留原始文件的扩展名。</p>
     *
     * @param file 文件对象
     * @return 上传结果，包含生成的文件名
     */
    @PostMapping("/upload")
    @SaCheckLogin
    @Operation(summary = "上传文件", description = "上传单个文件到 RustFS 存储服务")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadFile(
            @Parameter(description = "文件对象", required = true)
            @RequestParam("file") MultipartFile file) {

        log.info("文件上传请求: originalName={}, size={}", file.getOriginalFilename(), file.getSize());

        try {
            if (file.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "文件不能为空"));
            }

            String fileName = fileStorageService.uploadFile(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()
            );
            String fileUrl = fileStorageService.getFileUrl(fileName);

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("originalName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());
            result.put("fileUrl", fileUrl);

            log.info("文件上传成功: fileName={}", fileName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("文件上传成功", result));

        } catch (IOException e) {
            log.error("文件IO异常: originalName={}", file.getOriginalFilename(), e);
            return ResponseEntity.ok(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: originalName={}, error={}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: originalName={}", file.getOriginalFilename(), e);
            return ResponseEntity.ok(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("文件上传失败: originalName={}", file.getOriginalFilename(), e);
            return ResponseEntity.ok(ApiResponse.error("文件上传失败: " + e.getMessage()));
        }
    }

    /**
     * 下载文件
     *
     * <p>根据文件名下载文件。文件会以附件形式返回，浏览器会自动触发下载。</p>
     *
     * @param fileName 文件名
     * @param response HTTP 响应对象
     */
    @GetMapping("/download/{fileName}")
    @SaCheckLogin
    @Operation(summary = "下载文件", description = "根据文件名下载文件")
    public void downloadFile(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName,
            HttpServletResponse response) {

        log.info("文件下载请求: fileName={}", fileName);

        try {
            if (!fileStorageService.fileExists(fileName)) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getWriter().write("文件不存在");
                return;
            }

            Map<String, Object> fileInfo = fileStorageService.getFileInfo(fileName);
            String contentType = (String) fileInfo.get("contentType");
            if (contentType == null || contentType.isEmpty()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");

            try (OutputStream outputStream = response.getOutputStream();
                 var inputStream = fileStorageService.downloadFileStream(fileName)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            log.info("文件下载成功: fileName={}", fileName);

        } catch (IOException e) {
            log.error("文件下载失败: fileName={}", fileName, e);
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    /**
     * 获取文件信息
     *
     * @param fileName 文件名
     * @return 文件信息
     */
    @GetMapping("/{fileName}")
    @SaCheckLogin
    @Operation(summary = "获取文件信息", description = "获取文件的详细信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFileInfo(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.debug("获取文件信息: fileName={}", fileName);

        try {
            Map<String, Object> fileInfo = fileStorageService.getFileInfo(fileName);
            if (fileInfo == null) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "文件不存在"));
            }

            return ResponseEntity.ok(ApiResponse.success(fileInfo));

        } catch (IOException e) {
            log.error("文件IO异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件信息失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: fileName={}, error={}", fileName, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件信息失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("获取文件信息失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件信息失败: " + e.getMessage()));
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/{fileName}")
    @SaCheckLogin
    @Operation(summary = "删除文件", description = "删除指定文件")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.info("删除文件请求: fileName={}", fileName);

        try {
            if (!fileStorageService.fileExists(fileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "文件不存在"));
            }

            fileStorageService.deleteFile(fileName);
            log.info("文件删除成功: fileName={}", fileName);
            return ResponseEntity.ok(ApiResponse.success("文件删除成功", null));

        } catch (IOException e) {
            log.error("文件IO异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件删除失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: fileName={}, error={}", fileName, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件删除失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("文件删除失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件删除失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除文件
     *
     * @param request 包含文件名列表的请求体
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @SaCheckLogin
    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    public ResponseEntity<ApiResponse<Void>> batchDeleteFiles(
            @Parameter(description = "文件名列表", required = true)
            @RequestBody Map<String, List<String>> request) {

        List<String> fileNames = request.get("fileNames");
        log.info("批量删除文件请求: count={}", fileNames != null ? fileNames.size() : 0);

        if (fileNames == null || fileNames.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "文件名列表不能为空"));
        }

        try {
            fileStorageService.deleteFiles(fileNames);
            log.info("批量删除文件成功: count={}", fileNames.size());
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));

        } catch (IOException e) {
            log.error("文件IO异常", e);
            return ResponseEntity.ok(ApiResponse.error("批量删除失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: error={}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常", e);
            return ResponseEntity.ok(ApiResponse.error("批量删除失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            return ResponseEntity.ok(ApiResponse.error("批量删除失败: " + e.getMessage()));
        }
    }

    /**
     * 列出所有文件
     *
     * @return 文件列表
     */
    @GetMapping
    @SaCheckLogin
    @Operation(summary = "列出所有文件", description = "获取存储桶中的所有文件列表")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listFiles(
            @Parameter(description = "文件名前缀（可选）")
            @RequestParam(value = "prefix", required = false, defaultValue = "") String prefix) {

        log.debug("列出文件请求: prefix={}", prefix);

        try {
            List<Map<String, Object>> files = fileStorageService.listFiles(prefix);
            return ResponseEntity.ok(ApiResponse.success(files));

        } catch (IOException e) {
            log.error("文件IO异常", e);
            return ResponseEntity.ok(ApiResponse.error("列出文件失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: error={}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常", e);
            return ResponseEntity.ok(ApiResponse.error("列出文件失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("列出文件失败", e);
            return ResponseEntity.ok(ApiResponse.error("列出文件失败: " + e.getMessage()));
        }
    }

    /**
     * 获取文件访问 URL
     *
     * @param fileName 文件名
     * @return 文件访问 URL
     */
    @GetMapping("/{fileName}/url")
    @SaCheckLogin
    @Operation(summary = "获取文件访问URL", description = "获取文件的访问地址")
    public ResponseEntity<ApiResponse<Map<String, String>>> getFileUrl(
            @Parameter(description = "文件名", required = true)
            @PathVariable("fileName") String fileName) {

        log.debug("获取文件URL: fileName={}", fileName);

        try {
            if (!fileStorageService.fileExists(fileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "文件不存在"));
            }

            String fileUrl = fileStorageService.getFileUrl(fileName);
            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileUrl", fileUrl);

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (IOException e) {
            log.error("文件IO异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件URL失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: fileName={}, error={}", fileName, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件URL失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("获取文件URL失败: fileName={}", fileName, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件URL失败: " + e.getMessage()));
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFileName 源文件名
     * @param targetFileName 目标文件名
     * @return 复制结果
     */
    @PostMapping("/{sourceFileName}/copy")
    @SaCheckLogin
    @Operation(summary = "复制文件", description = "复制文件到新的位置")
    public ResponseEntity<ApiResponse<Void>> copyFile(
            @Parameter(description = "源文件名", required = true)
            @PathVariable("sourceFileName") String sourceFileName,
            @Parameter(description = "目标文件名", required = true)
            @RequestParam("targetFileName") String targetFileName) {

        log.info("复制文件请求: {} -> {}", sourceFileName, targetFileName);

        try {
            if (!fileStorageService.fileExists(sourceFileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "源文件不存在"));
            }

            fileStorageService.copyFile(sourceFileName, targetFileName);
            log.info("文件复制成功: {} -> {}", sourceFileName, targetFileName);
            return ResponseEntity.ok(ApiResponse.success("文件复制成功", null));

        } catch (IOException e) {
            log.error("文件IO异常: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件复制失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: {} -> {}, error={}", sourceFileName, targetFileName, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件复制失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件复制失败: " + e.getMessage()));
        }
    }

    /**
     * 移动文件
     *
     * @param sourceFileName 源文件名
     * @param targetFileName 目标文件名
     * @return 移动结果
     */
    @PostMapping("/{sourceFileName}/move")
    @SaCheckLogin
    @Operation(summary = "移动文件", description = "移动文件到新的位置")
    public ResponseEntity<ApiResponse<Void>> moveFile(
            @Parameter(description = "源文件名", required = true)
            @PathVariable("sourceFileName") String sourceFileName,
            @Parameter(description = "目标文件名", required = true)
            @RequestParam("targetFileName") String targetFileName) {

        log.info("移动文件请求: {} -> {}", sourceFileName, targetFileName);

        try {
            if (!fileStorageService.fileExists(sourceFileName)) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "源文件不存在"));
            }

            fileStorageService.moveFile(sourceFileName, targetFileName);
            log.info("文件移动成功: {} -> {}", sourceFileName, targetFileName);
            return ResponseEntity.ok(ApiResponse.success("文件移动成功", null));

        } catch (IOException e) {
            log.error("文件IO异常: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件移动失败: " + e.getMessage()));
        } catch (BusinessException e) {
            log.error("业务异常: {} -> {}, error={}", sourceFileName, targetFileName, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件移动失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("文件移动失败: {} -> {}", sourceFileName, targetFileName, e);
            return ResponseEntity.ok(ApiResponse.error("文件移动失败: " + e.getMessage()));
        }
    }
}