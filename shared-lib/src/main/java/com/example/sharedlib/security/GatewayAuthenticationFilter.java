//package com.example.sharedlib.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
///**
// * Универсальный фильтр для авторизации через Gateway.
// * Извлекает X-User-Id и X-User-Role из заголовков,
// * создаёт Authentication и кладёт в SecurityContext.
// */
//public class GatewayAuthenticationFilter extends OncePerRequestFilter {
//    private static final String USER_ID_HEADER = "X-User-Id";
//    private static final String USER_ROLE_HEADER = "X-User-Role";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String userId = request.getHeader(USER_ID_HEADER);
//        String userRole = request.getHeader(USER_ROLE_HEADER);
//
//        if (StringUtils.hasText(userId) && StringUtils.hasText(userRole)) {
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    userId,
//                    null,
//                    Collections.emptyList() // Можно добавить GrantedAuthority по роли
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }
//}