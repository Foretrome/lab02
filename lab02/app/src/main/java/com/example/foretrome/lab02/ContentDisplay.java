package com.example.foretrome.lab02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ContentDisplay extends AppCompatActivity {

    String url;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_display);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }


    private class MyWebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
