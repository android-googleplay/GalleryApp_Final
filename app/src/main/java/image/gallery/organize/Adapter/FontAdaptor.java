package image.gallery.organize.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import image.gallery.organize.R;

import java.util.List;

public class FontAdaptor extends RecyclerView.Adapter<FontAdaptor.ViewHolder> {

    private List<String> listfont;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFont;

        public ViewHolder(View v) {
            super(v);
            txtFont = v.findViewById(R.id.txtFont);
        }
    }

    public FontAdaptor(Activity activity, List<String> listfont) {
        this.listfont = listfont;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_text_font, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtFont.setTypeface(Typeface.createFromAsset(activity.getAssets(), listfont.get(position)));
    }

    @Override
    public int getItemCount() {
        return listfont.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
