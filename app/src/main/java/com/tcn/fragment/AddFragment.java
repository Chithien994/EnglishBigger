package com.tcn.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.tcn.adapters.LanguageAdapter;
import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.R;
import com.tcn.handle.AdFast;
import com.tcn.models.LocaleModels;


public class AddFragment extends Fragment {

    public static String cfAdded = "BroadcastAction";
    private LearnActivity learnActivity;
    private EditText txtAdd;
    private EditText txtTranslated;
    private Button btnAdd, btnTranslate;
    private ImageView btnSoundTop, btnSoundBottom, btnBack,imgReverse;
    private RadioButton radAutoResult, radNoAutoResult;

    private boolean cfAutoResult = true; // cfAutoResult: Verification is automatically translated or not (True automatic translation)
    private String url = "",en,vi,sound = "";
    private boolean bool = true; // "True" translates and adds or "false" translates only
    private boolean cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false
    private Timer mTimer = null;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonArrayRequest;
    private ProgressBar progressBar;
    private TextToSpeech t1;
    private Spinner spLanguage1, spLanguage2, spNameTopic;
    private ArrayList<LocaleModels> dsLocaleModelses;
    private LanguageAdapter languageAdapter;
    private String languageBefore = "en", languageAfter = "en";
    private int iBefore = 1, iAfter = 0;
    private ProgressDialog dialog;
    private boolean isCf = false;
    private ArrayList<String> dsNameTopic;
    private ArrayAdapter<String> arrayAdapter;
    private int idTopic;

