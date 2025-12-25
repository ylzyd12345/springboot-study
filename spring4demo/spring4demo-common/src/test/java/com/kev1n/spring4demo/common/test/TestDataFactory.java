package com.kev1n.spring4demo.common.test;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试数据工厂
 * 提供通用测试数据的生成方法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class TestDataFactory {

    private static final Faker faker = new Faker(Locale.CHINA);
    private static final AtomicLong idSequence = new AtomicLong(1);

    /**
     * 生成下一个ID
     */
    public static String nextId() {
        return String.valueOf(idSequence.getAndIncrement());
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

    /**
     * 创建随机的密码
     */
    public static String randomPassword() {
        return faker.internet().password(6, 12);
    }

    /**
     * 创建随机的公司名称
     */
    public static String randomCompany() {
        return faker.company().name();
    }

    /**
     * 创建随机的地址
     */
    public static String randomAddress() {
        return faker.address().fullAddress();
    }

    /**
     * 创建随机的URL
     */
    public static String randomUrl() {
        return faker.internet().url();
    }

    /**
     * 创建随机的时间戳
     */
    public static long randomTimestamp() {
        return faker.number().numberBetween(1600000000000L, System.currentTimeMillis());
    }
}