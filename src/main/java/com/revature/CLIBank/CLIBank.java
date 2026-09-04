package com.revature.CLIBank;
import com.revature.CLIBank.API.*;

import java.util.Scanner;

/**
 * The main entry point of the application.
 */
class CLIBank {
    private static void InteractiveMode() {
        Scanner sc = new Scanner(System.in);
        TheInterface.login();

        while(true) {
            /* Sanitize the inputs according to the grammar */
            System.out.println("Bank > ");
            executeCmd(null, null);
            break;
        }
    }

    private static void executeCmd(String cmd, String[] args) {
        Interpreter.execute(cmd, args);
    }

    public static void main(String[] args) {
        if(args.length == 0) InteractiveMode();
    }
}