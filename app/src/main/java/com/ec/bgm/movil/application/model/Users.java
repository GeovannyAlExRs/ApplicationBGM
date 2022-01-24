package com.ec.bgm.movil.application.model;

public class Users {

    private String use_id;
    private String use_first_name;
    private String use_last_name;
    private String use_address;
    private String use_email;
    private String use_phone;
    private String use_name;
    private String use_password;
    private String use_pass_crypt;
    private String use_photo;
    private long use_registration_date;
    private String use_employment_id;
    private String use_roles_id;
    private boolean use_status;

    public Users() {
    }

    public Users(String use_id, String use_first_name, String use_last_name, String use_address, String use_email,
                 String use_phone, String use_name, String use_password, String use_pass_crypt, String use_photo,
                 long use_registration_date, String use_employment_id, String use_roles_id, boolean use_status) {
        this.use_id = use_id;
        this.use_first_name = use_first_name;
        this.use_last_name = use_last_name;
        this.use_address = use_address;
        this.use_email = use_email;
        this.use_phone = use_phone;
        this.use_name = use_name;
        this.use_password = use_password;
        this.use_pass_crypt = use_pass_crypt;
        this.use_photo = use_photo;
        this.use_registration_date = use_registration_date;
        this.use_employment_id = use_employment_id;
        this.use_roles_id = use_roles_id;
        this.use_status = use_status;
    }

    public String getUse_id() {
        return use_id;
    }

    public void setUse_id(String use_id) {
        this.use_id = use_id;
    }

    public String getUse_first_name() {
        return use_first_name;
    }

    public void setUse_first_name(String use_first_name) {
        this.use_first_name = use_first_name;
    }

    public String getUse_last_name() {
        return use_last_name;
    }

    public void setUse_last_name(String use_last_name) {
        this.use_last_name = use_last_name;
    }

    public String getUse_address() {
        return use_address;
    }

    public void setUse_address(String use_address) {
        this.use_address = use_address;
    }

    public String getUse_email() {
        return use_email;
    }

    public void setUse_email(String use_email) {
        this.use_email = use_email;
    }

    public String getUse_phone() {
        return use_phone;
    }

    public void setUse_phone(String use_phone) {
        this.use_phone = use_phone;
    }

    public String getUse_name() {
        return use_name;
    }

    public void setUse_name(String use_name) {
        this.use_name = use_name;
    }

    public String getUse_password() {
        return use_password;
    }

    public void setUse_password(String use_password) {
        this.use_password = use_password;
    }

    public String getUse_pass_crypt() {
        return use_pass_crypt;
    }

    public void setUse_pass_crypt(String use_pass_crypt) {
        this.use_pass_crypt = use_pass_crypt;
    }

    public String getUse_photo() {
        return use_photo;
    }

    public void setUse_photo(String use_photo) {
        this.use_photo = use_photo;
    }

    public long getUse_registration_date() {
        return use_registration_date;
    }

    public void setUse_registration_date(long use_registration_date) {
        this.use_registration_date = use_registration_date;
    }

    public String getUse_employment_id() {
        return use_employment_id;
    }

    public void setUse_employment_id(String use_employment_id) {
        this.use_employment_id = use_employment_id;
    }

    public String getUse_roles_id() {
        return use_roles_id;
    }

    public void setUse_roles_id(String use_roles_id) {
        this.use_roles_id = use_roles_id;
    }

    public boolean getUse_status() {
        return use_status;
    }

    public void setUse_status(boolean use_status) {
        this.use_status = use_status;
    }
}