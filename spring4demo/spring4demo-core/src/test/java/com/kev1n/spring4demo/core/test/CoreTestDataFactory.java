package com.kev1n.spring4demo.core.test;

import com.github.javafaker.Faker;
import com.kev1n.spring4demo.core.entity.User;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core模块测试数据工厂
 * 提供Core模块实体对象的创建方法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class CoreTestDataFactory {

    private static final Faker faker = new Faker(Locale.CHINA);
    private static final AtomicLong idSequence = new AtomicLong(1);

    /**
     * 创建测试用户
     */
    public static User createTestUser() {
        return createTestUser(null);
    }

    /**
     * 创建指定ID的测试用户
     */
    public static User createTestUser(String id) {
        User user = new User();
        user.setId(id != null ? id : String.valueOf(idSequence.getAndIncrement()));
        user.setUsername(randomUsername());
        user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"); // password: 123456
        user.setEmail(randomEmail());
        user.setPhone(randomPhone());
        user.setRealName(randomChineseName());
        user.setAvatar(randomUrl());
        user.setStatus(1);
        user.setDeptId("dept-001");
        user.setCreateBy("system");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateBy("system");
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        user.setVersion(1);
        return user;
    }

    /**
     * 创建多个测试用户
     */
    public static User[] createTestUsers(int count) {
        User[] users = new User[count];
        for (int i = 0; i < count; i++) {
            users[i] = createTestUser();
        }
        return users;
    }

    /**
     * 创建非活跃用户
     */
    public static User createInactiveUser() {
        User user = createTestUser();
        user.setStatus(0);
        return user;
    }

    /**
     * 创建已删除用户
     */
    public static User createDeletedUser() {
        User user = createTestUser();
        user.setDeleted(1);
        return user;
    }

    // 辅助方法
    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomPhone() {
        return faker.phoneNumber().cellPhone();
    }

    public static String randomChineseName() {
        return faker.name().fullName();
    }

    public static String randomUrl() {
        return faker.internet().url();
    }
}