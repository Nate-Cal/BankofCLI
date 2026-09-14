package com.revature.CLIBank.model;

import java.util.UUID;

/**
 * Bank customer who can own one or more accounts.
 * Maps to the Owners table!
 */
public class User {
    private UUID userID;
    private String name;
    private int age;
    private String passWord;

    /**
     * Creates a new user and generates a unique ID.
     */
    public User(String name, int age, String passWord) {
        this.userID = UUID.randomUUID();
        this.name = name;
        this.age = age;
        this.passWord = passWord;
    }

    /**
     * Rebuilds a user from an existing database row.
     */
    public User(UUID userID, String name, int age, String passWord) {
        this.userID = userID;
        this.name = name;
        this.age = age;
        this.passWord = passWord;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

}
