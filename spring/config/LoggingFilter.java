package com.example.spring.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log request details
        logger.info("Request URI: {}", httpRequest.getRequestURI());
        logger.info("HTTP Method: {}", httpRequest.getMethod());
        logRequestHeaders(httpRequest);
        
        // Log the JWT token if available
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("JWT Token: {}", token);
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error occurred while processing request: {}", e.getMessage(), e);
            throw e; // Re-throw the exception to allow further processing by Spring
        }

        // Log response details
        logger.info("Response Status: {}", httpResponse.getStatus());
    }

    private void logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ");
        }
        logger.info("Headers: {}", headers.toString());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
