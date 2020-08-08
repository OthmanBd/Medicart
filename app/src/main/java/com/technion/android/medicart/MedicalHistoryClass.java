package com.technion.android.medicart;

public class MedicalHistoryClass {
    private String Date;
    private  String Time;
    private String AddedBy;
    private String Diagnosis;
    private String Allergies;
    public MedicalHistoryClass() {}

    public MedicalHistoryClass(String date, String time, String addedby, String diagnosis, String allergies) {
        Date = date;
        Time = time;
        AddedBy = addedby;
        Diagnosis = diagnosis;
        Allergies = allergies;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getAddedBy() {
        return AddedBy;
    }

    public String getDiagnosis() {
        return Diagnosis;
    }

    public String getAllergies() {
        return Allergies;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setAddedBy(String addedby) {
        AddedBy = addedby;
    }

    public void setDiagnosis(String diagnosis) {
        Diagnosis = diagnosis;
    }

    public void setAllergies(String allergies) {
        Allergies = allergies;
    }
}
