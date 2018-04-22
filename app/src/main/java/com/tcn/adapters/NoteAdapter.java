package com.tcn.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.fragment.NoteFragment;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.MyAction;
import com.tcn.models.NoteModels;

/**
 * Created by MyPC on 05/09/2017.
 */

public class NoteAdapter extends ArrayAdapter<NoteModels> {
    private Activity context;
    private int resource;
    private List<NoteModels> objects;
    private TopicActivity topicActivity;
    private NoteFragment noteFragment;
    private int pss;
    private String noteMeaning;
    private String noteSource;
    public static String MY_BRC_NOTE_ADAPTER = "MY_BRC_NOTE_ADAPTER";
    private static String TAG = "NOTE_ADAPTER";
    private AlertDialog alertDialog;
    private String nameTopic;
    private int scroll = 0;
    private boolean leanrnShow = true;

    public NoteAdapter(@NonNull Activity context, NoteFragment noteFragment, @LayoutRes int resource, @NonNull List<NoteModels> objects) {
        super(context, resource, objects);
        this.context = context;
        this.noteFragment = noteFragment;
        this.resource = resource;
        this.objects = objects;
        topicActivity = (TopicActivity) context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.resource, null);
        if (position == 0
                && MyAction.getFragmentBulb(topicActivity) != MyAction.WDPL_FRAGMENT)
            noteFragment.showLearnNow();//Show learning node when the list has one or more objects

        TextView txtNote = view.findViewById(R.id.txtNote);
        final LinearLayout layoutItem = view.findViewById(R.id.layoutItem);
        ConstraintLayout layoutDelete = view.findViewById(R.id.layoutDelete);
        ImageView imgDelete = view.findViewById(R.id.imgDelete);
        final NoteModels noteModels = this.objects.get(position);

