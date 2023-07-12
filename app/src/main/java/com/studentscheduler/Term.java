package com.studentscheduler;

import android.widget.Toast;

public class Term {
    private String TID;
    private String title;
    private String sDate;
    private String eDate;
    private Integer sAlert;
    private Integer eAlert;

    public Term(String TID, String title, String sDate, String eDate, Integer sAlert, Integer eAlert) {
        this.TID = TID;
        this.title = title;
        this.sDate = sDate;
        this.eDate = eDate;
        this.sAlert = sAlert;
        this.eAlert = eAlert;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
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
