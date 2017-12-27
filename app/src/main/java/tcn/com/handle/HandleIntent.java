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
    public static final int INTENT_TOPIC = 0;
    public static final int INTENT_LEARN = 1;
    public static final int INTENT_WHAT_DO_PEOPLE_LEARN = 3;
    public static final int INTENT_ADD = 4;

    private Activity activity;

    public static void intentActivity(Activity activity, int i){

        switch (i){

            case INTENT_TOPIC:
                handleLearnByTopic(activity);
                break;

            case INTENT_LEARN:
                handleLearnNow(activity);
                break;

            case INTENT_WHAT_DO_PEOPLE_LEARN:
                handleWhatDoPeopleLearn(activity);
                break;

            case INTENT_ADD:
                handleAdd(activity);
                break;

            default:{
                break;
            }
        }
    }

    private static void handleWhatDoPeopleLearn(Activity activity) {
        Intent intent = new Intent(activity, TopicActivity.class);
        intent.putExtra("type", INTENT_WHAT_DO_PEOPLE_LEARN);
        intent.putExtra("id", -1);
        activity.startActivity(intent);
    }

    private static void handleAdd(Activity activity) {
        Intent intent = new Intent(activity, LearnActivity.class);
        intent.putExtra("PUT",INTENT_ADD);
        activity.startActivity(intent);
    }

    private static void handleLearnNow(Activity activity) {
        Intent intent = new Intent(activity, LearnActivity.class);
        intent.putExtra("PUT",INTENT_LEARN);
        intent.putExtra("FINISH",false);
        activity.startActivity(intent);
    }

    private static void handleLearnByTopic(Activity activity) {
        Intent intent = new Intent(activity, TopicActivity.class);
        intent.putExtra("type", INTENT_TOPIC);
        intent.putExtra("id", INTENT_TOPIC);
        activity.startActivity(intent);
    }
}
