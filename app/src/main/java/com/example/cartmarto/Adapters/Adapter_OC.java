package com.example.cartmarto.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.Models.Item_o2;
import com.example.cartmarto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_OC extends RecyclerView.Adapter<Adapter_OC.MyViewHolder> {

    private Context context;
    private ArrayList<Item_h> itemList;
    private ArrayList<Item_o2> itemList2;

    public Adapter_OC(Context context, ArrayList<Item_h> itemList, ArrayList<Item_o2> itemList2) {
        this.context = context;
        this.itemList = itemList;
        this.itemList2 = itemList2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_item_orders, parent, false);
        return new Adapter_OC.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Item_h item_h = itemList.get(position);
        Item_o2 item_o2 = itemList2.get(position);
        Picasso.get().load(item_h.getImg_item_h()).into(holder.img_rv_ds);
        holder.txt_name_rv_ds.setText(item_h.getStr_name_item_h());
        holder.txt_pc_rv_ds.setText(item_o2.getStr_count() + " pc");

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_rv_ds;
        TextView txt_name_rv_ds, txt_pc_rv_ds;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_rv_ds = itemView.findViewById(R.id.img_rv_ds);
            txt_name_rv_ds = itemView.findViewById(R.id.txt_name_rv_ds);
            txt_pc_rv_ds = itemView.findViewById(R.id.txt_pc_rv_ds);
        }
    }

}
