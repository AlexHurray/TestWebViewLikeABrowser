package com.mks.testdebugactivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
            Log.e("!!!", str.substring(start, end));
        }
    }

    static class Wrapper {
        boolean first = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = new WebView(this);//findViewById(R.id.web_view);

        String url = getIntent().getStringExtra(KEY_URL);
        if (url == null) {
            //url = "https://yandex.ru/search/?lr=213&text=%D0%BF%D1%80%D0%BE%D0%B2%D0%B5%D1%80%D0%B8%D1%82%D1%8C%20referer%20%D0%BE%D0%BD%D0%BB%D0%B0%D0%B9%D0%BD";
            url = "http://amediateka.org/lp/4305";
            //url = "https://deleteme-479ee.firebaseapp.com/fingerprinttest/report.html?os=Android&version=4.4";
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        String ua = "Mozilla/5.0 (Android; Tablet; rv:20.0) Gecko/20.0 Firefox/20.0";

        settings.setUserAgentString(ua);
        final Wrapper wrapper = new Wrapper();

        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!wrapper.first) {
                    log("shouldOverrideUrlLoading URL = " + url);
                    return true;
                }

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                log("onPageStarted " + url);
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
//                if (wrapper.first) {
//                    webView.loadUrl("javascript:document.getElementById('ButtonSubmit').click()");
//                    wrapper.first = false;
//                } else {
//                    log("Sending request: URL = " + url);
//
//                    /*try {
//                        Thread.sleep(120000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    sendRequest(url);*/
//                }

                log("URL:" + url);
                String cookies = CookieManager.getInstance().getCookie(url);
                log("All the cookies in a string:" + cookies);

                sendRequest(url, cookies);
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });

        /*Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Referer", "http://elari.net");
        extraHeaders.put("X-Requested-With", "");*/

        webView.loadUrl(url);
    }

    public void sendRequest(final String link, final String cookies)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36";
                String COOKIE = "_ym_uid=1535118515170990367; supportOnlineTalkID=tpoUIJlRHKx1LiQbETfPSGQhEbhD5ZVl; _ym_d=1551075484";
                String ACCEPT = "text/html, application/xhtml+xml, */*";
                String REFER = "http://elari.net";
                String ACCEPT_LANGUAGE = "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7";
                String CACHE_CONTROL = "iCacheControl";
                String INSECURE_REQUEST = "1";

                System.setProperty("http.agent", USER_AGENT);
                System.setProperty("https.agent", USER_AGENT);
                URL url = null;
                try {

                    //url = new URL("http://moipodpiski.ssl.mts.ru/lp/?tmpl=AM_super-light_01_02_2019&SID=d6904936-65d2-11e9-81fe-c7572e48ec7c");
                    url = new URL(link);
                    HttpURLConnection conn =  getSimpleHttpsConnection(url);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Host", "moipodpiski.ssl.mts.ru");
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestProperty("Content-Length", "185");
                    conn.setRequestProperty("Cache-Control", "max-age=0");
                    conn.setRequestProperty("Origin", "http://moipodpiski.ssl.mts.ru");
                    conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                    conn.setRequestProperty("Referer", "http://moipodpiski.ssl.mts.ru/lp/?tmpl=AM_super-light_01_02_2019&SID=47f5f908-65cf-11e9-bcf8-53152e48ec7c");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
                    conn.setRequestProperty("Cookie", "mts_id=8a559f38-9ffe-40df-b261-28e8a130cf12; dspid=ac9e3cef-dc05-4797-ba38-819ccb29d88d; mts_id_last_sync=1554818669; _ga=GA1.4.250276592.1555931147; _gid=GA1.2.84715827.1555942781; IHLink=https://ihelper.mts.ru/selfcare/welcome.aspx; GeoRealRegionCookie=RU-MOW; _ga_92J299ZMRV=GS1.1.1555942977.1.0.1555942977.0; _ym_uid=1555942978384467020; _ym_d=1555942978; _ga=GA1.2.318557130.1555942781; sessionid-services=R1062265719; ASP.NET_SessionId=b3g1qmspefls4o5m5lgmhaxx; __RequestVerificationToken_L2xw0=90sgYmfjmIfup33y1i3oynDJPfcNXDTUYc3zSspoGTKUPX0zrG1zCRWLJCjpwojE5N7G6om6TUi6cDlVVKo1hkGxvbsZE4QHPScvr9tRSKc1");
                    //conn.setRequestProperty("Cookie", cookies);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write("CaptchaToken=&IAoC=True&HasMsisdnFromCookie=False&__RequestVerificationToken=dhjjNPDPsTdrNTWG7n3j8r8un2THN2OUpdVAwAOyiNRAuPE-TBjp6siTPoY4I8J__u1p_bcSTUoyfEVH6L7KUBR5m8Y0DpE5ivA9ZepOoi41");
                    osw.flush();
                    osw.close();
                    os.close();  //don't forget to close the OutputStream

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            /*String line;
            String res = "";

            while ((line = reader.readLine()) != null) {
                res += line + "\n";
            }*/

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType){}
            }
    };

    public HttpURLConnection getSimpleHttpsConnection(URL url) throws CertificateException, NoSuchAlgorithmException, IOException {
        HttpURLConnection connection = null;
        try {

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            //HttpURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            connection = (HttpURLConnection) url.openConnection();
            //connection.setHostnameVerifier(hostnameVerifier);

        } catch (Error e) {
            e.printStackTrace();
        }  catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