    public AddFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        //The two main components handle the control and event handlers
        AdFast.loadAdView(learnActivity, (RelativeLayout) view.findViewById(R.id.totalLayout), getString(R.string.ad_id_small_4));
        addControls(view);
        addEvents();
        return view;
    }

    private void addEvents() {

        //Used to add new words.
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBeforeAddNewWord();
            }
        });

        //Used to translate.
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               handleBeforeTranslate();
            }
        });

        //Used to automatically translate.
        txtAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Used to automatically translate or cancel auto-translation for a period of time.
                // cfAutoResult: Verification is automatically translated or not (True automatic translation)
                if (cfAutoResult){
                    if (isCf){//Restore
                        btnAdd.setEnabled(true);
                        btnTranslate.setEnabled(true);
                        isCf = false;
                    }
                    handleTimer();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtTranslated.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnAdd.setEnabled(true);
                btnTranslate.setEnabled(true);
                btnSoundBottom.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Use to pronounce words or phrases before translating
        btnSoundTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSoundTop.setEnabled(false); //Restrict users to click multiple times
                en = txtAdd.getText().toString();
                cf = true; // "true": Notify when the input box is empty, Automatic translation at import will be false
                sound = handleFilterBeforeAdd(en);
                if (sound.equals("")==false){
                    btnSoundTop.setImageResource(R.drawable.ic_sound2);

                    speakOutOnline(sound,languageBefore);
                }
            }
        });

        //Use to pronounce words or phrases after translation
        btnSoundBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSoundBottom.setEnabled(false); //Restrict users to click multiple times
                vi =txtTranslated.getText().toString();
                cf = true; // "true": Notify when the input box is empty, Automatic translation at import will be false
                sound = handleFilterBeforeAdd(vi);
                if (sound.equals("")==false){
                    btnSoundBottom.setImageResource(R.drawable.ic_sound2);
                    speakOutOnline(sound,languageAfter);
                    cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false

                }else {
                    btnTranslate.performClick();
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learnActivity.onBackPressed();
            }
        });

        //Select language before when translation
        handleSelectLanguageBeforeWhenTranslation();

        //Select language after when translation
        handleSelectLanguageAfterWhenTranslation();

        //Reverse language
        reverseLanguage();

        //Check which user selects which topic?
        checkTopic();

        //The test is automatically translated or not
        checkCFAutoResult();

        radAutoResult.setOnClickListener(checked);
        radNoAutoResult.setOnClickListener(checked);
    }

    private void reverseLanguage() {

        imgReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spLanguage1.setSelection(iAfter);
                spLanguage2.setSelection(iBefore);
            }
        });
    }

    private void checkCFAutoResult() {

        if (radAutoResult.isChecked()){

            cfAutoResult = true;

        }else if (radNoAutoResult.isChecked()){

            cfAutoResult = false;
        }

    }

    View.OnClickListener checked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            checkCFAutoResult();
        }
    };

    private void checkTopic() {
        spNameTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idTopic = learnActivity.topicModes.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void handleBeforeTranslate() {
        btnTranslate.setEnabled(false); //Restrict users to click multiple times
        en = txtAdd.getText().toString();
        if (handleFilterBeforeAdd(en).equals("")){
            isCf = true;
            btnTranslate.setEnabled(false); //Restrict user to press button when input is empty
            btnAdd.setEnabled(false);//Restrict user to press button when input is empty
            Toast.makeText(learnActivity, getString(R.string.msgYouHaveNotEnteredAnythingYet),Toast.LENGTH_LONG).show();
        }else {
            progressBar.setVisibility(View.VISIBLE); //Show "progress bar" when translating or adding
            txtAdd.setEnabled(false);
            txtAdd.setEnabled(true);

            cf = true; // "true": Notify when the input box is empty, Automatic translation at import will be false
            try {
                bool = false; // "True" translates and adds or "false" translates only
                handleTimeCancelDialog();//After 20 seconds untouched words or phrases will automatically cancel
                handleTranslateText(en);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBeforeAddNewWord() {
        try {
            jsonArrayRequest.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
        btnAdd.setEnabled(false); //Restrict users to click multiple times
        en = txtAdd.getText().toString();
        try {
            cf = true; // "true": Notify when the input box is empty
            if (handleFilterBeforeAdd(en).equals("")){
                txtTranslated.setText("");
                if (cf){
                    Toast.makeText(learnActivity, getString(R.string.msgYouHaveNotEnteredAnythingYet),Toast.LENGTH_LONG).show();
                    cf = false; // "true": Notify when the input box is empty
                    isCf = true;
                    btnTranslate.setEnabled(false);//Restrict user to press button when input is empty
                    return;
                }
            }else {
                myIntentFilter();
                bool = true; // "True" translates and adds or "false" translates only
                dialog = new ProgressDialog(learnActivity);
                dialog.setMessage(getString(R.string.pleaseWait));
                dialog.setCancelable(false);
                dialog.show(); //Show the waiting request dialog
                handleTimeCancelDialog();//After 20s have not added words or phrases will automatically cancel dialog

                // cfAutoResult: Verification is automatically translated or not (True automatic translation)
                if (cfAutoResult){

                    btnAdd.setEnabled(true); //Restore

                    progressBar.setVisibility(View.VISIBLE); //Show "progress bar" when translating or adding

                    //Translate meaning before adding (No need to enter meaning)
                    handleTranslateText(handleFilterBeforeAdd(en)); //Translate
                }else {

                    if (txtTranslated.getText().toString().equalsIgnoreCase("")){

                        //When not entered meaning

                        Toast.makeText(learnActivity, getString(R.string.youHaveNotEnteredTheMeaningOfIt),Toast.LENGTH_LONG).show();
                        cf = false; // "true": Notify when the input box is empty
                        isCf = true;
                        btnTranslate.setEnabled(false);//Restrict user to press button when input is empty
                        getActivity().unregisterReceiver(mReceiver); //Cancel registration
                        dialog.cancel(); //Close the notification dialog


                    }else {

                        //When entered meaning

                        //Do not translate the meaning before adding (Need to enter meaning)
                        handleAdd(handleFilterBeforeAdd(en));
                    }

                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Select language after when translation
    private void handleSelectLanguageAfterWhenTranslation() {
        spLanguage2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languageAfter = dsLocaleModelses.get(i).getLanguage();
                Log.d("TAB_SP","i = "+i + " iBefore = "+iBefore);
                if (i == iBefore){
                    spLanguage1.setSelection(iAfter);
                }
                iAfter = i;
                cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false
                if (handleFilterBeforeAdd(txtAdd.getText().toString()).equals("")==false){
                    btnTranslate.performClick();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Select language before when translation
    private void handleSelectLanguageBeforeWhenTranslation() {
        spLanguage1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languageBefore = dsLocaleModelses.get(i).getLanguage();
                Log.d("TAB_SP","iAfter = "+iAfter + " i = "+i);
                if (i == iAfter){
                    spLanguage2.setSelection(iBefore);
                }
                iBefore = i;
                cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false
                if (handleFilterBeforeAdd(txtAdd.getText().toString()).equals("")==false){
                    btnTranslate.performClick();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //After 20s have not added words or phrases will automatically cancel dialog
    private void handleTimeCancelDialog(){
        try {
            mTimer.cancel();
        }catch (Exception e){

        }
        mTimer= new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                learnActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (bool){
                                getActivity().unregisterReceiver(mReceiver);
                                Log.d("unregisterReceiver","Unregister Receiver");
                            }else {
                                btnTranslate.setEnabled(true);
                            }
                            requestQueue.stop();
                            progressBar.setVisibility(View.GONE);
                            mTimer.cancel();
                            try {
                                dialog.cancel();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Toast.makeText(learnActivity,getActivity().getString(R.string.errorInternetConnectionProblems), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },20000,100);
    }


    private void handleTimer(){
        progressBar.setVisibility(View.VISIBLE); //Show "progress bar" when translating or adding
        try {
            mTimer.cancel();
        }catch (Exception e){

        }
        mTimer= new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                learnActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        en = txtAdd.getText().toString();
                        try {
                            cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false
                            bool = false; // "True" translates and adds or "false" translates only
                            handleTranslateText(en);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mTimer.cancel();
                    }
                });
            }
        },700,1000);
    }

    //Function used to check the existence of new words
    private String handleFilterBeforeAdd(String en) {
        if (cf){
            txtAdd.setEnabled(false); //Disable the input box to close the keyboard
            txtAdd.setEnabled(true); //Enabling the input box again
        }
        String enl = "";
        for(int i = 0; i <en.length(); i ++){
            if (String.copyValueOf(new char[]{en.charAt(i)}).equals("\n")==false && String.copyValueOf(new char[]{en.charAt(i)}).equals(".")==false && String.copyValueOf(new char[]{en.charAt(i)}).equals("?")==false && String.copyValueOf(new char[]{en.charAt(i)}).equals(";")==false){
                enl += en.charAt(i);
            }
        }
        if (enl.equals("")){
            txtTranslated.setText("");
            if (cf){ // "true": Notify when the input box is empty, Automatic translation at import will be false
                Toast.makeText(learnActivity, getString(R.string.msgYouHaveNotEnteredAnythingYet),Toast.LENGTH_LONG).show();
                return "";
            }
        }else {
            return enl;
        }
        return "";
    }

    //Function used to add new words
    private void handleAdd(final String enl) {

        btnAdd.setEnabled(true);
        bool = false;

        vi = txtTranslated.getText().toString();
        if (!handleFilterBeforeAdd(vi).equalsIgnoreCase(handleFilterBeforeAdd(enl))){  //Before adding: check the new word and its meaning if the same. Then issue a "add" or "add not" confirmation request.

            add(enl,vi);

        }else {

            new AlertDialog.Builder(learnActivity)
                    .setMessage(getString(R.string.msgItsVocabularyAndMeaningAreTheSameDoYouReallyWantToAdd))
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {

                                dialog.cancel();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            add(enl,vi);
                        }
                    }).show();
        }
    }

    private void add(String enl, String vi) {
        String type = "_INSERT";
        JSONObject object = new JSONObject();

        try {
            JSONArray jsonArrayContent = new JSONArray();
            JSONObject objectContent1 = new JSONObject();
            JSONObject objectContent2 = new JSONObject();

            objectContent1.put("note",enl);
            objectContent1.put("langguage",languageBefore);
            objectContent2.put("note",vi);
            objectContent2.put("langguage",languageAfter);

            jsonArrayContent.put(objectContent1);
            jsonArrayContent.put(objectContent2);

            object.put("idTopic",idTopic);
            object.put("type",type);
            object.put("content",jsonArrayContent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        learnActivity.serverAPI.postVocabulary(learnActivity,object);
    }


    //Language Translation
    public void handleTranslateText(final String sourceText) throws UnsupportedEncodingException {

        final String query = URLEncoder.encode(sourceText, "UTF-8");

        url = "https://statickidz.com/scripts/traductor/?q="+query+"&source="+languageBefore+"&target="+languageAfter;
         jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        String vn;
                        progressBar.setVisibility(View.GONE); //Hide the "progeress bar" after translation or add
                        try {
                            vn = response.getString("translation");
                            fixShowVI(vn);  //Fix errors when entering whitespace only

                            if (bool == true){ // "True" translates and adds or "false" translates only
                                handleAdd(sourceText);  //Fix errors when entering whitespace only
                            }

                        } catch (JSONException e) {
                            vn = query;
                            fixShowVI(vn);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                txtTranslated.setText("");
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });
        requestQueue = Volley.newRequestQueue(this.learnActivity);
        requestQueue.add(jsonArrayRequest);

    }

    //Fix errors when entering whitespace only
    private void fixShowVI(String vn) {
        btnTranslate.setEnabled(true);//Restore
        txtTranslated.setText(vn);
        try {
            if (String.copyValueOf(new char[]{vn.charAt(0)}).equals("%") && String.copyValueOf(new char[]{vn.charAt(1)}).equals("0") && String.copyValueOf(new char[]{vn.charAt(2)}).equals("A")){
                txtTranslated.setText("");
                isCf = true;
                btnTranslate.setEnabled(false); //Restrict user to press button when input is empty
                btnAdd.setEnabled(false);//Restrict user to press button when input is empty
                if (cf){
                    Toast.makeText(learnActivity, getString(R.string.msgYouHaveNotEnteredAnythingYet),Toast.LENGTH_LONG).show();
                    cf = false; // "true": Notify when the input box is empty, Automatic translation at import will be false
                }
                return;
            }else {
                if (cf){
                    mTimer.cancel();
                    cf=false; // "true": Notify when the input box is empty, Automatic translation at import will be false
                }
                txtTranslated.setText(vn);
            }
        }catch (Exception ex){

            ex.printStackTrace();
        }
    }

    //Use to pronounce words or phrases (online).
    private void speakOutOnline(String soundText, String languageSpeak) {
        switch (languageSpeak){
            case "vi":
                languageSpeak = "en-GB";
                break;
            case "ja":
                break;
            case "zh":
                languageSpeak = "zh-CN";
                break;
            default:{
                languageSpeak = "en-GB";
                break;
            }
        }
        //https://responsivevoice.org/
        String url = "https://code.responsivevoice.org/getvoice.php?t="+soundText+"&tl="+languageSpeak+"&sv=&vn=&pitch=0.5&rate=0.5&vol=1";
        String url1 = "http://soundoftext.com/static/sounds/"+soundText+".mp3";
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.i("LINK","Link sound: "+url);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ER","ER1: "+e.toString());
            Log.i("CATCH","IN1: "+sound);
            speakOutOffline(sound);
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                btnSoundTop.setImageResource(R.drawable.ic_sound);
                btnSoundBottom.setImageResource(R.drawable.ic_sound);
                btnSoundTop.setEnabled(true);
                btnSoundBottom.setEnabled(true);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("CATCH","IN2: "+sound);
                speakOutOffline(sound);
                return false;
            }
        });
    }

    //Use to pronounce words or phrases (offline).
    private  void speakOutOffline(final String toSpeak){

        t1=new TextToSpeech(learnActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    btnSoundTop.setEnabled(true);
                    btnSoundBottom.setEnabled(true);
                    btnSoundTop.setImageResource(R.drawable.ic_sound);
                    btnSoundBottom.setImageResource(R.drawable.ic_sound);
                    Log.i("CATCH","OUT: "+toSpeak);
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });


    }

    private void addControls(View view) {
        btnAdd = view.findViewById(R.id.btnAdd);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        txtAdd = view.findViewById(R.id.txtAdd);
        txtTranslated = view.findViewById(R.id.txtTranslated);
        progressBar = view.findViewById(R.id.progressBar);
        btnSoundTop = view.findViewById(R.id.btnSoundTop);
        btnSoundBottom = view.findViewById(R.id.btnSoundBottom);
        spLanguage1 = view.findViewById(R.id.spLanguage1);
        spLanguage2 = view.findViewById(R.id.spLanguage2);
        spNameTopic = view.findViewById(R.id.spNameTopic);
        btnBack = view.findViewById(R.id.btnBack);
        radAutoResult = view.findViewById(R.id.radAutoResult);
        radNoAutoResult = view.findViewById(R.id.radNoAutoResult);
        imgReverse = view.findViewById(R.id.imgReverse);

        //Show when translating or adding new words.
        progressBar.setVisibility(View.GONE);

        //Initialize the language before and after when translating or adding
        handleLanguageBeforeAfterTranslation();

        setNameTopicToSpinner();

        handleGET();
    }

    private void handleGET() {
        spNameTopic.setSelection(learnActivity.pst);
        Log.i("position", learnActivity.pst +"");
    }


    private void setNameTopicToSpinner(){
        dsNameTopic = new ArrayList<>();

        for (int i = 0; i < learnActivity.topicModes.size(); i++){
            dsNameTopic.add(i, learnActivity.topicModes.get(i).getName());
        }

        arrayAdapter =  new ArrayAdapter<String>(learnActivity, R.layout.support_simple_spinner_dropdown_item, dsNameTopic);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spNameTopic.setAdapter(arrayAdapter);
    }

    //Initialize the language before and after when translating or adding
    private void handleLanguageBeforeAfterTranslation() {
        dsLocaleModelses = new ArrayList<>();
        dsLocaleModelses.add(new LocaleModels("en"));
        dsLocaleModelses.add(new LocaleModels("vi"));
        dsLocaleModelses.add(new LocaleModels("zh"));
        dsLocaleModelses.add(new LocaleModels("ja"));
        languageAdapter = new LanguageAdapter(this.getActivity(), R.layout.item_language, dsLocaleModelses);
        spLanguage1.setAdapter(languageAdapter);
        spLanguage2.setAdapter(languageAdapter);
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
    public void onResume() {
        super.onResume();
        handleGET();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(cfAdded);
        getActivity().registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(cfAdded)) {
                try {
                    if (dialog.isShowing()){
                        dialog.cancel();
                    }
                    mTimer.cancel();

                    Log.d("unregisterReceiver","Unregister Receiver");
                }catch (Exception e){
                    e.printStackTrace();
                }
                getActivity().unregisterReceiver(mReceiver);
            }
        }
    };

}
