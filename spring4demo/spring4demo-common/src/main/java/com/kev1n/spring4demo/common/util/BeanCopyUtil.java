package com.kev1n.spring4demo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean拷贝工具类
 * <p>
 * 基于Spring BeanUtils和Cglib BeanCopier实现，提供高性能的对象属性拷贝功能
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
public class BeanCopyUtil {

    /**
     * BeanCopier缓存
     */
    private static final Map<String, BeanCopier> COPIER_CACHE = new ConcurrentHashMap<>();

    /**
     * 私有构造函数
     */
    private BeanCopyUtil() {
    }

    /**
     * 拷贝对象属性（使用Spring BeanUtils）
     *
     * @param source      源对象
     * @param target      目标对象
     * @param ignoreProperties 忽略的属性名
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <S, T> T copyProperties(S source, T target, String... ignoreProperties) {
        if (source == null || target == null) {
            log.warn("源对象或目标对象为空，跳过拷贝");
            return target;
        }
        try {
            BeanUtils.copyProperties(source, target, ignoreProperties);
        } catch (BeansException e) {
            log.error("对象属性拷贝失败", e);
            throw new RuntimeException("对象属性拷贝失败", e);
        }
        return target;
    }

    /**
     * 拷贝对象属性（使用Cglib BeanCopier）
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     * @return 目标对象
     */
    public static <S, T> T copyPropertiesWithCopier(S source, T target) {
        if (source == null || target == null) {
            log.warn("源对象或目标对象为空，跳过拷贝");
            return target;
        }
        try {
            Class<?> sourceClass = source.getClass();
            Class<?> targetClass = target.getClass();
            String key = sourceClass.getName() + "->" + targetClass.getName();

            BeanCopier copier = COPIER_CACHE.computeIfAbsent(key, k ->
                    BeanCopier.create(sourceClass, targetClass, false));

            copier.copy(source, target, null);
        } catch (BeansException e) {
            log.error("对象属性拷贝失败: BeanUtils异常", e);
            throw new RuntimeException("对象属性拷贝失败: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("对象属性拷贝失败: 参数非法", e);
            throw new RuntimeException("对象属性拷贝失败: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("对象属性拷贝失败: 运行时异常", e);
            throw new RuntimeException("对象属性拷贝失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("对象属性拷贝失败: 未知异常", e);
            throw new RuntimeException("对象属性拷贝失败: " + e.getMessage(), e);
        }
        return target;
    }

    /**
     * 拷贝对象属性并返回新对象
     *
     * @param source      源对象
     * @param targetClass 目标对象类
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <S, T> T copyProperties(S source, Class<T> targetClass) {
        if (source == null) {
            log.warn("源对象为空，返回null");
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            return copyProperties(source, target);
        } catch (NoSuchMethodException e) {
            log.error("创建目标对象失败: 目标类缺少无参构造函数: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 目标类缺少无参构造函数: " + targetClass.getName(), e);
        } catch (InstantiationException e) {
            log.error("创建目标对象失败: 无法实例化目标类: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 无法实例化目标类: " + targetClass.getName(), e);
        } catch (IllegalAccessException e) {
            log.error("创建目标对象失败: 无法访问目标类构造函数: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 无法访问目标类构造函数: " + targetClass.getName(), e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error("创建目标对象失败: 目标类构造函数执行失败: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 目标类构造函数执行失败: " + targetClass.getName(), e);
        } catch (Exception e) {
            log.error("创建目标对象失败: 未知异常: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 拷贝对象属性并返回新对象（使用Cglib BeanCopier）
     *
     * @param source      源对象
     * @param targetClass 目标对象类
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <S, T> T copyPropertiesWithCopier(S source, Class<T> targetClass) {
        if (source == null) {
            log.warn("源对象为空，返回null");
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            return copyPropertiesWithCopier(source, target);
        } catch (NoSuchMethodException e) {
            log.error("创建目标对象失败: 目标类缺少无参构造函数: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 目标类缺少无参构造函数: " + targetClass.getName(), e);
        } catch (InstantiationException e) {
            log.error("创建目标对象失败: 无法实例化目标类: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 无法实例化目标类: " + targetClass.getName(), e);
        } catch (IllegalAccessException e) {
            log.error("创建目标对象失败: 无法访问目标类构造函数: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 无法访问目标类构造函数: " + targetClass.getName(), e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error("创建目标对象失败: 目标类构造函数执行失败: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: 目标类构造函数执行失败: " + targetClass.getName(), e);
        } catch (Exception e) {
            log.error("创建目标对象失败: 未知异常: {}", targetClass.getName(), e);
            throw new RuntimeException("创建目标对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 拷贝列表对象
     *
     * @param sourceList  源对象列表
     * @param targetClass 目标对象类
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象列表
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copyProperties(source, targetClass));
        }
        return targetList;
    }

    /**
     * 拷贝列表对象（使用Cglib BeanCopier）
     *
     * @param sourceList  源对象列表
     * @param targetClass 目标对象类
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象列表
     */
    public static <S, T> List<T> copyListWithCopier(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copyPropertiesWithCopier(source, targetClass));
        }
        return targetList;
    }

    /**
     * 清空BeanCopier缓存
     */
    public static void clearCache() {
        COPIER_CACHE.clear();
        log.info("BeanCopier缓存已清空");
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    public static int getCacheSize() {
        return COPIER_CACHE.size();
    }
}