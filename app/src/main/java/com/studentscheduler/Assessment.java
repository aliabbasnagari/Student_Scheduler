package com.studentscheduler;

public class Assessment {
    private String AID;
    private String title;
    private String sDate;
    private String eDate;
    private String type;
    private Integer sAlert;
    private Integer eAlert;

    public Assessment(String AID, String title, String sDate, String eDate, String type, Integer sAlert, Integer eAlert) {
        this.AID = AID;
        this.title = title;
        this.sDate = sDate;
        this.eDate = eDate;
        this.type = type;
        this.sAlert = sAlert;
        this.eAlert = eAlert;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
