package com.example.darshanh.photoserver;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ViewIndividualPhoto extends AppCompatActivity {
    ImageView clickedIv;
    Button deleteImageBtn;
    StorageReference storageRef;
    DatabaseReference mDatabase;
    String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_photo);
        clickedIv=(ImageView)findViewById(R.id.clickedIv);
        Bundle photo=getIntent().getExtras();
        final int position=photo.getInt("imagePosition");
        final List<Upload> uploads=(List<Upload>)photo.getSerializable("uploads");
        final Upload upload = uploads.get(position);
        Glide.with(getApplicationContext()).load(upload.getUrl()).into(clickedIv);
        deleteImageBtn=(Button)findViewById(R.id.deleteImage);
        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        android_id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
                        mDatabase= FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS+"/"+android_id);
                        Query query=mDatabase.orderByChild("url").equalTo(upload.getUrl());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                    uploads.remove(position);
                                    Intent intent=new Intent(getApplicationContext(),ViewUploadedImage.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),"Cancelled!!", Toast.LENGTH_SHORT).show();

                            }
                        });
                        Toast.makeText(getApplicationContext(),"File deleted sucessfully!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),"Unable to delete the file!!", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
    }
}
