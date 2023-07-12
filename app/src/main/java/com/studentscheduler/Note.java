package com.studentscheduler;

public class Note {
    private String NID;
    private String Note;

    public Note(String NID, String note) {
        this.NID = NID;
        Note = note;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String NID) {
        this.NID = NID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
