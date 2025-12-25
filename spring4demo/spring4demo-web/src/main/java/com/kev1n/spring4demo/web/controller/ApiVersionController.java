package com.kev1n.spring4demo.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API版本控制器
 * 提供API版本信息和版本管理功能
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/version")
@Tag(name = "API版本管理", description = "API版本相关接口")
public class ApiVersionController {

    /**
     * 获取当前支持的API版本列表
     */
    @GetMapping
    @Operation(summary = "获取API版本列表", description = "获取当前支持的API版本列表")
    public ResponseEntity<Map<String, Object>> getApiVersions() {
        Map<String, Object> response = new HashMap<>();
        
        // 支持的版本列表
        Map<String, Object> versions = new HashMap<>();
        versions.put("v1", Map.of(
            "version", "1.0",
            "status", "deprecated",
            "deprecatedSince", "2024-01-01",
            "removeAfter", "2024-12-31",
            "description", "用户管理API v1.0",
            "useInstead", "v2.0"
        ));
        
        versions.put("v2", Map.of(
            "version", "2.0",
            "status", "stable",
            "deprecatedSince", "",
            "removeAfter", "",
            "description", "用户管理API v2.0 - 稳定版本",
            "useInstead", ""
        ));
        
        response.put("versions", versions);
        response.put("current", "v2");
        response.put("recommended", "v2");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取特定版本的API信息
     */
    @GetMapping("/{version}")
    @Operation(summary = "获取版本详情", description = "获取特定API版本的详细信息")
    public ResponseEntity<Map<String, Object>> getVersionInfo(
            @PathVariable String version) {
        
        Map<String, Object> versionInfo = new HashMap<>();
        
        switch (version) {
            case "v1":
                versionInfo.put("version", "1.0");
                versionInfo.put("status", "deprecated");
                versionInfo.put("deprecatedSince", "2024-01-01");
                versionInfo.put("removeAfter", "2024-12-31");
                versionInfo.put("description", "用户管理API v1.0");
                versionInfo.put("useInstead", "v2.0");
                versionInfo.put("endpoints", new String[]{
                    "GET /api/v1/users",
                    "POST /api/v1/users",
                    "GET /api/v1/users/{id}",
                    "PUT /api/v1/users/{id}",
                    "DELETE /api/v1/users/{id}"
                });
                break;
                
            case "v2":
                versionInfo.put("version", "2.0");
                versionInfo.put("status", "stable");
                versionInfo.put("description", "用户管理API v2.0 - 稳定版本");
                versionInfo.put("endpoints", new String[]{
                    "GET /api/v2/users",
                    "POST /api/v2/users",
                    "GET /api/v2/users/{id}",
                    "PUT /api/v2/users/{id}",
                    "PATCH /api/v2/users/{id}",
                    "DELETE /api/v2/users/{id}",
                    "GET /api/v2/users/search"
                });
                break;
                
            default:
                return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(versionInfo);
    }

    /**
     * 检查API版本兼容性
     */
    @PostMapping("/check")
    @Operation(summary = "检查版本兼容性", description = "检查客户端版本与服务端的兼容性")
    public ResponseEntity<Map<String, Object>> checkCompatibility(
            @RequestBody Map<String, String> request) {
        
        String clientVersion = request.get("version");
        Map<String, Object> response = new HashMap<>();
        
        if (clientVersion == null) {
            response.put("compatible", false);
            response.put("message", "客户端版本不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        switch (clientVersion) {
            case "v1":
                response.put("compatible", true);
                response.put("status", "deprecated");
                response.put("message", "版本兼容，但建议升级到v2.0");
                response.put("recommended", "v2.0");
                break;
                
            case "v2":
                response.put("compatible", true);
                response.put("status", "stable");
                response.put("message", "版本兼容，推荐使用");
                break;
                
            default:
                response.put("compatible", false);
                response.put("message", "不支持的版本: " + clientVersion);
                response.put("supportedVersions", new String[]{"v1", "v2"});
                break;
        }
        
        return ResponseEntity.ok(response);
    }
}