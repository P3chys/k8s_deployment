package com.example.k8sbackend.model;

public class UserDto {
    private String dn;
    private String cn;
    private String uidNumber;
    private String gidNumber;
    private String homeDirectory;
    private String loginShell;
    private String objectClass;
    private String sn;
    private String givenName;

    public UserDto() {}

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(String uidNumber) {
        this.uidNumber = uidNumber;
    }

    public String getGidNumber() {
        return gidNumber;
    }

    public void setGidNumber(String gidNumber) {
        this.gidNumber = gidNumber;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public String getLoginShell() {
        return loginShell;
    }

    public void setLoginShell(String loginShell) {
        this.loginShell = loginShell;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
}