package com.tcn.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.Language;
import com.tcn.handle.MyAction;
import com.tcn.handle.ServerAPI;
import com.tcn.handle.Users;
import com.tcn.handle.UsersFB;
import com.tcn.models.TopicModels;

public class SplashActivity extends AppCompatActivity {

    private static long SPLASH_DISPLAY_LENGTH = 10000;
    public static final String BROADCAST_ACTION_SPLASH = "BROADCAST_ACTION_SPLASH";
    private Language language;
    private Toolbar toolbar;
    private String versionName;
    private ServerAPI serverAPI;
    private Handle handle;
    private Timer mTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        language = new Language(SplashActivity.this);
        language.setLanguageDivice(Locale.getDefault().getLanguage());
        versionName = BuildConfig.VERSION_NAME;
        serverAPI = new ServerAPI();
        handle = new Handle();
        mTimer= new Timer();
        handle.deleteCache(this);
        myIntentFilter();
        Users users = new Users(this);
        JSONObject object = new JSONObject();
        try {
            object.put("idUser",users.getIdUser());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverAPI.checkUpdateVersion(SplashActivity.this, object);
        MyAction.setAction(this,true);
        MyAction.setRefreshTopic(this, true);
        MyAction.setLoadedTopic(this, false);
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                openListActivity();
//            }
//        }, SPLASH_DISPLAY_LENGTH);

        openTimer();
    }

    private void openTimer() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openListActivity();
                    }
                });
            }
        },SPLASH_DISPLAY_LENGTH,1000);
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION_SPLASH);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_ACTION_SPLASH)) {
                try {
                    if (intent.getStringExtra("VERSION")!=null){
                        String newVersion = intent.getStringExtra("VERSION");
                        final String url = intent.getStringExtra("URL");

                        if (newVersion.compareTo(versionName) > 0){
                            SPLASH_DISPLAY_LENGTH *=2;
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle(getString(R.string.detectNewVersion))
                                    .setMessage(getString(R.string.currentVersion) + ": " + versionName + "\n"
                                                + getString(R.string.newVersion) + ": " + newVersion)
                                    .setNegativeButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent op = new Intent(Intent.ACTION_VIEW);
                                            op.setData(Uri.parse(url));
                                            startActivity(op);

                                        }
                                    })
                                    .setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            openListActivity();
                                        }
                                    }).show();
                        }else{
                            openListActivity();
                        }
                    }else {
                        openListActivity();
                    }

                }catch (Exception e){
                    openListActivity();
                    e.printStackTrace();
                }
            }else {
                openListActivity();
            }
        }
    };

    private void openListActivity(){
        Handle.unregisterReceiver(this, mReceiver);
        mTimer.cancel();
        IntentActivity.handleOpenListActivity(this);
        finish();
    }
}
