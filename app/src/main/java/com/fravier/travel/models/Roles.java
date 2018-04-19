package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Roles {
    public Long _id;
    public String name;
    public String permissions;
    public String created_at;
    public String updated_at;

    public Roles() {
    }

    public Roles(Long id, String name, String permissions, String created_at, String updated_at) {
        this._id = id;
        this.name = name;
        this.permissions = permissions;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
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
