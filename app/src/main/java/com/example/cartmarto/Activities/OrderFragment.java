package com.example.cartmarto.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cartmarto.Adapters.Adapter_OP;
import com.example.cartmarto.Adapters.Adapter_Prom;
import com.example.cartmarto.Interfaces.Order_RV_Interface;
import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.Models.Item_o1;
import com.example.cartmarto.Models.Item_o2;
import com.example.cartmarto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements Order_RV_Interface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v;

    private ArrayList<Item_o1> keyList;
    ProgressBar progressBar;
    private ArrayList<Item_o2> itemList;
    //To store user's ids.
    private ArrayList<String> userList;
    private HashMap<Item_o1, ArrayList<Item_o2>> orderHash;
    RecyclerView recyclerView;
    DatabaseReference databaseReference, databaseReference1;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
//        dataKeyList();
        progressBar.setVisibility(View.VISIBLE);
        dataOrderHash();
//        setRecyclerView(view);
        v = view;

    }

    private void setRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        Adapter_OP adapter_OP = new Adapter_OP(getActivity().getApplicationContext(),orderHash, keyList, this);
        recyclerView.setAdapter(adapter_OP);
        adapter_OP.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void dataOrderHash() {

        // Initializing HashMap
        orderHash = new HashMap<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keyList = new ArrayList<>();
                userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String s = dataSnapshot.getKey();
                    userList.add(s);
                    int iii = 0;
                    databaseReference1 = databaseReference.child(s).child("Details");
                    databaseReference1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                DataSnapshot dataSnapshot = task.getResult();
                                Item_o1 item_o1 = dataSnapshot.getValue(Item_o1.class);
                                keyList.add(item_o1);
                                int dfdf = 0;

                                databaseReference1 = databaseReference.child(s).child("Items");
                                databaseReference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        itemList = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                            Item_o2 item_o2 = dataSnapshot1.getValue(Item_o2.class);
                                            itemList.add(item_o2);
                                        }
                                        orderHash.put(item_o1, itemList);
                                        int sd = 0;
                                        setRecyclerView(v);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        // Initializing itemList
//        itemList = new ArrayList<>();
//
//        Item_h item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        Item_o2 item_o2 = new Item_o2(item_h, 2);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 1);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 4);
//        itemList.add(item_o2);
//
//        //Adding in HashMap
//
//        orderHash.put(keyList.get(0), itemList);
//
//
//        // Initializing itemList
//        itemList = new ArrayList<>();
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 3);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 1);
//        itemList.add(item_o2);
//
//
//
//        //Adding in HashMap
//
//        orderHash.put(keyList.get(1), itemList);
//
//        // Initializing itemList
//        itemList = new ArrayList<>();
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 1);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 1);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 2);
//        itemList.add(item_o2);
//
//        item_h = new Item_h("https://firebasestorage.googleapis.com/v0/b/cart-mart-745ea.appspot.com/o/Promotion%2F2023_08_22_14_28_19?alt=media&token=294f8f72-560f-4faf-aced-7f88112b6f9c", "123", "Watch1" , "100100", "1000", "Watches");
//        item_o2 = new Item_o2(item_h, 1);
//        itemList.add(item_o2);
//
//        //Adding in HashMap
//
//        orderHash.put(keyList.get(2), itemList);

    }

    private void dataKeyList() {

        //userID, name, phone, address, status;

//        keyList = new ArrayList<>();
//        Item_o1 item_o1 = new Item_o1("1", "M.Ali", "03217989537", "20 A/1, Gosha e Ahbab phase 2, near Azam Garden", "null");
//        keyList.add(item_o1);
//        item_o1 = new Item_o1("2", "Sufyan", "03204770051", "2 D, Gosha e Ahbab phase 2, near Azam Garden", "null");
//        keyList.add(item_o1);
//        item_o1 = new Item_o1("3", "Khurram", "03204770059", "Awan Town, Multan Road, Lahore", "null");
//        keyList.add(item_o1);


    }

    @Override
    public void onUpdateClicked(int position, String status) {
        progressBar.setVisibility(View.VISIBLE);
        if (status.equals("")){
            Toast.makeText(getActivity().getApplicationContext(), "Please Enter Status.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        else{
            String uid = userList.get(position);
            Item_o1 item_o2 = keyList.get(position);
            item_o2.setStr_status_ds(status);
            databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(uid).child("Details");
            databaseReference.setValue(item_o2).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getActivity().getApplicationContext(), (position+1) + ". " + status, Toast.LENGTH_SHORT).show();
                    setRecyclerView(v);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onFinishedClicked(int position) {
        progressBar.setVisibility(View.VISIBLE);
        String s = userList.get(position);
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        databaseReference.child(s).removeValue();
        orderHash.remove(keyList.get(position));
        keyList.remove(position);
        userList.remove(position);
        setRecyclerView(v);
        progressBar.setVisibility(View.GONE);
    }
}