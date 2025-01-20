package com.example.k8sbackend.service;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.stereotype.Service;

import com.example.k8sbackend.model.AuthResult;
import com.example.k8sbackend.model.UserDto;

@Service
public class LdapService {
    private final LdapConnector ldapConnector;

    public LdapService(LdapConnector ldapConnector) {
        this.ldapConnector = ldapConnector;
    }

    // In your LdapService
    public String determineUserType(String username) throws LdapException {
        // First try physical users
        Entry user = searchUser(username, "cn=physical,dc=example,dc=org");
        if (user != null) {
            return "physical";
        }
        
        // Then try machine users
        user = searchUser(username, "cn=machine,dc=example,dc=org");
        if (user != null) {
            return "machine";
        }
        
        return "unknown";
    }
    public AuthResult findAndAuthenticateUser(String username, String password) throws IOException {
    try {
        // First determine the user type
        String userType = determineUserType(username);
        
        // Then find the user in the appropriate location
        Entry user = searchUser(username, userType.equals("machine") ? 
            "cn=machine,dc=example,dc=org" : 
            "cn=physical,dc=example,dc=org");
            
        if (user != null) {
            boolean authenticated = authenticateUser(user.getDn().toString(), password);
            return AuthResult.fromEntry(authenticated, user, userType);  // Remove the empty string
        }
        
        return AuthResult.fromEntry(false, null, "unknown");  // Remove the empty string
    } catch (LdapException e) {
        System.err.println("Error during authentication: " + e.getMessage());
        return AuthResult.fromEntry(false, null, "error");  // Remove the empty string
    }
    }


    private Entry searchUser(String cn, String baseDn) throws LdapException {
        try {
            EntryCursor cursor = ldapConnector.getConnection().search(
                baseDn,                 // Base DN (physical or machine)
                "(cn=" + cn + ")",     // Search filter
                SearchScope.ONELEVEL,   // Search scope
                "*"                     // Return all attributes
            );
            
            if (cursor.next()) {
                Entry entry = cursor.get();
                cursor.close();
                return entry;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            throw new LdapException("Error searching for user: " + e.getMessage());
        }
    }

    private boolean authenticateUser(String userDn, String password) throws IOException {
        LdapConnection authConnection = null;
        try {
            authConnection = new LdapNetworkConnection("192.168.0.9", 389);
            authConnection.bind(userDn, password);
            return true;
        } catch (LdapException e) {
            System.err.println("Authentication failed for " + userDn + ": " + e.getMessage());
            return false;
        } finally {
            if (authConnection != null) {
                try {
                    authConnection.unBind();
                    authConnection.close();
                } catch (LdapException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

     public void printUserDetails(AuthResult result) {
        if (result.getUser() != null) {
            UserDto user = result.getUser();
            System.out.println("\nUser details:");
            System.out.println("Type: " + result.getUserType());
            System.out.println("DN: " + user.getDn());
            System.out.println("CN: " + user.getCn());
            System.out.println("UID Number: " + user.getUidNumber());
            System.out.println("GID Number: " + user.getGidNumber());
            System.out.println("Home Directory: " + user.getHomeDirectory());
            System.out.println("Authentication: " + (result.isAuthenticated() ? "Success" : "Failed"));
        } else {
            System.out.println("\nUser not found");
            System.out.println("Type: " + result.getUserType());
            System.out.println("Authentication: Failed");
        }
    }
}