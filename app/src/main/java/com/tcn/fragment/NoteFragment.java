package com.tcn.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.tcn.adapters.NoteAdapter;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.HideShowScrollListener;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.models.NoteModels;
import com.tcn.models.TopicModels;

public class NoteFragment extends Fragment {

    TopicActivity topicActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private static String TAG = "NOTE_FRAGMENT";
    private ArrayList<NoteModels> noteModels;
    public int pst; //Current topic position
    public int idTopic; //Current topic id
    public View thisView;
    private String nameTopic ="";
    public ArrayList<TopicModels> topicModes;
    public Button btnLearnNow;
    private ImageView imgBack;
    private ImageView imgAdd;
    private TextView txtNameTopic;
    private TextView txtName;
    private ListView lvNote;
    private AppBarLayout appBarLayout;
    private String nameUser = "";
    private boolean loadNote = true; //True: Download the vocabulary list of the topic
    public boolean touch = false; //True: When start scrolling listview. To start the function: check roll up or down

    private NoteAdapter noteAdapter;

    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_note, container, false);

        addControls(thisView);
        addEvents();

        return thisView;
    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "BACK: Size: " + topicActivity.size + "\nNoteModels.size(): " + noteModels.size());
                if (topicActivity.size != noteModels.size() && topicActivity.size != -1){
                    topicActivity.cfRefresh = true;
                    MyAction.setLoadedTopic(topicActivity, false); //Correct conditions to reload the topic
                    MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
                    IntentActivity.handleOpenTopicActivy(topicActivity);
                }else {
                    if(MyAction.getFragmentBulb(topicActivity) == MyAction.WDPL_FRAGMENT) topicActivity.showFragmentBottom();
                    else {
                        MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
                        topicActivity.callFragment(topicActivity.fragmentBack);
                    }
                }
                topicActivity.size = -1;
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyAction.getFragmentBulb(topicActivity) ==  MyAction.WDPL_FRAGMENT) handleBackupTopic(topicModes.get(pst));
                else openActivityAdd();
            }
        });

        btnLearnNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touch = false;
                MyAction.setIdTopic(topicActivity, idTopic);
                MyAction.setActivityBulb(topicActivity, MyAction.TOPIC_ACTIVITY);
                MyAction.setFragmentBulb(topicActivity, MyAction.NOTE_FRAGMENT);
                MyAction.setFragmentNew(topicActivity, MyAction.LEARN_FRAGMENT);
                IntentActivity.handleOpenLearnActivity(topicActivity);
            }
        });

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgAdd.performClick();
            }
        });
    }

    private void openActivityAdd() {
        loadNote = true;
        topicActivity.cfRefresh = true;
        topicActivity.size = noteModels.size();
        Log.i(TAG, "Add: Size: " + topicActivity.size);
        MyAction.setFragmentNew(topicActivity, MyAction.ADD_NEW_WORD_FRAGMENT);
        MyAction.setActivityBulb(topicActivity, MyAction.TOPIC_ACTIVITY);
        MyAction.setPosition(topicActivity, pst);
        IntentActivity.handleOpenLearnActivity(topicActivity);
    }

    private void handleBackupTopic(final TopicModels topicModels) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(topicActivity);
        LayoutInflater inflater = topicActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_set_name, null);
        dialogBuilder.setView(dialogView);

        ImageView imgCloseDialog = dialogView.findViewById(R.id.imgCloseDialog);
        final LinearLayout layoutNewNameDialog = dialogView.findViewById(R.id.layoutNewName);
        final LinearLayout layoutGetNameTopicDialog = dialogView.findViewById(R.id.layoutGetNameTopic);
        final Spinner spNameTopicDialog = dialogView.findViewById(R.id.spNameTopicDialog);
        final RadioButton radShowTXTNewName = dialogView.findViewById(R.id.radShowTXTNewName);
        final RadioButton radShowSPNewName = dialogView.findViewById(R.id.radShowSPNewName);
        Button btnBackupDialog = dialogView.findViewById(R.id.btnBackupDialog);
        final EditText txtNewNameTopicDialog = dialogView.findViewById(R.id.txtNewNameTopicDialog);
        final ArrayList<String> dsNameTopic = new ArrayList<>();
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        txtNewNameTopicDialog.setText(topicModels.getId() + " " + topicModels.getName());
        nameTopic = txtNewNameTopicDialog.getText().toString();
        layoutGetNameTopicDialog.setVisibility(View.GONE);
        layoutNewNameDialog.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter;
        dsNameTopic.add(0, getString(R.string.select_));
        for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
            dsNameTopic.add(i+1, topicActivity.topicYourModes.get(i).getName());
        }
        arrayAdapter =  new ArrayAdapter<String>(topicActivity, R.layout.support_simple_spinner_dropdown_item, dsNameTopic);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spNameTopicDialog.setAdapter(arrayAdapter);

        radShowSPNewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutGetNameTopicDialog.setVisibility(View.VISIBLE);
                layoutNewNameDialog.setVisibility(View.GONE);
                spNameTopicDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        nameTopic = dsNameTopic.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        radShowTXTNewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutGetNameTopicDialog.setVisibility(View.GONE);
                layoutNewNameDialog.setVisibility(View.VISIBLE);
            }
        });

        btnBackupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(topicActivity, thisView);

                if (radShowTXTNewName.isChecked()){
                    nameTopic = txtNewNameTopicDialog.getText().toString();

                    for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
                        if (nameTopic.equalsIgnoreCase(topicActivity.topicYourModes.get(i).getName())){
                            Toast.makeText(topicActivity, getString(R.string.topicAlreadyExists), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                if(!nameTopic.equalsIgnoreCase(getString(R.string.select_))){
                    JSONObject object = new JSONObject();
                    try {
                        object.put("idUser", topicActivity.usersFB.getIdUser());
                        object.put("nameTopic", nameTopic);
                        object.put("idTopic", topicModels.getId());
                        topicActivity.serverAPI.handleBackup(topicActivity, object, Constants.BACKUP_TOPIC, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (radShowSPNewName.isChecked())
                        MyAction.setRefreshTopic(topicActivity,true);
                    alertDialog.cancel();
                }else {
                    Toast.makeText(topicActivity, getString(R.string.msgPleaseSelectAValidTopicName), Toast.LENGTH_LONG).show();
                }

            }
        });

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(getActivity(),thisView);
                alertDialog.cancel();
            }
        });

    }

    private void addControls(View v) {
        appBarLayout = v.findViewById(R.id.appBarLayout);
        btnLearnNow = v.findViewById(R.id.btnLearnNow);
        imgBack = v.findViewById(R.id.imgBack);
        imgAdd = v.findViewById(R.id.imgAdd);
        txtNameTopic = v.findViewById(R.id.txtNameTopic);
        txtName = v.findViewById(R.id.txtName);
        lvNote = v.findViewById(R.id.lvNote);
        noteModels = new ArrayList<>();
        topicModes = new ArrayList<>();
        hideNow(); //hide the learning node
        getInfoOfTopic();

        //Getting the user's listview scrolling event
        lvNote.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            private float scrolledDistance = 0;
            private int countH = 0;
            private int countS = 0;
            private float hide = 0;
            private boolean controlsVisible = true;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (MyAction.getFragmentBulb(topicActivity) != MyAction.WDPL_FRAGMENT)
                    touch = true;
                return false;
            }
        });
    }

    public void hideLearnNow(){
        btnLearnNow.animate()
                .translationY(btnLearnNow.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btnLearnNow.setVisibility(View.GONE);
                    }
                });
    }

    public void hideNow(){
        btnLearnNow.animate()
                .translationY(btnLearnNow.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btnLearnNow.setVisibility(View.GONE);
                    }
                });
    }

    public void showLearnNow(){
        btnLearnNow.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {;
                        btnLearnNow.setVisibility(View.VISIBLE);
                    }
                });
        //layoutOptionView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1).scaleY(1);
    }


    private void getInfoOfTopic() {
        Bundle bundle = this.getArguments();
        if (bundle != null){

            pst = bundle.getInt("ID");
            if (topicActivity.type== MyAction.WDPL_FRAGMENT){
                if (MyAction.getPosition(topicActivity)==MyAction.TAB_BF_FRAGMENT){
                    topicModes = topicActivity.topicFriendsModes;
                }else{
                    topicModes = topicActivity.topicModes; //MyAction.TAB_BO_FRAGMENT
                }
            }else if (topicActivity.type == MyAction.TOPIC_FRIEND_FRAGMENT){
                topicModes = topicActivity.topicModes;
            }else{
                topicModes = topicActivity.topicYourModes;
            }
            nameUser = topicModes.get(pst).getNameUser();
            if (nameUser.equals("")) {
                txtName.setVisibility(View.GONE);
                txtNameTopic.setText(topicModes.get(pst).getName());
            } else {
                txtName.setVisibility(View.VISIBLE);
                txtNameTopic.setText(nameUser);
                txtName.setText(topicModes.get(pst).getName());
            }
            idTopic = topicModes.get(pst).getId();

            Log.i(TAG, "ID_TOPIC: "+idTopic);

        }
    }

    private void getVocabulary(int idTopic) {
        myIntentFilter();

        JSONObject object = new JSONObject();
        try {
            object.put("idTopic", idTopic);
            object.put("type","_SHOW");
            topicActivity.serverAPI.getVocabulary(topicActivity, object, Constants.GET_VOCABULARY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_NOTE);
        getActivity().registerReceiver(mReceiver,mIntentFilter);
        Log.i(TAG,"DK: mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_NOTE)) {
                if (intent.getSerializableExtra("NOTE") != null){
                    noteModels = (ArrayList<NoteModels>) intent.getSerializableExtra("NOTE");
                    topicActivity.sizeNow = noteModels.size();
                    handleShowListNote();
                }
                getActivity().unregisterReceiver(mReceiver);
                Log.d(TAG,"Unregister Receiver");
            }
        }
    };

    private void handleShowListNote() {
        noteAdapter = new NoteAdapter(topicActivity, this, R.layout.item_note, noteModels);
        lvNote.setAdapter(noteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyAction.getFragmentNew(topicActivity) == MyAction.NOTE_FRAGMENT && loadNote){
            loadNote = false;
            getVocabulary(idTopic);
        }
    }
}
