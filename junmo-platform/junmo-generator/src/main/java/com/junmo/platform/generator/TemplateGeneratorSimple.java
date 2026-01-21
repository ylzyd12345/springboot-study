package com.junmo.platform.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 简化版模板生成器
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Component
public class TemplateGeneratorSimple {

    /**
     * 生成自定义模板代码
     */
    public void generateCustomTemplate(String templateName, String outputPath, Map<String, Object> context) {
        try {
            // 简单实现 - 生成基础代码
            String fileContent = generateBasicContent(templateName, context);

            // 写入文件
            java.nio.file.Files.write(
                java.nio.file.Paths.get(outputPath),
                fileContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            log.info("自定义模板生成完成: {}", outputPath);

        } catch (java.nio.file.InvalidPathException e) {
            log.error("自定义模板生成失败: 路径无效 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (java.nio.file.NoSuchFileException e) {
            log.error("自定义模板生成失败: 路径不存在 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (java.nio.file.AccessDeniedException e) {
            log.error("自定义模板生成失败: 访问被拒绝 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (java.io.IOException e) {
            log.error("自定义模板生成失败: IO异常 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (SecurityException e) {
            log.error("自定义模板生成失败: 安全异常 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (RuntimeException e) {
            log.error("自定义模板生成失败: 运行时异常 - {}, outputPath: {}", templateName, outputPath, e);
        } catch (Exception e) {
            log.error("自定义模板生成失败: 未知异常 - {}, outputPath: {}", templateName, outputPath, e);
        }
    }

    /**
     * 生成基础内容
     */
    private String generateBasicContent(String templateName, Map<String, Object> context) {
        String entityName = context != null ? (String) context.get("entityName") : "Entity";

        return String.format("""
// Generated %s for %s
package com.junmo.platform.generated;

public class %sGenerated {
    // TODO: Implement %s

    public static void main(String[] args) {
        System.out.println("Generated %s class");
    }
}
""", templateName, entityName, entityName, entityName, entityName);
    }
}