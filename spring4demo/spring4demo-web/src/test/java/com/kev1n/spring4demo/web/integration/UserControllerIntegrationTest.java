//package com.kev1n.spring4demo.web.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kev1n.spring4demo.common.test.IntegrationTestBase;
//import com.kev1n.spring4demo.common.test.TestDataFactory;
//import com.kev1n.spring4demo.core.entity.User;
//import com.kev1n.spring4demo.core.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * 用户控制器集成测试
// *
// * @author spring4demo
// * @version 1.0.0
// */
//class UserControllerIntegrationTest extends IntegrationTestBase {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("应该能够创建用户")
//    void shouldCreateUser() throws Exception {
//        // Given
//        User user = TestDataFactory.createTestUser();
//        user.setId(null); // 确保ID为空
//
//        // When & Then
//        mockMvc.perform(post("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.username").value(user.getUsername()))
//                .andExpect(jsonPath("$.email").value(user.getEmail()));
//
//        // 验证数据库中的数据
//        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
//        assertThat(savedUser).isPresent();
//        assertThat(savedUser.get().getEmail()).isEqualTo(user.getEmail());
//    }
//
//    @Test
//    @DisplayName("应该能够获取用户列表")
//    void shouldGetUsers() throws Exception {
//        // Given
//        User[] users = TestDataFactory.createTestUsers(5);
//        for (User user : users) {
//            userRepository.save(user);
//        }
//
//        // When & Then
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content.length()").value(5))
//                .andExpect(jsonPath("$.totalElements").value(5))
//                .andExpect(jsonPath("$.totalPages").value(1));
//    }
//
//    @Test
//    @DisplayName("应该能够根据ID获取用户")
//    void shouldGetUserById() throws Exception {
//        // Given
//        User user = userRepository.save(TestDataFactory.createTestUser());
//
//        // When & Then
//        mockMvc.perform(get("/api/users/{id}", user.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(user.getId()))
//                .andExpect(jsonPath("$.username").value(user.getUsername()))
//                .andExpect(jsonPath("$.email").value(user.getEmail()));
//    }
//
//    @Test
//    @DisplayName("当用户不存在时应该返回404")
//    void shouldReturn404WhenUserNotFound() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/users/{id}", "nonexistent-id"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("应该能够更新用户")
//    void shouldUpdateUser() throws Exception {
//        // Given
//        User user = userRepository.save(TestDataFactory.createTestUser());
//        user.setRealName("更新的姓名");
//        user.setPhone("13800138000");
//
//        // When & Then
//        mockMvc.perform(put("/api/users/{id}", user.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(user.getId()))
//                .andExpect(jsonPath("$.realName").value("更新的姓名"))
//                .andExpect(jsonPath("$.phone").value("13800138000"));
//
//        // 验证数据库中的数据
//        Optional<User> updatedUser = userRepository.findById(user.getId());
//        assertThat(updatedUser).isPresent();
//        assertThat(updatedUser.get().getRealName()).isEqualTo("更新的姓名");
//        assertThat(updatedUser.get().getPhone()).isEqualTo("13800138000");
//    }
//
//    @Test
//    @DisplayName("应该能够删除用户")
//    void shouldDeleteUser() throws Exception {
//        // Given
//        User user = userRepository.save(TestDataFactory.createTestUser());
//
//        // When & Then
//        mockMvc.perform(delete("/api/users/{id}", user.getId()))
//                .andExpect(status().isNoContent());
//
//        // 验证用户已被软删除
//        Optional<User> deletedUser = userRepository.findById(user.getId());
//        assertThat(deletedUser).isEmpty();
//    }
//
//    @Test
//    @DisplayName("应该能够根据用户名搜索用户")
//    void shouldSearchUsersByUsername() throws Exception {
//        // Given
//        User user1 = TestDataFactory.createTestUser();
//        user1.setUsername("testuser1");
//        userRepository.save(user1);
//
//        User user2 = TestDataFactory.createTestUser();
//        user2.setUsername("testuser2");
//        userRepository.save(user2);
//
//        User user3 = TestDataFactory.createTestUser();
//        user3.setUsername("otheruser");
//        userRepository.save(user3);
//
//        // When & Then
//        mockMvc.perform(get("/api/users/search")
//                .param("username", "testuser"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content.length()").value(2))
//                .andExpect(jsonPath("$.totalElements").value(2));
//    }
//
//    @Test
//    @DisplayName("应该能够分页查询用户")
//    void shouldGetUsersWithPagination() throws Exception {
//        // Given
//        User[] users = TestDataFactory.createTestUsers(15);
//        for (User user : users) {
//            userRepository.save(user);
//        }
//
//        // When & Then
//        mockMvc.perform(get("/api/users")
//                .param("page", "0")
//                .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content.length()").value(10))
//                .andExpect(jsonPath("$.totalElements").value(15))
//                .andExpect(jsonPath("$.totalPages").value(2))
//                .andExpect(jsonPath("$.size").value(10))
//                .andExpect(jsonPath("$.number").value(0));
//    }
//
//    @Test
//    @DisplayName("应该验证必填字段")
//    void shouldValidateRequiredFields() throws Exception {
//        // Given
//        User user = new User();
//        user.setId(null);
//        user.setUsername(""); // 空用户名
//        user.setEmail("invalid-email"); // 无效邮箱
//
//        // When & Then
//        mockMvc.perform(post("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest());
//    }
//}