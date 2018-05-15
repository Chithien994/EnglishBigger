package com.tcn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

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


public class LearnNowFragment extends Fragment implements View.OnClickListener{
    private LearnActivity learnActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";
    private static String TAG = "LEARN_NOW_FRAGMENT";
    private View thisView;

    private ImageView imgVocabulary, imgTrueFalse, imgTrueFalse_2, imgBack;
    private TextView txtVocabulary, txtNumber, txtTrueFalse_2;
    private EditText txtInput;
    private Button btnOK, btnOK_2, btnPrevious, btnContinue, btnSound, btnShow;
    private RadioButton radA, radB, radC, radD;
    private LinearLayout layoutTrueFalse_2;
    private ConstraintLayout layoutCheck_1, layoutCheck_2;

    private String thisLanguage = "";
    private String original, means; //
    private TextToSpeech t1;
    private String languageSpeak;
    private boolean up = true; //In this session: Marked "memorized" when first responded correctly
    private ArrayList<Integer> positionLearn;
    private int position = 0;
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
        btnPrevious.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnOK_2.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTrueFalse.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnSound.setOnClickListener(this);

        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (imgTrueFalse.getVisibility() == View.VISIBLE)
                    imgTrueFalse.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOK:
                handleCheck();
                break;
            case R.id.btnOK_2:
                handleCheck_2();
                break;
            case R.id.btnContinue:
                handleContinue();
                break;
            case R.id.btnPrevious:
                handlePrevious();
                break;
            case R.id.imgBack:
                learnActivity.onBackPressed();
                break;
            case R.id.imgTrueFalse:
                txtInput.setText("");
                imgTrueFalse.setVisibility(View.GONE);
                break;
            case R.id.btnShow:
                handleShowResult(original, view);
                break;
            case R.id.btnSound:
                speakOutOnline(original, languageSpeak);
                break;
            default:break;
        }
    }

    //Show dialog box confirming topic transfer
    private void confirmTopicTransfer(){
        //Check whether the ad has loaded
        if (mInterstitialAd.isLoaded()) {
            //Show ad
            mInterstitialAd.show();
        }
        new AlertDialog.Builder(learnActivity)
                .setMessage(getActivity().getString(R.string.finish))
                .setCancelable(false)
                .setNegativeButton(getActivity().getString(R.string.repeat), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadAdFull(getString(R.string.ad_id_full_2));//Reload the new ad
                        //Set up random vocabulary for the next session
                        //And perform the next "learn" session
                        addRadPositionArrAndShow();
                    }
                })
                .setPositiveButton(getActivity().getString(R.string.otherTopic), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyAction.setFragmentNew(learnActivity, MyAction.TOPIC_FRAGMENT);
                        MyAction.setActivityBulb(learnActivity, MyAction.LEARN_ACTIVITY);
                        MyAction.setFragmentBulb(learnActivity, MyAction.LEARN_FRAGMENT);
                        IntentActivity.handleOpenTopicActivy(learnActivity);
                    }
                }).show();
        Log.i("MOVE","Move To First");
    }

    private void handleCheck_2() {
        String inputText = null;
        //Check which user selects the answer: A or B or C or D
        if (radA.isChecked()){
            inputText = radA.getText().toString();
        }else if (radB.isChecked()){
            inputText = radB.getText().toString();
        }else if (radC.isChecked()){
            inputText = radC.getText().toString();
        }else if (radD.isChecked()){
            inputText = radD.getText().toString();
        }

        layoutTrueFalse_2.setVisibility(View.VISIBLE);//Show layout notify
        //Check the answer
        if (original.equalsIgnoreCase(inputText)){
            //It's correct

            //Message to server: Remember this vocabulary
            if (learnActivity.noteModels.get(position).getLearned() == (byte) 0 && up){
                MyAction.setLoadedTopic(learnActivity, false);
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 1);
            }
            //Notify: Correct
            imgTrueFalse_2.setImageResource(R.drawable.ic_true);
            txtTrueFalse_2.setText(R.string.exactly);
        }else {
            //Wrong answer

            up = false;//The second answer will not be recognized
            //Message to server: Forget this vocabulary
            if (learnActivity.noteModels.get(position).getLearned() == (byte) 1){
                MyAction.setLoadedTopic(learnActivity, false);
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 0);
            }
            //Notify: Wrong answer
            imgTrueFalse_2.setImageResource(R.drawable.ic_false);
            txtTrueFalse_2.setText(R.string.wrong);
        }
    }

    private void handleCheck() {
        imgTrueFalse.setVisibility(View.VISIBLE);//Show notify
        String inputText = txtInput.getText().toString(); //Get the answer content
        //Check the answer
        if (original.equalsIgnoreCase(inputText)){
            //It's correct
            Handle.hideKeyboard(learnActivity, thisView);//Close the keyboard
            imgTrueFalse.setImageResource(R.drawable.ic_true);
            txtInput.setText(original);


            //Message to server: Remember this vocabulary
            if (learnActivity.noteModels.get(position).getLearned() == (byte) 0 && up){
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 1);
                MyAction.setLoadedTopic(learnActivity, false);
            }

        }else {
            //Wrong answer

            //Message to server: Forget this vocabulary
            if (learnActivity.noteModels.get(position).getLearned() == (byte) 1){
                learnActivity.serverAPI.setLearned(learnActivity, learnActivity.noteModels.get(position).getId(), (byte) 0);
                MyAction.setLoadedTopic(learnActivity, false);
            }
            up = false;
            imgTrueFalse.setImageResource(R.drawable.ic_false);
        }
    }
    //Previous vocabulary
    private void handlePrevious() {
        up = false;
        position--;
        if(position < 0)
            position = learnActivity.noteModels.size()-1;
        //Execute session with position from: positionLearn.get(position)
        handleShow(positionLearn.get(position));

    }

    //The next vocabulary
    private void handleContinue() {
        up = true;
        position++;
        if(position < learnActivity.noteModels.size()){
            //Execute session with position from: positionLearn.get(position)
            handleShow(positionLearn.get(position));
        }else {
            position = 0;
            //Show dialog box confirming topic transfer
            confirmTopicTransfer();
        }
    }
    //Execute session with position from: position
    private void handleShow(final int position) {

        //Hide the "notify" response
        if (layoutTrueFalse_2.getVisibility() == View.VISIBLE)
            layoutTrueFalse_2.setVisibility(View.INVISIBLE);

        if (imgTrueFalse.getVisibility() == View.VISIBLE)
            imgTrueFalse.setVisibility(View.GONE);

        Random rd = new Random();

        txtNumber.setText((this.position+1)+"/"+learnActivity.noteModels.size());
        original = learnActivity.noteModels.get(position).getNoteSource();
        means = learnActivity.noteModels.get(position).getNoteMeaning();
        txtVocabulary.setText(means);
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
            handleLearn_2(position);

        }
    }

    private void handleLearn_2(int position) {

        Random rd = new Random();
        int i1, i2, i3;

        //Take out 3 different words and different from the results
        while (true){
            i1 = rd.nextInt(learnActivity.noteModels.size());
            if (i1 != position){
                break;
            }
        }

        while (true){
            i2 = rd.nextInt(learnActivity.noteModels.size());
            if (i2 != i1 && i2 != position){
                break;
            }
        }

        while (true){
            i3 = rd.nextInt(learnActivity.noteModels.size());
            if (i3 != i1 && i3 != i2 && i3 != position){
                break;
            }
        }

        int kq = rd.nextInt(4) + 1;//Get the resultant position random.

        switch (kq){
            case 1:
                radA.setText(learnActivity.noteModels.get(position).getNoteSource());
                radB.setText(learnActivity.noteModels.get(i1).getNoteSource());
                radC.setText(learnActivity.noteModels.get(i2).getNoteSource());
                radD.setText(learnActivity.noteModels.get(i3).getNoteSource());
                break;
            case 2:
                radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
                radB.setText(learnActivity.noteModels.get(position).getNoteSource());
                radC.setText(learnActivity.noteModels.get(i2).getNoteSource());
                radD.setText(learnActivity.noteModels.get(i3).getNoteSource());
                break;
            case 3:
                radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
                radB.setText(learnActivity.noteModels.get(i2).getNoteSource());
                radC.setText(learnActivity.noteModels.get(position).getNoteSource());
                radD.setText(learnActivity.noteModels.get(i3).getNoteSource());
                break;
            case 4:
                radA.setText(learnActivity.noteModels.get(i1).getNoteSource());
                radB.setText(learnActivity.noteModels.get(i2).getNoteSource());
                radC.setText(learnActivity.noteModels.get(i3).getNoteSource());
                radD.setText(learnActivity.noteModels.get(position).getNoteSource());
                break;
            default:break;
        }

    }

    private void speakOutOnline(final String soundText, final String languageSpeak) {
        btnSound.setBackgroundResource(R.drawable.ic_sound2);
        String url = "https://code.responsivevoice.org /getvoice.php?t="+soundText+"&tl="+languageSpeak+"&sv=&vn=&pitch=0.5&rate=0.5&vol=1";
        final MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
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
                    btnSound.setBackgroundResource(R.drawable.ic_sound);
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });


    }

    private void handleShowResult(String en, View view) {
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
        txtVocabulary = view.findViewById(R.id.txtVocabulary);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        imgBack = view.findViewById(R.id.imgBack);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnShow = view.findViewById(R.id.btnShow);
        btnSound = view.findViewById(R.id.btnSound);
        txtNumber = view.findViewById(R.id.txtNumber);

        //Learning style: 1
        layoutCheck_1 = view.findViewById(R.id.layoutCheck1);
        imgVocabulary = view.findViewById(R.id.imgVocabulary);
        txtInput = view.findViewById(R.id.txtInput);
        btnOK = view.findViewById(R.id.btnOK);
        imgTrueFalse = view.findViewById(R.id.imgTrueFalse);
        imgTrueFalse.setVisibility(View.GONE);

        //Learning style: 2
        layoutCheck_2 = view.findViewById(R.id.layoutCheck2);
        imgTrueFalse_2 = view.findViewById(R.id.imgTrueFalse2);
        layoutTrueFalse_2 = view.findViewById(R.id.layoutTrueFalse2);
        txtTrueFalse_2 = view.findViewById(R.id.txtTrueFalse_2);
        radA = view.findViewById(R.id.radA);
        radB = view.findViewById(R.id.radB);
        radC = view.findViewById(R.id.radC);
        radD = view.findViewById(R.id.radD);
        btnOK_2 = view.findViewById(R.id.btnOK_2);
        layoutTrueFalse_2.setVisibility(View.INVISIBLE);

        //If the vocabulary was taken earlier
        if (learnActivity.noteModels != null
                && learnActivity.noteModels.size() > 0)
            addRadPositionArrAndShow();

    }

    //
    private void handleGET() {

        //learnActivity.id == -1: Did not learn any previous lessons
        //learnActivity.id != MyAction.getIdTopic(learnActivity): The previous lesson id is different from the current lesson id
        //(learnActivity.noteModels != null && learnActivity.noteModels.size() == 0): Vocabulary list is empty
        if (learnActivity.id == -1 ||
                learnActivity.id != MyAction.getIdTopic(learnActivity) ||
                (learnActivity.noteModels != null && learnActivity.noteModels.size() == 0)) {

            //If the vocabulary has not been previously retrieved
            learnActivity.id = MyAction.getIdTopic(learnActivity);
            learnActivity.noteModels = new ArrayList<>();
            myIntentFilter();
            learnActivity.id = (learnActivity.id == -1) ? learnActivity.topicModes.get(0).getId() :
                    learnActivity.id;
            //Get the list of words from the server, with idTopic: learnActivity.id
            learnActivity.serverAPI.getVocabulary(learnActivity, learnActivity.id, Constants.LEARN);
        }
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
                Handle.unregisterReceiver(context,mReceiver);
                learnActivity.topicLoaded = true;
                if (intent.getSerializableExtra("NOTE") != null){
                    learnActivity.noteModels = (ArrayList<NoteModels>) intent.getSerializableExtra("NOTE");
                    if (learnActivity.noteModels.size()>0)
                        addRadPositionArrAndShow();
                    else {
                        learnActivity.openedLearn = false;
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

    //Set up random vocabulary for the next session
    //And perform the next "learn" session
    private void addRadPositionArrAndShow() {
        position = 0;
        Set arrPosition = new LinkedHashSet<Integer>();
        Random rd = new Random();
        while (arrPosition.size() < learnActivity.noteModels.size())
            arrPosition.add(rd.nextInt(learnActivity.noteModels.size()));
        positionLearn = new ArrayList<>(arrPosition);
        //Execute session with position from: positionLearn.get(position)
        handleShow(positionLearn.get(position));


    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyAction.getFragmentNew(learnActivity)==MyAction.LEARN_FRAGMENT)
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

}
