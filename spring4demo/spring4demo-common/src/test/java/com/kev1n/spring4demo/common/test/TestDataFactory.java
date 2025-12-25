package com.kev1n.spring4demo.common.test;

import com.kev1n.spring4demo.core.entity.User;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试数据工厂
 * 提供测试数据的生成方法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class TestDataFactory {

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
        user.setUsername(faker.name().username());
        user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"); // password: 123456
        user.setEmail(faker.internet().emailAddress());
        user.setPhone(faker.phoneNumber().cellPhone());
        user.setRealName(faker.name().fullName());
        user.setAvatar(faker.internet().avatar());
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
     * 创建随机的中文名称
     */
    public static String randomChineseName() {
        return faker.name().fullName();
    }

    /**
     * 创建随机的邮箱
     */
    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * 创建随机的手机号
     */
    public static String randomPhone() {
        return faker.phoneNumber().cellPhone();
    }

    /**
     * 创建随机的用户名
     */
    public static String randomUsername() {
        return faker.name().username();
    }
}