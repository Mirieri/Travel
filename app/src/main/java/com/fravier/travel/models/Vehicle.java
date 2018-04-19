package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Vehicle {
    public Long _id;
    public String registration_plate;
    public String description;
    public String licence;
    public String condition;
    public Long driver_id;
    public int seats;
    public String comments;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Vehicle() {
    }

    public Vehicle(Long id, String registration_plate, String description, String licence, String condition, Long driver_id, int seats, String comments, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.registration_plate = registration_plate;
        this.description = description;
        this.licence = licence;
        this.condition = condition;
        this.driver_id = driver_id;
        this.seats = seats;
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

    public String getRegistration_plate() {
        return registration_plate;
    }

    public void setRegistration_plate(String registration_plate) {
        this.registration_plate = registration_plate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
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
