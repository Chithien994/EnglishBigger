package tcn.com.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tcn.com.adapters.BackupTopicAdapter;
import tcn.com.adapters.WhatDoPeopleLearnAdapter;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.models.DrawerModels;

public class WhatDoPeopleLearnFragment extends Fragment {

    public TopicActivity topicActivity;

    private ImageView imgBack, imgSearch;
    private TextView txtNameToolbar;
    private Toolbar toolbarView;
    private AppBarLayout appBarLayout;
    private ListView lvWhatDoPeopleLearn;
    private RecyclerView rvItem;

    private ArrayList<DrawerModels> dsDrawerModels;
    private WhatDoPeopleLearnAdapter adapter;

    private BackupTopicAdapter backupTopicAdapter;

    public WhatDoPeopleLearnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_what_do_people_learn, container, false);
        addControls(view);
        addEvents();
        return view;
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
        lvWhatDoPeopleLearn = view.findViewById(R.id.lvWhatDoPeopleLearn);

        if (topicActivity.type == 3){
            handleSetOptionsWhatDoPeopleLearn();
        }else {
            lvWhatDoPeopleLearn.setVisibility(View.GONE);
            rvItem = view.findViewById(R.id.rvItem);
            rvItem.setVisibility(View.VISIBLE);
            txtNameToolbar = view.findViewById(R.id.txtNameToolbar);
            txtNameToolbar.setText(topicActivity.usersModels.getName());
            showTopicFriend();
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

    private void handleSetOptionsWhatDoPeopleLearn() {
        dsDrawerModels = new ArrayList<>();
        dsDrawerModels.add(new DrawerModels(1, R.drawable.ic_friends ,getString(R.string.friend)));
        dsDrawerModels.add(new DrawerModels(2, R.drawable.ic_other ,getString(R.string.other)));
        adapter = new WhatDoPeopleLearnAdapter(
                topicActivity,
                R.layout.item_left_drawer,
                dsDrawerModels);
        lvWhatDoPeopleLearn.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopicActivity) {
            topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
