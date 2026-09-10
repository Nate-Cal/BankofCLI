package com.revature.CLIBank.Repository;

import com.revature.CLIBank.BusinessLogic.AccountInfo;
import com.revature.CLIBank.Utility.ConnectionFactory;

import java.sql.*;

public class AccountRepo {

    public void createAccountTable() {
        String query = "CREATE TABLE AccountInfo(\n" +
                "\tid text PRIMARY KEY,\n" +
                "\tusername text NOT NULL, \n" +
                "\tpassword text NOT NULL\n" +
                ");";
        try(
                Connection connection = ConnectionFactory.getAutoCommitConnect();
                Statement simpleStatement = connection.createStatement();
        ) {

            simpleStatement.execute(query);

            //simpleStatement.executeUpdate(query);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void createAccountRecord(String userName, String passWord) {
        String query = "INSERT INTO AccountInfo (userName, passWord) VALUES (?,?)";
        try (
                Connection connection = ConnectionFactory.getAutoCommitConnect();
                PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, userName);
            ps.setString(2, passWord);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Account Successfully Created");
            } else {
                System.out.println("Account was NOT created: rows affected = " + rowsAffected);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void getAccountRecord() {
        String sql = "SELECT * FROM AccountInfo";
        try (
                Connection connection = ConnectionFactory.getAutoCommitConnect();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                AccountInfo accountInfo = new AccountInfo();
                String userName = rs.getString("username");
                String passWord = rs.getString("password");
                accountInfo.setUserName(userName);
                accountInfo.setPassWord(passWord);
                System.out.println(accountInfo);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        AccountRepo repo = new AccountRepo();
//
//        repo.createAccountRecord("Slagathor", "SLAGATHORRULES");
//
//        repo.getAccountRecord();
    }

}
