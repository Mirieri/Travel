package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Route {
    public Long _id;
    public String name;
    public String description;
    public String town_from;
    public String town_to;
    public String estimated_time;
    public String comments;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Route() {
    }

    public Route(Long id, String name, String description, String town_from, String town_to, String estimated_time, String comments, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.name = name;
        this.description = description;
        this.town_from = town_from;
        this.town_to = town_to;
        this.estimated_time = estimated_time;
        this.comments = comments;
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

    public String getTown_from() {
        return town_from;
    }

    public void setTown_from(String town_from) {
        this.town_from = town_from;
    }

    public String getTown_to() {
        return town_to;
    }

    public void setTown_to(String town_to) {
        this.town_to = town_to;
    }

    public String getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(String estimated_time) {
        this.estimated_time = estimated_time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
