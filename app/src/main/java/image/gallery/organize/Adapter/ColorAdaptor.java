package image.gallery.organize.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import image.gallery.organize.R;


public class ColorAdaptor extends RecyclerView.Adapter<ColorAdaptor.ViewHolder> {

    private int[] listColor;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBg;

        public ViewHolder(View v) {
            super(v);
            imgBg = v.findViewById(R.id.imgBg);
        }
    }

    public ColorAdaptor(Activity activity, int[] listColor) {
        this.listColor = listColor;
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
        if (position == 0) {
            holder.imgBg.setImageResource(R.drawable.ic_text_none);
        } else {
            holder.imgBg.setColorFilter(listColor[position]);
        }
    }

    @Override
    public int getItemCount() {
        return listColor.length;
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
