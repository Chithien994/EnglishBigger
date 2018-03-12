package com.tcn.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.tcn.adapters.BackupTopicAdapter;
import com.tcn.adapters.ViewPagerAdapter;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.Constants;
import com.tcn.handle.MyAction;
import com.tcn.models.TopicModels;

import static android.content.ContentValues.TAG;

public class WhatDoPeopleLearnFragment extends Fragment {

    public TopicActivity topicActivity;
    public static final String BROADCAST_ACTION_WDPL = "BROADCAST_ACTION_WDPL";
    private ImageView imgBack, imgSearch;
    private TextView txtNameToolbar;
    private Toolbar toolbarView;
    private AppBarLayout appBarLayout;
    private RecyclerView rvItem;
    private BackupTopicAdapter backupTopicAdapter;
    private ViewPager vpWhatDoPeopleLearn;
    private TabLayout tabWhatDoPeopleLearn;
    private LinearLayout layoutOthers;
    private LinearLayout layoutFriends;
    private ViewPagerAdapter adapter;
    private View view = null;
    private boolean load = true;
    private static final float APPBAR_ELEVATION = 14f;


    public WhatDoPeopleLearnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WDPL_FRAGMENT","Action: onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("WDPL_FRAGMENT","Action: onCreateView");
        this.view = inflater.inflate(R.layout.fragment_what_do_people_learn, container, false);
        topicActivity.view = this.view;
        addControls(this.view);
        addEvents();
        return this.view;
    }

    private void addEvents() {
        //back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicActivity.onBackPressed();
            }
        });
    }

    private void handleAnimateAppBar(final AppBarLayout appBar) {
        appBar.animate()
                .translationY(-appBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        appBar.setElevation(0);
                        appBar.animate()
                                .translationY(0)
                                .setInterpolator(new LinearInterpolator())
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        appBar.setElevation(APPBAR_ELEVATION);
                                    }
                                });
                    }
                });
    }

    private void animationAppBarDown(final AppBarLayout appBar){
        new CountDownTimer(300, 1) {
            public void onTick(long millisUntilFinished) {
                appBar.setTranslationY(-appBar.getHeight());
            }

            public void onFinish() {
                appBar.animate()
                        .translationY(0)
                        .setDuration(500).start();
            }
        }.start();
    }

    private void addControls(View view) {
        MyAction.setPosition(topicActivity, MyAction.TAB_BF_FRAGMENT);
        topicActivity.cfFinish = true;
        toolbarView = view.findViewById(R.id.toolbarView);
        topicActivity.setSupportActionBar(toolbarView);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        animationAppBarDown(appBarLayout);
        handleAnimateAppBar(appBarLayout);
        imgBack = view.findViewById(R.id.imgBack);
        imgSearch = view.findViewById(R.id.imgSearch);
        vpWhatDoPeopleLearn = view.findViewById(R.id.vpWhatDoPeopleLearn);
        tabWhatDoPeopleLearn = view.findViewById(R.id.tabWhatDoPeopleLearn);


        if (topicActivity.type == MyAction.WDPL_FRAGMENT){
            setupViewPager(vpWhatDoPeopleLearn);
            tabWhatDoPeopleLearn.setupWithViewPager(vpWhatDoPeopleLearn);
            setupTabNames();
        }else {
            view.findViewById(R.id.layoutTab).setVisibility(View.GONE);
            rvItem = view.findViewById(R.id.rvItem);
            rvItem.setVisibility(View.VISIBLE);
            txtNameToolbar = view.findViewById(R.id.txtNameToolbar);
            txtNameToolbar.setText(MyAction.getNameFriend(topicActivity));
            myIntentFilter();
            JSONObject object = new JSONObject();
            try {
                object.put("allIDUsers", MyAction.getIDFriend(topicActivity));
                topicActivity.serverAPI.getTopic(topicActivity, object, Constants.GET_TOPIC_A_FRIEND);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void showTopicFriend() {
        rvItem.setLayoutManager(new LinearLayoutManager(topicActivity, LinearLayoutManager.VERTICAL, false));
        backupTopicAdapter = new BackupTopicAdapter(
                topicActivity,
                R.layout.item_topic_of_everyone,
                topicActivity.topicModes);
        rvItem.setAdapter(backupTopicAdapter);

    }

    @SuppressLint("ResourceType")
    public void setupTabNames() {
//        tabWhatDoPeopleLearn.getTabAt(0).setText(getString(R.string.friend));
//        tabWhatDoPeopleLearn.getTabAt(1).setText(getString(R.string.other));
        tabWhatDoPeopleLearn.getTabAt(0).setCustomView(R.layout.head_tab_friends);
        tabWhatDoPeopleLearn.getTabAt(1).setCustomView(R.layout.head_tab_others);
        layoutFriends = tabWhatDoPeopleLearn.getTabAt(0).getCustomView().findViewById(R.id.layoutFriends);
        layoutOthers = tabWhatDoPeopleLearn.getTabAt(1).getCustomView().findViewById(R.id.layoutOthers);
        layoutFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabWhatDoPeopleLearn.getTabAt(0).select();
            }
        });
        layoutOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTopicOther();
            }
        });

    }


    public void getTopicOther() {
        tabWhatDoPeopleLearn.getTabAt(1).select();
        if (load){
            JSONObject object = new JSONObject();
            try {
                object.put("idUser", topicActivity.usersFB.getIdUser());
                topicActivity.serverAPI.getTopic(topicActivity, object, Constants.GET_ALL_TOPIC);
                load = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new TabBackupFriendsFragment());
        adapter.addFrag(new TabBackupOthersFragment());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.setOnPageChangeListener(new PageListener());
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.i(TAG, "page selected " + position);
            if (position==1){
                getTopicOther();
                MyAction.setPosition(topicActivity, MyAction.TAB_BO_FRAGMENT);
            }else{
                MyAction.setPosition(topicActivity, MyAction.TAB_BF_FRAGMENT);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("WDPL_FRAGMENT","Action: onAttach");
        if (context instanceof TopicActivity) {
            topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("WDPL_FRAGMENT","Action: onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WDPL_FRAGMENT","Action: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("WDPL_FRAGMENT","Action: onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("WDPL_FRAGMENT","Action: onStop");
    }


    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION_WDPL);
        topicActivity.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_ACTION_WDPL)) {
                try {
                    topicActivity.topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                    showTopicFriend();
                    topicActivity.unregisterReceiver(mReceiver);
                    Log.d("unregisterReceiver","Unregister Receiver");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

}
