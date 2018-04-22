package com.tcn.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.tcn.fragment.AddFragment;
import com.tcn.fragment.LearnNowFragment;
import com.tcn.fragment.TopicFragment;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.Language;
import com.tcn.handle.MyAction;
import com.tcn.handle.ServerAPI;
import com.tcn.handle.Users;
import com.tcn.models.NoteModels;
import com.tcn.models.TopicModels;

public class LearnActivity extends AppCompatActivity {

    public static String TAG = "LearnActivity";
    public static String MY_BRC_LEARN_ACTIVITY = "MY_BRC_LEARN_ACTIVITY";
    public SQLiteDatabase database;
    private Toolbar toolbar;
    private LinearLayout fragmentLearn, fragmentAdd;
    public ArrayList<TopicModels> topicModes;
    public ArrayList<NoteModels> noteModels;
    public Language language;
    public ServerAPI serverAPI;
    public Handle handle;
    public int pst;
    public int id = -1;
    private int open = 1;
    private String thisLanguage = "";
    public boolean openedLearn = false; //openedLearn == false: First open this screen
    public boolean openedAdd = false; //openedAdd == false: First open this screen
    public boolean topicLoaded = false; //topicLoaded == false: Topic list not loaded
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        addControls();
        addEvents();

    }
    private void loadAdFull(String ad_id){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ad_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    private void addEvents() {

    }

    private void addControls() {
        topicModes = new ArrayList<>();
        serverAPI = new ServerAPI();
        handle = new Handle();
        language = new Language();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fragmentLearn = findViewById(R.id.fragmentLearn);
        fragmentAdd = findViewById(R.id.fragmentAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
    }

    private void handleGET(){

        if (!thisLanguage.equalsIgnoreCase(language.getLanguage(this))){
            thisLanguage = language.getLanguage(this);
            openedAdd = false;
            openedLearn = false;
        }
        if (MyAction.getRefreshTopic(this)){
            openedAdd = false;
            openedLearn = false;
            topicLoaded = false;
            MyAction.setRefreshTopic(this,false);
        }
        loadAdFull(getString(R.string.ad_id_full_1));
        pst = MyAction.getPosition(this);
        Log.d("pst",pst+"");
        open = MyAction.getFragmentNew(this);
        //Get the topic name from the server
        if(!topicLoaded){
            handleGetNameTopic();

        }else if (openedLearn && open == MyAction.LEARN_FRAGMENT){
            Log.d("LearnActivity","fragmentLearn");
            fragmentAdd.setVisibility(View.GONE);
            fragmentLearn.setVisibility(View.VISIBLE);

        }else if (openedAdd && open == MyAction.ADD_NEW_WORD_FRAGMENT){
            Log.d("LearnActivity","fragmentAdd");
            fragmentAdd.setVisibility(View.VISIBLE);
            fragmentLearn.setVisibility(View.GONE);
        }else {
            handleOpenFragment();
        }

    }

    //Get the topic name from the server
    private void handleGetNameTopic() {
        topicLoaded = true;
        myIntentFilter();

        Users users = new Users(this);
        JSONObject object = new JSONObject();
        try {
            object.put("idUser", users.getIdUser());
            serverAPI.getTopic(this, object, Constants.GET_NAME_TOPIC);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleOpenFragment(){
        fragmentLearn.setVisibility(View.GONE);
        fragmentAdd.setVisibility(View.GONE);
        if (open == MyAction.LEARN_FRAGMENT){
            fragmentLearn.setVisibility(View.VISIBLE);
            openedLearn = true;
            callFragment(R.id.fragmentLearn, new  LearnNowFragment());
        }else if (open == MyAction.ADD_NEW_WORD_FRAGMENT){
            fragmentAdd.setVisibility(View.VISIBLE);
            openedAdd = true;
            callFragment(R.id.fragmentAdd, new  AddFragment());
        }
    }

    public void callFragment(int idFM, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(idFM, fragment);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_LEARN_ACTIVITY);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_LEARN_ACTIVITY)) {
                if (intent.getSerializableExtra("TOPIC") != null){
                    topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                    if (topicModes.size() > 0){
                        handleOpenFragment();
                    }else {
                        // If not topic
                        topicLoaded = false;
                        new AlertDialog.Builder(LearnActivity.this)
                                .setMessage(getString(R.string.youHaveNoTopic))
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //close learn now
                                        if ((open == MyAction.LEARN_FRAGMENT))
                                            finish();
                                        else callFragment(R.id.fragmentAdd, new AddFragment());
                                    }
                                })
                                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //add topic && close learn now
                                        MyAction.setFragmentNew(LearnActivity.this, MyAction.ADD_TOPIC_FRAGMENT);
                                        IntentActivity.handleOpenTopicActivy(LearnActivity.this);
                                        finish();

                                    }
                                }).show();
                    }

                    Handle.unregisterReceiver(context, mReceiver);

                }
            }
        }
    };
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart\nopenedLearn: "+openedLearn+"\nopenedAdd: "+openedAdd);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume\nopenedLearn: "+openedLearn+"\nopenedAdd: "+openedAdd);
        handleGET();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause\nopenedLearn: "+openedLearn+"\nopenedAdd: "+openedAdd);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            loadAdFull(getString(R.string.ad_id_full_2));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop:\nopenedLearn: "+openedLearn+"\nopenedAdd: "+openedAdd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy\nopenedLearn: "+openedLearn+"\nopenedAdd: "+openedAdd);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        switch (MyAction.getActivityBulb(this)){
            case MyAction.LIST_ACTIVITY:
                MyAction.setActivityBulb(this, MyAction.LEARN_ACTIVITY);
                IntentActivity.handleOpenListActivity(this);
                break;
            case MyAction.TOPIC_ACTIVITY:
                MyAction.setFragmentNew(this, MyAction.NOTE_FRAGMENT);
                IntentActivity.handleOpenTopicActivy(this);
                break;
            default:{
                MyAction.setActivityBulb(this, MyAction.LEARN_ACTIVITY);
                IntentActivity.handleOpenListActivity(this);
                break;
            }
        }

    }

}
