package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Passenger {
    public Long _id;
    public String first_name;
    public String last_name;
    public String telephone;
    public String national_id;
    public String dob;
    public String email;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Passenger() {
    }

    public Passenger(Long id, String first_name, String last_name, String telephone, String national_id, String dob, String email, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.telephone = telephone;
        this.national_id = national_id;
        this.dob = dob;
        this.email = email;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
