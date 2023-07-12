package com.studentscheduler;

public class Course {
    private String CID;
    private String title;
    private String sdate;
    private String edate;
    private String status;
    private Integer sAlert;
    private Integer eAlert;

    public Course(String CID, String title, String sdate, String edate, String status, Integer sAlert, Integer eAlert) {
        this.CID = CID;
        this.title = title;
        this.sdate = sdate;
        this.edate = edate;
        this.status = status;
        this.sAlert = sAlert;
        this.eAlert = eAlert;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getsAlert() {
        return sAlert;
    }

    public void setsAlert(Integer sAlert) {
        this.sAlert = sAlert;
    }

    public Integer geteAlert() {
        return eAlert;
    }

    public void seteAlert(Integer eAlert) {
        this.eAlert = eAlert;
    }
}
