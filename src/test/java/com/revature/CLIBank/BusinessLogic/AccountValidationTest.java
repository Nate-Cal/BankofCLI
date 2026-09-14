package com.revature.CLIBank.BusinessLogic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountValidationTest {
    public AccountValidation accountValidation;

//    @Test
//    public void UUIDPositive(){
//
//    }

    @Test
    public void userIDPositive() {
        String validUserID ="UserIDValid";
        boolean userIDPositive = AccountValidation.isUserNameValid(validUserID);

        Assertions.assertTrue(userIDPositive, "Expected UserID to be valid");
    }

    /*
    Boundary Value Analysis     -> test the "edges" of your requirements
            - example: if testing password length is 5-15 characters you can start with 4 different passwords:
                - positive data
                    - 5 character password
                    - 15 character password
                - negative data
                    - 4 character password
                    - 16 character password
        Equivalence Partitioning    -> let 1 value represent all possible values of a "class"
            - example: testing if a password is correctly checked for lower, upper, and numeric characters
                - positive data
                    - P0sitive
                - negative data
                    - p0sitive
                    - Positive
                    - P0SITIVE
     */

    @Test
    public void passWordPositive() {
        String validPassword = "ValidPassword$2026";
        boolean passWordPositive = AccountValidation.isPassWordValid(validPassword);

        Assertions.assertTrue(passWordPositive, "Expected Password to be valid");
    }
}
