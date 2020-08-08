package com.technion.android.medicart;

public class Caregiver {
    private String Date;
    private  String Time;
    private String Name;
    private String Email;

    public Caregiver(String date, String time, String name, String email) {
        Date = date;
        Time = time;
        Name = name;
        Email = email;
    }
    public Caregiver(){

    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }
}
