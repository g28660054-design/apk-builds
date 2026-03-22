package com.example.myapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.EditText;

public class MainActivity extends Activity {
    WebView wv;
    ValueCallback<Uri[]> filePathCallback;
    static final int FILE_CHOOSER_CODE = 1;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        wv = new WebView(this); setContentView(wv);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
            View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true); ws.setDomStorageEnabled(true); ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(true); ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true); ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setMediaPlaybackRequiresUserGesture(false); ws.setBuiltInZoomControls(false);
        ws.setDisplayZoomControls(false); ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        wv.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, String url){ v.loadUrl(url); return true; }
            @Override public void onReceivedError(WebView v,int c,String d,String u){}
        });
        wv.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onJsAlert(WebView v,String url,String msg,final JsResult r){
                new AlertDialog.Builder(MainActivity.this).setMessage(msg).setCancelable(false)
                    .setPositiveButton("OK",(d,w)->r.confirm()).show(); return true;
            }
            @Override public boolean onJsConfirm(WebView v,String url,String msg,final JsResult r){
                new AlertDialog.Builder(MainActivity.this).setMessage(msg).setCancelable(false)
                    .setPositiveButton("OK",(d,w)->r.confirm()).setNegativeButton("Отмена",(d,w)->r.cancel()).show(); return true;
            }
            @Override public boolean onJsPrompt(WebView v,String url,String msg,String def,final JsPromptResult r){
                final EditText et=new EditText(MainActivity.this); et.setText(def);
                new AlertDialog.Builder(MainActivity.this).setMessage(msg).setView(et).setCancelable(false)
                    .setPositiveButton("OK",(d,w)->r.confirm(et.getText().toString())).setNegativeButton("Отмена",(d,w)->r.cancel()).show(); return true;
            }
            @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){
                filePathCallback=cb; startActivityForResult(Intent.createChooser(p.createIntent(),"Выбери файл"),FILE_CHOOSER_CODE); return true;
            }
        });
        if(s!=null) wv.restoreState(s);
        else wv.loadUrl("file:///android_asset/splash.html");
    }
    @Override protected void onActivityResult(int req,int res,Intent data){
        if(req==FILE_CHOOSER_CODE){Uri[] r=null;if(res==Activity.RESULT_OK&&data!=null)r=new Uri[]{data.getData()};if(filePathCallback!=null)filePathCallback.onReceiveValue(r);filePathCallback=null;}
    }
    @Override protected void onSaveInstanceState(Bundle o){super.onSaveInstanceState(o);wv.saveState(o);}
    @Override public void onBackPressed(){if(wv.canGoBack())wv.goBack();else super.onBackPressed();}
}