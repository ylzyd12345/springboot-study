package com.kev1n.spring4demo.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * 自定义模板生成器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class TemplateGenerator {

    /**
     * 生成自定义模板代码
     */
    public void generateCustomTemplate(ConfigBuilder config, String templateName, String outputPath, Map<String, Object> context) {
        try {
            VelocityTemplateEngine engine = new VelocityTemplateEngine();
            
            // 构建模板文件路径
            String templatePath = getTemplatePath(templateName);
            
            // 生成文件内容
            String fileContent = engine.writer(context, templatePath).toString();
            
            // 写入文件
            FileUtil.writeUtf8String(fileContent, outputPath);
            
            log.info("自定义模板生成完成: {}", outputPath);
            
        } catch (Exception e) {
            log.error("自定义模板生成失败: {}", templateName, e);
        }
    }

    /**
     * 获取模板文件路径
     */
    private String getTemplatePath(String templateName) {
        return String.format(ConstVal.TEMPLATE_PATH + "%s%s", templateName, ConstVal.VM_SUFFIX);
    }

    /**
     * 生成CRUD控制器
     */
    public void generateCrudController(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "crud-controller.java";
        String outputPath = buildOutputPath(config, "controller", entityName + "Controller.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("entityInstanceName", StrUtil.lowerFirst(entityName));
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 生成DTO类
     */
    public void generateDto(ConfigBuilder config, String entityName, String packageName) {
        // 生成Request DTO
        generateRequestDto(config, entityName, packageName);
        
        // 生成Response DTO
        generateResponseDto(config, entityName, packageName);
    }

    /**
     * 生成Request DTO
     */
    private void generateRequestDto(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "request-dto.java";
        String outputPath = buildOutputPath(config, "dto/request", entityName + "Request.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 生成Response DTO
     */
    private void generateResponseDto(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "response-dto.java";
        String outputPath = buildOutputPath(config, "dto/response", entityName + "Response.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 生成VO类
     */
    public void generateVo(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "vo.java";
        String outputPath = buildOutputPath(config, "vo", entityName + "VO.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 生成常量类
     */
    public void generateConstants(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "constants.java";
        String outputPath = buildOutputPath(config, "constant", entityName + "Constants.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 构建输出路径
     */
    private String buildOutputPath(ConfigBuilder config, String modulePath, String fileName) {
        String outputPath = config.getStrategyConfig().getGlobalConfig().getOutputDir();
        String packagePath = config.getPackageInfo().getController().replace(".", File.separator);
        
        return outputPath + File.separator + packagePath + File.separator + modulePath + File.separator + fileName;
    }

    /**
     * 生成测试类
     */
    public void generateTestClass(ConfigBuilder config, String entityName, String packageName) {
        // 生成Controller测试
        generateControllerTest(config, entityName, packageName);
        
        // 生成Service测试
        generateServiceTest(config, entityName, packageName);
    }

    /**
     * 生成Controller测试类
     */
    private void generateControllerTest(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "controller-test.java";
        String outputPath = buildTestOutputPath(config, "controller", entityName + "ControllerTest.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("entityInstanceName", StrUtil.lowerFirst(entityName));
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 生成Service测试类
     */
    private void generateServiceTest(ConfigBuilder config, String entityName, String packageName) {
        String templateName = "service-test.java";
        String outputPath = buildTestOutputPath(config, "service", entityName + "ServiceTest.java");
        
        Map<String, Object> context = config.buildTemplateFilePath(packageName, entityName);
        context.put("entityName", entityName);
        context.put("entityInstanceName", StrUtil.lowerFirst(entityName));
        context.put("packageName", packageName);
        
        generateCustomTemplate(config, templateName, outputPath, context);
    }

    /**
     * 构建测试输出路径
     */
    private String buildTestOutputPath(ConfigBuilder config, String modulePath, String fileName) {
        String outputPath = config.getStrategyConfig().getGlobalConfig().getOutputDir();
        String packagePath = config.getPackageInfo().getController().replace(".", File.separator);
        
        return outputPath + File.separator + "src" + File.separator + "test" + File.separator + "java" + 
               File.separator + packagePath + File.separator + modulePath + File.separator + fileName;
    }
}