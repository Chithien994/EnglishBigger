package com.tcn.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.tcn.fragment.AddAndEditTopicFragment;
import com.tcn.fragment.TopicFragment;
import com.tcn.fragment.WhatDoPeopleLearnFragment;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.handle.ServerAPI;
import com.tcn.handle.Users;
import com.tcn.handle.UsersFB;
import com.tcn.models.TopicModels;

public class TopicActivity extends AppCompatActivity {
    private Toolbar toolbarTopic;
    public ArrayList<TopicModels> topicModes;
    public ArrayList<TopicModels> topicFriendsModes;
    public ArrayList<TopicModels> topicYourModes;
    public LinearLayout layoutFragmentTopic;
    public LinearLayout layoutFragmentWhatDoPeopleLearn;
    public Fragment fragmentBack;
    public boolean cfFinish; //cfFinish = true: Closes the current activity when the back button is pressed
    public View view;

    public static final String broadcastAction = "BroadcastAction_TOPIC";

    public ServerAPI serverAPI;
    public UsersFB usersFB;
    public Handle handle;
    public int type;
    public int id;//id topic need edit
    public int size; //Saves the number of words of a selected topic
    public int sizeNow; //Saves the number of words of a selected topic
    public int position = -1; //The updated image position will be assigned to it
    public boolean cfRefresh = true;
    public boolean loadedTopic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TOPIC_ACTIVITY","Action: onCreate");
        setContentView(R.layout.activity_topic);
        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    public void saveFragmen(Fragment fragment){
        cfFinish = false;
        fragmentBack = fragment;
    }

    private void addControls() {
        size = -1;
        toolbarTopic = (Toolbar) findViewById(R.id.toolbarTopic);
        setSupportActionBar(toolbarTopic);
        layoutFragmentWhatDoPeopleLearn = findViewById(R.id.layoutFragmentWhatDoPeopleLearn);
        layoutFragmentTopic = findViewById(R.id.layoutFragmentTopic);
        topicModes = new ArrayList<>();
        topicYourModes = new ArrayList<>();
        topicFriendsModes = new ArrayList<>();
        serverAPI = new ServerAPI();
        usersFB = new UsersFB(TopicActivity.this);
        handle = new Handle();
        position = getIntent().getIntExtra("position",-1);
        handleGET();
    }

    private void handleGET(){
        if (!loadedTopic){
            cfRefresh = true;
        }
        if (cfRefresh){
            MyAction.setActivityBulb(this, MyAction.LIST_ACTIVITY);
            cfRefresh = false;
            //type = 3 Open a topic section
            //type = 4 Open a add section
            //type = 5 Open a edit section
            //type = 7 Open a WhatDoPeopleLearn
            //type = 9 Open a friend's topic
            type = MyAction.getFragmentNew(this);
            if (!loadedTopic && type!=MyAction.ADD_TOPIC_FRAGMENT && type!=MyAction.EDIT_TOPIC_FRAGMENT){
                loadingTopic();
            }else{
                handleOpenFragment();
            }

        }
    }

    public void loadingTopic() {
        loadedTopic = true;
        myIntentFilter();
        Users users = new Users(TopicActivity.this);
        JSONObject object = new JSONObject();
        String idUser = users.getIdUser();
        try {
            object.put("idUser", idUser);
            serverAPI.getTopic(this, object, Constants.GET_TOPIC);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showFragmentTop(){
        layoutFragmentTopic.setVisibility(View.VISIBLE);
        layoutFragmentWhatDoPeopleLearn.setVisibility(View.GONE);
    }

    public void showFragmentBottom(){
        MyAction.setFragmentBulb(this, MyAction.getFragmentNew(this));
        MyAction.setFragmentNew(this, MyAction.getFragmentBulb(this));
        cfFinish = true;
        layoutFragmentTopic.setVisibility(View.GONE);
        layoutFragmentWhatDoPeopleLearn.setVisibility(View.VISIBLE);
    }

    public void callFragment(Fragment fragment) {
        showFragmentTop();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.layoutFragmentTopic, fragment);
        transaction.commit();
        manager.executePendingTransactions();
    }
    public void callFragment(Fragment fragment, int rs) {
        showFragmentBottom();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(rs, fragment);
        transaction.commit();
        manager.executePendingTransactions();
    }
    public void callFragment(Fragment fragment, String type, int id) {
        showFragmentTop();
        Bundle bundle = new Bundle();
        bundle.putString("SELECT", type);
        bundle.putInt("ID", id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.layoutFragmentTopic, fragment);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(broadcastAction);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(broadcastAction)) {
                try {
                    topicYourModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                    unregisterReceiver(mReceiver);
                    Log.d("unregisterReceiver","Unregister Receiver");
                    handleOpenFragment();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private void handleOpenFragment(){
        switch (type){
            case MyAction.TOPIC_FRAGMENT:
                callFragment(new TopicFragment());
                break;
            case MyAction.ADD_TOPIC_FRAGMENT:
                callFragment(new AddAndEditTopicFragment(), "add", 0);
                break;
            case MyAction.EDIT_TOPIC_FRAGMENT:
                callFragment(new AddAndEditTopicFragment(), "edit", id);
                break;
            case MyAction.WDPL_FRAGMENT:
            case MyAction.TOPIC_FRIEND_FRAGMENT:
                callFragment(new WhatDoPeopleLearnFragment(), R.id.layoutFragmentWhatDoPeopleLearn);
                break;
            default:{
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleGET();
        Log.d("TOPIC_ACTIVITY","Action: onResume");
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //back
            if (type==MyAction.NOTE_FRAGMENT || type == MyAction.ADD_TOPIC_FRAGMENT || type==MyAction.EDIT_TOPIC_FRAGMENT){
                MyAction.setFragmentNew(this, MyAction.TOPIC_FRAGMENT);
            }
            
            if(cfFinish){
                onBackPressed();
            }else {
                if (size != sizeNow && size != -1){
                    cfRefresh = true;
                    loadedTopic = false;
                    IntentActivity.handleOpenTopicActivy(this);
                }else {
                    if(type==MyAction.WDPL_FRAGMENT || type==MyAction.TOPIC_FRIEND_FRAGMENT) showFragmentBottom();
                    else callFragment(fragmentBack);
                }
                size = -1;
                sizeNow = 0;
            }

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        cfRefresh = true;
        switch (MyAction.getActivityBulb(this)){
            case MyAction.LIST_ACTIVITY:
                IntentActivity.handleOpenListActivity(this);
                break;
            case MyAction.LEARN_ACTIVITY:
                IntentActivity.handleOpenTopicActivy(this);
                MyAction.setActivityBulb(this, MyAction.TOPIC_ACTIVITY);
                break;
            default:{
                IntentActivity.handleOpenListActivity(this);
                break;
            }
        }
    }
}
