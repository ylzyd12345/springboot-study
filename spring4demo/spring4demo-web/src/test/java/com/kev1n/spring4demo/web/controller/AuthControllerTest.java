package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.kev1n.spring4demo.api.dto.AuthResult;
import com.kev1n.spring4demo.api.dto.UserDTO;
import com.kev1n.spring4demo.core.service.AuthService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 测试
 *
 * <p>测试 AuthController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("认证控制器测试")
class AuthControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDTO.builder()
                .id("1")
                .username("testuser")
                .email("test@example.com")
                .realName("测试用户")
                .status(1)
                .build();
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() throws Exception {
        // Given
        AuthResult authResult = AuthResult.builder()
                .success(true)
                .token("test-token-123")
                .tokenType("Bearer")
                .expiresIn(7200L)
                .user(testUser)
                .message("登录成功")
                .build();

        when(authService.login("testuser", "password123")).thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("test-token-123"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(7200))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("登录成功"));

        verify(authService, times(1)).login("testuser", "password123");
    }

    @Test
    @DisplayName("用户登录 - 失败")
    void login_Failed() throws Exception {
        // Given
        AuthResult authResult = AuthResult.builder()
                .success(false)
                .message("用户名或密码错误")
                .build();

        when(authService.login("testuser", "wrongpassword")).thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .param("username", "testuser")
                        .param("password", "wrongpassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(authService, times(1)).login("testuser", "wrongpassword");
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() throws Exception {
        // Given
        AuthResult authResult = AuthResult.builder()
                .success(true)
                .token("new-token-456")
                .tokenType("Bearer")
                .expiresIn(7200L)
                .user(testUser)
                .message("注册成功")
                .build();

        when(authService.register("newuser", "password123", "new@example.com", "新用户"))
                .thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("email", "new@example.com")
                        .param("realName", "新用户")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("new-token-456"))
                .andExpect(jsonPath("$.message").value("注册成功"));

        verify(authService, times(1)).register("newuser", "password123", "new@example.com", "新用户");
    }

    @Test
    @DisplayName("用户注册 - 失败")
    void register_Failed() throws Exception {
        // Given
        AuthResult authResult = AuthResult.builder()
                .success(false)
                .message("用户名已存在")
                .build();

        when(authService.register("testuser", "password123", "test@example.com", "测试用户"))
                .thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(authService, times(1)).register("testuser", "password123", "test@example.com", "测试用户");
    }

    @Test
    @DisplayName("刷新Token - 成功")
    void refreshToken_Success() throws Exception {
        // Given
        String token = mockLogin(1L);

        AuthResult authResult = AuthResult.builder()
                .success(true)
                .token("refreshed-token-789")
                .tokenType("Bearer")
                .expiresIn(7200L)
                .message("Token刷新成功")
                .build();

        when(authService.refreshToken()).thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("refreshed-token-789"))
                .andExpect(jsonPath("$.message").value("Token刷新成功"));

        verify(authService, times(1)).refreshToken();
    }

    @Test
    @DisplayName("获取当前用户信息 - 成功")
    void getCurrentUser_Success() throws Exception {
        // Given
        String token = mockLogin(1L);

        when(authService.getCurrentUser()).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/auth/me")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.realName").value("测试用户"));

        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("获取当前用户信息 - 用户不存在")
    void getCurrentUser_NotFound() throws Exception {
        // Given
        String token = mockLogin(1L);

        when(authService.getCurrentUser()).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/auth/me")
                        .header("satoken", token))
                .andExpect(status().isNotFound());

        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("修改密码 - 成功")
    void changePassword_Success() throws Exception {
        // Given
        String token = mockLogin(1L);

        AuthResult authResult = AuthResult.builder()
                .success(true)
                .message("密码修改成功")
                .build();

        when(authService.changePassword("oldpassword", "newpassword")).thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/change-password")
                        .param("oldPassword", "oldpassword")
                        .param("newPassword", "newpassword")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        verify(authService, times(1)).changePassword("oldpassword", "newpassword");
    }

    @Test
    @DisplayName("修改密码 - 原密码错误")
    void changePassword_WrongOldPassword() throws Exception {
        // Given
        String token = mockLogin(1L);

        AuthResult authResult = AuthResult.builder()
                .success(false)
                .message("原密码错误")
                .build();

        when(authService.changePassword("wrongpassword", "newpassword")).thenReturn(authResult);

        // When & Then
        mockMvc.perform(post("/auth/change-password")
                        .param("oldPassword", "wrongpassword")
                        .param("newPassword", "newpassword")
                        .header("satoken", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("原密码错误"));

        verify(authService, times(1)).changePassword("wrongpassword", "newpassword");
    }

    @Test
    @DisplayName("用户退出 - 成功")
    void logout_Success() throws Exception {
        // Given
        String token = mockLogin(1L);

        org.mockito.Mockito.doAnswer(invocation -> {
            StpUtil.logout();
            return null;
        }).when(authService).logout();

        // When & Then
        mockMvc.perform(post("/auth/logout")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("退出成功"));

        verify(authService, times(1)).logout();
    }

    @Test
    @DisplayName("检查登录状态 - 已登录")
    void isLogin_True() throws Exception {
        // Given
        mockLogin(1L);

        // When & Then
        mockMvc.perform(get("/auth/is-login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("检查登录状态 - 未登录")
    void isLogin_False() throws Exception {
        // Given - 不模拟登录

        // When & Then
        mockMvc.perform(get("/auth/is-login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}