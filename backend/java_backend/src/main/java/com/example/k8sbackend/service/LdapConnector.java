package com.example.k8sbackend.service;

import java.io.IOException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.stereotype.Component;

@Component
public class LdapConnector {
    private LdapConnection connection;

    public void LdapConnection() {
        try {
            connect();

        } catch (LdapException e) {
            System.err.println("Failed to initialize LDAP connection: " + e.getMessage());
        }
    }

    public void connect() throws LdapException {
        connection = new LdapNetworkConnection("192.168.0.9", 389);
        connection.bind("cn=admin,dc=example,dc=org", "admin");
        System.out.println("Successfully connected to LDAP server");
    }

    public LdapConnection getConnection() {
        return connection;
    }

    public void disconnect() throws IOException {
        if (connection != null) {
            try {
                connection.unBind();
                connection.close();
            } catch (LdapException e) {
                System.err.println("Error closing LDAP connection: " + e.getMessage());
            }
        }
    }
}