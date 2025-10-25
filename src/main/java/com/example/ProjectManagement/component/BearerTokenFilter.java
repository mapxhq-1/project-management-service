package com.example.ProjectManagement.component;

import com.example.ProjectManagement.client.AuthServiceClient;
import com.example.ProjectManagement.dto.GlobalDto.FlowStartResponseModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.equals("/auth-service/check-token"))
        {
            filterChain.doFilter(request, response);
            return;
        }



        // ✅ Extract client_name from headers

        String clientName = request.getHeader("client_name");
        if (clientName == null || clientName.isBlank()) {
            writeUnauthorized(response, "Missing required header: client_name");
            return;
        }

        // ✅ Extract token from Authorization header
        String token = extractTokenFromHeader(request);
        if (token == null || token.isBlank()) {
            writeUnauthorized(response, "Missing or empty Authorization header");
            return;
        }

        try {
            // ✅ Call Auth Service to validate token
            FlowStartResponseModel resp = authServiceClient.checkToken(clientName, token);

            if (resp == null) {
                writeUnauthorized(response, "Auth-service returned null response");
                return;
            }

            if (!"success".equalsIgnoreCase(resp.getStatus())) {
                writeUnauthorized(response, "Unauthorized access");
                return;
            }

            if (resp.getClientTokenCheckResult() == null) {
                writeUnauthorized(response, "Token validation failed");
                return;
            }

            if (resp.getClientTokenCheckResult().getIdentity() == null) {
                writeUnauthorized(response, "Token invalid: no identity found in token");
                return;
            }

            // ✅ Token valid → continue
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            writeUnauthorized(response, "Unauthorized access " );
        }
    }

    /**
     * Extract token from Authorization header
     * Expected format: Authorization: Bearer <token>
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // remove "Bearer "
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write( message);
    }
}
