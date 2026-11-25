package com.vasuki.apppppp;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
  private static final int REQ_FILE = 1001;
  private static final int REQ_CAM = 1002;
  private static final int REQ_PERM = 1003;
  private static final boolean CAM = false;
  private static final boolean MIC = false;
  private static final boolean FILES = false;
  private static final boolean LOC = false;
  private static final boolean NOTIF = false;

  private WebView web;
  private ValueCallback<Uri[]> fileCb;
  private Uri camUri;

  private List<String> needed() {
    List<String> l = new ArrayList<String>();
    if (CAM) {
      l.add(android.Manifest.permission.CAMERA);
      if (Build.VERSION.SDK_INT <= 28) l.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if (MIC) l.add(android.Manifest.permission.RECORD_AUDIO);
    if (LOC) {
      l.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
      l.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    if (NOTIF && Build.VERSION.SDK_INT >= 33) l.add("android.permission.POST_NOTIFICATIONS");
    if (FILES) {
      if (Build.VERSION.SDK_INT >= 33) {
        l.add("android.permission.READ_MEDIA_IMAGES");
        l.add("android.permission.READ_MEDIA_VIDEO");
      } else if (Build.VERSION.SDK_INT <= 28) {
        l.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        l.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
      } else {
        l.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
      }
    }
    return l;
  }

  private void askPerms() {
    List<String> need = new ArrayList<String>();
    for (String p : needed()) {
      if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) need.add(p);
    }
    if (!need.isEmpty()) requestPermissions(need.toArray(new String[0]), REQ_PERM);
  }

  private void saveDownload(String url, String cd, String mime) {
    try {
      if (url == null || url.startsWith("blob:") || url.startsWith("data:")) {
        Toast.makeText(this, "This file is made inside the page and cannot be saved by the app. Use a web link instead.", Toast.LENGTH_LONG).show();
        return;
      }
      String name = URLUtil.guessFileName(url, cd, mime);
      DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
      DownloadManager.Request rq = new DownloadManager.Request(Uri.parse(url));
      rq.setTitle(name);
      rq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
      rq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
      dm.enqueue(rq);
      Toast.makeText(this, "Downloading " + name, Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Toast.makeText(this, "Could not start the download.", Toast.LENGTH_SHORT).show();
    }
  }

  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.parseColor("#0f5c3a"));
    FrameLayout root = new FrameLayout(this);
    web = new WebView(this);
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    s.setAllowContentAccess(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    s.setGeolocationEnabled(false);
    s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
        if (fileCb != null) fileCb.onReceiveValue(null);
        fileCb = cb;
        boolean snap = CAM && p != null && p.isCaptureEnabled();
        if (snap) {
          try {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "vasuki_" + System.currentTimeMillis() + ".jpg");
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            camUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ci.putExtra(MediaStore.EXTRA_OUTPUT, camUri);
            startActivityForResult(ci, REQ_CAM);
            return true;
          } catch (Exception e) {
            camUri = null;
          }
        }
        Intent get = new Intent(Intent.ACTION_GET_CONTENT);
        get.addCategory(Intent.CATEGORY_OPENABLE);
        get.setType("*/*");
        if (p != null && p.getAcceptTypes() != null && p.getAcceptTypes().length > 0) {
          String[] acc = p.getAcceptTypes();
          if (acc.length == 1) {
            String a = acc[0];
            if (a.startsWith(".")) get.setType("*/*"); else get.setType(a);
          } else {
            get.putExtra(Intent.EXTRA_MIME_TYPES, acc);
          }
        }
        if (p != null && p.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) get.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
          startActivityForResult(Intent.createChooser(get, "Select file"), REQ_FILE);
        } catch (Exception e) {
          if (fileCb != null) fileCb.onReceiveValue(null);
          fileCb = null;
          Toast.makeText(MainActivity.this, "No file picker found on this phone.", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
      @Override public void onPermissionRequest(final PermissionRequest r) {
        runOnUiThread(new Runnable() { public void run() {
          List<String> ok = new ArrayList<String>();
          for (String res : r.getResources()) {
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(res) && CAM) ok.add(res);
            else if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(res) && MIC) ok.add(res);
          }
          if (ok.isEmpty()) r.deny(); else r.grant(ok.toArray(new String[0]));
        }});
      }
      @Override public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback cb) {
        cb.invoke(origin, LOC, false);
      }
    });
    web.setDownloadListener(new DownloadListener() {
      public void onDownloadStart(String url, String ua, String cd, String mime, long len) {
        saveDownload(url, cd, mime);
      }
    });
    root.addView(web);
    final ImageView splash = new ImageView(this);
    splash.setBackgroundColor(Color.parseColor("#ffffff"));
    splash.setImageResource(R.drawable.splash);
    splash.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    root.addView(splash, new FrameLayout.LayoutParams(-1, -1));
    setContentView(root);
    web.loadUrl("file:///android_asset/www/index.html");
    askPerms();
    new Handler().postDelayed(new Runnable() { public void run() { splash.setVisibility(View.GONE); } }, 2000);
  }

  @Override protected void onActivityResult(int req, int res, Intent data) {
    if (req == REQ_FILE || req == REQ_CAM) {
      Uri[] out = null;
      if (res == RESULT_OK) {
        if (req == REQ_CAM) {
          if (camUri != null) out = new Uri[]{camUri};
        } else if (data != null) {
          if (data.getClipData() != null) {
            int n = data.getClipData().getItemCount();
            out = new Uri[n];
            for (int i = 0; i < n; i++) out[i] = data.getClipData().getItemAt(i).getUri();
          } else if (data.getData() != null) {
            out = new Uri[]{data.getData()};
          }
        }
      }
      if (fileCb != null) { fileCb.onReceiveValue(out); fileCb = null; }
      camUri = null;
      return;
    }
    super.onActivityResult(req, res, data);
  }

  @Override public void onBackPressed() {
    if (web.canGoBack()) web.goBack(); else super.onBackPressed();
  }
}
