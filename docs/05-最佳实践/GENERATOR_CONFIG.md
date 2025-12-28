# 代码生成器配置指南

## 📋 概述

本项目集成了 MyBatis-Plus 代码生成器，可以根据数据库表结构快速生成 Entity、Mapper、Service、Controller 等代码。

## 🚀 使用步骤

### 1. 配置数据库连接

修改 `spring4demo-generator/src/main/resources/application-generator.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 2. 运行代码生成器

```bash
cd spring4demo-generator
mvn spring-boot:run
```

或者直接运行 `CodeGenerator.java` 的 `main` 方法。

### 3. 配置生成器参数

修改 `CodeGenerator.java` 中的配置：

```java
FastAutoGenerator.create(url, username, password)
    .globalConfig(builder -> {
        builder.author("作者名称")           // 设置作者
            .enableSwagger()                // 开启 swagger 模式
            .outputDir("输出目录")           // 指定输出目录
            .commentDate("yyyy-MM-dd")      // 注释日期
            .fileOverride()                // 覆盖已生成文件
    })
    .packageConfig(builder -> {
        builder.parent("父包名")            // 设置父包名
            .moduleName("模块名")           // 设置模块名
            .entity("entity")               // entity 实体类包名
            .service("service")             // service 包名
            .controller("controller")       // controller 包名
    })
    .strategyConfig(builder -> {
        builder.addInclude("表名1", "表名2") // 设置需要生成的表名
            .addTablePrefix("t_", "sys_")   // 设置过滤表前缀
            .entityBuilder()               // Entity 策略配置
            .controllerBuilder()           // Controller 策略配置
            .serviceBuilder()              // Service 策略配置
            .mapperBuilder()               // Mapper 策略配置
    })
    .templateEngine(new VelocityTemplateEngine()) // 使用 Velocity 模板引擎
    .execute();
```

## 📝 生成模板配置

### Entity 模板

路径：`spring4demo-generator/src/main/resources/templates/entity.java.vm`

```velocity
package ${package.Entity};

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("${table.name}")
public class ${Entity} {

    @TableId(value = "${table.name}.${keyGenerator.cfg.keyColumn}", type = IdType.${keyGenerator.cfg.keyType})
    private ${keyGenerator.cfg.keyType} ${keyGenerator.cfg.keyProperty};

#foreach($column in $table.columns)
    #if(!$column.isKey)##生成普通字段
    @TableField("${column.name}")
    private ${column.columnType} ${column.propertyName};
    #end
#end

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @Version
    private Integer version;
}
```

### Mapper 模板

路径：`spring4demo-generator/src/main/resources/templates/mapper.java.vm`

```velocity
package ${package.Mapper};

import ${package.Entity}.${Entity};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * ${table.comment!} Mapper 接口
 *
 * @author ${author}
 * @since ${date}
 */
@Mapper
public interface ${Entity}Mapper extends BaseMapper<${Entity}> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    ${Entity} selectByUsername(@Param("username") String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在数量
     */
    int countByUsername(@Param("username") String username);
}
```

### Service 模板

路径：`spring4demo-generator/src/main/resources/templates/service.java.vm`

```velocity
package ${package.Service};

import ${package.Entity}.${Entity};
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ${table.comment!} 服务类
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${Entity}Service extends IService<${Entity}> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    ${Entity} findByUsername(String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
```

### ServiceImpl 模板

路径：`spring4demo-generator/src/main/resources/templates/serviceImpl.java.vm`

```velocity
package ${package.Service}.impl;

import ${package.Entity}.${Entity};
import ${package.Mapper}.${Entity}Mapper;
import ${package.Service}.${Entity}Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${Entity}ServiceImpl extends ServiceImpl<${Entity}Mapper, ${Entity}> implements ${Entity}Service {

    @Override
    public ${Entity} findByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return baseMapper.countByUsername(username) > 0;
    }
}
```

### Controller 模板

路径：`spring4demo-generator/src/main/resources/templates/controller.java.vm`

```velocity
package ${package.Controller};

import ${package.Entity}.${Entity};
import ${package.Service}.${Entity}Service;
import ${package.dto}.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${table.comment!} 控制器
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
@RestController
@RequestMapping("/api/${table.entityPath}")
@RequiredArgsConstructor
@Tag(name = "${table.comment!}管理", description = "${table.comment!}相关接口")
public class ${Entity}Controller {

    private final ${Entity}Service ${table.entityPath}Service;

    @PostMapping
    @Operation(summary = "创建${table.comment!}", description = "创建新的${table.comment!}")
    public ResponseEntity<ApiResponse<${Entity}>> create${Entity}(
            @Parameter(description = "${table.comment!}信息") @RequestBody ${Entity} ${table.entityPath}) {
        
        log.info("创建${table.comment!}: {}", ${table.entityPath});
        
        boolean result = ${table.entityPath}Service.save(${table.entityPath});
        
        if (result) {
            return ResponseEntity.status(201)
                    .body(ApiResponse.success("${table.comment!}创建成功", ${table.entityPath}));
        } else {
            return ResponseEntity.ok(ApiResponse.error("${table.comment!}创建失败"));
        }
    }