        try {

            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString sNumber=  new SpannableString((position + 1) +"). ");
            SpannableString s1=  new SpannableString(noteModels.getNoteSource());
            SpannableString s2=  new SpannableString(" -- " + noteModels.getNoteMeaning());
            sNumber.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorText)), 0, sNumber.length(), 0);
            s1.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorTextName)), 0, s1.length(), 0);
            s2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorText)), 0, s2.length(), 0);
            builder.append(sNumber);
            builder.append(s1);
            builder.append(s2);

            txtNote.setText(builder);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (topicActivity.type== MyAction.WDPL_FRAGMENT || topicActivity.type==MyAction.TOPIC_FRIEND_FRAGMENT){
            imgDelete.setImageResource(R.drawable.ic_add);
        }

        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicActivity.type!= MyAction.WDPL_FRAGMENT && topicActivity.type!=MyAction.TOPIC_FRIEND_FRAGMENT) handleDeleteNote(noteModels, position);
                else handleBackupWord(noteModels);
            }
        });

        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicActivity.type!= MyAction.WDPL_FRAGMENT && topicActivity.type!=MyAction.TOPIC_FRIEND_FRAGMENT) handleEdit(noteModels, position, layoutItem);
            }
        });

        if (noteFragment.touch)
            hideOrShowLearnNow(position);

        return view;
    }

    //
    private void hideOrShowLearnNow(int position) {
        int minus = (scroll-position)>0 ? scroll-position : (scroll-position)*(-1);
        if (minus==1){
            if (scroll - position > 0 && !leanrnShow){
                leanrnShow = true;
                noteFragment.showLearnNow();//Show learning button when scrolling up
            }else if (scroll - position < 0 && leanrnShow){
                leanrnShow = false;
                noteFragment.hideLearnNow();//Hide learning button when scrolling down
            }
        }
        scroll = position;
    }

    private void handleBackupWord(final NoteModels noteModels) {
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

        txtNewNameTopicDialog.setText(noteFragment.topicModes.get(noteFragment.pst).getId() + " " + noteFragment.topicModes.get(noteFragment.pst).getName());
        nameTopic = txtNewNameTopicDialog.getText().toString();
        layoutGetNameTopicDialog.setVisibility(View.GONE);
        layoutNewNameDialog.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter;
        dsNameTopic.add(0, context.getString(R.string.select_));
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

                Handle.hideKeyboard(topicActivity, noteFragment.thisView);

                if (radShowTXTNewName.isChecked()){
                    nameTopic = txtNewNameTopicDialog.getText().toString();
                    for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
                        if (nameTopic.equals(topicActivity.topicYourModes.get(i).getName())){
                            Toast.makeText(topicActivity, topicActivity.getString(R.string.topicAlreadyExists), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                if(!nameTopic.equalsIgnoreCase(context.getString(R.string.select_))){
                    JSONObject object = new JSONObject();
                    try {
                        object.put("idUser", topicActivity.usersFB.getIdUser());
                        object.put("nameTopic", nameTopic);
                        object.put("idTopic", noteFragment.topicModes.get(noteFragment.pst).getId());
                        object.put("id", noteModels.getId());
                        topicActivity.serverAPI.handleBackup(topicActivity, object, Constants.BACKUP_VOCABULARY, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!radShowSPNewName.isChecked())
                        MyAction.setRefreshTopic(topicActivity,true);
                    alertDialog.cancel();
                }else {
                    Toast.makeText(topicActivity, context.getString(R.string.msgPleaseSelectAValidTopicName), Toast.LENGTH_LONG).show();
                }

            }
        });

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(topicActivity, noteFragment.thisView);
                alertDialog.cancel();
            }
        });
    }

    private void handleDeleteNote(final NoteModels noteModels, final int position) {
        final JSONObject object = new JSONObject();
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.doYouWantToDeleteIt))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            myIntentFilter();
                            pss = position;
                            object.put("id", noteModels.getId());
                            object.put("type","_DELETE");
                            //
                            topicActivity.serverAPI.handleDeleteTopic(topicActivity, object, Constants.DELETE_NOTE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.no), null)
                .show();
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_NOTE_ADAPTER);
        context.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_NOTE_ADAPTER)) {
                Log.d("unregisterReceiver","Unregister Receiver");

                handleShowListNote(intent.getIntExtra("TYPE",0),
                        intent.getBooleanExtra("CF",false));
                Handle.unregisterReceiver(context, mReceiver);
            }
        }
    };

    //Called after deleting or editing successfully
    private void handleShowListNote(int type, boolean cfErr) {
        try {

            Log.i("TYPE", type + "");

            if (type == Constants.DELETE_NOTE && !cfErr){

                this.objects.remove(pss);
                topicActivity.size--;
                Log.i("Add", "Size: " + topicActivity.size);

            }else if (type == Constants.EDIT_VOCABULARY && !cfErr){

                alertDialog.cancel(); //Close the edit dialog
                Log.i("noteSource", noteSource + "\n" +"noteMeaning: " +noteMeaning);
                this.objects.get(pss).setNoteSource(noteSource);
                this.objects.get(pss).setNoteMeaning(noteMeaning);
            }
            notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleEdit(final NoteModels noteModels, int position, LinearLayout layout) {
        pss = position;


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_edit_note, null);
        dialogBuilder.setView(dialogView);

        ImageView imgCloseDialog = dialogView.findViewById(R.id.imgCloseDialog);
        final EditText txtNoteSourceDialog = dialogView.findViewById(R.id.txtNoteSourceDialog);
        final EditText txtNoteMeaningDialog = dialogView.findViewById(R.id.txtNoteMeaningDialog);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        txtNoteSourceDialog.setText(noteModels.getNoteSource());
        txtNoteMeaningDialog.setText(noteModels.getNoteMeaning());

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();


        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(topicActivity, noteFragment.thisView);
                alertDialog.cancel();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myIntentFilter();
                noteSource = txtNoteSourceDialog.getText().toString();
                noteMeaning =   txtNoteMeaningDialog.getText().toString();
                //check keyboard hide or show
                //If it's showing then hide it
                Handle.hideKeyboard(topicActivity, noteFragment.thisView);
                add(noteModels);
            }
        });


    }

    public void add(NoteModels noteModles) {
        String type = "_UPDATE";
        JSONObject object = new JSONObject();

        try {
            JSONArray jsonArrayContent = new JSONArray();
            JSONObject objectContent1 = new JSONObject();
            JSONObject objectContent2 = new JSONObject();

            objectContent1.put("note", noteSource);
            objectContent1.put("langguage", noteModles.getLangguageSource());
            objectContent2.put("note", noteMeaning);
            objectContent2.put("langguage", noteModles.getLangguageMeaning());

            jsonArrayContent.put(objectContent1);
            jsonArrayContent.put(objectContent2);

            object.put("id", noteModles.getId());
            object.put("type", type);
            object.put("idTopic",noteFragment.idTopic);
            object.put("content", jsonArrayContent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        topicActivity.serverAPI.postEditVocabulary(topicActivity,object);
    }


}
