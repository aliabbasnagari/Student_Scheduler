package com.studentscheduler;

public class Instructor {
    private String IID;
    private String name;
    private String phone;
    private String email;

    public Instructor(String IID, String name, String phone, String email) {
        this.IID = IID;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getIID() {
        return IID;
    }

    public void setIID(String IID) {
        this.IID = IID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
