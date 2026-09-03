# BankOfCLI architecture 
BankOfCLI is a three-layer monolithic application. 
1. The API layer, defined in section "Usage."
2. The business layer, defined in section "Authentication"
3. The repository layer, defined in sections "Transactions" and "SQL
tables." 

The architecture emphasizes **flexiblity**. The fewest number of things
as possible are hard-coded. Instead, bank policy is defined in SQL tables,
which are then processed by the business layer.

## Usage
The program is run using:
```sh
java -jar BankOfCLI
```

A user executing this command will automatically enter _interactive mode_. 
Users who wish to execute a single command, or want to automate their
transactions can use _batch_ mode.

```sh
java -jar BankOfCLI batch -u $BCLI_USERNAME -p $BCLI_PIN <command>
```

### Commands
The BankOfCLI interface is highly regular.

```sh
deposit <amount in a decimal number> 
```

```sh
withdraw <amount in a decimal number> [<other bank account>]
```

```sh
transfer <amount in a decimal number> <other bank account>
```

Note that `transfer` and `withdraw` perform the same action when two
arguments are provided.


## Authentication
In interactive mode, the user will be prompted to enter his or her 
credentials: the bank account number (a sequence of digits) and a PIN 
(a four-digit number).

There is a reserved administrator account that can view all transactions,
modify the state of bank accounts, and modify bank account policy. 

## Transactions
The business layer mediates transactions. After a user is authorized, all
subsequent transactions are authenticated. 

When the application receives a request for a transaction, it queries
the database and verifies a transaction against the rules. If it is
determined that the transaction is forbidden.

BankOfCLI does not give credit.

## SQL tables

### Accounts 
Schema:
```sql
CREATE TABLE Accounts (acct PRIMARY KEY, pin VARCHAR(4), opened DATE,  type FOREIGN KEY, balance DECIMAL(10, 5), frozen BOOLEAN);  
```

### Transactions
BankOfCLI uses positive differences to signify deposits, and negative
differences to signify withdrawls. 

```sql
CREATE TABLE Transactions (acct FOREIGN KEY, difference DECIMAL(10, 5), whom TEXT, time DATE);
```

### AcctTypes
Schema: 
```sql 
CREATE TABLE AcctTypes (accttype PRIMARY KEY, name TEXT, transactionlimit INTEGER, penalty DECIMAL(10, 5), rules FOREIGN KEY);
```

### InterestRules
Schema:
```sql
CREATE TABLE InterestRules (accttype FOREIGN KEY, rules PRIMARY KEY, APY DECIMAL(3, 2));
```

