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
import com.tcn.handle.HideShowScrollListener;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.models.NoteModels;
import com.tcn.models.TopicModels;

public class NoteFragment extends Fragment {

    TopicActivity topicActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private ArrayList<NoteModels> noteModels;
    public int pst; //Current topic position
    public int idTopic; //Current topic id
    public View view;
    private String nameTopic ="";
    private ArrayList<TopicModels> topicModes;

    private ImageView imgBack;
    private ImageView imgAdd;
    private TextView txtNameTopic;
    private ListView lvNote;
    private AppBarLayout appBarLayout;

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
        view = inflater.inflate(R.layout.fragment_note, container, false);

        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BACK", "Size: " + topicActivity.size + "\nNoteModels.size(): " + noteModels.size());
                if (topicActivity.size != noteModels.size() && topicActivity.size != -1){
                    topicActivity.cfRefresh = true;
                    topicActivity.loadedTopic = false;
                    MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
                    IntentActivity.handleOpenTopicActivy(topicActivity);
                }else {
                    if(topicActivity.type==MyAction.WDPL_FRAGMENT || topicActivity.type==MyAction.TOPIC_FRIEND_FRAGMENT) topicActivity.showFragmentBottom();
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
                if (topicActivity.type!=MyAction.WDPL_FRAGMENT && topicActivity.type!=MyAction.TOPIC_FRIEND_FRAGMENT) openActivityAdd();
                else   handleBackupTopic(topicModes.get(pst));
            }
        });
    }

    private void openActivityAdd() {
        topicActivity.cfRefresh = true;
        topicActivity.size = noteModels.size();
        Log.i("Add", "Size: " + topicActivity.size);
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
                nameTopic = txtNewNameTopicDialog.getText().toString();
            }
        });

        btnBackupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    MyAction.setRefreshTopic(topicActivity,true);
                    topicActivity.loadedTopic = false;
                    alertDialog.cancel();
                }else {
                    Toast.makeText(topicActivity, getString(R.string.msgPleaseSelectAValidTopicName), Toast.LENGTH_LONG).show();
                }

            }
        });

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }

    private void addControls(View v) {
        appBarLayout = v.findViewById(R.id.appBarLayout);
        imgBack = v.findViewById(R.id.imgBack);
        imgAdd = v.findViewById(R.id.imgAdd);
        txtNameTopic = v.findViewById(R.id.txtNameTopic);
        lvNote = v.findViewById(R.id.lvNote);
        noteModels = new ArrayList<>();
        topicModes = new ArrayList<>();
        getInfoOfTopic();
    }


    private void getInfoOfTopic() {
        Bundle bundle = this.getArguments();
        if (bundle != null){

            pst = bundle.getInt("ID");
            if (topicActivity.type== MyAction.WDPL_FRAGMENT){
                if (MyAction.getPosition(topicActivity)==MyAction.TAB_BF_FRAGMENT){
                    topicModes = topicActivity.topicFriendsModes;
                }else{
                    topicModes = topicActivity.topicModes;
                }
            }else if (topicActivity.type == MyAction.TOPIC_FRIEND_FRAGMENT){
                topicModes = topicActivity.topicModes;
            }else{
                topicModes = topicActivity.topicYourModes;
            }
            idTopic = topicModes.get(pst).getId();
            txtNameTopic.setText(topicModes.get(pst).getName());
            Log.i("ID_TOPIC", idTopic +"");

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
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_NOTE)) {
                Log.d("unregisterReceiver","Unregister Receiver");
                if (intent.getSerializableExtra("NOTE") != null){
                    noteModels = (ArrayList<NoteModels>) intent.getSerializableExtra("NOTE");
                    topicActivity.sizeNow = noteModels.size();
                    handleShowListNote();
                }
                getActivity().unregisterReceiver(mReceiver);
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
        if (MyAction.getFragmentNew(topicActivity) == MyAction.NOTE_FRAGMENT){
            getVocabulary(idTopic);
        }
    }
}
