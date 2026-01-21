package com.junmo.platform.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 *
 * <p>用于在非Spring管理的类中获取Spring Bean实例</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Bean名称获取Bean实例
     *
     * @param name Bean名称
     * @param <T>  Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据Class类型获取Bean实例
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据Bean名称和Class类型获取Bean实例
     *
     * @param name  Bean名称
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}