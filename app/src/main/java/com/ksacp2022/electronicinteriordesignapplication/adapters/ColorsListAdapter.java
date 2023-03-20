package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022.electronicinteriordesignapplication.R;

import java.util.List;

public class ColorsListAdapter extends RecyclerView.Adapter<ColorItem> {
    List<Integer> colors;
    public ColorsListAdapter(List<Integer> colors) {
        this.colors = colors;

    }

    @NonNull
    @Override
    public ColorItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_list_item,parent,false);
        return new ColorItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorItem holder, int position) {
        int pos=position;
        Integer color= colors.get(pos);
        holder.color_item.setBackgroundColor(color);

        holder.color_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               colors.remove(color);
               notifyItemRemoved(pos);
               notifyItemRangeChanged(pos,colors.size());
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return colors.size();
    }
}

class ColorItem extends RecyclerView.ViewHolder{

    View color_item;

    public ColorItem(@NonNull View itemView) {
        super(itemView);
        color_item=itemView.findViewById(R.id.color_item);
    }
}
