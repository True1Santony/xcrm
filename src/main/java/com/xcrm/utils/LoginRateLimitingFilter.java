package com.xcrm.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();
    private final RateLimitProperties properties;

    public LoginRateLimitingFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(properties.getMaxAttempts(), Duration.ofSeconds(properties.getRefillDuration()));
        Bandwidth limit = Bandwidth.classic(properties.getMaxAttempts(), refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean esRutaProtegida = method.equalsIgnoreCase("POST") &&
                (path.equals("/login") ||
                        path.equals("/registro") ||
                        path.equals("/enviar-contacto"));

        if (esRutaProtegida) {
            String ip = request.getRemoteAddr();
            Bucket bucket = bucketMap.computeIfAbsent(ip, k -> createNewBucket());

            if (!bucket.tryConsume(1)) {
                log.warn("IP BLOQUEADA: {} en {}", ip, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Demasiados intentos. Intenta más tarde.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
 }

