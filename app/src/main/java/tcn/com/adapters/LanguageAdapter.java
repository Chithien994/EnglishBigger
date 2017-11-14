package tcn.com.adapters;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tcn.com.englishbigger.R;
import tcn.com.models.LocaleModels;

/**
 * Created by MyPC on 23/07/2017.
 */

public class LanguageAdapter extends ArrayAdapter<LocaleModels> {
    Activity context;
    int resource;
    List<LocaleModels> objects;

    public LanguageAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<LocaleModels> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = this.context.getLayoutInflater();
        View row=inflater.inflate(this.resource,null);
        TextView txtLanguage = (TextView) row.findViewById(R.id.txtLanguage);
        LocaleModels localeModels = this.objects.get(position);
        if (localeModels.getLanguage().equals("en")){
            txtLanguage.setText("English");
        }else if (localeModels.getLanguage().equals("vi")){
            txtLanguage.setText("Viet Nam");
        }else if (localeModels.getLanguage().equals("zh")){
            txtLanguage.setText("Chinese");
        }else if (localeModels.getLanguage().equals("ja")){
            txtLanguage.setText("Japanase");
        }



        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = this.context.getLayoutInflater();
        View row=inflater.inflate(R.layout.item_language_dropdown,null);
        TextView txtLanguage = (TextView) row.findViewById(R.id.txtLanguage);
        LocaleModels localeModels = this.objects.get(position);
        if (localeModels.getLanguage().equals("en")){
            txtLanguage.setText("English");
        }else if (localeModels.getLanguage().equals("vi")){
            txtLanguage.setText("Viet Nam");
        }else if (localeModels.getLanguage().equals("zh")){
            txtLanguage.setText("Chinese");
        }else if (localeModels.getLanguage().equals("ja")){
            txtLanguage.setText("Japanase");
        }

        return row;
    }
}
