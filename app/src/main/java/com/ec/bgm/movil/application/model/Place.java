package com.ec.bgm.movil.application.model;

public class Place {

    private String pla_id;
    private String pla_name;
    private String pla_description;
    private boolean pla_status;

    public Place() {
    }

    public Place(String pla_id, String pla_name, String pla_description, boolean pla_status) {
        this.pla_id = pla_id;
        this.pla_name = pla_name;
        this.pla_description = pla_description;
        this.pla_status = pla_status;
    }

    public String getPla_id() {
        return pla_id;
    }

    public void setPla_id(String pla_id) {
        this.pla_id = pla_id;
    }

    public String getPla_name() {
        return pla_name;
    }

    public void setPla_name(String pla_name) {
        this.pla_name = pla_name;
    }

    public String getPla_description() {
        return pla_description;
    }

    public void setPla_description(String pla_description) {
        this.pla_description = pla_description;
    }

    public boolean getPla_status() {
        return pla_status;
    }

    public void setPla_status(boolean pla_status) {
        this.pla_status = pla_status;
    }
}