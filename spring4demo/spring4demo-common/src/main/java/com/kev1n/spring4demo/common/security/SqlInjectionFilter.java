package com.kev1n.spring4demo.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * SQL注入防护过滤器
 * 防护SQL注入攻击，检测并拦截恶意请求
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class SqlInjectionFilter implements Filter {

    /**
     * SQL注入检测正则表达式
     * 检测常见的SQL注入关键字和特殊字符
     */
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('|(\\-\\-)|(;)|(\\bor\\b|\\band\\b|\\bexec\\b|\\binsert\\b|\\bselect\\b|\\bdelete\\b|\\bupdate\\b|\\bcount\\b|\\bdrop\\b|\\bcreate\\b|\\balter\\b|\\btruncate\\b|\\bunion\\b|\\bjoin\\b|\\bwhere\\b|\\bhaving\\b|\\bgroup\\b|\\border\\b|\\bby\\b|\\bfrom\\b|\\binto\\b|\\bvalues\\b|\\bset\\b|\\bgrant\\b|\\brevoke\\b|\\bcommit\\b|\\brollback\\b|\\bdeclare\\b|\\bcase\\b|\\bwhen\\b|\\bthen\\b|\\belse\\b|\\bend\\b|\\bif\\b|\\bwhile\\b|\\bloop\\b|\\bbegin\\b|\\bend\\b|\\bwaitfor\\b|\\bdelay\\b|\\bsleep\\b|\\bshutdown\\b|\\bkill\\b|\\bexecute\\b|\\bsp_exec\\b|\\bxp_cmdshell\\b|\\bopenrowset\\b|\\bopendatasource\\b|\\bload_file\\b|\\binto\\b|\\boutfile\\b|\\bexport\\b|\\bimport\\b|\\bshell\\b|\\bsystem\\b|\\bpassthru\\b|\\beval\\b|\\bexec\\b|\\bassert\\b|\\binclude\\b|\\brequire\\b|\\bfile_get_contents\\b|\\bfile_put_contents\\b|\\bfopen\\b|\\bfwrite\\b|\\bfputs\\b|\\bfclose\\b|\\bfread\\b|\\bfile\\b|\\bunlink\\b|\\brename\\b|\\bcopy\\b|\\bmove\\b|\\bchmod\\b|\\bchown\\b|\\btouch\\b|\\bmkdir\\b|\\brmdir\\b|\\bexec\\b|\\bpopen\\b|\\bproc_open\\b|\\bproc_close\\b|\\bproc_terminate\\b|\\bproc_get_status\\b|\\bproc_nice\\b|\\bproc_terminate\\b|\\bshell_exec\\b|\\bsystem\\b|\\bpassthru\\b)\\s+)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 检查请求参数
        if (containsSqlInjection(httpRequest)) {
            log.warn("检测到SQL注入攻击: URI={}, IP={}", httpRequest.getRequestURI(), httpRequest.getRemoteAddr());
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":400,\"message\":\"检测到SQL注入攻击\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 检查请求是否包含SQL注入
     *
     * @param request HTTP请求
     * @return 是否包含SQL注入
     */
    private boolean containsSqlInjection(HttpServletRequest request) {
        // 检查所有请求参数
        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                if (checkForSqlInjection(value)) {
                    throw new SecurityException("检测到SQL注入: " + key + "=" + value);
                }
            }
        });

        // 检查请求头
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (checkForSqlInjection(headerValue)) {
                throw new SecurityException("检测到SQL注入: " + headerName + "=" + headerValue);
            }
        }

        // 检查请求路径
        String requestUri = request.getRequestURI();
        if (checkForSqlInjection(requestUri)) {
            throw new SecurityException("检测到SQL注入: " + requestUri);
        }

        return false;
    }

    /**
     * 检查字符串是否包含SQL注入
     *
     * @param input 输入字符串
     * @return 是否包含SQL注入
     */
    private boolean checkForSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查SQL注入模式
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            return true;
        }

        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("SQL注入防护过滤器初始化");
    }

    @Override
    public void destroy() {
        log.info("SQL注入防护过滤器销毁");
    }
}