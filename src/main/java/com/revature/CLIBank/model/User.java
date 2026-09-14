package com.revature.CLIBank.model;

import com.revature.CLIBank.Repository.AccountRepo;

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
    public boolean exists;

    /**
     * Creates a new user and generates a unique ID.
     */
    public User(String name, int age, String passWord) {
        this.userID = UUID.randomUUID();
        this.name = name;
        this.age = age;
        this.passWord = passWord;
        this.exists = false;
    }

    /**
     * Rebuilds a user from an existing database row.
     */
    public User(UUID userID, String name, int age, String passWord) {
        this.userID = userID;
        this.name = name;
        this.age = age;
        this.passWord = passWord;
        this.exists = true;
    }

    /**
     * Looks up an existing user by username and password.
     * Sets exists to true and copies the row if the credentials match.
     */
    public User(String username, String password) {
        User found = new AccountRepo().findUserByNameAndPassword(username, password);
        if (found != null) {
            this.userID = found.userID;
            this.name = found.name;
            this.age = found.age;
            this.passWord = found.passWord;
            this.exists = true;
        } else {
            this.name = username;
            this.passWord = password;
            this.exists = false;
        }
    }

    /**
     * Populates the object based on the username and password
     * both being correct.
     */
    public User(String username, String password) {
        this.exists = true;
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
