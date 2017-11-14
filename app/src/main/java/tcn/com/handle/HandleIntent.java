package tcn.com.handle;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import tcn.com.englishbigger.LearnActivity;
import tcn.com.englishbigger.ListActivity;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;

/**
 * Created by MyPC on 12/10/2017.
 */

public class HandleIntent {
    public static int INTENT_TOPIC = 0;
    public static int INTENT_LEARN = 1;
    public static int INTENT_WHAT_DO_PEOPLE_LEARN = 3;
    public static int INTENT_ADD = 4;

    private Activity activity;

    public HandleIntent(){

    }

    public void intentActivity(Activity activity, int i){

        switch (i){

            case 0:
                handleLearnByTopic(activity, i);
                break;

            case 1:
                handleLearnNow(activity, i);
                break;

            case 3:
                handleWhatDoPeopleLearn(activity, i);
                break;

            case 4:
                handleAdd(activity, i);
                break;

            default:{
                break;
            }
        }
    }

    private void handleWhatDoPeopleLearn(Activity activity, int i) {
        Intent intent = new Intent(activity, TopicActivity.class);
        intent.putExtra("type", i);
        intent.putExtra("id", -1);
        activity.startActivity(intent);
    }

    private void handleAdd(Activity activity, int i) {
        Intent intent = new Intent(activity, LearnActivity.class);
        intent.putExtra("PUT",i);
        activity.startActivity(intent);
    }

    private void handleLearnNow(Activity activity, int i) {
        Intent intent = new Intent(activity, LearnActivity.class);
        intent.putExtra("PUT",i);
        intent.putExtra("FINISH",false);
        activity.startActivity(intent);
    }

    private void handleLearnByTopic(Activity activity, int i) {
        Intent intent = new Intent(activity, TopicActivity.class);
        intent.putExtra("type", i);
        intent.putExtra("id", i);
        activity.startActivity(intent);
    }
}
