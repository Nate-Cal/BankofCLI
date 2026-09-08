package com.revature.CLIBank.model;

import java.util.UUID;

public class User {
    private UUID userID;
    private String firstname;
    private String lastname;
    private int age;

    public User(String firstname, String lastname, int age) {
        this.userID = UUID.randomUUID();
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public User(UUID userID, String firstname, String lastname, int age) {
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
