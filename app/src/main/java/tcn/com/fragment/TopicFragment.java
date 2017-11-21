package tcn.com.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tcn.com.adapters.TopicAdapter;
import tcn.com.englishbigger.ListActivity;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Handle;


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

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    public boolean cfEdit = false;

    public TopicFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });
        //add Topic
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicActivity.saveFragmen(new TopicFragment());
                topicActivity.callFragment(new AddAndEditTopicFragment(), "add", 0);
            }
        });

        layoutFragmentTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutOptionView.getVisibility()==View.VISIBLE){
                    layoutOptionView.setVisibility(View.GONE);
                }
            }
        });
        layoutView.setOnClickListener(mView); //open option view or close option view
        //handle select Grid view
        layoutGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handle.saveInfoView(topicActivity, "grid");
                checkInfoShowView();
            }
        });
        //handle select List view
        layoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handle.saveInfoView(topicActivity, "list");
                checkInfoShowView();

            }
        });

        //tricksHideOrShow(); //Hide or show when scrolling RecyclerView

    }
    //Hide or show when scrolling RecyclerView
    private void tricksHideOrShow() {
        rvTopic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    handleHideAndShowActionBar(true);
                    controlsVisible = false;
                    scrolledDistance = -1;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    handleHideAndShowActionBar(false);
                    controlsVisible = true;
                    scrolledDistance = -1;
                }

                if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                    scrolledDistance += dy;
                }
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

        handle = new Handle();
        spf = getActivity().getSharedPreferences(getActivity().getString(R.string.saveInfoApp), getActivity().MODE_PRIVATE);
        checkInfoShowView(); //check user selected view?(Grid view or List view)

    }



    //check user selected view? (Grid view or List view)
    private void checkInfoShowView() {
        imgTrueGrid.setVisibility(View.GONE);
        imgTrueList.setVisibility(View.GONE);
        if (spf.getString("view","list").equals("grid")){

            imgView.setImageResource(R.drawable.ic_grid);
            txtNameView.setText(getActivity().getString(R.string.grid));
            imgTrueGrid.setVisibility(View.VISIBLE);
            handleShowView(R.layout.item_topic_grid, 2, true);//show list view topic
        }else {

            imgView.setImageResource(R.drawable.ic_list);
            txtNameView.setText(getActivity().getString(R.string.list));
            imgTrueList.setVisibility(View.VISIBLE);
            handleShowView(R.layout.item_topic_list, 1, false);//show list view topic
        }
        layoutOptionView.setVisibility(View.GONE);
    }

    //show list view topic
    //resource: layout item displayed
    //The number of columns displayed
    private void handleShowView(int resource, int column, boolean type) {
        Log.d("Tab_View","Column: "+column);
        rvTopic.setLayoutManager(new GridLayoutManager(this.topicActivity, column));
        topicAdapter = new TopicAdapter(topicActivity,this,resource,topicActivity.topicModes,type);
        rvTopic.setAdapter(topicAdapter);
    }


    public void handleHideAndShowActionBar(boolean cf){
        if (cf){
            appBarLayout.setVisibility(View.GONE);
        }else {
           appBarLayout.setVisibility(View.VISIBLE);
        }

    }
    //open option view or close option view
    View.OnClickListener mView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cfEdit == false){
                if (layoutOptionView.getVisibility() == View.GONE){
                    layoutOptionView.setVisibility(View.VISIBLE);
                }else {
                    layoutOptionView.setVisibility(View.GONE);
                }
            }
        }
    };


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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
