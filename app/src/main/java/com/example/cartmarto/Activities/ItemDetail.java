package com.example.cartmarto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ItemDetail extends AppCompatActivity {
    ImageView img_item_id, img_back, img_delete;
    TextView txt_name_item_id, txt_price_item_id, txt_Description_item_id;
    Button btn_addToCart_id;

    Item_h item_h;

    ProgressBar progressBar;
    int count;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        findViewsItemDetail();
        count = 1;

        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
//        String img = intent.getStringExtra("itemImage");
//        String name = intent.getStringExtra("ItemName");
//        String price = intent.getStringExtra("ItemPrice");
//        String id = intent.getStringExtra("ItemID");
//        String description = intent.getStringExtra("ItemDescription");
        item_h = new Item_h();
        item_h.setImg_item_h(intent.getStringExtra("itemImage"));
        item_h.setStr_id_item_h(intent.getStringExtra("ItemID"));
        item_h.setStr_category_item_h(intent.getStringExtra("ItemCategory"));
        item_h.setStr_description_item_h(intent.getStringExtra("ItemDescription"));
        item_h.setStr_name_item_h(intent.getStringExtra("ItemName"));
        item_h.setStr_price_item_h(intent.getStringExtra("ItemPrice"));


        Picasso.get().load(item_h.getImg_item_h()).into(img_item_id);
        txt_name_item_id.setText(item_h.getStr_name_item_h());
        txt_price_item_id.setText(item_h.getStr_price_item_h() + " RS");
        txt_Description_item_id.setText(item_h.getStr_description_item_h());




        progressBar.setVisibility(View.GONE);


        btn_addToCart_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItemFromFB();
            }
        });



    }

    private void deleteItemFromFB() {
        progressBar.setVisibility(View.VISIBLE);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(item_h.getImg_item_h());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Category").child(item_h.getStr_category_item_h());
                databaseReference.child(item_h.getStr_id_item_h()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ItemDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void findViewsItemDetail() {
        img_item_id = findViewById(R.id.img_item_id);
        txt_name_item_id = findViewById(R.id.txt_name_item_id);
        txt_price_item_id = findViewById(R.id.txt_price_item_id);
        txt_Description_item_id = findViewById(R.id.txt_Description_item_id);
        btn_addToCart_id = findViewById(R.id.btn_addToCart_id);
        img_back = findViewById(R.id.img_back);
        img_delete = findViewById(R.id.img_delete);
        progressBar = findViewById(R.id.progressBar);
    }
}