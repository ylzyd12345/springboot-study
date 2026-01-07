package com.kev1n.spring4demo.core.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

/**
 * 用户文档（Elasticsearch）
 *
 * 使用IK分词器进行中文分词
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user_index")
@Setting(settingPath = "elasticsearch/user-settings.json")
public class UserDocument {

    /**
     * 文档ID（使用用户ID）
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /**
     * 用户ID
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * 用户名（使用IK分词器）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String username;

    /**
     * 邮箱
     */
    @Field(type = FieldType.Keyword)
    private String email;

    /**
     * 手机号
     */
    @Field(type = FieldType.Keyword)
    private String phone;

    /**
     * 真实姓名（使用IK分词器）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String realName;

    /**
     * 头像URL
     */
    @Field(type = FieldType.Keyword)
    private String avatar;

    /**
     * 用户状态（0-禁用，1-启用）
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 部门ID
     */
    @Field(type = FieldType.Keyword)
    private String deptId;

    /**
     * 创建人
     */
    @Field(type = FieldType.Keyword)
    private String createBy;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Field(type = FieldType.Keyword)
    private String updateBy;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updateTime;

    /**
     * 删除标记（0-未删除，1-已删除）
     */
    @Field(type = FieldType.Integer)
    private Integer deleted;

    /**
     * 版本号
     */
    @Field(type = FieldType.Integer)
    private Integer version;

    /**
     * 从User实体转换为UserDocument
     *
     * @param user User实体
     * @return UserDocument
     */
    public static UserDocument fromUser(com.kev1n.spring4demo.core.entity.User user) {
        return UserDocument.builder()
                .id(String.valueOf(user.getId()))
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .deptId(user.getDeptId())
                .createBy(user.getCreateBy())
                .createTime(user.getCreateTime())
                .updateBy(user.getUpdateBy())
                .updateTime(user.getUpdateTime())
                .deleted(user.getDeleted())
                .version(user.getVersion())
                .build();
    }
}