package com.example.loginproject.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class NoCacheFilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> noCacheFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        // 필터 정의: 모든 응답에 캐시 방지 헤더 추가
        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws java.io.IOException, ServletException {
                HttpServletResponse res = (HttpServletResponse) response;
                res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                res.setHeader("Pragma", "no-cache");
                res.setDateHeader("Expires", 0);
                chain.doFilter(request, response);
            }
        });

        // 전역 적용
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("noCacheFilterV2");
        registrationBean.setOrder(1); // 우선순위 설정 (낮을수록 먼저 적용됨)
        return registrationBean;
    }
}
