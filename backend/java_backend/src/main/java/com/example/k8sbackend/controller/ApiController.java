package com.example.k8sbackend.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.k8sbackend.model.AuthResult;
import com.example.k8sbackend.service.LdapConnector;
import com.example.k8sbackend.service.LdapService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApiController {
    
    private final LdapService ldapService;
    
    public ApiController() {
        LdapConnector ldap = new LdapConnector();
        ldap.LdapConnection();
        this.ldapService = new LdapService(ldap);
    }
    @PostMapping("/api/physical/login")
    public ResponseEntity<AuthResult> physicalLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {
            System.out.println("Received login request for user: "+ username);
            System.out.println("Received login request for user: "+ request.getRequestURI());
        try {
            AuthResult authResult = ldapService.findAndAuthenticateUser(username, password);
            // Only allow physical users through this endpoint
            if (!"physical".equals(authResult.getUserType())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            return ResponseEntity.ok(authResult);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/api/machine/login")
    public ResponseEntity<AuthResult> machineLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {
            System.out.println("Received login request for user: "+ username);
            System.out.println("Received login request for user: "+ request.getRequestURI());
        try {
            AuthResult authResult = ldapService.findAndAuthenticateUser(username, password);
            // Only allow machine users through this endpoint
            if (!"machine".equals(authResult.getUserType())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            return ResponseEntity.ok(authResult);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/public/status")
    public ResponseEntity<Map<String, Object>> getPublicStatus() {
        // Creating a response with some system information
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", new Date());
        status.put("status", "operational");
        status.put("version", "1.0.0");
        
        // Add some basic system metrics
        Runtime runtime = Runtime.getRuntime();
        status.put("memory", Map.of(
            "total", runtime.totalMemory(),
            "free", runtime.freeMemory(),
            "max", runtime.maxMemory()
        ));

        return ResponseEntity.ok(status);
    }
}