package com.revature.CLIBank.API;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.Assert.*;

public class APITest {

    @Test
    public void login_badcreds() {
        API api = new API();
        /* Bad username, bad password that do not meet standads */
        Assert.assertFalse(api.login("username", "password"));
    }

    public void login_fail_goodcreds() {
        API api = new API();

        Assert.assertFalse(api.login("Username", "Pa$$w0rdPa$$w0rd"));
    }

}
