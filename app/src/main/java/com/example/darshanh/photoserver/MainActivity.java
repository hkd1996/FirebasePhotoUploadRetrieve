package com.example.darshanh.photoserver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;


import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int PHOTO_REQUEST = 1888;
    boolean doubleBackToExitPressedOnce = false;


    Button uploadBtn,viewbtn;
    Uri imageUri;
    String url;
    ProgressDialog pd;
    UploadTask uploadTask;
    StorageReference storageRef,imageRef;
    boolean permission;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String filename;
    private DatabaseReference mDatabase;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission=isStoragePermissionGranted();

            FirebaseApp.initializeApp(getApplicationContext());
            storageRef = FirebaseStorage.getInstance().getReference();
            android_id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

            uploadBtn=(Button)findViewById(R.id.uploadBtn);
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                }
            });
            viewbtn=(Button)findViewById(R.id.viewImages);
            viewbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),ViewUploadedImage.class);
                    startActivity(intent);
                }
            });


    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void choosePhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            imageUri=data.getData();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
                uploadToFirebaseServer();
        }
    }

    private void uploadToFirebaseServer() {
        CompressImage compressImage=new CompressImage(imageUri,getApplicationContext());
        String compressedImagePath=compressImage.compressImage(imageUri.toString());
        imageUri=Uri.fromFile(new File(compressedImagePath));
        if(imageUri!=null){
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            filename = "Image-" + n + ".jpg";
           final StorageReference sRef = storageRef.child(Constants.STORAGE_PATH_UPLOADS +mAuth.getCurrentUser().getUid()+"/"+ filename);

            pd=new ProgressDialog(this);
            pd.setMax(100);
            pd.setTitle("Uploading to the server!!");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
            pd.setCancelable(false);

            uploadTask=sRef.putFile(imageUri);

            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    url=sRef.getDownloadUrl().toString();
                    // Continue with the task to get the download URL
                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        url=downloadUri.toString();
                        Upload upload=new Upload(url);
                        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS+"/"+mAuth.getCurrentUser().getUid());
                        String uploadId = mDatabase.push().getKey();
                        mDatabase.child(uploadId).setValue(upload);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd.incrementProgressBy((int) progress);
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed! to Upload", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Toast.makeText(getApplicationContext(),"Uploaded successfully!!",Toast.LENGTH_LONG).show();
                    pd.dismiss();

                }
            });
        }



    }
}
