package com.example.cartmarto.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartmarto.Interfaces.Order_RV_Interface;
import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.Models.Item_o1;
import com.example.cartmarto.Models.Item_o2;
import com.example.cartmarto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter_OP extends RecyclerView.Adapter<Adapter_OP.MyViewHolder> {

    Context context;
    int orderNo;
    private HashMap<Item_o1, ArrayList<Item_o2>> orderHash;
    private ArrayList<Item_o1> keyList;
    private ArrayList<Item_o2> itemList;
    private ArrayList<Item_h> itemList_h;
    Order_RV_Interface order_rv_interface;
    DatabaseReference databaseReference;
    int sizeArray;

    public Adapter_OP(Context context, HashMap<Item_o1, ArrayList<Item_o2>> orderHash, ArrayList<Item_o1> keyList, Order_RV_Interface order_rv_interface) {
        this.context = context;
        this.orderHash = orderHash;
        this.keyList = keyList;
        this.orderNo = 1;
        this.order_rv_interface = order_rv_interface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_orders, parent, false);
        return new Adapter_OP.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Item_o1 item_o1 = keyList.get(position);
        holder.txt_orderNo_rv_orders.setText("Order No: " + Integer.toString(orderNo));
        orderNo ++;
        holder.txt_Name_ds.setText(item_o1.getStr_name_ds());
        holder.txt_Phone_ds.setText(item_o1.getStr_phone_ds());
        holder.txt_Address_ds.setText(item_o1.getStr_address_ds());
        holder.txt_DCharge_ds.setText("20 Rs");
//
//        //For Price
        itemList = new ArrayList<>();
        itemList = orderHash.get(item_o1);

        itemList_h = new ArrayList<>();
        sizeArray = 0;
        for (Item_o2 item_c: itemList){
            databaseReference = FirebaseDatabase.getInstance().getReference("Category").child(item_c.getStr_category()).child(item_c.getStr_item_id());
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()){
                        DataSnapshot dataSnapshot = task.getResult();
                        Item_h item_h = dataSnapshot.getValue(Item_h.class);
                        itemList_h.add(item_h);
                        sizeArray++;
                    }
                    if (sizeArray == itemList.size()){
                        int price = 0;
                        int total = 0;

                        for (int i = 0; i<itemList_h.size(); i++){

                            Item_h item_h = itemList_h.get(i);
                            Item_o2 item_o2 = itemList.get(0);
                            int no = Integer.parseInt(item_o2.getStr_count());
                            int p =Integer.parseInt(item_h.getStr_price_item_h());
                            price += no * p;
                        }

                        total = price + 20;

                        holder.txt_Price_ds.setText(Integer.toString(price));
                        holder.txt_Total_ds.setText(Integer.toString(total));

                        //setting nested adapter

                        Adapter_OC adapter_oc = new Adapter_OC(context, itemList_h, itemList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        holder.rv_item_rv_orders.setLayoutManager(linearLayoutManager);
                        holder.rv_item_rv_orders.setAdapter(adapter_oc);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order_rv_interface.onFinishedClicked(position);
            }
        });
//
        holder.btn_update_rv_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strET = holder.et_status_rv_orders.getText().toString().trim();
                order_rv_interface.onUpdateClicked(position, strET);
            }
        });


    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_orderNo_rv_orders, txt_Name_ds, txt_Phone_ds, txt_Address_ds, txt_Price_ds, txt_DCharge_ds, txt_Total_ds;
        EditText et_status_rv_orders;
        Button btn_update_rv_orders, btn_finish;
        RecyclerView rv_item_rv_orders;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_orderNo_rv_orders = itemView.findViewById(R.id.txt_orderNo_rv_orders);
            txt_Name_ds = itemView.findViewById(R.id.txt_Name_ds);
            txt_Phone_ds = itemView.findViewById(R.id.txt_Phone_ds);
            txt_Address_ds = itemView.findViewById(R.id.txt_Address_ds);
            txt_Price_ds = itemView.findViewById(R.id.txt_Price_ds);
            txt_DCharge_ds = itemView.findViewById(R.id.txt_DCharge_ds);
            txt_Total_ds = itemView.findViewById(R.id.txt_Total_ds);
            et_status_rv_orders = itemView.findViewById(R.id.et_status_rv_orders);
            btn_update_rv_orders = itemView.findViewById(R.id.btn_update_rv_orders);
            rv_item_rv_orders = itemView.findViewById(R.id.rv_item_rv_orders);
            btn_finish = itemView.findViewById(R.id.btn_finish);
        }
    }

}
