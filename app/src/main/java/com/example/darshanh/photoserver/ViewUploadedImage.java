package com.example.darshanh.photoserver;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewUploadedImage extends AppCompatActivity {
    private RecyclerView recyclerView;
    GridView gridView;
  //  private RecyclerAdapter adapter;
    private List<Upload> uploads;
    private DatabaseReference mDatabaseRef;
    GridViewAdapter adapter;
    private String android_id;
    TextView noItemsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploaded_image);
        gridView=(GridView)findViewById(R.id.gridView);
        noItemsTv=(TextView)findViewById(R.id.textView);
        uploads = new ArrayList<>();
        android_id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS+"/"+android_id);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                //creating adapter
                adapter=new GridViewAdapter(getApplicationContext(), uploads);
               /* adapter = new RecyclerAdapter(getApplicationContext(), uploads);*/
               if(adapter.isEmpty()){
                   noItemsTv.setText("No Photos to display");
               }
               else{
                   noItemsTv.setVisibility(View.INVISIBLE);
               }
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent=new Intent(getApplicationContext(),ViewIndividualPhoto.class);
                        intent.putExtra("imagePosition",i);
                        intent.putExtra("uploads", (Serializable) uploads);
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
