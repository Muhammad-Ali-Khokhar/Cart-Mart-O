package com.example.cartmarto.Activities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.cartmarto.Adapters.Adapter_Catg;
import com.example.cartmarto.Adapters.Adapter_Item;
import com.example.cartmarto.Interfaces.Catg_RV_Interface;
import com.example.cartmarto.Interfaces.Item_RV_Interface;
import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.Models.ModelPromotion;
import com.example.cartmarto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements Catg_RV_Interface, Item_RV_Interface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressBar progressBar;

    ConstraintLayout constraintLayoutHome;

    //ArrayList for Categories
    private ArrayList<String> catgArrayList;
    //RecyclerView for Categories
    private RecyclerView recyclerview1, recyclerview2;
    DatabaseReference databaseReference;

    private ArrayList<Item_h> itemArrayList, itemArrayList1;

    private HashMap<String, ArrayList<Item_h>> itemHash;
    View v;


    ImageView img_promotion;
    String newCateg;
    EditText et_category;
    Button btn_save, btn_delete;
    TextView txt_cancel, txt_cancel2, txt_delete;
    private ArrayList<ModelPromotion> promotionArrayList;

    StorageReference storageReference;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBar);

        getImagesFromFB(view);

        constraintLayoutHome = view.findViewById(R.id.constraintLayoutHome);
        v = view;



        img_promotion = view.findViewById(R.id.img_promotion);
        img_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Promotion.class);
                startActivity(intent);
            }
        });



    }

    private void setRecyclerview1(View view){
        recyclerview1 = view.findViewById(R.id.rv_categ_h);
        recyclerview1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerview1.setHasFixedSize(true);
        Adapter_Catg adapter_catg = new Adapter_Catg(getContext(), catgArrayList, this);
        recyclerview1.setAdapter(adapter_catg);
        adapter_catg.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void setRecyclerview2(View view){
        recyclerview2 = view.findViewById(R.id.rv_item_h);
        recyclerview2.setLayoutManager(new GridLayoutManager(getContext(), 2){
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        recyclerview2.setHasFixedSize(true);
        Adapter_Item adapter_item = new Adapter_Item(getContext(), itemArrayList, this);
        recyclerview2.setAdapter(adapter_item);
        adapter_item.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void dataItemInit(){
        itemArrayList = new ArrayList<>();
        ArrayList<Item_h> tempArr = new ArrayList<>();
        itemArrayList = tempArr;
        for (String i: itemHash.keySet()){
            tempArr = itemHash.get(i);
            for (Item_h j: tempArr){

                itemArrayList.add(j);
            }
        }
    }

    //To set the data in recycle views for categories.
    private void dataCatgInit(){
        catgArrayList = new ArrayList<>();
        catgArrayList.add("All");
//        catgArrayList.add("Books");
//        catgArrayList.add("Cars");
//        catgArrayList.add("Watches");
//        catgArrayList.add("Clothes");
//        catgArrayList.add("Food");

        for (String i: itemHash.keySet()){
            catgArrayList.add(i);
        }

        catgArrayList.add("  +  ");
    }

    //Following function add data in HashMap
    private void dataHashInit(View view){
        itemHash = new HashMap<>();
        dataCatgInit();
        setRecyclerview1(view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String cat = dataSnapshot.getKey();
                    itemArrayList = new ArrayList<>();
                    databaseReference.child(cat).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            itemArrayList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                Item_h item_h = dataSnapshot1.getValue(Item_h.class);
                                itemArrayList.add(item_h);
                            }
                            itemHash.put(cat, itemArrayList);
                            dataCatgInit();
                            dataItemInit();

                            //Adding the catgArrayList data in Recycle View for Categories
                            setRecyclerview1(view);
                            setRecyclerview2(view);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
//        for (String i: catgArrayList){
//            if (i.equals("Cars") || i.equals("Clothes")){
//                itemHash.put(i, itemArrayList1);
//            }
//            else{
//                itemHash.put(i, itemArrayList);
//            }
//        }

//        itemArrayList = new ArrayList<>();
//        Item_h item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//
//        itemHash.put("Books", itemArrayList);
//
//
//
//        itemArrayList = new ArrayList<>();
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//
//
//        itemHash.put("Cars", itemArrayList);
//
//
//        itemArrayList = new ArrayList<>();
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//
//
//        itemHash.put("Clothes", itemArrayList);
//
//
//        itemArrayList = new ArrayList<>();
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//
//
//        itemHash.put("Food", itemArrayList);
//
//
//        itemArrayList = new ArrayList<>();
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//        item_h = new Item_h(promotionArrayList.get(0).getUrl(), "123", "Watch1" , "100100", "1000", "Watches");
//        itemArrayList.add(item_h);
//
//
//        itemHash.put("Watches", itemArrayList);


    }

    //Followin function works when any category is clicked and change the view in item recycler view
    @Override
    public void onCatgClicked(int position) {
        progressBar.setVisibility(View.VISIBLE);

        if(catgArrayList.get(position).equals("All")){
            ArrayList<Item_h> tempArr = new ArrayList<>();
            itemArrayList = tempArr;
            for (String i: itemHash.keySet()){
                tempArr = itemHash.get(i);
                for (Item_h j: tempArr){

                    itemArrayList.add(j);
                }
            }


        }
        else if (catgArrayList.get(position).equals("  +  ")){
            createPopUpWindow();
            progressBar.setVisibility(View.GONE);
        }
        else{
            itemArrayList = itemHash.get(catgArrayList.get(position));
        }
        try {
            Adapter_Item adapter_item = new Adapter_Item(getContext(), itemArrayList, this);
            recyclerview2.setAdapter(adapter_item);
            adapter_item.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCatgLongClicked(int position) {
        progressBar.setVisibility(View.VISIBLE);
        if (catgArrayList.get(position).equals("All") || catgArrayList.get(position).equals("  +  ")){

            progressBar.setVisibility(View.GONE);
        }
        else {
            createPopUpWindow2(position);
            progressBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingInflatedId")
    private void createPopUpWindow2(int position) {

        progressBar.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.pop_delete_category, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int hight = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUpView, width, hight, focusable);
        constraintLayoutHome.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(constraintLayoutHome, Gravity.CENTER, 0, 0);
            }
        });
        txt_delete = popUpView.findViewById(R.id.txt_delete);
        btn_delete = popUpView.findViewById(R.id.btn_delete);
        txt_cancel2 = popUpView.findViewById(R.id.txt_cancel2);
        txt_delete.setText("Delete Category (" + catgArrayList.get(position) + ") ?");

        progressBar.setVisibility(View.GONE);
        txt_cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCatgFromRFB(position);
                popupWindow.dismiss();
            }
        });
    }

    private void deleteCatgFromRFB(int position) {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.child(catgArrayList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Item_h item_h = dataSnapshot.getValue(Item_h.class);
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item_h.getImg_item_h());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            databaseReference.child(catgArrayList.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Category Deleted", Toast.LENGTH_SHORT).show();
                                    catgArrayList.remove(position);
                                    setRecyclerview1(v);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
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

//        databaseReference.child(catgArrayList.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(getActivity().getApplicationContext(), "Category Deleted", Toast.LENGTH_SHORT).show();
//                catgArrayList.remove(position);
//                setRecyclerview1(v);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void createPopUpWindow() {

        progressBar.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.pop_add_category, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int hight = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUpView, width, hight, focusable);
        constraintLayoutHome.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(constraintLayoutHome, Gravity.CENTER, 0, 0);
            }
        });
        et_category = popUpView.findViewById(R.id.et_category);
        btn_save = popUpView.findViewById(R.id.btn_save);
        txt_cancel = popUpView.findViewById(R.id.txt_cancel);
        progressBar.setVisibility(View.GONE);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCateg = et_category.getText().toString().trim();
                saveCatigoryOnFB(popupWindow);
            }
        });

    }

    private void saveCatigoryOnFB(PopupWindow popupWindow) {
        progressBar.setVisibility(View.VISIBLE);
        if (catgArrayList.contains(newCateg)){
            Toast.makeText(getActivity().getApplicationContext(), "Category already exists.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        else {

            databaseReference = FirebaseDatabase.getInstance().getReference("Category");
            databaseReference.child(newCateg).setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getActivity().getApplicationContext(), newCateg, Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                    progressBar.setVisibility(View.GONE);
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
    public void onItemClicked(Item_h item_h) {
        Toast.makeText(getActivity().getApplicationContext(), item_h.getStr_name_item_h(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity().getApplicationContext(), ItemDetail.class);
        intent.putExtra("itemImage", item_h.getImg_item_h());
        intent.putExtra("ItemName", item_h.getStr_name_item_h());
        intent.putExtra("ItemPrice", item_h.getStr_price_item_h());
        intent.putExtra("ItemID", item_h.getStr_id_item_h());
        intent.putExtra("ItemDescription", item_h.getStr_description_item_h());
        intent.putExtra("ItemCategory", item_h.getStr_category_item_h());
        startActivity(intent);
    }
    public void setImageSlider(){

    }
    private void getImagesFromFB(View view){
        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Promotion");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelPromotion promotion = dataSnapshot.getValue(ModelPromotion.class);
                    promotionArrayList.add(promotion);
                }
                ImageSlider imageSlider = view.findViewById(R.id.slider_h);

                ArrayList<SlideModel> slideModels = new ArrayList<>();
                for (ModelPromotion promotion: promotionArrayList){
                    slideModels.add(new SlideModel(promotion.getUrl(), ScaleTypes.CENTER_INSIDE));
                }

//        slideModels.add(new SlideModel(promotionArrayList.get(0).getUrl(), ScaleTypes.CENTER_INSIDE));
//        slideModels.add(new SlideModel(R.drawable.promotion2, ScaleTypes.CENTER_INSIDE));
//        slideModels.add(new SlideModel(R.drawable.promotion3, ScaleTypes.CENTER_INSIDE));
//        slideModels.add(new SlideModel(R.drawable.promotion4, ScaleTypes.CENTER_INSIDE));
//        slideModels.add(new SlideModel(R.drawable.promotion5, ScaleTypes.CENTER_INSIDE));

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                //        Adding the data in catgArrayList
                dataHashInit(view);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
