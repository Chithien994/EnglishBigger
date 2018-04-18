package com.tcn.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.tcn.adapters.TopicAdapter;
import com.tcn.englishbigger.ListActivity;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.Handle;
import com.tcn.handle.HideShowScrollListener;
import com.tcn.handle.MyAction;
import com.tcn.models.TopicModels;


public class TopicFragment extends Fragment {
    public TopicActivity topicActivity;

    private ImageView btnBack, btnAdd, imgView, imgTrueList, imgTrueGrid;
    private TextView txtNameView;
    public ImageView btnEdit;
    public LinearLayout layoutOptionView, layoutList, layoutGrid;
    public Toolbar toolbarViewTopic;
    private ConstraintLayout layoutView;
    private CoordinatorLayout layoutFragmentTopic;
    private AppBarLayout appBarLayout;

    private RecyclerView rvTopic;
    private TopicAdapter topicAdapter;
    private SharedPreferences spf;
    private Handle handle;

    private static final float APPBAR_ELEVATION = 14f;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    public boolean cfEdit = false;

    public TopicFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TOPIC_FRAGMENT","Action: onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TOPIC_FRAGMENT","Action: onCreateView");
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        addControls(view);
        addEvents();
        return view;
    }

    private void addEvents() {
        //back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicActivity.onBackPressed();
            }
        });
        //add Topic
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicActivity.type = MyAction.ADD_TOPIC_FRAGMENT;
                topicActivity.saveFragmen(new TopicFragment());
                topicActivity.callFragment(new AddAndEditTopicFragment(), "add", 0);
            }
        });

        layoutFragmentTopic.setOnClickListener(hideViewOption);
        layoutView.setOnClickListener(mView); //open option view or close option view
        //handle select Grid view
        layoutGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAction.setNumItemView(topicActivity, MyAction.GRID_VIEW);
                checkInfoShowView();
            }
        });
        //handle select List view
        layoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAction.setNumItemView(topicActivity, MyAction.LIST_VIEW);
                checkInfoShowView();

            }
        });

      //  handleScrollRecyclerView();//Hide or show when scrolling RecyclerView
    }

    private void handleScrollRecyclerView() {
        //Hide or show when scrolling RecyclerView
        rvTopic.addOnScrollListener(new HideShowScrollListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onHide() {

                appBarLayout.animate()
                        .translationY(-appBarLayout.getHeight())
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                appBarLayout.setVisibility(View.GONE);
                            }
                        });
            }
            @Override
            public void onShow() {
                appBarLayout.animate()
                        .translationY(0)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                appBarLayout.setVisibility(View.VISIBLE);
                            }
                        });
                // do your showing animation here
            }
        });
    }


    private void addControls(View view) {
        topicActivity.cfFinish = true;
        appBarLayout = view.findViewById(R.id.appBarLayout);
        toolbarViewTopic = view.findViewById(R.id.toolbarViewTopic);
        topicActivity.setSupportActionBar(toolbarViewTopic);

        btnBack = view.findViewById(R.id.btnBack);
        btnAdd = view.findViewById(R.id.btnAdd);
        imgView = view.findViewById(R.id.imgView);
        imgTrueList = view.findViewById(R.id.imgTrueList);
        imgTrueGrid = view.findViewById(R.id.imgTrueGrid);
        btnEdit = view.findViewById(R.id.btnEdit);
        txtNameView = view.findViewById(R.id.txtNameView);
        layoutOptionView = view.findViewById(R.id.layoutOptionView);
        layoutList = view.findViewById(R.id.layoutList);
        layoutGrid = view.findViewById(R.id.layoutGrid);
        rvTopic = view.findViewById(R.id.rvTopic);
        layoutView = view.findViewById(R.id.layoutView);
        layoutFragmentTopic = view.findViewById(R.id.layoutFragmentTopic);
        hideOptionView();
        handle = new Handle();
        spf = getActivity().getSharedPreferences(getActivity().getString(R.string.saveInfoApp), getActivity().MODE_PRIVATE);
        checkInfoShowView(); //check user selected view?(Grid view or List view)
    }



    //check user selected view? (Grid view or List view)
    private void checkInfoShowView() {
        imgTrueGrid.setVisibility(View.GONE);
        imgTrueList.setVisibility(View.GONE);

        switch (MyAction.getNumItemView(topicActivity)){
            case MyAction.GRID_VIEW:
                imgView.setImageResource(R.drawable.ic_grid);
                txtNameView.setText(getActivity().getString(R.string.grid));
                imgTrueGrid.setVisibility(View.VISIBLE);
                handleShowView(R.layout.item_topic_grid, getNumItem(), true);//show grid view topic
                break;

            case MyAction.LIST_VIEW:
                imgView.setImageResource(R.drawable.ic_list);
                txtNameView.setText(getActivity().getString(R.string.list));
                imgTrueList.setVisibility(View.VISIBLE);
                handleShowView(R.layout.item_topic_list, 1, false);//show list view topic
                break;
            default:break;
        }

        hideOptionView(); //close option view
    }

    //Calculates the number of items displayed per line, to fit the screen size
    private int getNumItem(){
        Display display = topicActivity.getWindowManager().getDefaultDisplay(); //get the screen size
        int inWidth = display.getWidth(); //get the screen width
        return (inWidth/288 >= 2) ? inWidth/288 : 2; //Estimate the size of each item: 288 px
    }

    //show list view topic
    //resource: layout item displayed
    //The number of columns displayed
    private void handleShowView(int resource, int column, boolean type) {
        Log.d("Tab_View","Column: "+column);
        rvTopic.setLayoutManager(new GridLayoutManager(this.topicActivity, column));
        topicAdapter = new TopicAdapter(topicActivity,this,resource,topicActivity.topicYourModes,type);
        rvTopic.setAdapter(topicAdapter);
        topicAdapter.notifyDataSetChanged();
    }

    //open option view or close option view
    View.OnClickListener mView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cfEdit == false){
                if (layoutOptionView.getVisibility() == View.GONE){
                    showOptionView();
                }else {
                    hideOptionView();
                }
            }
        }
    };

    View.OnClickListener hideViewOption = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (layoutOptionView.getVisibility() == View.VISIBLE){
                hideOptionView();
            }
        }
    };

    public void hideOptionView(){
        layoutOptionView.animate()
                .translationY(-layoutOptionView.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layoutOptionView.setVisibility(View.GONE);
                    }
                });
    }

    public void showOptionView(){
        layoutOptionView.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {;
                        layoutOptionView.setVisibility(View.VISIBLE);
                    }
                });
        //layoutOptionView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1).scaleY(1);
    }


    @Override
    public void onAttach(Context context) {
        Log.d("TOPIC_FRAGMENT","Action: onAttach");
        super.onAttach(context);
        if (context instanceof TopicActivity) {
            topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
