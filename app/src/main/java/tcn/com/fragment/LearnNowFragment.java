package tcn.com.fragment;

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

import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Constants;
import tcn.com.englishbigger.LearnActivity;
import tcn.com.englishbigger.R;
import it.sephiroth.android.library.tooltip.Tooltip;
import tcn.com.models.NoteModels;


public class LearnNowFragment extends Fragment {
    private LearnActivity learnActivity;
    public static String MY_BRC_NOTE = "MY_BRC_NOTE";

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
    private ArrayList<NoteModels> noteModels;
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
        View view = inflater.inflate(R.layout.fragment_learn_now, container, false);
        loadAdFull(getString(R.string.ad_id_full_1));
        loadAdView(view);
        addControls(view);
        addEvents();
        return view;
    }

    private void loadAdFull(String ad_id){
        mInterstitialAd = new InterstitialAd(learnActivity);
        mInterstitialAd.setAdUnitId(ad_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void loadAdView(View view) {
        AdView adView = null;
        // Sample AdMob app ID: ca-app-pub-7825788831137519~8179742154
        MobileAds.initialize(learnActivity, getString(R.string.ad_mob_app_id));
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
                getActivity().onBackPressed();
                getActivity().finish();
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

            if (noteModels.get(position).getLearned() == (byte) 0){
                learnActivity.serverAPI.setLearned(learnActivity, noteModels.get(position).getId(), (byte) 1);
            }

            imgTrueFalse_2.setImageResource(R.drawable.ic_true);
            txtTrueFalse.setText(R.string.exactly);
        }else {

            imgTrueFalse_2.setImageResource(R.drawable.ic_false);
            txtTrueFalse.setText(R.string.wrong);
        }
    }

    private void handleCheck() {
        btnOK.setBackgroundResource(R.color.colorButtonOK);
        imgTrueFalse.setVisibility(View.VISIBLE);
        String inputText = txtInput.getText().toString();
        if (en.equalsIgnoreCase(inputText) == true){
            imgTrueFalse.setImageResource(R.drawable.ic_true);
            cf = true;
            txtInput.setText(en);
            cf = false;
            txtInput.setEnabled(false);
            txtInput.setEnabled(true);

            if (noteModels.get(position).getLearned() == (byte) 0){
                learnActivity.serverAPI.setLearned(learnActivity, noteModels.get(position).getId(), (byte) 1);
            }

            if (number == noteModels.size()){
                new AlertDialog.Builder(learnActivity)
                        .setMessage(getActivity().getString(R.string.finish))
                        .setCancelable(false)
                        .setNegativeButton(getActivity().getString(R.string.repeat), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadAdFull(getString(R.string.ad_id_full_1));
                                number = 1;
                                learned.clear();
                                handleShow(true);
                            }
                        })
                        .setPositiveButton(getActivity().getString(R.string.otherTopic), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(learnActivity, TopicActivity.class);
                                intent.putExtra("type", 0);
                                intent.putExtra("id", 0);
                                startActivity(intent);
                                learnActivity.finish();


                            }
                        }).show();

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
            number = noteModels.size();
            Log.i("MOVE","Move To Last");

        }else {
            number--;
        }
        positionLearned ++;
        Log.i("Back_positionLearned",positionLearned +"");
        handleShow(false);
    }

    private void handleContinue() {

        if(noteModels.size() == number){
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

                            if (learnActivity.cfFinish){

                                learnActivity.finish();

                            }else {
                                Intent intent = new Intent(learnActivity, TopicActivity.class);
                                intent.putExtra("type", 0);
                                intent.putExtra("id", 0);
                                startActivity(intent);
                                learnActivity.finish();
                            }



                        }
                    }).show();
            Log.i("MOVE","Move To First");

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
            layoutTrueFalse_2.setVisibility(View.GONE);
        }

        if (imgTrueFalse.getVisibility() == View.VISIBLE){
            imgTrueFalse.setVisibility(View.GONE);
        }

        if (layoutCheck_1.getVisibility() == View.VISIBLE){
            txtInput.setText("");
        }

        Random rd = new Random();
        if (rand){

            position = rd.nextInt(noteModels.size());
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
            en = noteModels.get(position).getNoteSource();
            vi = noteModels.get(position).getNoteMeaning();
            txtVocabulary.setText(vi);
            languageSpeak = noteModels.get(position).getLangguageSource();;

            // rd.nextBoolean() == true || noteModels.size() < 4: word fill
            // rd.nextBoolean() == false && noteModels.size() >= 4: Multiple-choice
            if (rd.nextBoolean() || noteModels.size() < 4){
                //Hide: Multiple-choice
                layoutCheck_2.setVisibility(View.GONE);
                //Show: word fill
                layoutCheck_1.setVisibility(View.VISIBLE);

            }else {
                //Hide: word fill
                layoutCheck_1.setVisibility(View.GONE);
                //Show: Multiple-choice
                layoutCheck_2.setVisibility(View.VISIBLE);
                handleLearn_2();

            }

        }catch (Exception e){
            e.printStackTrace();


            if (learnActivity.topicModes.size() > 0){
                JSONObject object = new JSONObject();
                try {
                    object.put("idTopic", learnActivity.topicModes.get(0).getId());
                    object.put("type","_SHOW");
                    learnActivity.serverAPI.getVocabulary(learnActivity, object, Constants.LEARN);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }else {


            }

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
                sound = noteModels.get(position).getLangguageSource();
                speakOutOnline(inputText, languageSpeak);
            }
        });
    }

    public class AsyncTaskRandom extends AsyncTask<Boolean, Void, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Boolean... aBooleen) {

            Random rd = new Random();
            position = rd.nextInt(noteModels.size());

            return position;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    private void handleLearn_2() {

        Random rd = new Random();
        int i1, i2, i3;

        while (true){
            i1 = rd.nextInt(noteModels.size()-1);
            if (i1 != position){
                break;
            }
        }

        while (true){
            i2 = rd.nextInt(noteModels.size()-1);
            if (i1 != i2 && i2 != position){
                break;
            }
        }

        while (true){
            i3 = rd.nextInt(noteModels.size()-1);
            if (i3 != i1 && i3 != i2 && i3 != position){
                break;
            }
        }

        int kq = rd.nextInt(4) + 1;

        if (kq == 1){
            radA.setText(noteModels.get(position).getNoteSource());
            radB.setText(noteModels.get(i1).getNoteSource());
            radC.setText(noteModels.get(i2).getNoteSource());
            radD.setText(noteModels.get(i3).getNoteSource());

        }else if (kq == 2){
            radA.setText(noteModels.get(i1).getNoteSource());
            radB.setText(noteModels.get(position).getNoteSource());
            radC.setText(noteModels.get(i2).getNoteSource());
            radD.setText(noteModels.get(i3).getNoteSource());

        }else if (kq == 3){
            radA.setText(noteModels.get(i1).getNoteSource());
            radB.setText(noteModels.get(i2).getNoteSource());
            radC.setText(noteModels.get(position).getNoteSource());
            radD.setText(noteModels.get(i3).getNoteSource());

        }else if (kq == 4){
            radA.setText(noteModels.get(i1).getNoteSource());
            radB.setText(noteModels.get(i2).getNoteSource());
            radC.setText(noteModels.get(i3).getNoteSource());
            radD.setText(noteModels.get(position).getNoteSource());

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
        layoutTrueFalse_2.setVisibility(View.GONE);

        myIntentFilter();
        learnActivity.serverAPI.getVocabulary(learnActivity, learnActivity.handle.getIdTopic(learnActivity), Constants.LEARN);
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
                    noteModels = new ArrayList<>();
                    learned = new ArrayList<Integer>();
                    noteModels = (ArrayList<NoteModels>) intent.getSerializableExtra("NOTE");
                    handleShow(true);
                }else {
                    learnActivity.finish();
                }

                getActivity().unregisterReceiver(mReceiver);
            }
        }
    };


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
