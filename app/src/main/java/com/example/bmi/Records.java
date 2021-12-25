package com.example.bmi;

public class Records implements Comparable<Records> {
    private String bmi;
    private String state;
    private String weight;
    private String length;
    private String dob;
    private String date;
    private String chosenTime;
    private String userId;


    public Records() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChosenTime() {
        return chosenTime;
    }

    public void setChosenTime(String chosenTime) {
        this.chosenTime = chosenTime;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    @Override
    public int compareTo(Records o) {
        if (getDate() == null || o.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(o.getDate());
    }
}
