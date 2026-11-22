package com.vasuki.myapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity {
  private WebView web;
  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.parseColor("#0f5c3a"));
    FrameLayout root = new FrameLayout(this);
    web = new WebView(this);
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient());
    root.addView(web);
    final ImageView splash = new ImageView(this);
    splash.setBackgroundColor(Color.parseColor("#ffff00"));
    splash.setImageResource(R.drawable.splash);
    splash.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    root.addView(splash, new FrameLayout.LayoutParams(-1, -1));
    setContentView(root);
    web.loadUrl("file:///android_asset/www/index.html");
    new Handler().postDelayed(new Runnable() { public void run() { splash.setVisibility(View.GONE); } }, 2000);
  }
  @Override public void onBackPressed() {
    if (web.canGoBack()) web.goBack(); else super.onBackPressed();
  }
}
