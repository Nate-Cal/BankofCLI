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

    /**
     * Creates a new user and generates a unique ID.
     * @param name the owner's name
     * @param age  the owner's age
     */
    public User(String name, int age) {
        this.userID = UUID.randomUUID();
        this.name = name;
        this.age = age;
    }

    /**
     * Rebuilds a user from an existing database row.
     * @param userID the ID already stored in Owners
     * @param name the owner's name
     * @param age the owner's age
     */
    public User(UUID userID, String name, int age) {
        this.userID = userID;
        this.name = name;
        this.age = age;
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


    

}
