package com.revature.CLIBank.BusinessLogic;

public class AccountValidation {

    /*
        Public Methods for Checking Regex?
            hasUpper
            hasLower
            hasSpecialCharacter
            hasNumber
     */

    /*
        Checking validity of UUID
        Params : No Duplicate UUID
     */

    public boolean isUUIDValid(String accountID) {
        return true;
    }

    /*
        Checking validity of Username
        Params : 1 UpperCase, 1 Lowercase,  8 - 16 characters
     */

    public boolean isUserNameValid (String userName) {
        if (userName.matches("^(?=.*[A-Z])(?=.*[a-z]).{8,16}$")) {
            return true;
        } else {
            System.out.println("Username is invalid... Please Enter a valid Username");
            System.out.println("Username must include 1 Uppercase, 1 Lowercase, and must " +
                    "be between 8 and 16 characters");
            return false;
        }
    }

    /*
        Checking validity of Password
        Params : 1 Uppercase, 1 Lowercase, 1 number, 1 special character, 16 - 24 Characters
        Special Characters include : !,@,#,$,%,^,&,*,?,/,\
     */

}
