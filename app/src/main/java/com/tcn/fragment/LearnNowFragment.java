package com.tcn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.AdFast;
import com.tcn.handle.Constants;
import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.R;
import it.sephiroth.android.library.tooltip.Tooltip;

import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.models.NoteModels;


public class LearnNowFragment extends Fragment {
    private LearnActivity learnActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private View thisView;

    private ImageView imgVocabulary, imgTrueFalse, imgTrueFalse_2, imgBack;
    private TextView txtVocabulary, txtNumber, txtTrueFalse;
    private EditText txtInput;
    private Button btnOK, btnOK_2, btnBack, btnContinue, btnSound, btnShow;
    private RadioButton radA, radB, radC, radD;
    private LinearLayout layoutTrueFalse_2;
    private ConstraintLayout layoutCheck_1, layoutCheck_2;

    private String en,vi,sound;
    private TextToSpeech t1;
    private int number = 1;
    private String languageSpeak;
    private boolean cf = false;
    private ArrayList<Integer> learned;
    private int position;
    private int positionLearned = 0;
    private InterstitialAd mInterstitialAd;

    public LearnNowFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_learn_now, container, false);
        AdFast.loadAdView(learnActivity, (RelativeLayout) thisView.findViewById(R.id.totalLayout), getString(R.string.ad_id_small_2));
        loadAdFull(getString(R.string.ad_id_full_1));
        addControls(thisView);
        addEvents();
        return thisView;
    }

    private void loadAdFull(String ad_id){
        mInterstitialAd = new InterstitialAd(learnActivity);
        mInterstitialAd.setAdUnitId(ad_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void addEvents() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBack();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleContinue();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOK.setBackgroundResource(R.color.colorAccent);
                handleCheck();
            }
        });
        btnOK_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheck_2();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learnActivity.onBackPressed();
            }
        });

        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (cf == false){
                    imgTrueFalse.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void confirmTopicTransfer(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        new AlertDialog.Builder(learnActivity)
                .setMessage(getActivity().getString(R.string.finish))
                .setCancelable(false)
                .setNegativeButton(getActivity().getString(R.string.repeat), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadAdFull(getString(R.string.ad_id_full_2));
                        number = 1;
                        learned.clear();
                        handleShow(true);
                    }
                })
                .setPositiveButton(getActivity().getString(R.string.otherTopic), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyAction.setFragmentNew(learnActivity, MyAction.TOPIC_FRAGMENT);
                        MyAction.setActivityBulb(learnActivity, MyAction.LEARN_ACTIVITY);
                        MyAction.setFragmentBulb(learnActivity, MyAction.LEARN_FRAGMENT);
                        if (learnActivity.cfUpdate) {
                            learnActivity.cfUpdate = false;
                            IntentActivity.openAndUpdateTopicActivy(learnActivity);
                        }
                        else IntentActivity.handleOpenTopicActivy(learnActivity);
                    }
                }).show();
        Log.i("MOVE","Move To First");
    }

    private void handleCheck_2() {
        String inputText = null;
        if (radA.isChecked()){
            inputText = radA.getText().toString();
        }else if (radB.isChecked()){
            inputText = radB.getText().toString();
        }else if (radC.isChecked()){
            inputText = radC.getText().toString();
        }else if (radD.isChecked()){
            inputText = radD.getText().toString();
        }

        layoutTrueFalse_2.setVisibility(View.VISIBLE);
        if (en.equalsIgnoreCase(inputText)){

            if (learnActivity.noteModels.get(position).getLearned() == (byte) 0){
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 1);
                learnActivity.cfUpdate = true;
            }

            imgTrueFalse_2.setImageResource(R.drawable.ic_true);
            txtTrueFalse.setText(R.string.exactly);
        }else {

            imgTrueFalse_2.setImageResource(R.drawable.ic_false);
            txtTrueFalse.setText(R.string.wrong);
        }
    }

    private void handleCheck() {
        Handle.hideKeyboard(learnActivity, thisView);
        btnOK.setBackgroundResource(R.drawable.layout_border_full_green);
        imgTrueFalse.setVisibility(View.VISIBLE);
        String inputText = txtInput.getText().toString();
        if (en.equalsIgnoreCase(inputText) == true){
            imgTrueFalse.setImageResource(R.drawable.ic_true);
            cf = true;
            txtInput.setText(en);
            cf = false;
            txtInput.setEnabled(false);
            txtInput.setEnabled(true);

            if (learnActivity.noteModels.get(position).getLearned() == (byte) 0){
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 1);
                learnActivity.cfUpdate = true;
            }

            if (number == learnActivity.noteModels.size()){
                confirmTopicTransfer();
            }

        }else {
            cf = false;
            imgTrueFalse.setImageResource(R.drawable.ic_false);
        }
        imgTrueFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInput.setText("");
                imgTrueFalse.setVisibility(View.GONE);
            }
        });
    }

    private void handleBack() {

        if (number == 1){
            number = learnActivity.noteModels.size();
            Log.i("MOVE","Move To Last");

        }else {
            number--;
        }
        positionLearned ++;
        Log.i("Back_positionLearned",positionLearned +"");
        handleShow(false);
    }

    private void handleContinue() {
        if(learnActivity.noteModels.size() == number){
            confirmTopicTransfer();
        }else {

            number++;

            if (positionLearned > 0){

                positionLearned --;
                handleShow(false);

            }else {
                positionLearned = 0;
                handleShow(true);
            }
        }
    }

    private void handleShow(boolean rand) {

        if (number == 1){
            btnBack.setEnabled(false);

        }else {
            btnBack.setEnabled(true);
        }

        if (layoutTrueFalse_2.getVisibility() == View.VISIBLE){
            layoutTrueFalse_2.setVisibility(View.INVISIBLE);
        }

        if (imgTrueFalse.getVisibility() == View.VISIBLE){
            imgTrueFalse.setVisibility(View.GONE);
        }

        Random rd = new Random();
        if (rand){

            position = rd.nextInt(learnActivity.noteModels.size());
            Log.i("position",position+" ");

            if (learned != null){
                for (int i = 0; i < learned.size(); i++){

                    if (position == learned.get(i)){
                        handleShow(true);
                        return;
                    }
                }
            }else {

                number = 1;
            }

            learned.add(position);

        }else {

            position = learned.get(learned.size() - 1 - positionLearned);
            Log.i("position",position+" ");

        }

        txtNumber.setText(number+"");
        try {
            en = learnActivity.noteModels.get(position).getNoteSource();
            vi = learnActivity.noteModels.get(position).getNoteMeaning();
            txtVocabulary.setText(vi);
            languageSpeak = learnActivity.noteModels.get(position).getLangguageSource();;

            // rd.nextBoolean() == true || noteModels.size() < 4: word fill
            // rd.nextBoolean() == false && noteModels.size() >= 4: Multiple-choice
            if (rd.nextBoolean() || learnActivity.noteModels.size() < 4){
                //Hide: Multiple-choice
                layoutCheck_2.setVisibility(View.GONE);
                //Show: word fill
                layoutCheck_1.setVisibility(View.VISIBLE);
                txtInput.setText("");

            }else {
                //Hide: word fill
                layoutCheck_1.setVisibility(View.GONE);
                //Show: Multiple-choice
                layoutCheck_2.setVisibility(View.VISIBLE);
                handleLearn_2();

            }

        }catch (Exception e){
            e.printStackTrace();

        }


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShowEN(en, view);
            }
        });
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSound.setBackgroundResource(R.drawable.ic_sound2);
                String inputText = en;
