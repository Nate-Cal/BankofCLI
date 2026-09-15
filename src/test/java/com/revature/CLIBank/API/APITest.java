package com.revature.CLIBank.API;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class APITest {

    @Test
    public void login_fail_badcreds() {
        API api = new API();
        /* Bad username, bad password that do not meet standads */
        Assertions.assertFalse(api.login("username", "password"));
    }

    /**
     * Test login when there are credentials that pass the
     * requirements but do not exist within the database.
     * @author Nicholas DiGirolamo
     */
    @Test
    public void login_fail_goodcreds() {
        API api = new API();
        Assertions.assertFalse(api.login("Username", "Pa$$w0rdPa$$w0rd"));
    }

    @Test
    public void login_fail_empty() {
        API api = new API();

        api.login("", "Pa$$w0rdPassw0rd");
        Assertions.assertEquals("Please enter a username", api.getResult());
    }

    @Test
    public void zeroTransactions() {
       API api = new API();

       api.getAcctTransactions(null, 0);
       Assertions.assertTrue(api.getResult().isEmpty());
    }

    /**
     * This program does not support mill (1/1000 dollar).
     * The API truncates numbers after the decimal point to
     * two decimal positions.
     */
    @Test
    public void moneyMill() {
        API api = new API();

        long intended = 12345L;
        Assertions.assertEquals(intended, api.parseMoney("$123.456789"));
    }

    @Test
    public void negativeCents() {
        API api = new API();
        Assertions.assertEquals(Long.MIN_VALUE, api.parseMoney("100.-10"));
    }

    @Test
    public void dollarAmountEquality() {
        API api = new API();

        long intended = 10000L;
        long amt0 = api.parseMoney("100.00");
        long amt1 = api.parseMoney("$100");

        Assertions.assertNotEquals(0L, amt0);
        Assertions.assertEquals(intended, amt0);
        Assertions.assertEquals(intended, amt1);
        Assertions.assertEquals(amt0, amt1);
    }
}
