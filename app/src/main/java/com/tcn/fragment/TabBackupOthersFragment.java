package com.tcn.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.tcn.adapters.BackupTopicAdapter;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.Constants;
import com.tcn.models.TopicModels;

public class TabBackupOthersFragment extends Fragment {
    TopicActivity topicActivity;
    public static String BROADCAST_ACTION_OTHERS = "BROADCAST_ACTION_OTHERS";
    RecyclerView rvItem;
    private BackupTopicAdapter backupTopicAdapter;
    private ProgressDialog dialog;

    public TabBackupOthersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_backup_others, container, false);
        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {

    }


    private void showTopicOther() {
        rvItem.setLayoutManager(new LinearLayoutManager(topicActivity, LinearLayoutManager.VERTICAL, false));
        backupTopicAdapter = new BackupTopicAdapter(
                topicActivity,
                R.layout.item_topic_of_everyone,
                topicActivity.topicModes);
        rvItem.setAdapter(backupTopicAdapter);

    }

    private void addControls(View view) {
        myIntentFilter();
        rvItem = view.findViewById(R.id.rvItem);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopicActivity) {
            this.topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION_OTHERS);
        topicActivity.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_ACTION_OTHERS)) {
                topicActivity.topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                showTopicOther();
                topicActivity.unregisterReceiver(mReceiver);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Tab_2","Action: onStop");
    }

}