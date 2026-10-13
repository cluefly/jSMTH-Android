package com.zfdang.jsmth_android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

import org.junit.Test;

import java.util.List;

public class WebviewCookieHandlerTest {
  private static final HttpUrl WWW = HttpUrl.parse("https://www.newsmth.net/nForum/");

  @Test
  public void keepsBracketedSessionCookie() {
    String header = "main[UTMPUSERID]=mozilla; main[UTMPKEY]=abc; main[UTMPNUM]=1";
    Cookie parsed = Cookie.parse(WWW, "main[UTMPUSERID]=mozilla");
    assertEquals("main[UTMPUSERID]", parsed.name());

    List<Cookie> cookies = WebviewCookieHandler.cookiesForUrl(WWW, header);
    assertEquals(3, cookies.size());
    assertEquals("main[UTMPUSERID]", cookies.get(0).name());
    assertEquals("mozilla", cookies.get(0).value());
    assertEquals("main[UTMPKEY]", cookies.get(1).name());
  }

  @Test
  public void readsLoginIdAndIgnoresGuest() {
    assertEquals("mozilla", WebviewCookieHandler.userIdFromPairs(
        WebviewCookieHandler.parseCookiePairs("other=1; main[UTMPUSERID]=mozilla")));
    assertNull(WebviewCookieHandler.userIdFromPairs(
        WebviewCookieHandler.parseCookiePairs("main[UTMPUSERID]=guest")));
    assertNull(WebviewCookieHandler.userIdFromPairs(
        WebviewCookieHandler.parseCookiePairs(null)));
  }

  @Test
  public void laterHostCookieWinsWhenMerging() {
    List<Cookie> cookies = WebviewCookieHandler.cookiesForUrl(WWW,
        "main[UTMPUSERID]=guest",
        "main[UTMPUSERID]=mozilla; main[UTMPKEY]=k");
    assertEquals(2, cookies.size());
    assertEquals("mozilla", cookies.get(0).value());
    assertTrue(WebviewCookieHandler.isNewsmthHost("wap.newsmth.net"));
    assertTrue(!WebviewCookieHandler.isNewsmthHost("example.com"));
  }
}
