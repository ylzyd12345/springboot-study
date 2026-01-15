package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.api.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API版本控制器
 * 
 * <p>提供API版本信息和版本管理功能，支持版本兼容性检查和版本状态管理。
 * 该控制器遵循RESTful设计原则，提供清晰的版本管理接口。</p>
 * 
 * <p>版本状态说明：</p>
 * <ul>
 *   <li>stable - 稳定版本，推荐使用</li>
 *   <li>deprecated - 已弃用版本，仍可用但建议升级</li>
 *   <li>experimental - 实验性版本，不建议生产使用</li>
 * </ul>
 * 
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/version")
@Tag(name = "API版本管理", description = "API版本相关接口，提供版本信息查询和兼容性检查")
public class ApiVersionController {

    /** 当前推荐版本 */
    private static final String RECOMMENDED_VERSION = "v2";
    
    /** 支持的版本配置 */
    private static final Map<String, VersionInfo> SUPPORTED_VERSIONS = Map.of(
        "v1", new VersionInfo("1.0", VersionStatus.DEPRECATED, 
                "2024-01-01", "2024-12-31", 
                "用户管理API v1.0", "v2.0"),
        "v2", new VersionInfo("2.0", VersionStatus.STABLE, 
                "", "", 
                "用户管理API v2.0 - 稳定版本", "")
    );

