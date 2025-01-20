// File: src/main/java/com/example/k8sbackend/model/AuthResult.java
package com.example.k8sbackend.model;

import org.apache.directory.api.ldap.model.entry.Entry;

public class AuthResult {
    private final boolean authenticated;
    private final UserDto user;
    private final String userType;

    public static AuthResult fromEntry(boolean authenticated, Entry entry, String userType) {
        UserDto userDto = null;
        if (entry != null) {
            userDto = new UserDto();
            try {
                userDto.setDn(entry.getDn().toString());
                userDto.setCn(entry.get("cn") != null ? entry.get("cn").getString() : null);
                userDto.setUidNumber(entry.get("uidNumber") != null ? entry.get("uidNumber").getString() : null);
                userDto.setGidNumber(entry.get("gidNumber") != null ? entry.get("gidNumber").getString() : null);
                userDto.setHomeDirectory(entry.get("homeDirectory") != null ? entry.get("homeDirectory").getString() : null);
                userDto.setLoginShell(entry.get("loginShell") != null ? entry.get("loginShell").getString() : null);
                userDto.setSn(entry.get("sn") != null ? entry.get("sn").getString() : null);
                userDto.setGivenName(entry.get("givenName") != null ? entry.get("givenName").getString() : null);
            } catch (Exception e) {
                System.err.println("Error converting LDAP entry to DTO: " + e.getMessage());
            }
        }
        return new AuthResult(authenticated, userDto, userType);
    }

    private AuthResult(boolean authenticated, UserDto user, String userType) {
        this.authenticated = authenticated;
        this.user = user;
        this.userType = userType;
    }

    public String generateUserTypeHeader() {
        if (this.userType != null) {
            return "X-User-Type: " + this.userType;
        }
        return null;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public UserDto getUser() {
        return user;
    }

    public String getUserType() {
        return userType;
    }
}