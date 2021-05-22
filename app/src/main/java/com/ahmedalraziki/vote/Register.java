package com.ahmedalraziki.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {

    EditText edEcn, edPassword;
    Button ecC, ecU, idC, idU, reg;
    Uri imgID = null;
    Uri imgEC = null;
    int modeC = 0;
    int modeU = 0;
    String ecn;
    String ecnUrl = null;
    String idUrl  = null;

    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //1-
        Assigner();
        Clicker();

        //2-
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }


    private void Assigner(){
        edEcn       = findViewById(R.id.vr_etEcN);
        edPassword  = findViewById(R.id.vr_etPa);
        ecC         = findViewById(R.id.vr_btnEcPicC);
        ecU         = findViewById(R.id.vr_btnEcPicU);
        idC         = findViewById(R.id.vr_btnIDPicC);
        idU         = findViewById(R.id.vr_btnIDPicU);
        reg         = findViewById(R.id.vr_btnReg);
    }

    private void Clicker(){

        ecC.setOnClickListener(v -> {
            modeC = 1;
            SelectImage();
        });

        idC.setOnClickListener(v -> {
            modeC = 2;
            SelectImage();
        });

        ecU.setOnClickListener(v -> {
            modeU = 1;
            uploadImage();
        });

        idU.setOnClickListener(v -> {
            modeU = 2;
            uploadImage();
        });

        reg.setOnClickListener(v -> {
            Register();
        });
    }


    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            if (modeC == 1) {
                imgEC = data.getData();
            } else if (modeC == 2){
                imgID = data.getData();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        Uri filePath = null;
        if (modeU == 1){
            filePath = imgEC;
        } else if (modeU == 2){
            filePath = imgID;
        }

        if (filePath != null) {
            // Setting ECN
            ecn = edEcn.getText().toString();
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("جار الرفع ...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("users/"+ ecn);

            // adding listeners on upload
            // or failure of image

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (modeU == 1){
                                        ecnUrl = ref.getDownloadUrl().toString();
                                    } else if (modeU == 2){
                                        idUrl = ref.getDownloadUrl().toString();
                                    }
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(Register.this, "تم !!",
                                                    Toast.LENGTH_SHORT).show();

                                }})


                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "فشل :(" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }})

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("تم رفع " + (int)progress + "%");
                                }});
        }
    }

    //Finally Register
    private void Register(){

        String ecNumber = edEcn.getText().toString();
        String password = edPassword.getText().toString();
        boolean vu      = false;

        if (!ecNumber.equals("") && !password.equals("") && imgEC != null && imgID != null){
        DatabaseReference refMain = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refUser = refMain.child("User").child(ecNumber);
        refUser.child("id").setValue(ecNumber);
        refUser.child("pass").setValue(password);
        refUser.child("vu").setValue(vu);
        } else {
            Toast.makeText(this, "املأ جميع الحقول رجاءً", Toast.LENGTH_LONG).show(); }
    }

}