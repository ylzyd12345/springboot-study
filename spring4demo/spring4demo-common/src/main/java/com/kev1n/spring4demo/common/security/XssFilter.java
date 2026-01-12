package com.kev1n.spring4demo.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * XSS防护过滤器
 * 防护XSS（跨站脚本）攻击，检测并拦截恶意脚本
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class XssFilter implements Filter {

    /**
     * XSS攻击检测正则表达式
     * 检测常见的XSS攻击模式
     */
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<(script|iframe|object|embed|form|input|textarea|button|link|meta|style|img|video|audio|canvas|svg|math|div|span|a|p|table|tr|td|th|ul|ol|li|h[1-6]|b|i|u|s|strike|tt|code|pre|blockquote|cite|abbr|acronym|address|bdo|bdi|del|ins|q|dfn|kbd|samp|var|strong|em|mark|small|big|sub|sup|time|data|wbr|br|hr|col|colgroup|section|article|aside|header|footer|nav|main|figure|figcaption|details|summary|dialog|menu|menuitem|command|datalist|keygen|output|progress|meter|fieldset|legend|label|datalist|option|optgroup|select|textarea|button|form|input|object|param|embed|video|audio|source|track|canvas|map|area|base|link|meta|style|script|noscript|template|slot|shadow|content|picture|source|iframe|frame|frameset|noframes|applet|marquee|blink|font|basefont|center|dir|listing|plaintext|xmp|listing|menu|dir|multicol|nextid|server|client|ilayer|layer|nolayer|xml|namespace|import|implement|declare|element|attribute|interface|include|exclude|annotation|processing-instruction|entity|notation|attribute|cdata|comment|text|pcdata|any|mixed|empty)\\s*.*?>|javascript:|vbscript:|on\\w+\\s*=|expression\\(|eval\\(|alert\\(|confirm\\(|prompt\\(|document\\.cookie|window\\.location|document\\.write|document\\.getElementById|document\\.createElement|document\\.appendChild|innerHTML|outerHTML|insertAdjacentHTML|insertAdjacentText|createContextualFragment|Range\\.createContextualFragment|DOMParser\\.parseFromString|XMLHttpRequest|fetch|WebSocket|EventSource|FormData|File|Blob|URL|URLSearchParams|atob|btoa|escape|unescape|encodeURI|decodeURI|encodeURIComponent|decodeURIComponent|setTimeout|setInterval|requestAnimationFrame|cancelAnimationFrame|requestIdleCallback|cancelIdleCallback|Promise|async|await|import|export|from|as|class|extends|super|this|new|typeof|instanceof|in|of|delete|void|return|throw|try|catch|finally|switch|case|break|continue|do|while|for|if|else|with|debugger|yield|let|const|var|function|=>|=>",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 检查请求参数
        if (containsXss(httpRequest)) {
            log.warn("检测到XSS攻击: URI={}, IP={}", httpRequest.getRequestURI(), httpRequest.getRemoteAddr());
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":400,\"message\":\"检测到XSS攻击\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 检查请求是否包含XSS攻击
     *
     * @param request HTTP请求
     * @return 是否包含XSS攻击
     */
    private boolean containsXss(HttpServletRequest request) {
        // 检查所有请求参数
        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                if (checkForXss(value)) {
                    throw new SecurityException("检测到XSS攻击: " + key + "=" + value);
                }
            }
        });

        // 检查请求头
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (checkForXss(headerValue)) {
                throw new SecurityException("检测到XSS攻击: " + headerName + "=" + headerValue);
            }
        }

        // 检查请求路径
        String requestUri = request.getRequestURI();
        if (checkForXss(requestUri)) {
            throw new SecurityException("检测到XSS攻击: " + requestUri);
        }

        return false;
    }

    /**
     * 检查字符串是否包含XSS攻击
     *
     * @param input 输入字符串
     * @return 是否包含XSS攻击
     */
    private boolean checkForXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查XSS模式
        if (XSS_PATTERN.matcher(input).find()) {
            return true;
        }

        return false;
    }

    /**
     * 清理XSS攻击字符串
     * 移除危险的HTML标签和JavaScript代码
     *
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String cleanXss(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 移除HTML标签
        String cleaned = input.replaceAll("<[^>]*>", "");
        
        // 移除JavaScript代码
        cleaned = cleaned.replaceAll("(?i)javascript:", "");
        cleaned = cleaned.replaceAll("(?i)vbscript:", "");
        cleaned = cleaned.replaceAll("(?i)on\\w+\\s*=", "");
        
        // 移除表达式
        cleaned = cleaned.replaceAll("(?i)expression\\(", "");
        cleaned = cleaned.replaceAll("(?i)eval\\(", "");
        cleaned = cleaned.replaceAll("(?i)alert\\(", "");
        
        return cleaned;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("XSS防护过滤器初始化");
    }

    @Override
    public void destroy() {
        log.info("XSS防护过滤器销毁");
    }
}