package com.revature.CLIBank;

import com.revature.CLIBank.API.*;
import com.revature.CLIBank.BusinessLogic.BankLog;
import com.revature.CLIBank.Repository.RepositoryException;

import java.util.*;

public class CLIBank {
    private static final String usage =
            """
            usage CLIBank [-h] | [batch <command> -u <username> -p <password>]
            """;

    private static final String prompt = "CLIBank> ";

    private static final String logo =
            """
            ____              _             __    _____ _      _____\s
           |  _ \\            | |           / _|  / ____| |    |_   _|
           | |_) | __ _ _ __ | | __   ___ | |_  | |    | |      | | \s
           |  _ < / _` | '_ \\| |/ /  / _ \\|  _| | |    | |      | | \s
           | |_) | (_| | | | |   <  | (_) | |   | |____| |____ _| |_\s
           |____/ \\__,_|_| |_|_|\\_\\  \\___/|_|    \\_____|______|_____|
           """;

    protected static final String helpTxt =
            """
            Bank of CLI shell commands:
            
            help/?: Show this help
            accounts: List your accounts
            transactions <your account> [optional: n]: List recent transactions
            create <PIN> <checking|savings>: create a new account
            delete <your account> <PIN>: delete an account
            deposit <your account> <amount>: Add money
            withdraw <your account> <amount>: Remove money
            transfer <your account> <recipient account number> <amount>: Transfer money
            exit/quit: End your session
            """;

    private static API api;
    // Share one reader so a menu does not consume another menu's input.
    private static final Scanner input = new Scanner(System.in);

    protected static void printError(String str) {
        BankLog.outcome(BankLog.Event.INPUT, false, null);
        System.err.println(str);
        System.err.println();
    }

    protected static void register() {
        String username, password;
        Scanner sc = input;
        boolean registered; //Mo


        do {
            //if(api.getUser() == null && api.getResult() != null)
            //System.out.print(api.getResult());
            // I removed this because the login error was showing up again when I
            // selected N. interactive() already prints the login error, so register()
            // was printing the same old message a second time.

            System.out.println("Register for a new account with the Bank of CLI.");
            System.out.print("Username: ");
            username = sc.nextLine();
            System.out.print("Password: ");
            password = sc.nextLine();

            // api.register() was already creating an error message when the username
            // or password was invalid, but the CLI wasn't printing it. I saved the
            // return value so I can print the error before asking the user to try again.
            registered = api.register(username, password);

            if (!registered) {
                System.out.println(api.getResult());
                System.out.println();
            }

        } while (!registered); //while(!api.register(username, password));

        System.out.println("Successfully registered. Please log in.");
    }

    protected static boolean login() {
        Scanner sc = input;

        System.out.println("Bank of CLI Login");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        /* Mutates the API object to authorize all future API calls. */
        return api.login(username, password);
    }

    protected static void exec(List<String> args) {
        String cmd = args.getFirst();

        if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("quit")) {
            System.out.println("Goodbye!");
            System.exit(0); /* Normal, planned exit. */
        } else if(cmd.equalsIgnoreCase("create")) {
            if(args.size() < 3) {
                printError("Create: not enough arguments");
                return;
            } else if (args.size() > 3) {
                printError("Create: too many arguments");
                return;
            }
            api.creatAcct(args.get(1), args.get(2));
            System.out.println(api.getResult());
        } else if(cmd.equals("delete")) {
            if(args.size() < 3) {
                printError("Delete: not enough arguments");
                return;
            } else if (args.size() > 3) {
                printError("Delete: too many arguments");
                return;
            }
            api.deleteAcct(args.get(1), args.get(2));
            System.out.println(api.getResult());
        } else if (cmd.equalsIgnoreCase("accounts")) {
            api.getAccts();
            System.out.print(api.getResult());
        } else if (cmd.equalsIgnoreCase("deposit")) {
            try {
                api.deposit(args.get(1), args.get(2));
                System.out.println(api.getResult());
            } catch (Exception e) {
                if (e instanceof IndexOutOfBoundsException)
                    printError("Syntax error: too few arguments.");
            }
        } else if (cmd.equalsIgnoreCase("withdraw")) {
            try {
                api.withdraw(args.get(1), args.get(2));
                System.out.println(api.getResult());
            } catch (Exception e) {
                if (e instanceof IndexOutOfBoundsException) {
                    printError("Syntax error: too few arguments");
                }
            }
        } else if (cmd.equalsIgnoreCase("transfer")) {
            if (args.size() != 4) { printError("Use: transfer <source> <destination> <amount>"); return; }
            api.transfer(args.get(1), args.get(2), args.get(3));
            System.out.println(api.getResult());
        } else if (cmd.equalsIgnoreCase("transactions")) {
            // The account is a UUID; only the optional row count is a number
            try {
                if (args.size() == 1) api.getTransactions(100);
                else if (args.size() == 2) api.getAcctTransactions(args.get(1), 100);
                else if (args.size() == 3) {
                    int rows = Integer.parseInt(args.get(2));
                    if (rows < 0 || rows > 1000) { printError("Choose 0 to 1000 rows."); return; }
                    api.getAcctTransactions(args.get(1), rows);
                } else { printError("Use: transactions [account] [rows]"); return; }
                System.out.print(api.getResult());
            } catch (NumberFormatException e) {
                printError("The row count must be a whole number.");
            }
        } else if(cmd.equalsIgnoreCase("help") || cmd.equals("?")) {
            System.out.println(helpTxt);
        } else {
            printError("Unrecognized command");
        }
    }

    protected static void interactive() {
        Scanner sc = input;
        System.out.print(logo);

        boolean status = false;
        do {
            System.out.print("Welcome to the Bank of CLI. Are you a new user or returning user? (N/R) ");
            String response = sc.nextLine();
            if (response.equalsIgnoreCase("n")) {
                register();
                status = login();
            } else if (response.equalsIgnoreCase("r")) {
                status = login();
            }
            if(!status && api.getResult() != null) {
                System.out.println(api.getResult());
                System.out.println();
            }

        } while (!status);

        while(true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            exec(Arrays.asList(line.split(" ")));
        }
    }

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        try { api = new API(); }
        catch (RepositoryException e) { printError("Service unavailable. Please try again."); return; }

        if(arguments.isEmpty()) {
            interactive();
        }

        if(arguments.contains("-h")) {
            System.out.print(CLIBank.usage);
            return;
        }

        if(arguments.getFirst().equalsIgnoreCase("batch")) {
            // Locate credential flags after commands of different lengths
            int u = arguments.indexOf("-u");
            int p = arguments.indexOf("-p");
            if (u < 2 || p != u + 2 || p + 1 != arguments.size() - 1) {
                printError("Use: batch <command> -u <username> -p <password>");
                return;
            }
            if (!api.login(arguments.get(u + 1), arguments.get(p + 1))) {
                printError(api.getResult());
                return;
            }
            exec(arguments.subList(1, u));
        }

    }
}
