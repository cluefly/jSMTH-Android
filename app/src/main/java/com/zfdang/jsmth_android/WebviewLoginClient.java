package com.zfdang.jsmth_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

// login chains:
// http://m.newsmth.net/index
//   ==> POST: https://m.newsmth.net/user/login
//     ==> 302 location: http://m.newsmth.net/index?m=0108
public class WebviewLoginClient extends WebViewClient {

    private static final String TAG = "WebviewLoginClient";
    private final String username;
    private final String password;

    Activity activity;
    private boolean finished;

    public WebviewLoginClient(Activity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (request != null && request.getUrl() != null) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        Log.d(TAG, "shouldOverrideUrlLoading" + url);
        if (isLoginReturnUrl(url) || loggedInAsSubmittedUser()) {
            finishLogin();
        }
        return false;
    }

    // m.newsmth.net now redirects to wap.newsmth.net. Success is either the historical
    // index?m= bounce or a session cookie for the account just submitted.
    private static boolean isLoginReturnUrl(String url) {
        if (url == null) {
            return false;
        }
        String normalized = url.toLowerCase(Locale.US);
        return (normalized.startsWith("https://") || normalized.startsWith("http://"))
                && (normalized.contains("://m.newsmth.net/index?m=")
                || normalized.contains("://wap.newsmth.net/index?m=")
                || normalized.contains("://www.newsmth.net/index?m="));
    }

    private boolean loggedInAsSubmittedUser() {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.equals(WebviewCookieHandler.readLoginUserId());
    }

    private void finishLogin() {
        if (finished || activity == null || activity.isFinishing()) {
            return;
        }
        finished = true;
        // Copy m/wap cookies onto .newsmth.net before OkHttp reads them.
        WebviewCookieHandler.shareLoginCookiesAcrossHosts();
        Intent resultIntent = new Intent();
        String id = WebviewCookieHandler.readLoginUserId();
        if (id != null) {
            resultIntent.putExtra(LoginActivity.USERNAME, id);
        } else if (username != null && !username.isEmpty()) {
            resultIntent.putExtra(LoginActivity.USERNAME, username);
        }
        activity.setResult(Activity.RESULT_OK, resultIntent);
        activity.finish();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if(url.contains("ads")) {
            return new WebResourceResponse("text/javascript", "UTF-8", null);
        }
        return null;
    }

    public void onPageFinished(WebView view, String url) {
//        Log.d(TAG, "onPageFinished" + url);
        if (isLoginReturnUrl(url) || loggedInAsSubmittedUser()) {
            finishLogin();
            return;
        }
        if (url != null && (url.contains("m.newsmth.net") || url.contains("wap.newsmth.net"))) {
            // login page, input id and passwd automatically
            String safeUser = org.json.JSONObject.quote(this.username != null ? this.username : "");
            String safePass = org.json.JSONObject.quote(this.password != null ? this.password : "");
            final String js = "javascript: " +
                    "var ids = document.getElementsByName('id');" +
                    "if (ids.length > 0) ids[0].value = " + safeUser + ";" +
                    "var passwds = document.getElementsByName('passwd');" +
                    "if (passwds.length > 0) passwds[0].value = " + safePass + ";" +
                    "var cap = document.getElementById('TencentCaptcha');" +
                    "if (cap) cap.click();";

            if (Build.VERSION.SDK_INT >= 19) {
                view.evaluateJavascript(js, s -> { });
            } else {
                view.loadUrl(js);
            }
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "WebView error: " + error.getDescription() + " for URL: " + request.getUrl());
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG, "WebView error: " + description + " (code: " + errorCode + ") for URL: " + failingUrl);
    }

    // Handle SSL errors: some devices / WebView versions may fail TLS handshake for certain cert chains.
    // For better UX, allow proceeding for our known host (m.newsmth.net) while keeping other hosts safe.
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        try {
            String host = (error != null && error.getUrl() != null) ? error.getUrl() : null;
            if (host == null && view != null && view.getUrl() != null) {
                host = view.getUrl();
            }
            Log.e(TAG, "onReceivedSslError for host: " + host + ", primaryError=" + (error != null ? error.getPrimaryError() : "null"));
            // Be conservative: only proceed automatically for our own domain to avoid security issues.
            if (host != null && host.contains("newsmth.net")) {
                handler.proceed();
                Log.w(TAG, "Proceeding SSL error for trusted host: " + host);
            } else {
                handler.cancel();
                Log.w(TAG, "Cancelled SSL for host: " + host);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onReceivedSslError", e);
            try {
                handler.cancel();
            } catch (Exception ignored) {}
        }
    }
}
