package image.gallery.organize.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import image.gallery.organize.R;

import java.util.ArrayList;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.Viewholder> {

    private ArrayList<String> itemlist;
    private ArrayList<Integer> itemIcon;
    private onItemClick itemlistener;
    public int selectedpos = 0;

    public interface onItemClick{
        void onItemClick(int pos);
    }

    public AdapterMenu(ArrayList<String> data, ArrayList<Integer> listIcon, onItemClick listener) {
        itemlist = data;
        itemIcon = listIcon;
        itemlistener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        holder.txtName.setText(itemlist.get(position));
        holder.imgThumb.setImageResource(itemIcon.get(position));

        if (position == selectedpos){
            holder.relSelection.setVisibility(View.VISIBLE);
        }else{
            holder.relSelection.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (position < 3){
                selectedpos = position;
                notifyDataSetChanged();
            }
            itemlistener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgThumb;
        RelativeLayout relSelection;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtName = itemView.findViewById(R.id.txtName);
            relSelection = itemView.findViewById(R.id.relSelection);
        }
    }
}
