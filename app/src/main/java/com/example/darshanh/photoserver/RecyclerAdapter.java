package com.example.darshanh.photoserver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private Context context;
    private List<Upload> uploads;

    public RecyclerAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_display, viewGroup, false);
        ImageViewHolder viewHolder = new ImageViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {
        Upload upload = uploads.get(i);
        Glide.with(context).load(upload.getUrl()).into(imageViewHolder.uploadedImage);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView uploadedImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            uploadedImage=(ImageView)itemView.findViewById(R.id.imageView);

        }
    }
}
