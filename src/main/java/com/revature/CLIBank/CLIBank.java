package com.revature.CLIBank;

import com.revature.CLIBank.API.*;

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
    
    help: Show this help
    accounts: List your accounts
    transactions <your account> [optional: n]: List recent transactions
    deposit <your account> <amount>: Add money
    withdraw <your account> <amount>: Remove money
    transfer <your account> <recipient account number> <amount>: Transfer money
    exit: End your session
    quit: End your session
    """;

    private static API api;

    private static void register() {
        String username, password;
        Scanner sc = new Scanner(System.in);

        do {
            if(api.getUser() == null && api.getResult() != null)
                System.out.print(api.getResult());

            System.out.println("Register for a new account with the Bank of CLI.");
            System.out.print("Username: ");
            username = sc.next();
            System.out.print("Password: ");
            password = sc.next();
        } while(!api.register(username, password));

        System.out.println("Successfully registered. Please log in.");
    }

    private static boolean login() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Bank of CLI Login");
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        /* Mutates the API object to authorize all future API calls. */
        return api.login(username, password);
    }

    private static void exec(List<String> args) {
        String cmd = args.getFirst();

        if(cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("quit")) {
            System.out.println("Goodbye!");
            System.exit(0); /* Normal, planned exit. */
        } else if(cmd.equalsIgnoreCase("accounts")) {
            api.getAccts();
        } else if(cmd.equalsIgnoreCase("deposit")) {
            try {
                api.deposit(args.get(1), args.get(2));
            } catch(Exception e) {
                if(e instanceof IndexOutOfBoundsException)
                    System.err.println("Syntax error: too few arguments.");
            }
        } else if(cmd.equalsIgnoreCase("withdraw")) {
            try {
                api.withdraw(args.get(1), args.get(2));
            } catch(Exception e) {
                if(e instanceof IndexOutOfBoundsException) {
                    System.err.println("Syntax error: too few arguments");
                }
            }
        } else if(cmd.equalsIgnoreCase("transactions")) {
            if(args.size() == 1)
                api.getTransactions();
            else if(args.size() == 2) {
                int stagedRows = Integer.parseInt(args.get(1));
                if(stagedRows <= 1000) {
                    /* Get stagedRows count of most recent ones */
                } else {
                    /* Is actually an account */
                }

            } else if(args.size() == 3) {
                /* Get accounts */
            }
        } else if(cmd.equalsIgnoreCase("help")) {
            System.out.println(helpTxt);
        }
    }

    private static void interactive() {
        Scanner sc = new Scanner(System.in);

        System.out.print(logo);

        boolean status = false;
        do {
            System.out.print("Welcome to the Bank of CLI. Are you a new user or returning user? (N/R) ");
            String response = sc.next();
            if (response.equalsIgnoreCase("n")) {
                register();
                status = login();
            } else if (response.equalsIgnoreCase("r")) {
                status = login();
            }
            if(!status && api.getResult() != null)
                System.out.println(api.getResult());

        } while (!status);

        while(true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            exec(Arrays.asList(line.split(" ")));
        }
    }

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        api = new API();

        if(arguments.isEmpty()) {
            interactive();
        }

        if(arguments.contains("-h")) {
            System.out.print(CLIBank.usage);
            return;
        }

        if(arguments.getFirst().equalsIgnoreCase("batch")) {
            try {
                api.login(arguments.get(3), arguments.get(5));
            } catch(IndexOutOfBoundsException e) {
                System.err.println("Syntax error. Exiting.");
                return;
            }

            exec(arguments.subList(1, arguments.size()));
        }

    }
}
