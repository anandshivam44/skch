package com.example.tryingsketch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveSketch extends AppCompatActivity {

    private static final String TAG = "MyTag";
    @BindView(R.id.button_1)
    Button button;
    @BindView(R.id.button_2)
    Button button2;
    @BindView(R.id.button_3)
    Button button3;
    @BindView(R.id.saveImage)
    ImageView imageView;
    @BindView(R.id.button_4)
    Button button4;
    @BindView(R.id.button_5)
    Button button5;

    private AdView add;
    File fname;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_sketch);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView.setImageBitmap(BitmapHelper.getInstance().getBitmap());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        add = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        add.loadAd(adRequest);

    }

    @OnClick(R.id.button_1)
    public void onViewClicked() {
        handlePermission();
        saveScreenshot();
    }

    @OnClick(R.id.button_2)
    public void onButton2Clicked() {
        handlePermission();
        Toast.makeText(this, "Button 3 Clicked", Toast.LENGTH_SHORT).show();
        saveScreenshot();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path); // a directory
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }

    @OnClick(R.id.button_3)
    public void onButton3Clicked() {
        handlePermission();
        Toast.makeText(this, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
        saveScreenshot();
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fname);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @OnClick(R.id.button_4)
    public void onButton4Clicked() {
        saveScreenshot();

        Uri imgUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fname);
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        whatsappIntent.setType("image/jpeg");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_5)
    public void onButton5Clicked() {
        saveScreenshot();

        Uri imgUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fname);
        Intent Intent = new Intent(android.content.Intent.ACTION_SEND);
        Intent.putExtra(Intent.EXTRA_STREAM, imgUri);
        Intent.setType("image/jpeg");
        Intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }

    private void saveScreenshot() {


        if (ActivityCompat.checkSelfPermission(SaveSketch.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences sharedPreferences = getSharedPreferences("fileName", MODE_PRIVATE);
            int index = sharedPreferences.getInt("index", 0);
            Bitmap bb;
            String filename = Environment.getExternalStorageDirectory() + "/Pictures/Sketch/Sketch_" + String.valueOf(index) + ".png";
            path = Environment.getExternalStorageDirectory() + "/Pictures/Sketch/";
            Toast.makeText(this, "Saved as " + filename, Toast.LENGTH_SHORT).show();
            bb = BitmapHelper.getInstance().getBitmap();
            File file = new File(filename);
            file.getParentFile().mkdirs();

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bb.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fname = file;
            index++;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("index", index);
            editor.apply();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void handlePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(SaveSketch.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        102);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Read Storage Granted", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "Read Storage Granted");

            } else {
                Toast.makeText(this, "Can't save without Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Read Storage DECLINED");
            }
        }

    }

}