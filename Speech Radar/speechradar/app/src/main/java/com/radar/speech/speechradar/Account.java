package com.radar.speech.speechradar;

public class Account {
    String d_id;
    String d_firstname;
    String d_lastname;
    String d_password;
    String d_confirm_password;
    String d_email_address;

    public Account(String d_id, String d_firstname, String d_lastname, String d_password, String d_confirm_password, String d_email_address) {
        this.d_id = d_id;
        this.d_firstname = d_firstname;
        this.d_lastname = d_lastname;
        this.d_password = d_password;
        this.d_confirm_password = d_confirm_password;
        this.d_email_address = d_email_address;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }

    public String getD_firstname() {
        return d_firstname;
    }

    public void setD_firstname(String d_firstname) {
        this.d_firstname = d_firstname;
    }

    public String getD_lastname() {
        return d_lastname;
    }

    public void setD_lastname(String d_lastname) {
        this.d_lastname = d_lastname;
    }

    public String getD_password() {
        return d_password;
    }

    public void setD_password(String d_password) {
        this.d_password = d_password;
    }

    public String getD_confirm_password() {
        return d_confirm_password;
    }

    public void setD_confirm_password(String d_confirm_password) {
        this.d_confirm_password = d_confirm_password;
    }

    public String getD_email_address() {
        return d_email_address;
    }

    public void setD_email_address(String d_email_address) {
        this.d_email_address = d_email_address;
    }
}
