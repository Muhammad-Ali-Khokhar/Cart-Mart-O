package com.example.cartmarto.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cartmarto.Adapters.Adapter_Prom;
import com.example.cartmarto.Interfaces.Prom_RV_Interface;
import com.example.cartmarto.Models.ModelPromotion;
import com.example.cartmarto.Models.Owner;
import com.example.cartmarto.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Promotion extends AppCompatActivity implements Prom_RV_Interface {

    ArrayList<Integer> promArrayList;
    ArrayList<ModelPromotion> promotionArrayList;
    RecyclerView recyclerView;

    ImageView img_back;

    Button btn_add_img;
    String uid;
    ProgressBar progressBar;
    Uri uri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        btn_add_img = findViewById(R.id.btn_add_img);
        databaseReference = FirebaseDatabase.getInstance().getReference("Promotion");

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Promotion.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        getImagesFromFB();
        dataImageInit();

    }


    private void deleteImagesOnFB(int position){
        progressBar.setVisibility(View.VISIBLE);
        ModelPromotion promotion = promotionArrayList.get(position);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(promotion.getUrl());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Promotion");
                databaseReference.child(promotion.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        promotionArrayList.remove(position);
                        setRecycleView();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Promotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Promotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    private void getImagesFromFB(){
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelPromotion promotion = dataSnapshot.getValue(ModelPromotion.class);
                    promotionArrayList.add(promotion);
                }

                setRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImageToRFB(){
        progressBar.setVisibility(View.VISIBLE);
        ModelPromotion promotion = new ModelPromotion(uid, urlImage);
        databaseReference.child(uid).setValue(promotion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Image Added", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Promotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void uploadImageToFB() {
        progressBar.setVisibility(View.VISIBLE);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        uid = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("Promotion");
        storageReference.child(uid).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlImage = uri.toString();
                                uploadImageToRFB();
//                                Toast.makeText(Promotion.this, uri.toString(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Promotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Promotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        uploadImageToFB();
    }

    private void setRecycleView() {

        recyclerView = findViewById(R.id.rv_promotion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        Adapter_Prom adapter_prom = new Adapter_Prom(this, promotionArrayList, this);
        recyclerView.setAdapter(adapter_prom);
        adapter_prom.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    private void dataImageInit() {

        promArrayList = new ArrayList<>();
        promArrayList.add(R.drawable.promotion1);
        promArrayList.add(R.drawable.promotion2);
        promArrayList.add(R.drawable.promotion3);
        promArrayList.add(R.drawable.promotion4);
        promArrayList.add(R.drawable.promotion5);

    }

    @Override
    public void onDeleteClicked(int position) {
        deleteImagesOnFB(position);
        Toast.makeText(this, "Deleted" + position, Toast.LENGTH_SHORT).show();
    }
}