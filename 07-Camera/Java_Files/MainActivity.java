package com.example.camerastorage;

import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Experiment 7 - capture an image with the camera app and store it in the
 * app's INTERNAL storage (getFilesDir()/images). Saved images are listed in a grid.
 */
public class MainActivity extends AppCompatActivity implements ImageAdapter.Listener {

    private static final String STATE_PENDING = "pending_file";

    private ImageView ivPreview;
    private TextView tvPath, tvEmpty;
    private ImageAdapter adapter;
    private File pendingFile;                       // file the camera is writing to
    private File previewFile;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCapture = findViewById(R.id.btnCapture);
        ivPreview = findViewById(R.id.ivPreview);
        tvPath = findViewById(R.id.tvPath);
        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rv = findViewById(R.id.rvImages);

        adapter = new ImageAdapter(this);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(STATE_PENDING);
            if (path != null) pendingFile = new File(path);
        }

        // Camera result handler: true = picture was written to the Uri
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (success && pendingFile != null && pendingFile.length() > 0) {
                        Toast.makeText(this, "Image saved to internal storage", Toast.LENGTH_SHORT).show();
                        showPreview(pendingFile);
                    } else if (pendingFile != null) {
                        pendingFile.delete();       // user cancelled -> remove empty file
                        Toast.makeText(this, "Capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                    pendingFile = null;
                    loadSavedImages();
                });

        btnCapture.setOnClickListener(v -> capturePhoto());
        loadSavedImages();
    }

    private File getImageDir() {
        File dir = new File(getFilesDir(), "images");   // internal storage, private to this app
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private void capturePhoto() {
        try {
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            pendingFile = new File(getImageDir(), "IMG_" + stamp + ".jpg");
            pendingFile.createNewFile();
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pendingFile);
            takePictureLauncher.launch(uri);
        } catch (IOException e) {
            Toast.makeText(this, "Could not create file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No camera app found on this device", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSavedImages() {
        File[] found = getImageDir().listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".jpg"));
        List<File> list = new ArrayList<>();
        if (found != null) {
            list.addAll(Arrays.asList(found));
            Collections.sort(list, (a, b) -> Long.compare(b.lastModified(), a.lastModified())); // newest first
        }
        adapter.setFiles(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        tvPath.setText("Folder: " + getImageDir().getAbsolutePath() + "\nSaved images: " + list.size());
        if (list.isEmpty()) {
            previewFile = null;
            ivPreview.setImageDrawable(null);
        }
    }

    private void showPreview(File file) {
        previewFile = file;
        ivPreview.setImageBitmap(ImageUtils.loadBitmap(file, 1024));
        tvPath.append("\nSelected: " + file.getName() + " (" + (file.length() / 1024) + " KB)");
    }

    // ---- ImageAdapter.Listener
    @Override
    public void onImageClick(File file) {
        showPreview(file);
    }

    @Override
    public void onImageLongClick(File file) {
        new AlertDialog.Builder(this)
                .setTitle("Delete image?")
                .setMessage(file.getName())
                .setPositiveButton("Delete", (d, w) -> {
                    if (file.delete()) {
                        if (file.equals(previewFile)) {
                            previewFile = null;
                            ivPreview.setImageDrawable(null);
                        }
                        loadSavedImages();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (pendingFile != null) outState.putString(STATE_PENDING, pendingFile.getAbsolutePath());
    }
}
