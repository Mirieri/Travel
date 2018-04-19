package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Trip {
    public Long _id;
    public Long route_id;
    public Long vehicle_id;
   public String date_travel;
    public String time_reporting;
    public String time_departure;
    public double fare;
    public double total_fare;
    public double total_expenses;
    public double total_net;
    public String close_status;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Trip() {
    }

    public Trip(Long id, Long route_id,Long vehicle_id, String date_travel, String time_reporting, String time_departure, double fare, double total_fare, double total_expenses, double total_net,String close_status, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.route_id = route_id;
        this.vehicle_id=vehicle_id;
        this.date_travel = date_travel;
        this.time_reporting = time_reporting;
        this.time_departure = time_departure;
        this.fare = fare;
        this.total_fare = total_fare;
        this.total_expenses = total_expenses;
        this.total_net = total_net;
        this.close_status=close_status;
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

    public Long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Long route_id) {
        this.route_id = route_id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getDate_travel() {
        return date_travel;
    }

    public void setDate_travel(String date_travel) {
        this.date_travel = date_travel;
    }

    public String getTime_reporting() {
        return time_reporting;
    }

    public void setTime_reporting(String time_reporting) {
        this.time_reporting = time_reporting;
    }

    public String getTime_departure() {
        return time_departure;
    }

    public void setTime_departure(String time_departure) {
        this.time_departure = time_departure;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public double getTotal_fare() {

        return total_fare;
    }

    public void setTotal_fare(double total_fare) {

        this.total_fare = total_fare;
    }

    public double getTotal_expenses() {

        return total_expenses;
    }

    public void setTotal_expenses(double total_expenses) {
        this.total_expenses = total_expenses;
    }

    public double getTotal_net() {
        return total_net;
    }

    public void setTotal_net(double total_net) {
        this.total_net = total_net;
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

    public String getClose_status() {
        return close_status;
    }

    public void setClose_status(String close_status) {
        this.close_status = close_status;
    }
}
