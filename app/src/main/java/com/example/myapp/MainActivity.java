package com.example.myapp;
import android.app.Activity;import android.os.Bundle;
import android.view.View;import android.view.Window;import android.view.WindowManager;
import android.webkit.*;
public class MainActivity extends Activity {
    WebView wv;
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        wv=new WebView(this);setContentView(wv);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
            View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        WebSettings ws=wv.getSettings();
        ws.setJavaScriptEnabled(true);ws.setDomStorageEnabled(true);ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(true);ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setMediaPlaybackRequiresUserGesture(false);ws.setBuiltInZoomControls(false);ws.setDisplayZoomControls(false);
        wv.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v,String url){
                return!(url.startsWith("file://")||url.startsWith("about:"));}});
        wv.setWebChromeClient(new WebChromeClient());
        if(s!=null)wv.restoreState(s);else wv.loadUrl("file:///android_asset/index.html");
    }
    @Override protected void onSaveInstanceState(Bundle o){super.onSaveInstanceState(o);wv.saveState(o);}
    @Override public void onBackPressed(){if(wv.canGoBack())wv.goBack();else super.onBackPressed();}
}