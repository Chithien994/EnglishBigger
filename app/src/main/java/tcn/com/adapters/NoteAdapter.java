package tcn.com.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.models.NoteModels;

/**
 * Created by MyPC on 05/09/2017.
 */

public class NoteAdapter extends ArrayAdapter<NoteModels> {
    Activity context;
    int resource;
    List<NoteModels> objects;
    TopicActivity topicActivity;
    private int pss;
    private String noteMeaning;
    private String noteSource;
    public static String MY_BRC_NOTE_ADAPTER = "MY_BRC_NOTE_ADAPTER";

    public NoteAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<NoteModels> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        topicActivity = (TopicActivity) context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.resource, null);

        TextView txtNote = view.findViewById(R.id.txtNote);
        LinearLayout layoutItem = view.findViewById(R.id.layoutItem);
        ConstraintLayout layoutDelete = view.findViewById(R.id.layoutDelete);

        final NoteModels noteModels = this.objects.get(position);

        try {

            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString sNumber=  new SpannableString((position + 1) +"). ");
            SpannableString s1=  new SpannableString(noteModels.getNoteSource());
            SpannableString s2=  new SpannableString(" -- " + noteModels.getNoteMeaning());
            sNumber.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorText)), 0, sNumber.length(), 0);
            s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);
            s2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorText)), 0, s2.length(), 0);
            builder.append(sNumber);
            builder.append(s1);
            builder.append(s2);

            txtNote.setText(builder);

        }catch (Exception e){
            e.printStackTrace();
        }

        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDeleteNote(noteModels, position);
            }
        });

        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEdit(noteModels, position);
            }
        });

        return view;
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
                            topicActivity.serverAPI.handleDeleteTopic(topicActivity, object, "deletenote");

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

                handleShowListNote(intent.getStringExtra("TYPE"));
                context.unregisterReceiver(mReceiver);
            }
        }
    };

    private void handleShowListNote(String type) {
        try {

            Log.i("TYPE", type);

            if (type.equals("deletenote")){

                this.objects.remove(pss);
                topicActivity.size--;
                Log.i("Add", "Size: " + topicActivity.size);

            }else if (type.equals("edit_vocabulary")){

                Log.i("noteSource", noteSource + "\n" +"noteMeaning: " +noteMeaning);
                this.objects.get(pss).setNoteSource(noteSource);
                this.objects.get(pss).setNoteMeaning(noteMeaning);
            }
            notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleEdit(final NoteModels noteModels, int position) {
        pss = position;


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_edit_note, null);
        dialogBuilder.setView(dialogView);

        ImageView imgCloseDialog = dialogView.findViewById(R.id.imgCloseDialog);
        final EditText txtNoteSourceDialog = dialogView.findViewById(R.id.txtNoteSourceDialog);
        final EditText txtNoteMeaningDialog = dialogView.findViewById(R.id.txtNoteMeaningDialog);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        txtNoteSourceDialog.setText(noteModels.getNoteSource());
        txtNoteMeaningDialog.setText(noteModels.getNoteMeaning());

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myIntentFilter();
                noteSource = txtNoteSourceDialog.getText().toString();
                noteMeaning =   txtNoteMeaningDialog.getText().toString();
                alertDialog.cancel();
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
            object.put("content", jsonArrayContent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        topicActivity.serverAPI.postEditVocabulary(topicActivity,object);
    }


}
