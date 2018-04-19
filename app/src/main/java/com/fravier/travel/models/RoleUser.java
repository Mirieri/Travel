package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class RoleUser {
    public Long user_id;
    public Long role_id;
    public String created_at;
    public String updated_at;

    public RoleUser() {
    }

    public RoleUser(Long user_id, Long role_id, String created_at, String updated_at) {
        this.user_id = user_id;
        this.role_id = role_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
