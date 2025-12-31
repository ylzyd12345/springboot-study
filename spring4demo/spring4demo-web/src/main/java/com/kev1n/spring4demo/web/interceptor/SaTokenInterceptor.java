package com.kev1n.spring4demo.web.interceptor;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sa-Token认证拦截器
 * 处理用户认证和授权
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class SaTokenInterceptor extends SaInterceptor {

    public SaTokenInterceptor() {
        super(handle -> {
            SaRouter
                .match("/**")
                .notMatch("/auth/login")
                .notMatch("/auth/register")
                .notMatch("/doc.html")
                .notMatch("/v3/api-docs/**")
                .notMatch("/webjars/**")
                .notMatch("/img.icons/**")
                .notMatch("/actuator/**")
                .notMatch("/error")
                .check(r -> StpUtil.checkLogin());
        });
        log.info("Sa-Token认证拦截器初始化完成");
    }
}