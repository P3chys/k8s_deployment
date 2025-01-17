package com.example.k8sbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class ApiController {

    @GetMapping("/api/internal/data")
    public ResponseEntity<?> getInternalData(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        boolean isWebRequest = userAgent != null && 
            (userAgent.contains("Mozilla") || userAgent.contains("Chrome") || userAgent.contains("Safari"));

        if (isWebRequest) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. This API is only accessible via command line.");
            return ResponseEntity.status(403).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Internal API response");
        response.put("timestamp", Instant.now());
        response.put("source", "internal");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/external/data")
    public ResponseEntity<?> getExternalData(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        boolean isWebRequest = userAgent != null && 
            (userAgent.contains("Mozilla") || userAgent.contains("Chrome") || userAgent.contains("Safari"));

        if (!isWebRequest) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. This API is only accessible via web browser.");
            return ResponseEntity.status(403).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "External API response");
        response.put("timestamp", Instant.now());
        response.put("source", "external");
        return ResponseEntity.ok(response);
    }
}