package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Expense {
    public Long _id;
    public Long trip_id;
    public String name;
    public String description;
    public double amount;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Expense() {
    }

    public Expense(Long id, Long trip_id, String name, String description, double amount, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.trip_id = trip_id;
        this.name = name;
        this.description = description;
        this.amount = amount;
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

    public Long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(Long trip_id) {
        this.trip_id = trip_id;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
