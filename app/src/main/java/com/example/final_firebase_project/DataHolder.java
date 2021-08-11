package com.example.final_firebase_project;

public class DataHolder
{
    String name1,dob,quali;

    public DataHolder(String name1, String dob ) {
        this.name1 = name1;
        this.dob = dob;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
