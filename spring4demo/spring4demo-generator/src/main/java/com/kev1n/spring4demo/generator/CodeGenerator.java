package com.kev1n.spring4demo.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * MyBatis-Plus代码生成器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
public class CodeGenerator {

    public static void main(String[] args) {
        SpringApplication.run(CodeGenerator.class, args);
    }

    @Component
    public static class GeneratorRunner implements CommandLineRunner {

        @Value("${spring.datasource.url}")
        private String url;

        @Value("${spring.datasource.username}")
        private String username;

        @Value("${spring.datasource.password}")
        private String password;

        @Override
        public void run(String... args) {
            log.info("开始执行代码生成...");
            
            FastAutoGenerator.create(url, username, password)
                    .globalConfig(builder -> {
                        builder.author("spring4demo") // 设置作者
                                .enableSwagger() // 开启 swagger 模式
                                .outputDir(System.getProperty("user.dir") + "/spring4demo-generator/src/main/java") // 指定输出目录
                                .commentDate("yyyy-MM-dd") // 注释日期
                                .disableOpenDir(); // 禁止打开输出目录
                    })
                    .packageConfig(builder -> {
                        builder.parent("com.kev1n.spring4demo") // 设置父包名
                                .moduleName("") // 设置父包模块名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, 
                                        System.getProperty("user.dir") + "/spring4demo-core/src/main/resources/mapper")); // 设置mapperXml生成路径
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude("sys_user", "sys_role", "sys_permission") // 设置需要生成的表名
                                .addTablePrefix("sys_", "t_") // 设置过滤表前缀
                                .entityBuilder()
                                .enableLombok() // 开启 lombok 模型
                                .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                                .versionColumnName("version") // 乐观锁字段名
                                .logicDeleteColumnName("deleted") // 逻辑删除字段名
                                .naming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
                                .columnNaming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel) // 数据库表字段映射到实体的命名策略
                                .controllerBuilder()
                                .enableRestStyle() // 生成@RestController 控制器
                                .serviceBuilder()
                                .formatServiceFileName("%sService") // 格式化 service 接口文件名称
                                .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称
                                .mapperBuilder()
                                .enableBaseResultMap() // 启用 BaseResultMap 生成
                                .enableBaseColumnList(); // 启用 BaseColumnList
                    })
                    .templateEngine(new VelocityTemplateEngine()) // 使用Velocity模板引擎
                    .execute();
            
            log.info("代码生成完成！");
        }
    }
}