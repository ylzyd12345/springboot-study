package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController 集成测试
 *
 * <p>测试 UserController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("用户控制器集成测试")
class UserControllerIT extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;

    private String token;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);

        // 模拟登录用户并设置权限
        token = mockLogin(1L);
        StpUtil.getSession().set("role", "ADMIN");
        StpUtil.getSession().set("permissionList", Arrays.asList("user:create", "user:update", "user:delete", "user:query", "user:status"));
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void createUser_Success() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("new@example.com")).thenReturn(false);
        when(userService.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("2");
            return true;
        });

        String requestBody = """
            {
                "username": "newuser",
                "password": "Password123!",
                "email": "new@example.com",
                "phone": "13900139000",
                "realName": "新用户",
                "status": 1
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));

        verify(userService, times(1)).existsByUsername("newuser");
        verify(userService, times(1)).existsByEmail("new@example.com");
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("创建用户 - 用户名已存在")
    void createUser_UsernameExists() throws Exception {
        // Given
        when(userService.existsByUsername("testuser")).thenReturn(true);

        String requestBody = """
            {
                "username": "testuser",
                "password": "Password123!",
                "email": "test2@example.com",
                "realName": "测试用户2"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(userService, times(1)).existsByUsername("testuser");
        verify(userService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("创建用户 - 邮箱已存在")
    void createUser_EmailExists() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        String requestBody = """
            {
                "username": "newuser",
                "password": "Password123!",
                "email": "test@example.com",
                "realName": "新用户"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱已存在"));

        verify(userService, times(1)).existsByUsername("newuser");
        verify(userService, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("创建用户 - 参数验证失败")
    void CreateUser_ValidationFailed() throws Exception {
        // Given - 用户名为空
        String requestBody = """
            {
                "username": "",
                "password": "Password123!",
                "email": "test@example.com",
                "realName": "测试用户"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取用户列表 - 成功")
    void getUsers_Success() throws Exception {
        // Given
        when(userService.page(any(), any())).thenAnswer(invocation -> {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page =
                invocation.getArgument(0);
            page.setRecords(Arrays.asList(testUser));
            return page;
        });

        // When & Then
        mockMvc.perform(get("/api/users")
                        .param("current", "1")
                        .param("size", "10")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records.length()").value(1));

        verify(userService, times(1)).page(any(), any());
    }

    @Test
    @DisplayName("获取用户列表 - 带关键字搜索")
    void getUsers_WithKeyword() throws Exception {
        // Given
        when(userService.page(any(), any())).thenAnswer(invocation -> {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page =
                invocation.getArgument(0);
            page.setRecords(Arrays.asList(testUser));
            return page;
        });

        // When & Then
        mockMvc.perform(get("/api/users")
                        .param("current", "1")
                        .param("size", "10")
                        .param("keyword", "test")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService, times(1)).page(any(), any());
    }

    @Test
    @DisplayName("获取用户详情 - 成功")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getOptById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/1")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).getOptById(1L);
    }

    @Test
    @DisplayName("获取用户详情 - 用户不存在")
    void getUserById_NotFound() throws Exception {
        // Given
        when(userService.getOptById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/999")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        verify(userService, times(1)).getOptById(999L);
    }

    @Test
    @DisplayName("更新用户信息 - 成功")
    void updateUser_Success() throws Exception {
        // Given
        when(userService.getOptById(1L)).thenReturn(Optional.of(testUser));
        when(userService.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userService.updateById(any(User.class))).thenReturn(true);

        String requestBody = """
            {
                "email": "newemail@example.com",
                "phone": "13900139000",
                "realName": "更新用户",
                "status": 1
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户更新成功"));

        verify(userService, times(1)).getOptById(1L);
        verify(userService, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户信息 - 用户不存在")
    void updateUser_NotFound() throws Exception {
        // Given
        when(userService.getOptById(999L)).thenReturn(Optional.empty());

        String requestBody = """
            {
                "email": "newemail@example.com",
                "realName": "更新用户"
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        verify(userService, times(1)).getOptById(999L);
        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void deleteUser_Success() throws Exception {
        // Given
        when(userService.getOptById(1L)).thenReturn(Optional.of(testUser));
        when(userService.removeById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/1")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService, times(1)).getOptById(1L);
        verify(userService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("批量删除用户 - 成功")
    void batchDeleteUsers_Success() throws Exception {
        // Given
        when(userService.removeByIds(anyList())).thenReturn(true);

        String requestBody = "[1, 2, 3]";

        // When & Then
        mockMvc.perform(delete("/api/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"));

        verify(userService, times(1)).removeByIds(anyList());
    }

    @Test
    @DisplayName("批量删除用户 - ID列表为空")
    void batchDeleteUsers_EmptyIds() throws Exception {
        // Given
        String requestBody = "[]";

        // When & Then
        mockMvc.perform(delete("/api/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户ID列表不能为空"));

        verify(userService, never()).removeByIds(anyList());
    }

    @Test
    @DisplayName("更新用户状态 - 成功")
    void updateUserStatus_Success() throws Exception {
        // Given
        when(userService.getOptById(1L)).thenReturn(Optional.of(testUser));
        when(userService.updateById(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/users/1/status")
                        .param("status", "0")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户状态更新成功"));

        verify(userService, times(1)).getOptById(1L);
        verify(userService, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户状态 - 用户不存在")
    void updateUserStatus_NotFound() throws Exception {
        // Given
        when(userService.getOptById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/users/999/status")
                        .param("status", "0")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        verify(userService, times(1)).getOptById(999L);
        verify(userService, never()).updateById(any(User.class));
    }
}