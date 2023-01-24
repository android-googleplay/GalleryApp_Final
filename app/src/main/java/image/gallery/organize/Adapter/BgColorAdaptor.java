package image.gallery.organize.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import image.gallery.organize.R;

public class BgColorAdaptor extends RecyclerView.Adapter<BgColorAdaptor.ViewHolder> {

    private int[] listBackgroundColor;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBg;

        public ViewHolder(View v) {
            super(v);
            imgBg = v.findViewById(R.id.imgBg);
        }
    }

    public BgColorAdaptor(Activity activity, int[] listBackgroundColor) {
        this.listBackgroundColor = listBackgroundColor;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_text_bgcolor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imgBg.setColorFilter(listBackgroundColor[position]);
        if (position == 0) {
            holder.imgBg.setImageResource(R.drawable.ic_text_none);
        } else {
            holder.imgBg.setColorFilter(listBackgroundColor[position]);
        }
        holder.imgBg.requestLayout();
    }

    @Override
    public int getItemCount() {
        return listBackgroundColor.length;
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

