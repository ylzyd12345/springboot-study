package com.kev1n.spring4demo.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 自定义代码生成器配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "generator.enabled", havingValue = "true")
public class CustomCodeGenerator {

    @Bean
    public CommandLineRunner generatorRunner(GeneratorProperties properties) {
        return args -> {
            log.info("开始执行自定义代码生成...");
            
            FastAutoGenerator.create(properties.getUrl(), properties.getUsername(), properties.getPassword())
                    .globalConfig(builder -> {
                        builder.author(properties.getAuthor())
                                .enableSwagger()
                                .outputDir(properties.getOutputDir())
                                .commentDate("yyyy-MM-dd")
                                .disableOpenDir();
                    })
                    .packageConfig(builder -> {
                        builder.parent(properties.getParentPackage())
                                .moduleName(properties.getModuleName())
                                .pathInfo(Collections.singletonMap(OutputFile.xml, properties.getMapperXmlPath()));
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude(properties.getIncludeTables().toArray(new String[0]))
                                .addTablePrefix(properties.getTablePrefix().toArray(new String[0]))
                                .entityBuilder()
                                .enableLombok()
                                .enableTableFieldAnnotation()
                                .versionColumnName("version")
                                .logicDeleteColumnName("deleted")
                                .naming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                                .columnNaming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                                .controllerBuilder()
                                .enableRestStyle()
                                .serviceBuilder()
                                .formatServiceFileName("%sService")
                                .formatServiceImplFileName("%sServiceImpl")
                                .mapperBuilder()
                                .enableBaseResultMap()
                                .enableBaseColumnList();
                    })
                    .templateEngine(new VelocityTemplateEngine())
                    .execute();
            
            log.info("自定义代码生成完成！");
        };
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "generator")
    public static class GeneratorProperties {
        
        /**
         * 数据库连接URL
         */
        private String url;
        
        /**
         * 数据库用户名
         */
        private String username;
        
        /**
         * 数据库密码
         */
        private String password;
        
        /**
         * 作者名称
         */
        private String author = "spring4demo";
        
        /**
         * 父包名
         */
        private String parentPackage = "com.kev1n.spring4demo";
        
        /**
         * 模块名
         */
        private String moduleName = "";
        
        /**
         * 输出目录
         */
        private String outputDir = System.getProperty("user.dir") + "/spring4demo-generator/src/main/java";
        
        /**
         * Mapper XML输出目录
         */
        private String mapperXmlPath = System.getProperty("user.dir") + "/spring4demo-core/src/main/resources/mapper";
        
        /**
         * 包含的表名
         */
        private List<String> includeTables;
        
        /**
         * 表前缀
         */
        private List<String> tablePrefix;
    }
}