package com.revature.CLIBank;

import com.revature.CLIBank.API.TheInterface;

/**
 * @author Nicholas DiGirolamo
 * The Interpreter class provides the utility routines
 * for parsing input.
 */
class Interpreter {
    static void execute(String cmd, String[] args) {
        int result = -1; /* Ternary: -1 unset, 0 fail, 1 pass */

        if (cmd.equalsIgnoreCase("balance")) {
            TheInterface.viewBalance();
        } else if (cmd.equalsIgnoreCase("exit")) {
            System.out.println("You have been logged out. Goodbye!");
        } else if (cmd.equalsIgnoreCase(("deposit"))) {
            TheInterface.deposit();
        } else if (cmd.equalsIgnoreCase("withdraw")) {
            TheInterface.withdraw();
        } else if (cmd.equalsIgnoreCase("transfer")) {
            result = TheInterface.transfer(arg[0], arg[1], arg[2]) ? 1 : 0;
        } else {
            System.out.println("Unrecognized command.");
        }

        if (result == 0) {
            System.out.println("Error. Please try again.");
        }
    }
}