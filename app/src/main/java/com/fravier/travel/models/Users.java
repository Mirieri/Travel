package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Users {
    public Long _id;
    public String email;
    public String password;
    public String permissions;
    public String last_login;
    public String first_name;
    public String last_name;
    public String created_at;
    public String updated_at;

    public Users() {
    }

    public Users(Long id, String email, String password, String permissions, String last_login, String first_name, String last_name, String created_at, String updated_at) {
        this._id = id;
        this.email = email;
        this.password = password;
        this.permissions = permissions;
        this.last_login = last_login;
        this.first_name = first_name;
        this.last_name = last_name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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
