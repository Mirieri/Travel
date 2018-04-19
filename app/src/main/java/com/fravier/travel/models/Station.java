package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Station {
    public Long _id;
    public Long town_id;
    public String name;
    public String description;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Station() {
    }

    public Station(Long id, Long town_id, String name, String description, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.town_id = town_id;
        this.name = name;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.created_by = created_by;
        this.updated_by = updated_by;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public Long getTown_id() {
        return town_id;
    }

    public void setTown_id(Long town_id) {
        this.town_id = town_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }
}
