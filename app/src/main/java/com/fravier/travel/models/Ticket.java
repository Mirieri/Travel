package com.fravier.travel.models;

/**
 * Created by francis on 06/07/2016.
 */
public class Ticket {
    public Long _id;
    public Long passenger_id;
    public Long trip_id;
    public String ticket_serial;
    public Long station_id;//pickup location
    public String seat_no;
    public String created_at;
    public String updated_at;
    public String created_by;
    public String updated_by;

    public Ticket() {
    }

    public Ticket(Long id, Long passenger_id, Long trip_id, String ticket_serial, Long station_id, String seat_no, String created_at, String updated_at, String created_by, String updated_by) {
        this._id = id;
        this.passenger_id = passenger_id;
        this.trip_id = trip_id;
        this.ticket_serial = ticket_serial;
        this.station_id = station_id;
        this.seat_no = seat_no;
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

    public Long getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(Long passenger_id) {
        this.passenger_id = passenger_id;
    }

    public Long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(Long trip_id) {
        this.trip_id = trip_id;
    }

    public String getTicket_serial() {
        return ticket_serial;
    }

    public void setTicket_serial(String ticket_serial) {
        this.ticket_serial = ticket_serial;
    }

    public Long getStation_id() {
        return station_id;
    }

    public void setStation_id(Long station_id) {
        this.station_id = station_id;
    }

    public String getSeat_no() {
        return seat_no;
    }

    public void setSeat_no(String seat_no) {
        this.seat_no = seat_no;
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
