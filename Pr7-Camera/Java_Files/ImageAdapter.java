package com.example.camerastorage;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Shows the saved image files as a grid of thumbnails. */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ThumbHolder> {

    public interface Listener {
        void onImageClick(File file);
        void onImageLongClick(File file);
    }

    private final List<File> files = new ArrayList<>();
    private final Listener listener;

    public ImageAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setFiles(List<File> newFiles) {
        files.clear();
        files.addAll(newFiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThumbHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ThumbHolder((ImageView) v);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbHolder holder, int position) {
        File file = files.get(position);
        Bitmap bmp = ImageUtils.loadBitmap(file, 250);
        holder.imageView.setImageBitmap(bmp);
        holder.imageView.setOnClickListener(v -> listener.onImageClick(file));
        holder.imageView.setOnLongClickListener(v -> {
            listener.onImageLongClick(file);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class ThumbHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ThumbHolder(@NonNull ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}