//                String [] strings = inputText.split(" ");
//                String sourceText = strings[0];
//                for(int i = 1; i < strings.length; i++){
//                    sourceText += "+"+strings[i];
//                }
                sound = learnActivity.noteModels.get(position).getLangguageSource();
                speakOutOnline(inputText, languageSpeak);
            }
        });
    }

    private void handleLearn_2() {

        Random rd = new Random();
        int i1, i2, i3;

        while (true){
            i1 = rd.nextInt(learnActivity.noteModels.size()-1);
            if (i1 != position){
                break;
            }
        }

        while (true){
            i2 = rd.nextInt(learnActivity.noteModels.size()-1);
            if (i1 != i2 && i2 != position){
                break;
            }
        }

        while (true){
            i3 = rd.nextInt(learnActivity.noteModels.size()-1);
            if (i3 != i1 && i3 != i2 && i3 != position){
                break;
            }
        }

        int kq = rd.nextInt(4) + 1;

        if (kq == 1){
            radA.setText(learnActivity.noteModels.get(position).getNoteSource());
            radB.setText(learnActivity.noteModels.get(i1).getNoteSource());
            radC.setText(learnActivity.noteModels.get(i2).getNoteSource());
            radD.setText(learnActivity.noteModels.get(i3).getNoteSource());

        }else if (kq == 2){
            radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
            radB.setText(learnActivity.noteModels.get(position).getNoteSource());
            radC.setText(learnActivity.noteModels.get(i2).getNoteSource());
            radD.setText(learnActivity.noteModels.get(i3).getNoteSource());

        }else if (kq == 3){
            radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
            radB.setText(learnActivity.noteModels.get(i2).getNoteSource());
            radC.setText(learnActivity.noteModels.get(position).getNoteSource());
            radD.setText(learnActivity.noteModels.get(i3).getNoteSource());

        }else if (kq == 4){
            radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
            radB.setText(learnActivity.noteModels.get(i2).getNoteSource());
            radC.setText(learnActivity.noteModels.get(i3).getNoteSource());
            radD.setText(learnActivity.noteModels.get(position).getNoteSource());

        }

    }

    private void speakOutOnline(final String soundText, final String languageSpeak) {
        String url = "https://code.responsivevoice.org/getvoice.php?t="+soundText+"&tl="+languageSpeak+"&sv=&vn=&pitch=0.5&rate=0.5&vol=1";
        final MediaPlayer mediaPlayer = new MediaPlayer();

        Log.i("LINK","Link sound: "+url);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("SpeakOutOffline","IN1: "+soundText);
            speakOutOffline(soundText, languageSpeak);
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                btnSound.setBackgroundResource(R.drawable.ic_sound);
                mp.start();
            }
        });
        //Register a callback to be invoked when the status of a network stream's buffer has changed
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int i) {Log.i("BufferingUpdate","DK");
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("SpeakOutOffline","IN2: "+soundText);
                speakOutOffline(soundText, languageSpeak);
                return false;
            }
        });
    }

    private  void speakOutOffline(final String toSpeak, final String languageSpeak){
        final Locale locale = new Locale(languageSpeak);

        t1=new TextToSpeech(learnActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(locale);
                    Log.i("SpeakOutOffline","soundText: "+toSpeak);
                    Log.i("SpeakOutOffline","languageSpeak: " + locale.getLanguage());
                    btnSound.setBackgroundResource(R.drawable.ic_sound);
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });


    }

    private void handleShowEN(String en, View view) {

        Tooltip.make(this.learnActivity,
                new Tooltip.Builder(101)
                        .anchor(view, Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 10000)
                        .activateDelay(800)
                        .showDelay(300)
                        .text(en)
                        .maxWidth(316)
                        .withArrow(true)
                        .withOverlay(true)
                        .withStyleId(R.style.ToolTip)
                        .typeface(null)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();

    }

    private void addControls(View view) {
        imgVocabulary = view.findViewById(R.id.imgVocabulary);
        txtVocabulary = view.findViewById(R.id.txtVocabulary);
        txtNumber = view.findViewById(R.id.txtNumber);
        txtInput = view.findViewById(R.id.txtInput);
        btnOK = view.findViewById(R.id.btnOK);
        btnBack = view.findViewById(R.id.btnBack);
        imgBack = view.findViewById(R.id.imgBack);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnShow = view.findViewById(R.id.btnShow);
        btnSound = view.findViewById(R.id.btnSound);
        imgTrueFalse = view.findViewById(R.id.imgTrueFalse);
        imgTrueFalse_2 = view.findViewById(R.id.imgTrueFalse2);
        layoutTrueFalse_2 = view.findViewById(R.id.layoutTrueFalse2);
        txtTrueFalse = view.findViewById(R.id.txtTrueFalse);
        radA = view.findViewById(R.id.radA);
        radB = view.findViewById(R.id.radB);
        radC = view.findViewById(R.id.radC);
        radD = view.findViewById(R.id.radD);
        btnOK_2 = view.findViewById(R.id.btnOK_2);
        layoutCheck_1 = view.findViewById(R.id.layoutCheck1);
        layoutCheck_2 = view.findViewById(R.id.layoutCheck2);


        imgTrueFalse.setVisibility(View.GONE);
        layoutTrueFalse_2.setVisibility(View.INVISIBLE);

    }

    private void handleGET() {
        learned = new ArrayList<Integer>();
        if (!learnActivity.openedLearn ||
                learnActivity.id == -1 ||
                learnActivity.id != MyAction.getIdTopic(learnActivity) ||
                (learnActivity.noteModels != null && learnActivity.noteModels.size() == 0)){
            learnActivity.id = MyAction.getIdTopic(learnActivity);
            learnActivity.noteModels = new ArrayList<>();
            myIntentFilter();
            learnActivity.id = (learnActivity.id==-1) ? learnActivity.topicModes.get(learnActivity.id).getId() :
                    learnActivity.id;
            learnActivity.serverAPI.getVocabulary(learnActivity, learnActivity.id, Constants.LEARN);
        }else {
            if (learnActivity.noteModels != null &&learnActivity.noteModels.size() > 0)
                handleShow(true);
        }
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
                Handle.unregisterReceiver(context,mReceiver);
                if (intent.getSerializableExtra("NOTE") != null){
                    learnActivity.noteModels = (ArrayList<NoteModels>) intent.getSerializableExtra("NOTE");
                    number = 1;
                    if (learnActivity.noteModels.size()>0)
                        handleShow(true);
                    else {
                        learnActivity.openedLearn = false;
                        learnActivity.openedAdd = true;
                        MyAction.setActivityBulb(learnActivity, MyAction.LIST_ACTIVITY);
                        MyAction.setFragmentNew(learnActivity, MyAction.TOPIC_FRAGMENT);
                        IntentActivity.handleOpenTopicActivy(learnActivity);
                    }
                }else {
                    learnActivity.openedLearn = false;
                    learnActivity.onBackPressed();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handleGET();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LearnActivity) {
            this.learnActivity = (LearnActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
