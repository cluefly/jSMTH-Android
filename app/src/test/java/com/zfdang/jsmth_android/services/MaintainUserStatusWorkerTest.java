package com.zfdang.jsmth_android.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MaintainUserStatusWorkerTest {

    @Test
    public void detectsExpiredSessionFromMobileLoginForm() {
        String loginPage = "<html><body><form action=\"/user/login\" method=\"post\">"
                + "<input name=\"id\"><input type=\"password\" name=\"passwd\"></form></body></html>";
        assertTrue(MaintainUserStatusWorker.isMobileSessionExpired(loginPage));
    }

    @Test
    public void treatsLoggedInMobilePageAsValid() {
        String loggedInPage = "<html><body><a href=\"/user/logout\">注销</a></body></html>";
        assertFalse(MaintainUserStatusWorker.isMobileSessionExpired(loggedInPage));
    }

    @Test
    public void treatsErrorOrEmptyHtmlAsInconclusive() {
        assertFalse(MaintainUserStatusWorker.isMobileSessionExpired(null));
        assertFalse(MaintainUserStatusWorker.isMobileSessionExpired(""));
        assertFalse(MaintainUserStatusWorker.isMobileSessionExpired("<html><body>502 Bad Gateway</body></html>"));
    }
}
