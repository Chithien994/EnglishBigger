package tcn.com.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class NoteFragment extends Fragment {

    TopicActivity topicActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private ArrayList<NoteModels> noteModels;
    public int pst; //Current topic position
    public int idTopic; //Current topic id
    public View view;

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
                    HandleIntent hi = new HandleIntent();
                    hi.intentActivity(topicActivity, HandleIntent.INTENT_TOPIC);
                    topicActivity.finish();
                }else {
                    topicActivity.callFragment(topicActivity.fragmentBack);
                }
                topicActivity.size = -1;
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicActivity.size = noteModels.size();
                Log.i("Add", "Size: " + topicActivity.size);
                Intent intent = new Intent(topicActivity, LearnActivity.class);
                intent.putExtra("PUT",4);
                intent.putExtra("ID",pst);
                startActivity(intent);
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
