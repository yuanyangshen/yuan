package com.shenyy.yuan.model;

import java.util.List;

public class SysRole {
    private Integer id;
    private String role;
    private String desciption;
    private Boolean availabale = Boolean.FALSE;
    private List<SysPermission> permissions;
    private List<User> users;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public Boolean getAvailabale() {
        return availabale;
    }

    public void setAvailabale(Boolean availabale) {
        this.availabale = availabale;
    }

    public List<SysPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SysPermission> permissions) {
        this.permissions = permissions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
