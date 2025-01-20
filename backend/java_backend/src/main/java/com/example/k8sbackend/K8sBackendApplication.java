package com.example.k8sbackend;

import java.io.IOException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.k8sbackend.service.LdapConnector;


@SpringBootApplication
public class K8sBackendApplication {
    public static void main(String[] args) throws LdapException, IOException {
        LdapConnector connector  = new LdapConnector();
        connector.LdapConnection();
        DatabaseConnection.extractSchemaJDBC();
        SpringApplication.run(K8sBackendApplication.class, args);
    }
}