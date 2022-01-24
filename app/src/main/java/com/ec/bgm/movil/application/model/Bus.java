package com.ec.bgm.movil.application.model;

public class Bus {

    private String bus_id;
    private String bus_make;
    private String bus_model;
    private int bus_number_disc;
    private long bus_registration_date;
    private String bus_registration_number;
    private int bus_size;
    private String bus_propietor_id;
    private String bus_state;
    private boolean bus_status;

    public Bus() {
    }

    public Bus(String bus_id, String bus_make, String bus_model, int bus_number_disc, long bus_registration_date, String bus_registration_number, int bus_size, String bus_propietor_id, String bus_state, boolean bus_status) {
        this.bus_id = bus_id;
        this.bus_make = bus_make;
        this.bus_model = bus_model;
        this.bus_number_disc = bus_number_disc;
        this.bus_registration_date = bus_registration_date;
        this.bus_registration_number = bus_registration_number;
        this.bus_size = bus_size;
        this.bus_propietor_id = bus_propietor_id;
        this.bus_state = bus_state;
        this.bus_status = bus_status;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getBus_make() {
        return bus_make;
    }

    public void setBus_make(String bus_make) {
        this.bus_make = bus_make;
    }

    public String getBus_model() {
        return bus_model;
    }

    public void setBus_model(String bus_model) {
        this.bus_model = bus_model;
    }

    public int getBus_number_disc() {
        return bus_number_disc;
    }

    public void setBus_number_disc(int bus_number_disc) {
        this.bus_number_disc = bus_number_disc;
    }

    public long getBus_registration_date() {
        return bus_registration_date;
    }

    public void setBus_registration_date(long bus_registration_date) {
        this.bus_registration_date = bus_registration_date;
    }

    public String getBus_registration_number() {
        return bus_registration_number;
    }

    public void setBus_registration_number(String bus_registration_number) {
        this.bus_registration_number = bus_registration_number;
    }

    public int getBus_size() {
        return bus_size;
    }

    public void setBus_size(int bus_size) {
        this.bus_size = bus_size;
    }

    public String getBus_propietor_id() {
        return bus_propietor_id;
    }

    public void setBus_propietor_id(String bus_propietor_id) {
        this.bus_propietor_id = bus_propietor_id;
    }

    public String getBus_state() {
        return bus_state;
    }

    public void setBus_state(String bus_state) {
        this.bus_state = bus_state;
    }

    public boolean getBus_status() {
        return bus_status;
    }

    public void setBus_status(boolean bus_status) {
        this.bus_status = bus_status;
    }
}