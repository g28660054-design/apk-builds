package com.buildenapk.apkbuilder;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    WebView wv;
    ValueCallback<Uri[]> filePathCallback;
    static final int FILE_CHOOSER_CODE = 1;
    static final int PERM_CODE = 2;

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

        // File download handler — opens native Save dialog
        wv.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try {
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                req.setMimeType(mimeType);
                req.addRequestHeader("User-Agent", userAgent);
                req.setDescription("Скачивание файла...");
                req.setTitle(fileName);
                req.allowScanningByMediaScanner();
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(req);
                Toast.makeText(this, "Скачивается: " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Fallback: open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        wv.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, String url){
                if(url.startsWith("tel:")){ startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse(url))); return true; }
                if(url.startsWith("mailto:")){ startActivity(new Intent(Intent.ACTION_SENDTO,Uri.parse(url))); return true; }
                if(url.startsWith("geo:")){ startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); return true; }
                v.loadUrl(url); return true;
            }
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
            @Override public void onPermissionRequest(PermissionRequest request){
                request.grant(request.getResources());
            }
        });
        requestRuntimePerms();
        if(s!=null) wv.restoreState(s);
        else wv.loadUrl("file:///android_asset/splash.html");
    }

    void requestRuntimePerms(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) return;
        List<String> need=new ArrayList<>();
        String[] perms={
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CALL_PHONE
        };
        for(String p:perms){
            try{ if(checkSelfPermission(p)!=PackageManager.PERMISSION_GRANTED) need.add(p); }catch(Exception ignored){}
        }
        if(!need.isEmpty()) requestPermissions(need.toArray(new String[0]),PERM_CODE);
    }

    @Override protected void onActivityResult(int req,int res,Intent data){
        if(req==FILE_CHOOSER_CODE){Uri[] r=null;if(res==Activity.RESULT_OK&&data!=null)r=new Uri[]{data.getData()};if(filePathCallback!=null)filePathCallback.onReceiveValue(r);filePathCallback=null;}
    }
    @Override protected void onSaveInstanceState(Bundle o){super.onSaveInstanceState(o);wv.saveState(o);}
    @Override public void onBackPressed(){if(wv.canGoBack())wv.goBack();else super.onBackPressed();}
}