package com.kev1n.spring4demo.web;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Web 测试基类
 *
 * <p>提供 Web 层测试的通用配置和工具方法</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest(classes = com.kev1n.spring4demo.web.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseWebTest {

    @Autowired
    protected MockMvc mockMvc;

    /**
     * 每个测试方法执行前的初始化操作
     */
    @BeforeEach
    void setUp() {
        // 清除之前的登录状态
        StpUtil.logout();
        setupTest();
    }

    /**
     * 每个测试方法执行后的清理操作
     */
    @AfterEach
    void tearDown() {
        cleanupTest();
        // 清除登录状态
        StpUtil.logout();
    }

    /**
     * 测试初始化钩子方法，子类可以覆盖
     */
    protected void setupTest() {
        // 默认空实现，子类可以覆盖
    }

    /**
     * 测试清理钩子方法，子类可以覆盖
     */
    protected void cleanupTest() {
        // 默认空实现，子类可以覆盖
    }

    /**
     * 模拟用户登录
     *
     * @param userId 用户ID
     * @return token 值
     */
    protected String mockLogin(Long userId) {
        StpUtil.login(userId);
        StpUtil.getSession().set("loginId", userId);
        return StpUtil.getTokenValue();
    }

    /**
     * 模拟管理员登录
     */
    protected void mockAdminLogin() {
        mockLogin(1L);
        StpUtil.getSession().set("role", "ADMIN");
    }

    /**
     * 模拟普通用户登录
     */
    protected void mockUserLogin() {
        mockLogin(2L);
        StpUtil.getSession().set("role", "USER");
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 执行 GET 请求
     *
     * @param url 请求 URL
     * @return ResultActions
     */
    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 执行带 token 的 GET 请求
     *
     * @param url   请求 URL
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performGet(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("satoken", token));
    }

    /**
     * 执行 POST 请求
     *
     * @param url  请求 URL
     * @param body 请求体
     * @return ResultActions
     */
    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    /**
     * 执行带 token 的 POST 请求
     *
     * @param url   请求 URL
     * @param body  请求体
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performPost(String url, Object body, String token) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body))
                .header("satoken", token));
    }

    /**
     * 执行 POST 请求（无请求体）
     *
     * @param url 请求 URL
     * @return ResultActions
     */
    protected ResultActions performPost(String url) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 执行带 token 的 POST 请求（无请求体）
     *
     * @param url   请求 URL
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performPost(String url, String token) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("satoken", token));
    }

    /**
     * 执行 PUT 请求
     *
     * @param url  请求 URL
     * @param body 请求体
     * @return ResultActions
     */
    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    /**
     * 执行带 token 的 PUT 请求
     *
     * @param url   请求 URL
     * @param body  请求体
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performPut(String url, Object body, String token) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body))
                .header("satoken", token));
    }

    /**
     * 执行 DELETE 请求
     *
     * @param url 请求 URL
     * @return ResultActions
     */
    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 执行带 token 的 DELETE 请求
     *
     * @param url   请求 URL
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performDelete(String url, String token) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("satoken", token));
    }

    /**
     * 执行 PATCH 请求
     *
     * @param url  请求 URL
     * @param body 请求体
     * @return ResultActions
     */
    protected ResultActions performPatch(String url, Object body) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    /**
     * 执行带 token 的 PATCH 请求
     *
     * @param url   请求 URL
     * @param body  请求体
     * @param token Sa-Token
     * @return ResultActions
     */
    protected ResultActions performPatch(String url, Object body, String token) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body))
                .header("satoken", token));
    }

    /**
     * 等待一段时间（用于异步测试）
     *
     * @param millis 毫秒数
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}