package com.tcn.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.Language;
import com.tcn.handle.MyAction;
import com.tcn.handle.ServerAPI;
import com.tcn.handle.Users;

public class SplashActivity extends AppCompatActivity {

    private static long SPLASH_DISPLAY_LENGTH = 10000;
    public static final String BROADCAST_ACTION_SPLASH = "BROADCAST_ACTION_SPLASH";

    private Toolbar toolbar;
    private String versionName;
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setLanguage();
        versionName = BuildConfig.VERSION_NAME;
        mTimer= new Timer();
        new Handle().deleteCache(this);
        myIntentFilter();
        //check version
        try {
            new ServerAPI().checkUpdateVersion(this,
                    new JSONObject().put("idUser",
                            new Users(this).getIdUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder("fHL0lV27gZE:APA91bGvSuVx2bgkZwHZ5gbEfNkREAOlIsoQziHkubJ5akY44anDrIfDqP642oDOgT94aVYvar02Xu2ZnrdvJaMR9-Oo_OlbR6Ygy2DoYYnUax6nP6auyx4TG43Q01mKv4UkuKJ8YpJH" + "@gcm.googleapis.com")
                        .setMessageId("foLAvvrHHKo:APA91bF_MOCqJyIbbdcAtgnuOWA2tOQ3S5Dw9ulEVVrPa4D910o2nk1Q5vS6rHDTTgKBvzRHr_UkZBXKvhLX4b2KbYvmsa9L8UanPdvSAIviFCI9N-oHzaDKVNaq47dwzbMevRBLD4F3")
                        .addData("123456", "Hello!")
                        .build());

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

    private void setLanguage() {
        Language language = new Language(this);
        language.setLanguageDivice(Locale.getDefault().getLanguage());
        language.settingLanguage(this);
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
                            //There is a new version
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
