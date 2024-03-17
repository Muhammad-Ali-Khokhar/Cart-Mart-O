package com.example.cartmarto.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cartmarto.Models.Item_h;
import com.example.cartmarto.Models.Owner;
import com.example.cartmarto.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressBar progressBar;
    
    private ImageView img_item_ai;
    private EditText et_name_ai, et_price_ai, et_category_ai, et_description_ad;
    private String st_name_ai, st_price_ai, st_category_ai, st_description_ai;
    private Button btn_add_ai;
    private ArrayList<String>  catgArrayList;
    Uri uri;
    String imageID, imageURL;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
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
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewsAddItem(view);
        dataCatgInit();
        btn_add_ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                getValuesFromET();
                if (checkStrings()){
                    uploadImageOnFB();
                }
            }
        });
        img_item_ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddItemFragment.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        img_item_ai.setImageURI(uri);
    }

    private void setETClear(){
        et_category_ai.setText("");
        et_name_ai.setText("");
        et_description_ad.setText("");
        et_price_ai.setText("");
        img_item_ai.setImageResource(R.drawable.logo_add_image);
        progressBar.setVisibility(View.GONE);
    }
    private boolean checkStrings(){
        if (st_name_ai.isEmpty() || st_price_ai.isEmpty() || st_category_ai.isEmpty() || st_description_ai.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "Please enter complete info.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (uri == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (!catgArrayList.contains(st_category_ai)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please choose existing category.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        else {
            return true;
        }
        return false;
    }
    private void getValuesFromET(){
        st_name_ai = et_name_ai.getText().toString().trim();
        st_price_ai = et_price_ai.getText().toString().trim();
        st_category_ai = et_category_ai.getText().toString().trim();
        st_description_ai = et_description_ad.getText().toString().trim();
    }
    private void findViewsAddItem(View view) {
        img_item_ai = view.findViewById(R.id.img_item_ai);
        et_name_ai = view.findViewById(R.id.et_name_ai);
        et_price_ai = view.findViewById(R.id.et_price_ai);
        et_category_ai = view.findViewById(R.id.et_category_ai);
        et_description_ad = view.findViewById(R.id.et_description_ad);
        btn_add_ai = view.findViewById(R.id.btn_add_ai);
        progressBar = view.findViewById(R.id.progressBar);
    }
    private void dataCatgInit(){
        progressBar.setVisibility(View.VISIBLE);
        catgArrayList = new ArrayList<>();
//        catgArrayList.add("Books");
//        catgArrayList.add("Cars");
//        catgArrayList.add("Watches");
//        catgArrayList.add("Clothes");
//        catgArrayList.add("Food");
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String s = dataSnapshot.getKey();
                    catgArrayList.add(s);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

//    private void readNameFromFB(String mail) {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    Owner owner = dataSnapshot.getValue(Owner.class);
//                    if (owner.getEmail().equals(mail)){
//                        str_name = owner.getName();
//                        txt_name.setText(str_name);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void uploadImageOnFB(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM-dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        imageID = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("Items");
        storageReference.child(imageID).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(imageID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        uploadDataOnRFB();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void uploadDataOnRFB() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Category").child(st_category_ai);
        String uID = databaseReference.push().getKey();
        Item_h item_h = new Item_h(imageURL, uID, st_name_ai, st_description_ai, st_price_ai, st_category_ai);
        databaseReference.child(uID).setValue(item_h).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity().getApplicationContext(), "Item Added.", Toast.LENGTH_SHORT).show();
                setETClear();

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