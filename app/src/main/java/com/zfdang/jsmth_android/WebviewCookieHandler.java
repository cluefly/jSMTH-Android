package com.zfdang.jsmth_android;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Shares the WebView cookie store with OkHttp.
 *
 * newsmth identifies the session with {@code main[UTMPUSERID]} (aSM reads the
 * mobile home page; the 2026 nForum workaround reads this cookie when
 * ajax_session.json is 403 or 500). Cookies set on m/wap are also attached to
 * www requests, because a host-only cookie would not be sent to nForum.
 * Pairs are built with {@link Cookie.Builder} so a name OkHttp's parser skips
 * does not drop the rest of the jar.
 */
public final class WebviewCookieHandler implements CookieJar {
    static final String USER_ID_COOKIE = "main[UTMPUSERID]";
    private static final String[] SESSION_COOKIE_NAMES = {
            USER_ID_COOKIE, "main[UTMPKEY]", "main[UTMPNUM]", "main[XWJOKE]"
    };
    private static final String[] NEWSMTH_URLS = {
            "https://www.newsmth.net/",
            "https://m.newsmth.net/",
            "https://wap.newsmth.net/",
            "https://newsmth.net/"
    };

    private final CookieManager webviewCookieManager = CookieManager.getInstance();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        String urlString = url.toString();
        for (Cookie cookie : cookies) {
            webviewCookieManager.setCookie(urlString, cookie.toString());
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (isNewsmthHost(url.host())) {
            String[] headers = new String[NEWSMTH_URLS.length + 1];
            for (int i = 0; i < NEWSMTH_URLS.length; i++) {
                headers[i] = webviewCookieManager.getCookie(NEWSMTH_URLS[i]);
            }
            headers[NEWSMTH_URLS.length] = webviewCookieManager.getCookie(url.toString());
            return cookiesForUrl(url, headers);
        }
        return cookiesForUrl(url, webviewCookieManager.getCookie(url.toString()));
    }

    /** Logged-in id from the WebView cookie store, or null when absent or guest. */
    public static String readLoginUserId() {
        CookieManager manager = CookieManager.getInstance();
        Map<String, String> pairs = new LinkedHashMap<>();
        for (String url : NEWSMTH_URLS) {
            pairs.putAll(parseCookiePairs(manager.getCookie(url)));
        }
        return userIdFromPairs(pairs);
    }

    /**
     * Copies whatever session cookies the WebView stored onto every newsmth host.
     * A host-only cookie set on m/wap would otherwise be invisible to www.
     */
    public static void shareLoginCookiesAcrossHosts() {
        CookieManager manager = CookieManager.getInstance();
        Map<String, String> pairs = new LinkedHashMap<>();
        for (String url : NEWSMTH_URLS) {
            pairs.putAll(parseCookiePairs(manager.getCookie(url)));
        }
        if (pairs.isEmpty()) {
            manager.flush();
            return;
        }
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String setCookie = entry.getKey() + "=" + entry.getValue() + "; Domain=.newsmth.net; Path=/";
            for (String url : NEWSMTH_URLS) {
                manager.setCookie(url, setCookie);
            }
        }
        manager.flush();
    }

    public static void clearLoginCookies() {
        CookieManager manager = CookieManager.getInstance();
        for (String url : NEWSMTH_URLS) {
            for (String name : SESSION_COOKIE_NAMES) {
                manager.setCookie(url, name + "=; Max-Age=0; Path=/; Domain=.newsmth.net");
                manager.setCookie(url, name + "=; Max-Age=0; Path=/");
            }
        }
        manager.flush();
    }

    static boolean isNewsmthHost(String host) {
        if (host == null) {
            return false;
        }
        String normalized = host.toLowerCase(Locale.US);
        return normalized.equals("newsmth.net") || normalized.endsWith(".newsmth.net")
                || normalized.equals("mysmth.net") || normalized.endsWith(".mysmth.net");
    }

    static Map<String, String> parseCookiePairs(String header) {
        Map<String, String> pairs = new LinkedHashMap<>();
        if (header == null || header.isEmpty()) {
            return pairs;
        }
        for (String part : header.split(";")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int eq = trimmed.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String name = trimmed.substring(0, eq).trim();
            String value = trimmed.substring(eq + 1).trim();
            if (!name.isEmpty()) {
                pairs.put(name, value);
            }
        }
        return pairs;
    }

    static String userIdFromPairs(Map<String, String> pairs) {
        if (pairs == null) {
            return null;
        }
        String value = pairs.get(USER_ID_COOKIE);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty() || "guest".equalsIgnoreCase(value)) {
            return null;
        }
        if (value.indexOf(' ') >= 0 || value.indexOf(';') >= 0 || value.indexOf('\n') >= 0
                || value.indexOf('\r') >= 0) {
            return null;
        }
        return value;
    }

    static List<Cookie> cookiesForUrl(HttpUrl url, String... headers) {
        if (url == null) {
            return Collections.emptyList();
        }
        Map<String, String> pairs = new LinkedHashMap<>();
        if (headers != null) {
            for (String header : headers) {
                pairs.putAll(parseCookiePairs(header));
            }
        }
        if (pairs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Cookie> cookies = new ArrayList<>(pairs.size());
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            try {
                cookies.add(new Cookie.Builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .domain(url.host())
                        .path("/")
                        .build());
            } catch (IllegalArgumentException ignored) {
                // Skip a pair OkHttp still refuses. Do not drop the rest of the jar.
            }
        }
        return cookies;
    }
}
