package com.moodleV2.Academia.jwt_security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtValidationFilter implements Filter {

    private final Map<String, String> roleAccessMap = new HashMap<>() {{
        put("/profesori", "ADMIN");
        put("/students", "STUDENT");
    }};

    private final GrpcClientService grpcClientService;

    public JwtValidationFilter(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("Filter invoked");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // FIXME: should i raise an exception an catch it later?
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authorizationHeader.substring(7);

        System.out.println("\nToken: " + token + "\n");

        try {
            String role = grpcClientService.validateTokenAndGetRole(token);

            if (!isRoleAllowed(httpRequest.getRequestURI(), role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for role: " + role);
                return;
            }

            httpRequest.setAttribute("userRole", role);

            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private boolean isRoleAllowed(String uri, String role) {
        System.out.println("\nUri: " + uri + "\n");
        return roleAccessMap.entrySet().stream()
                .anyMatch(entry -> uri.split("\\?")[0].endsWith(entry.getKey()) && entry.getValue().contains(role));
    }

}
