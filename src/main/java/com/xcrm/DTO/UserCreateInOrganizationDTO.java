package com.xcrm.DTO;


public class UserCreateInOrganizationDTO {
    private String username;
    private String password;
    private Long organizationId;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
