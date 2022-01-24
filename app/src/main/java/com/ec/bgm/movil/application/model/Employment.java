package com.ec.bgm.movil.application.model;

public class Employment {

    private String emp_id;
    private String emp_name;
    private String emp_description;
    private boolean emp_status;

    public Employment() {
    }

    public Employment(String emp_id, String emp_name, String emp_description, boolean emp_status) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.emp_description = emp_description;
        this.emp_status = emp_status;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_description() {
        return emp_description;
    }

    public void setEmp_description(String emp_description) {
        this.emp_description = emp_description;
    }

    public boolean getEmp_status() {
        return emp_status;
    }

    public void setEmp_status(boolean emp_status) {
        this.emp_status = emp_status;
    }
}