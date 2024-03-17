package com.example.cartmarto.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartmarto.Interfaces.Prom_RV_Interface;
import com.example.cartmarto.Models.ModelPromotion;
import com.example.cartmarto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Prom extends RecyclerView.Adapter<Adapter_Prom.MyViewHolder>{

    Context context;
    ArrayList<ModelPromotion> promArrayList;
    Prom_RV_Interface prom_rv_interface;

    public Adapter_Prom(Context context, ArrayList<ModelPromotion> promArrayList, Prom_RV_Interface prom_rv_interface) {
        this.context = context;
        this.promArrayList = promArrayList;
        this.prom_rv_interface = prom_rv_interface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_promotion, parent, false);

        return new Adapter_Prom.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelPromotion promotion = promArrayList.get(position);
//
//        holder.img_prom_rv.setImageURI(Uri.parse(promotion.getUrl()));
        Picasso.get().load(promotion.getUrl()).into(holder.img_prom_rv);
//        holder.img_prom_rv.setImageResource(R.drawable.promotion1);
        holder.img_delete_rv_prom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prom_rv_interface.onDeleteClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return promArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_prom_rv, img_delete_rv_prom;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_prom_rv = itemView.findViewById(R.id.img_prom_rv);
            img_delete_rv_prom = itemView.findViewById(R.id.img_delete_rv_prom);

        }
    }

}
