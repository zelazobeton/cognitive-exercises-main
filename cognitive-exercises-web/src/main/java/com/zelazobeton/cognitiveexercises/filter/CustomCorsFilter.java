package com.zelazobeton.cognitiveexercises.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

// Spring Cloud Gateway is handling CORS
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
class CustomCorsFilter implements Filter {

    @Value("${frontend-server-address}")
    private String frontendAddress;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", this.frontendAddress);
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
