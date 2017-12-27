package tcn.com.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import tcn.com.adapters.NoteAdapter;
import tcn.com.englishbigger.LearnActivity;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Constants;
import tcn.com.handle.HandleIntent;
import tcn.com.models.NoteModels;
import tcn.com.models.TopicModels;

public class NoteFragment extends Fragment {

    TopicActivity topicActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private ArrayList<NoteModels> noteModels;
    public int pst; //Current topic position
    public int idTopic; //Current topic id
    public View view;
    private String nameTopic ="";

    private ImageView imgBack;
    private ImageView imgAdd;
    private TextView txtNameTopic;
    private ListView lvNote;

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
                    HandleIntent.intentActivity(topicActivity, HandleIntent.INTENT_TOPIC);
                    topicActivity.finish();
                }else {
                    if(topicActivity.type==3 || topicActivity.type==4) topicActivity.showFragmentBottom();
                    else topicActivity.callFragment(topicActivity.fragmentBack);
                }
                topicActivity.size = -1;
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicActivity.type!=3 && topicActivity.type!=4) openActivityAdd();
                else   handleBackupTopic(topicActivity.topicModes.get(pst));
            }
        });
    }

    private void openActivityAdd() {
        topicActivity.size = noteModels.size();
        Log.i("Add", "Size: " + topicActivity.size);
        Intent intent = new Intent(topicActivity, LearnActivity.class);
        intent.putExtra("PUT",4);
        intent.putExtra("ID",pst);
        startActivity(intent);
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
        imgBack = v.findViewById(R.id.imgBack);
        imgAdd = v.findViewById(R.id.imgAdd);
        txtNameTopic = v.findViewById(R.id.txtNameTopic);
        lvNote = v.findViewById(R.id.lvNote);
        noteModels = new ArrayList<>();
        getInfoOfTopic();
    }

    private void getInfoOfTopic() {
        Bundle bundle = this.getArguments();
        if (bundle != null){

            pst = bundle.getInt("ID");
            idTopic = topicActivity.topicModes.get(pst).getId();
            txtNameTopic.setText(topicActivity.topicModes.get(pst).getName());

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
        getVocabulary(idTopic);
    }
}
