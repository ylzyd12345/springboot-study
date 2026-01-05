package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.FileStorageService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * FileStorageController 测试
 *
 * <p>测试 FileStorageController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("文件存储控制器测试")
class FileStorageControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    private String token;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        token = mockLogin(1L);

        // 创建模拟文件
        mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file content".getBytes()
        );
    }

    @Test
    @DisplayName("上传文件 - 成功")
    void uploadFile_Success() throws Exception {
        // Given
        when(fileStorageService.uploadFile(any(InputStream.class), anyString(), anyString(), anyLong()))
                .thenReturn("2025/01/05/test-abc123.txt");

        when(fileStorageService.getFileUrl("2025/01/05/test-abc123.txt"))
                .thenReturn("http://localhost:9000/files/2025/01/05/test-abc123.txt");

        // When & Then
        mockMvc.perform(multipart("/api/files/upload")
                        .file(mockFile)
                        .header("satoken", token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件上传成功"))
                .andExpect(jsonPath("$.data.fileName").value("2025/01/05/test-abc123.txt"))
                .andExpect(jsonPath("$.data.originalName").value("test.txt"))
                .andExpect(jsonPath("$.data.fileSize").value(25))
                .andExpect(jsonPath("$.data.contentType").value("text/plain"))
                .andExpect(jsonPath("$.data.fileUrl").value("http://localhost:9000/files/2025/01/05/test-abc123.txt"));

        verify(fileStorageService, times(1)).uploadFile(any(InputStream.class), anyString(), anyString(), anyLong());
        verify(fileStorageService, times(1)).getFileUrl(anyString());
    }

    @Test
    @DisplayName("上传文件 - 文件为空")
    void uploadFile_EmptyFile() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/api/files/upload")
                        .file(emptyFile)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("文件不能为空"));

        verify(fileStorageService, times(0)).uploadFile(any(InputStream.class), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("获取文件信息 - 成功")
    void getFileInfo_Success() throws Exception {
        // Given
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("fileName", "test.txt");
        fileInfo.put("fileSize", 25L);
        fileInfo.put("contentType", "text/plain");
        fileInfo.put("lastModified", System.currentTimeMillis());

        when(fileStorageService.getFileInfo("test.txt")).thenReturn(fileInfo);

        // When & Then
        mockMvc.perform(get("/api/files/test.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.txt"))
                .andExpect(jsonPath("$.data.fileSize").value(25));

        verify(fileStorageService, times(1)).getFileInfo("test.txt");
    }

    @Test
    @DisplayName("获取文件信息 - 文件不存在")
    void getFileInfo_NotFound() throws Exception {
        // Given
        when(fileStorageService.getFileInfo("notfound.txt")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/files/notfound.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文件不存在"));

        verify(fileStorageService, times(1)).getFileInfo("notfound.txt");
    }

    @Test
    @DisplayName("删除文件 - 成功")
    void deleteFile_Success() throws Exception {
        // Given
        when(fileStorageService.fileExists("test.txt")).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/files/test.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件删除成功"));

        verify(fileStorageService, times(1)).fileExists("test.txt");
        verify(fileStorageService, times(1)).deleteFile("test.txt");
    }

    @Test
    @DisplayName("删除文件 - 文件不存在")
    void deleteFile_NotFound() throws Exception {
        // Given
        when(fileStorageService.fileExists("notfound.txt")).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/files/notfound.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文件不存在"));

        verify(fileStorageService, times(1)).fileExists("notfound.txt");
        verify(fileStorageService, times(0)).deleteFile(anyString());
    }

    @Test
    @DisplayName("批量删除文件 - 成功")
    void batchDeleteFiles_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "fileNames": ["file1.txt", "file2.txt", "file3.txt"]
            }
            """;

        // When & Then
        mockMvc.perform(delete("/api/files/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"));

        verify(fileStorageService, times(1)).deleteFiles(any());
    }

    @Test
    @DisplayName("批量删除文件 - 文件名列表为空")
    void batchDeleteFiles_EmptyList() throws Exception {
        // Given
        String requestBody = """
            {
                "fileNames": []
            }
            """;

        // When & Then
        mockMvc.perform(delete("/api/files/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("文件名列表不能为空"));

        verify(fileStorageService, times(0)).deleteFiles(any());
    }

    @Test
    @DisplayName("列出所有文件 - 成功")
    void listFiles_Success() throws Exception {
        // Given
        Map<String, Object> file1 = new HashMap<>();
        file1.put("fileName", "file1.txt");
        file1.put("fileSize", 100L);

        Map<String, Object> file2 = new HashMap<>();
        file2.put("fileName", "file2.txt");
        file2.put("fileSize", 200L);

        when(fileStorageService.listFiles("")).thenReturn(List.of(file1, file2));

        // When & Then
        mockMvc.perform(get("/api/files")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(fileStorageService, times(1)).listFiles("");
    }

    @Test
    @DisplayName("列出所有文件 - 带前缀")
    void listFiles_WithPrefix() throws Exception {
        // Given
        when(fileStorageService.listFiles("2025/01/")).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/files")
                        .param("prefix", "2025/01/")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(fileStorageService, times(1)).listFiles("2025/01/");
    }

    @Test
    @DisplayName("获取文件访问URL - 成功")
    void getFileUrl_Success() throws Exception {
        // Given
        when(fileStorageService.fileExists("test.txt")).thenReturn(true);
        when(fileStorageService.getFileUrl("test.txt"))
                .thenReturn("http://localhost:9000/files/test.txt");

        // When & Then
        mockMvc.perform(get("/api/files/test.txt/url")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.txt"))
                .andExpect(jsonPath("$.data.fileUrl").value("http://localhost:9000/files/test.txt"));

        verify(fileStorageService, times(1)).fileExists("test.txt");
        verify(fileStorageService, times(1)).getFileUrl("test.txt");
    }

    @Test
    @DisplayName("获取文件访问URL - 文件不存在")
    void getFileUrl_NotFound() throws Exception {
        // Given
        when(fileStorageService.fileExists("notfound.txt")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/files/notfound.txt/url")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文件不存在"));

        verify(fileStorageService, times(1)).fileExists("notfound.txt");
        verify(fileStorageService, times(0)).getFileUrl(anyString());
    }

    @Test
    @DisplayName("复制文件 - 成功")
    void copyFile_Success() throws Exception {
        // Given
        when(fileStorageService.fileExists("source.txt")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/files/source.txt/copy")
                        .param("targetFileName", "target.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件复制成功"));

        verify(fileStorageService, times(1)).fileExists("source.txt");
        verify(fileStorageService, times(1)).copyFile("source.txt", "target.txt");
    }

    @Test
    @DisplayName("复制文件 - 源文件不存在")
    void copyFile_SourceNotFound() throws Exception {
        // Given
        when(fileStorageService.fileExists("notfound.txt")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/files/notfound.txt/copy")
                        .param("targetFileName", "target.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("源文件不存在"));

        verify(fileStorageService, times(1)).fileExists("notfound.txt");
        verify(fileStorageService, times(0)).copyFile(anyString(), anyString());
    }

    @Test
    @DisplayName("移动文件 - 成功")
    void moveFile_Success() throws Exception {
        // Given
        when(fileStorageService.fileExists("source.txt")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/files/source.txt/move")
                        .param("targetFileName", "target.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件移动成功"));

        verify(fileStorageService, times(1)).fileExists("source.txt");
        verify(fileStorageService, times(1)).moveFile("source.txt", "target.txt");
    }

    @Test
    @DisplayName("移动文件 - 源文件不存在")
    void moveFile_SourceNotFound() throws Exception {
        // Given
        when(fileStorageService.fileExists("notfound.txt")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/files/notfound.txt/move")
                        .param("targetFileName", "target.txt")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("源文件不存在"));

        verify(fileStorageService, times(1)).fileExists("notfound.txt");
        verify(fileStorageService, times(0)).moveFile(anyString(), anyString());
    }
}