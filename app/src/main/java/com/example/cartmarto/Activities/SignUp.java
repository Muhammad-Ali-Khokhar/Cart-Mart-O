package com.example.cartmarto.Activities;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {
    Button btn_signUp;
    EditText et_name, et_email, et_pass, et_confirm_pass, et_key;
    String st_name, st_email, st_pass, st_confirm_pass, st_key, fb_key;
    ImageView img_profile, img_back;
    Uri uri;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseDatabase db;
    DatabaseReference databaseReference;


    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        linkViewsSignUp();
        firebaseAuth = FirebaseAuth.getInstance();
        //initializing key value
        fb_key = "cart mart";
        getValueForKey();


        //following click listener will help us to pick image from gallery or camera
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(SignUp.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(VISIBLE);
                getValuesFromET();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getValueForKey() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Key");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        fb_key = dataSnapshot.getValue().toString();
//                        Toast.makeText(SignUp.this, fb_key, Toast.LENGTH_SHORT).show();
                    }
                    else {

                    }
                } else {

                }
            }
        });
    }

    private void uploadImageToFB() {

        storageReference = FirebaseStorage.getInstance().getReference("OwnerPic");
        storageReference.child(st_email).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(st_email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Toast.makeText(SignUp.this, uri.toString(), Toast.LENGTH_SHORT).show();
                                uploadNameToFB();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //following function is setting the image in img_profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        img_profile.setImageURI(uri);
    }

    //following function getting values from all edit texts in strings and checking if the data is complete and correct the moving to login page
    private void getValuesFromET(){
        st_name = et_name.getText().toString().trim();
        st_email = et_email.getText().toString().trim();
        st_pass = et_pass.getText().toString().trim();
        st_confirm_pass = et_confirm_pass.getText().toString().trim();
        st_key = et_key.getText().toString().trim();
        if (st_name.isEmpty() || st_email.isEmpty() || st_pass.isEmpty() || st_key.isEmpty() || st_confirm_pass.isEmpty()){
            Toast.makeText(this, "Please Enter Complete Info.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);

        } else if (!st_confirm_pass.equals(st_pass)) {
            et_confirm_pass.setError("Doesn't match with password.");
            progressBar.setVisibility(View.GONE);
        } else if (!st_key.equals(fb_key)) {
            et_key.setError("Wrong Key.");
            progressBar.setVisibility(View.GONE);
        } else if (uri == null) {
            Toast.makeText(this, "Please select a image.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        else {
            storeDataOnFirebase();
        }
    }

    private void uploadNameToFB() {
        db = FirebaseDatabase.getInstance();
        Owner owner = new Owner(st_name, st_email);
        databaseReference = db.getReference("OwnerName");
        String oid = databaseReference.push().getKey().toString();
        databaseReference.child(oid).setValue(owner).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Sign Up Successful.", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(SignUp.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //following function is linking views
    private void linkViewsSignUp(){
        btn_signUp = findViewById(R.id.btn_signUp);
        et_name = findViewById(R.id.et_name);
        et_confirm_pass = findViewById(R.id.et_confirm_pass);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        img_profile = findViewById(R.id.img_profile);
        img_back = findViewById(R.id.img_back);
        et_key = findViewById(R.id.et_key);
        progressBar = findViewById(R.id.progressBar);
    }
    private void storeDataOnFirebase(){
        st_email = st_email + "o";
        firebaseAuth.createUserWithEmailAndPassword(st_email, st_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            Toast.makeText(SignUp.this, "Sign Up Successful",Toast.LENGTH_SHORT).show();
                            uploadImageToFB();
                        }
                        else {
                            Toast.makeText(SignUp.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}