    /**
     * 获取当前支持的API版本列表
     * 
     * @return 包含所有支持版本信息的响应
     */
    @GetMapping
    @Operation(summary = "获取API版本列表", 
               description = "获取当前系统支持的所有API版本信息，包括版本状态、弃用时间等")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApiVersions() {
        log.debug("获取API版本列表");
        
        try {
            Map<String, Object> versions = new HashMap<>();
            SUPPORTED_VERSIONS.forEach((version, info) -> {
                versions.put(version, Map.of(
                    "version", info.getVersion(),
                    "status", info.getStatus().name(),
                    "deprecatedSince", info.getDeprecatedSince(),
                    "removeAfter", info.getRemoveAfter(),
                    "description", info.getDescription(),
                    "useInstead", info.getUseInstead()
                ));
            });

            Map<String, Object> response = Map.of(
                "versions", versions,
                "current", RECOMMENDED_VERSION,
                "recommended", RECOMMENDED_VERSION,
                "total", versions.size()
            );

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.error("参数错误", e);
            return ResponseEntity.ok(ApiResponse.error("获取版本列表失败: 参数错误"));
        } catch (RuntimeException e) {
            log.error("运行时异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取版本列表失败"));
        } catch (Exception e) {
            log.error("获取API版本列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取版本列表失败"));
        }
    }

    /**
     * 获取特定版本的API详细信息
     * 
     * @param version API版本号
     * @return 版本详细信息
     */
    @GetMapping("/{version}")
    @Operation(summary = "获取版本详情", 
               description = "获取指定API版本的详细信息，包括支持的端点列表")
    public ResponseEntity<ApiResponse<VersionInfo>> getVersionInfo(
            @Parameter(description = "API版本号，如v1、v2") 
            @PathVariable String version) {
        
        log.debug("获取版本详情: {}", version);
        
        VersionInfo versionInfo = SUPPORTED_VERSIONS.get(version);
        if (versionInfo == null) {
            return ResponseEntity.ok(ApiResponse.error("不支持的版本: " + version));
        }
        
        // 添加端点信息
        versionInfo.setEndpoints(getVersionEndpoints(version));
        
        return ResponseEntity.ok(ApiResponse.success(versionInfo));
    }

    /**
     * 检查API版本兼容性
     * 
     * @param request 包含客户端版本信息的请求体
     * @return 兼容性检查结果
     */
    @PostMapping("/check")
    @Operation(summary = "检查版本兼容性", 
               description = "检查客户端版本与服务端的兼容性，提供升级建议")
    public ResponseEntity<ApiResponse<CompatibilityResult>> checkCompatibility(
            @Parameter(description = "兼容性检查请求") 
            @RequestBody CompatibilityRequest request) {
        
        log.debug("检查版本兼容性: {}", request.getVersion());
        
        try {
            String clientVersion = request.getVersion();

            if (clientVersion == null || clientVersion.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("客户端版本不能为空"));
            }

            VersionInfo versionInfo = SUPPORTED_VERSIONS.get(clientVersion);
            if (versionInfo == null) {
                CompatibilityResult result = CompatibilityResult.incompatible(
                    "不支持的版本: " + clientVersion,
                    SUPPORTED_VERSIONS.keySet().toArray(new String[0])
                );
                return ResponseEntity.ok(ApiResponse.success(result));
            }

            // 根据版本状态返回兼容性结果
            CompatibilityResult result = switch (versionInfo.getStatus()) {
                case STABLE -> CompatibilityResult.compatible("版本兼容，推荐使用");
                case DEPRECATED -> CompatibilityResult.compatibleWithWarning(
                    "版本兼容，但建议升级到" + versionInfo.getUseInstead(),
                    versionInfo.getUseInstead()
                );
                case EXPERIMENTAL -> CompatibilityResult.compatibleWithWarning(
                    "实验性版本，不建议生产使用",
                    RECOMMENDED_VERSION
                );
            };

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (IllegalArgumentException e) {
            log.error("参数错误", e);
            return ResponseEntity.ok(ApiResponse.error("兼容性检查失败: 参数错误"));
        } catch (RuntimeException e) {
            log.error("运行时异常", e);
            return ResponseEntity.ok(ApiResponse.error("兼容性检查失败"));
        } catch (Exception e) {
            log.error("版本兼容性检查失败", e);
            return ResponseEntity.ok(ApiResponse.error("兼容性检查失败"));
        }
    }

    /**
     * 获取指定版本支持的端点列表
     * 
     * @param version 版本号
     * @return 端点列表
     */
    private String[] getVersionEndpoints(String version) {
        return switch (version) {
            case "v1" -> new String[]{
                "GET /api/v1/users",
                "POST /api/v1/users", 
                "GET /api/v1/users/{id}",
                "PUT /api/v1/users/{id}",
                "DELETE /api/v1/users/{id}"
            };
            case "v2" -> new String[]{
                "GET /api/v2/users",
                "POST /api/v2/users",
                "GET /api/v2/users/{id}",
                "PUT /api/v2/users/{id}",
                "PATCH /api/v2/users/{id}",
                "DELETE /api/v2/users/{id}",
                "GET /api/v2/users/search"
            };
            default -> new String[0];
        };
    }

    /**
     * 版本信息内部类
     */
    public static class VersionInfo {
        private String version;
        private VersionStatus status;
        private String deprecatedSince;
        private String removeAfter;
        private String description;
        private String useInstead;
        private String[] endpoints;

        public VersionInfo() {}

        public VersionInfo(String version, VersionStatus status, String deprecatedSince, 
                          String removeAfter, String description, String useInstead) {
            this.version = version;
            this.status = status;
            this.deprecatedSince = deprecatedSince;
            this.removeAfter = removeAfter;
            this.description = description;
            this.useInstead = useInstead;
        }

        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public VersionStatus getStatus() { return status; }
        public void setStatus(VersionStatus status) { this.status = status; }
        
        public String getDeprecatedSince() { return deprecatedSince; }
        public void setDeprecatedSince(String deprecatedSince) { this.deprecatedSince = deprecatedSince; }
        
        public String getRemoveAfter() { return removeAfter; }
        public void setRemoveAfter(String removeAfter) { this.removeAfter = removeAfter; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUseInstead() { return useInstead; }
        public void setUseInstead(String useInstead) { this.useInstead = useInstead; }
        
        public String[] getEndpoints() { return endpoints; }
        public void setEndpoints(String[] endpoints) { this.endpoints = endpoints; }
    }

    /**
     * 版本状态枚举
     */
    public enum VersionStatus {
        STABLE("稳定"),
        DEPRECATED("已弃用"),
        EXPERIMENTAL("实验性");

        private final String description;

        VersionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 兼容性检查请求
     */
    public static class CompatibilityRequest {
        private String version;

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    /**
     * 兼容性检查结果
     */
    public static class CompatibilityResult {
        private boolean compatible;
        private String message;
        private String recommended;
        private String[] supportedVersions;

        private CompatibilityResult(boolean compatible, String message, 
                                   String recommended, String[] supportedVersions) {
            this.compatible = compatible;
            this.message = message;
            this.recommended = recommended;
            this.supportedVersions = supportedVersions;
        }

        public static CompatibilityResult compatible(String message) {
            return new CompatibilityResult(true, message, null, null);
        }

        public static CompatibilityResult compatibleWithWarning(String message, String recommended) {
            return new CompatibilityResult(true, message, recommended, null);
        }

        public static CompatibilityResult incompatible(String message, String[] supportedVersions) {
            return new CompatibilityResult(false, message, null, supportedVersions);
        }

        // Getters
        public boolean isCompatible() { return compatible; }
        public String getMessage() { return message; }
        public String getRecommended() { return recommended; }
        public String[] getSupportedVersions() { return supportedVersions; }
    }
}