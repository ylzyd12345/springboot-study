package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.DocumentPreviewService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DocumentPreviewController 测试
 *
 * <p>测试 DocumentPreviewController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("文档预览控制器测试")
class DocumentPreviewControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentPreviewService documentPreviewService;

    private String token;

    private static final String PREVIEW_URL = "http://localhost:8012/onlinePreview?url=http://localhost:8080/api/files/test.pdf&satoken=test-token";

    private static final String PREVIEW_URL_WITH_WATERMARK = "http://localhost:8012/onlinePreview?url=http://localhost:8080/api/files/test.pdf&watermark=test&satoken=test-token";

    @BeforeEach
    void setUp() {
        token = mockLogin(1L);
    }

    @Test
    @DisplayName("生成文件预览URL - 成功")
    void generatePreviewUrl_Success() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.pdf")).thenReturn(true);
        when(documentPreviewService.generatePreviewUrlWithToken("test.pdf", token))
                .thenReturn(PREVIEW_URL);

        // When & Then
        mockMvc.perform(get("/api/preview/test.pdf")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.data.previewUrl").value(PREVIEW_URL))
                .andExpect(jsonPath("$.data.supportPreview").value(true));

        verify(documentPreviewService, times(1)).isSupportPreview("test.pdf");
        verify(documentPreviewService, times(1)).generatePreviewUrlWithToken("test.pdf", token);
    }

    @Test
    @DisplayName("生成文件预览URL - 不支持的文件类型")
    void generatePreviewUrl_UnsupportedType() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.unknown")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/preview/test.unknown")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(415))
                .andExpect(jsonPath("$.message").value("该文件类型不支持预览"));

        verify(documentPreviewService, times(1)).isSupportPreview("test.unknown");
        verify(documentPreviewService, times(0)).generatePreviewUrlWithToken(anyString(), anyString());
    }

    @Test
    @DisplayName("生成带水印的预览URL - 成功")
    void generatePreviewUrlWithWatermark_Success() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.pdf")).thenReturn(true);
        when(documentPreviewService.generatePreviewUrlWithWatermark("test.pdf", "test"))
                .thenReturn(PREVIEW_URL_WITH_WATERMARK);

        // When & Then
        mockMvc.perform(get("/api/preview/test.pdf/watermark")
                        .param("watermark", "test")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.data.previewUrl").value(PREVIEW_URL_WITH_WATERMARK))
                .andExpect(jsonPath("$.data.watermark").value("test"))
                .andExpect(jsonPath("$.data.supportPreview").value(true));

        verify(documentPreviewService, times(1)).isSupportPreview("test.pdf");
        verify(documentPreviewService, times(1)).generatePreviewUrlWithWatermark("test.pdf", "test");
    }

    @Test
    @DisplayName("生成带水印的预览URL - 不支持的文件类型")
    void generatePreviewUrlWithWatermark_UnsupportedType() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.unknown")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/preview/test.unknown/watermark")
                        .param("watermark", "test")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(415))
                .andExpect(jsonPath("$.message").value("该文件类型不支持预览"));

        verify(documentPreviewService, times(1)).isSupportPreview("test.unknown");
        verify(documentPreviewService, times(0)).generatePreviewUrlWithWatermark(anyString(), anyString());
    }

    @Test
    @DisplayName("检查文件是否支持预览 - 支持")
    void checkSupportPreview_Supported() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.pdf")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/preview/test.pdf/check")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.data.supportPreview").value(true));

        verify(documentPreviewService, times(1)).isSupportPreview("test.pdf");
    }

    @Test
    @DisplayName("检查文件是否支持预览 - 不支持")
    void checkSupportPreview_NotSupported() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.unknown")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/preview/test.unknown/check")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test.unknown"))
                .andExpect(jsonPath("$.data.supportPreview").value(false));

        verify(documentPreviewService, times(1)).isSupportPreview("test.unknown");
    }

    @Test
    @DisplayName("获取支持的文件类型列表 - 成功")
    void getSupportedFileTypes_Success() throws Exception {
        // Given
        String[] supportedTypes = new String[]{
                "doc", "docx", "xls", "xlsx", "ppt", "pptx",
                "pdf", "txt", "xml", "html", "htm", "md", "json", "csv",
                "jpg", "jpeg", "png", "gif", "bmp", "svg", "webp",
                "zip", "rar", "7z", "tar", "gz",
                "mp4", "avi", "mkv", "mov", "wmv", "flv",
                "mp3", "wav", "ogg", "flac", "aac"
        };

        when(documentPreviewService.getSupportedFileTypes()).thenReturn(supportedTypes);

        // When & Then
        mockMvc.perform(get("/api/preview/supported-types")
                        .header("satoken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.supportedTypes").isArray())
                .andExpect(jsonPath("$.data.count").value(supportedTypes.length));

        verify(documentPreviewService, times(1)).getSupportedFileTypes();
    }

    @Test
    @DisplayName("重定向到预览页面 - 成功")
    void redirectToPreview_Success() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.pdf")).thenReturn(true);
        when(documentPreviewService.generatePreviewUrlWithToken("test.pdf", token))
                .thenReturn(PREVIEW_URL);

        // When & Then
        mockMvc.perform(get("/api/preview/test.pdf/redirect")
                        .header("satoken", token))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", PREVIEW_URL));

        verify(documentPreviewService, times(1)).isSupportPreview("test.pdf");
        verify(documentPreviewService, times(1)).generatePreviewUrlWithToken("test.pdf", token);
    }

    @Test
    @DisplayName("重定向到预览页面 - 不支持的文件类型")
    void redirectToPreview_UnsupportedType() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.unknown")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/preview/test.unknown/redirect")
                        .header("satoken", token))
                .andExpect(status().isBadRequest());

        verify(documentPreviewService, times(1)).isSupportPreview("test.unknown");
        verify(documentPreviewService, times(0)).generatePreviewUrlWithToken(anyString(), anyString());
    }

    @Test
    @DisplayName("重定向到预览页面 - 生成URL失败")
    void redirectToPreview_GenerateUrlFailed() throws Exception {
        // Given
        when(documentPreviewService.isSupportPreview("test.pdf")).thenReturn(true);
        when(documentPreviewService.generatePreviewUrlWithToken("test.pdf", token))
                .thenThrow(new RuntimeException("生成URL失败"));

        // When & Then
        mockMvc.perform(get("/api/preview/test.pdf/redirect")
                        .header("satoken", token))
                .andExpect(status().isInternalServerError());

        verify(documentPreviewService, times(1)).isSupportPreview("test.pdf");
        verify(documentPreviewService, times(1)).generatePreviewUrlWithToken("test.pdf", token);
    }
}