package com.mks.testdebugactivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String KEY_URL = "KEY_URL";

    public static void start(@NonNull Context context, String url) {
        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.putExtra(KEY_URL, url);
        context.startActivity(startIntent);
    }

    WebView webView;

    private void log(String str) {
        int maxLogSize = 1000;
        for(int i = 0; i <= str.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > str.length() ? str.length() : end;
            Log.e("111", str.substring(start, end));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.web_view);//new WebView(this);//findViewById(R.id.web_view);

        String url = getIntent().getStringExtra(KEY_URL);
        if (url == null) {
            //url = "https://yandex.ru/search/?lr=213&text=%D0%BF%D1%80%D0%BE%D0%B2%D0%B5%D1%80%D0%B8%D1%82%D1%8C%20referer%20%D0%BE%D0%BD%D0%BB%D0%B0%D0%B9%D0%BD";
            url = "http://am.mks.group/api_test";
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        String ua = "Mozilla/5.0 (Android; Tablet; rv:20.0) Gecko/20.0 Firefox/20.0";

        settings.setUserAgentString(ua);

        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
               // webView.loadUrl("javascript:document.getElementsByTagName('li')[2].getElementsByTagName('h2')[0].click()");

                /*Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Referer", "http://elari.net");*/

                //webView.loadUrl("javascript:document.querySelector(\"a[href='http://whatsmyreferer.com/']\").click()", extraHeaders);
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Referer", "http://elari.net");
        extraHeaders.put("X-Requested-With", "");

        //webView.loadUrl(url);
        webView.loadUrl(url, extraHeaders);
    }
}
