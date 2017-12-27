package tcn.com.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcn.com.adapters.BackupTopicAdapter;
import tcn.com.adapters.ViewPagerAdapter;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Constants;
import tcn.com.models.TopicModels;

import static android.content.ContentValues.TAG;

public class WhatDoPeopleLearnFragment extends Fragment {

    public TopicActivity topicActivity;
    public static final String BROADCAST_ACTION_WDPL = "BROADCAST_ACTION_WDPL";
    private ImageView imgBack, imgSearch;
    private TextView txtNameToolbar;
    private Toolbar toolbarView;
    private RecyclerView rvItem;
    private ViewPager vpWhatDoPeopleLearn;
    private TabLayout tabWhatDoPeopleLearn;
    private Button btnOthers;
    private Button btnFriends;
    private ViewPagerAdapter adapter;
    private View view = null;
    private boolean load = true;
    private BackupTopicAdapter backupTopicAdapter;


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
        addControls(this.view);
        addEvents();
        return this.view;
    }

    private void addEvents() {
        //back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });
    }

    private void addControls(View view) {
        topicActivity.cfFinish = true;
        toolbarView = view.findViewById(R.id.toolbarView);
        topicActivity.setSupportActionBar(toolbarView);
        imgBack = view.findViewById(R.id.imgBack);
        imgSearch = view.findViewById(R.id.imgSearch);
        vpWhatDoPeopleLearn = view.findViewById(R.id.vpWhatDoPeopleLearn);
        tabWhatDoPeopleLearn = view.findViewById(R.id.tabWhatDoPeopleLearn);


        if (topicActivity.type == TopicActivity.OP_WHAT_DO_PEOPLE_LEARN){
            setupViewPager(vpWhatDoPeopleLearn);
            tabWhatDoPeopleLearn.setupWithViewPager(vpWhatDoPeopleLearn);
            setupTabNames();
        }else {
            view.findViewById(R.id.layoutTab).setVisibility(View.GONE);
            rvItem = view.findViewById(R.id.rvItem);
            rvItem.setVisibility(View.VISIBLE);
            txtNameToolbar = view.findViewById(R.id.txtNameToolbar);
            txtNameToolbar.setText(topicActivity.usersModels.getName());
            myIntentFilter();
            JSONObject object = new JSONObject();
            try {
                object.put("allIDUsers", topicActivity.usersModels.getIdUser());
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
        tabWhatDoPeopleLearn.getTabAt(0).setCustomView(R.layout.head_tab_friends);
        tabWhatDoPeopleLearn.getTabAt(1).setCustomView(R.layout.head_tab_others);
        btnFriends = tabWhatDoPeopleLearn.getTabAt(0).getCustomView().findViewById(R.id.btnFriends);
        btnOthers = tabWhatDoPeopleLearn.getTabAt(1).getCustomView().findViewById(R.id.btnOthers);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabWhatDoPeopleLearn.getTabAt(0).select();
            }
        });
        btnOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTopicOther();
            }
        });

    }

    private void getTopicFriend() {


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
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new TabBackupFriendsFragment());
        adapter.addFrag(new TabBackupOthersFragment());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.setOnPageChangeListener(new PageListener());
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.i(TAG, "page selected " + position);
            if (position==1) getTopicOther();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("WDPL_FRAGMENT","Action: onDestroyView");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("WDPL_FRAGMENT","Action: onLowMemory");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WDPL_FRAGMENT","Action: onDestroy");
        if (topicActivity.topicYourModes!=null) topicActivity.topicYourModes.clear();
    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("WDPL_FRAGMENT","Action: onDetach");
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
