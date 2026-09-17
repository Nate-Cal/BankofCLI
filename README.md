# Bank of CLI
This is a submission for the first assignment of the Revature Fall 2026 Teaneck cohort. 
It is not legitimate banking application, and the authors nor their employer do not
claim any liability or warranty for this program, expressed or implied.

The Bank of CLI ("Manjuban") is a command-line program meant to teach
standard CRUD tooling in addition to project workflows using Git.
This program happens to be written against standard Java tooling and uses
SQLite for persistence.

## Contributors
- Nathen Calderon
- Nicholas DiGirolamo
- Benedict Martinez
- Mo Saha
- Juan Vasquez

## Password Encryption Setup

If you are running the project for the first time, you will need to set up the `CLIBank` run configuration in IntelliJ.

1. Go to Run → Edit Configurations.
2. Click the '+' button and select 'Application'.
3. Set the name to `CLIBank`.
4. For 'Main class', enter:
   `com.revature.CLIBank.CLIBank`
5. For 'Environment variables', add the following variable:
        PASSWORD-KEY=1234567890123456
6. Click 'OK', then 'Apply'.
7. Select the `CLIBank` configuration and run the program.
