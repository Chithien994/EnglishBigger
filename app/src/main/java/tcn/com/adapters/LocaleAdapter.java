package tcn.com.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tcn.com.englishbigger.ListActivity;
import tcn.com.englishbigger.R;
import tcn.com.models.LocaleModels;


/**
 * Created by MyPC on 08/08/2017.
 */

public class LocaleAdapter extends RecyclerView.Adapter<LocaleAdapter.ViewHolder> {
    ListActivity context;
    ArrayList<LocaleModels> objects;

    public LocaleAdapter(ListActivity context, ArrayList<LocaleModels> objects) {
        this.context = context;
        this.objects = objects;
    }


    @Override
    public LocaleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locale, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocaleAdapter.ViewHolder holder, int position) {

        final LocaleModels localeModels = this.objects.get(position);
        if (localeModels.getLanguage().equals("vi")){
            holder.txtLanguage.setText("Tiếng Việt");
            holder.imgEnsign.setImageResource(R.drawable.vi);
        }else if (localeModels.getLanguage().equals("en")){
            holder.txtLanguage.setText("English");
            holder.imgEnsign.setImageResource(R.drawable.en);
        }else if (localeModels.getLanguage().equals("zh")){
            holder.txtLanguage.setText("中国");
            holder.imgEnsign.setImageResource(R.drawable.zh);
        }else if (localeModels.getLanguage().equals("ja")){
            holder.txtLanguage.setText("日本");
            holder.imgEnsign.setImageResource(R.drawable.ja_rjp);
        }
        holder.layoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSelectLangguage(localeModels);
            }
        });
        if (context.language.getLanguage(context).equals(localeModels.getLanguage()) && context.language.getCFLanguageDivice(context) == false){
            holder.imgSelected.setVisibility(View.VISIBLE);
        }else {
            holder.imgSelected.setVisibility(View.GONE);
        }
    }

    private void handleSelectLangguage(LocaleModels localeModels) {
        context.language.setCFLanguageDivice(false);
        context.language.setLanguage(localeModels.getLanguage());
        context.language.settingLanguage(localeModels.getLanguage());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEnsign;
        ImageView imgSelected;
        TextView txtLanguage;
        LinearLayout layoutLanguage;
        public ViewHolder(View view) {
            super(view);
            imgEnsign = view.findViewById(R.id.imgEnsign);
            imgSelected = view.findViewById(R.id.imgSelected);
            txtLanguage = view.findViewById(R.id.txtLanguage);
            layoutLanguage = view.findViewById(R.id.layoutLanguage);
        }
    }
}