    @GetMapping
    @Operation(summary = "获取${table.comment!}列表", description = "获取${table.comment!}列表")
    public ResponseEntity<ApiResponse<List<${Entity}>>> get${Entity}s() {
        
        List<${Entity}> list = ${table.entityPath}Service.list();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取${table.comment!}详情", description = "根据ID获取${table.comment!}详情")
    public ResponseEntity<ApiResponse<${Entity}>> get${Entity}ById(
            @Parameter(description = "${table.comment!}ID") @PathVariable ${keyGenerator.cfg.keyType} id) {
        
        ${Entity} ${table.entityPath} = ${table.entityPath}Service.getById(id);
        if (${table.entityPath} != null) {
            return ResponseEntity.ok(ApiResponse.success(${table.entityPath}));
        } else {
            return ResponseEntity.ok(ApiResponse.error(404, "${table.comment!}不存在"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新${table.comment!}", description = "更新${table.comment!}信息")
    public ResponseEntity<ApiResponse<${Entity}>> update${Entity}(
            @Parameter(description = "${table.comment!}ID") @PathVariable ${keyGenerator.cfg.keyType} id,
            @Parameter(description = "${table.comment!}信息") @RequestBody ${Entity} ${table.entityPath}) {
        
        ${table.entityPath}.setId(id);
        boolean result = ${table.entityPath}Service.updateById(${table.entityPath});
        
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("${table.comment!}更新成功", ${table.entityPath}));
        } else {
            return ResponseEntity.ok(ApiResponse.error("${table.comment!}更新失败"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${table.comment!}", description = "根据ID删除${table.comment!}")
    public ResponseEntity<ApiResponse<Void>> delete${Entity}(
            @Parameter(description = "${table.comment!}ID") @PathVariable ${keyGenerator.cfg.keyType} id) {
        
        boolean result = ${table.entityPath}Service.removeById(id);
        
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("${table.comment!}删除成功"));
        } else {
            return ResponseEntity.ok(ApiResponse.error("${table.comment!}删除失败"));
        }
    }
}
```

## 🔧 自定义配置

### 1. 修改生成策略

```java
.strategyConfig(builder -> {
    builder.addInclude("sys_user", "sys_role")  // 设置需要生成的表名
        .addTablePrefix("t_", "sys_")           // 设置过滤表前缀
        .entityBuilder()                         // Entity 策略配置
        .nameConvert(INameConvertType.underlineToCamel)  // 下划线转驼峰
        .columnNaming(NamingStrategy.underline_to_camel)  // 数据库字段映射到实体
        .enableLombok()                          // 开启 lombok 模型
        .logicDeleteColumnName("deleted")        // 逻辑删除字段名
        .versionColumnName("version")            // 乐观锁字段名
        .controllerBuilder()                     // Controller 策略配置
        .enableRestStyle()                       // 开启生成@RestController 控制器
        .serviceBuilder()                        // Service 策略配置
        .formatServiceFileName("%sService")     // 格式化 service 接口文件名称
        .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称
        .mapperBuilder()                         // Mapper 策略配置
        .enableBaseResultMap()                   // 启用 BaseResultMap 生成
        .enableBaseColumnList()                  // 启用 BaseColumnList
})
```

### 2. 自定义模板变量

在模板中可以使用以下变量：

- `${package}` - 包名
- `${module}` - 模块名
- `${author}` - 作者
- `${date}` - 日期
- `${table}` - 表信息
- `${entity}` - 实体类名
- `${table.entityPath}` - 实体类名（小写）

### 3. 自定义输出路径

```java
.packageConfig(builder -> {
    builder.parent("com.example.project")      // 设置父包名
        .moduleName("system")                  // 设置模块名
        .pathInfo(Collections.singletonMap(     // 路径配置信息
            OutputFile.xml, "src/main/resources/mapper"))
        .entity("entity")                      // entity 实体类包名
        .service("service")                    // service 包名
        .serviceImpl("service.impl")           // service impl 包名
        .mapper("mapper")                      // mapper 包名
        .controller("controller")              // controller 包名
        .other("other")                        // 其他文件包名
})
```

## 📋 最佳实践

### 1. 数据库表设计规范

- 表名使用小写字母和下划线
- 字段名使用小写字母和下划线
- 必须包含主键 `id`
- 必须包含通用字段：`create_time`, `update_time`, `deleted`, `version`

### 2. 代码生成后处理

1. **调整实体类**：添加业务相关的方法和验证
2. **完善 Service**：添加业务逻辑和事务管理
3. **优化 Controller**：添加权限控制和参数验证
4. **编写测试**：添加单元测试和集成测试

### 3. 模板定制

根据项目需求定制模板，可以：
- 添加统一的响应格式
- 添加权限注解
- 添加日志记录
- 添加参数验证

## 🚨 注意事项

1. **备份现有代码**：生成前备份现有代码，避免覆盖
2. **检查生成结果**：生成后检查代码是否符合项目规范
3. **手动调整**：某些复杂业务逻辑需要手动编写
4. **测试验证**：生成后进行充分的测试验证

---

**🎉 通过代码生成器，您可以快速生成标准化的代码结构，提高开发效率！**