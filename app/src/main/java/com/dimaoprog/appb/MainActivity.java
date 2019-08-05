package com.dimaoprog.appb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dimaoprog.appb.databinding.ActivityMainBinding;
import com.dimaoprog.appb.work.DeleteLinkWorker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.dimaoprog.appb.utils.Constants.COLUMN_ID;
import static com.dimaoprog.appb.utils.Constants.COLUMN_OPEN_TIME;
import static com.dimaoprog.appb.utils.Constants.COLUMN_STATUS;
import static com.dimaoprog.appb.utils.Constants.COLUMN_URL;
import static com.dimaoprog.appb.utils.Constants.FULL_PATH;
import static com.dimaoprog.appb.utils.Constants.OPEN_START;
import static com.dimaoprog.appb.utils.Constants.STATUS_ERROR;
import static com.dimaoprog.appb.utils.Constants.STATUS_LOADED;
import static com.dimaoprog.appb.utils.Constants.URI_IMAGE;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();

    private long id;
    private String url;
    private int status;
    private long openTime;
    private long openStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        url = getIntent().getStringExtra(COLUMN_URL);
        status = getIntent().getIntExtra(COLUMN_STATUS, 0);
        id = getIntent().getLongExtra(COLUMN_ID, 0);
        openTime = getIntent().getLongExtra(COLUMN_OPEN_TIME, 0);
        openStart = getIntent().getLongExtra(OPEN_START, 0);

        switch (status) {
            case 0:
                openTime = System.currentTimeMillis() - openStart;
                addNewLink();
                break;
            case STATUS_LOADED:
                startDeleteLinkWork();
                loadImageToStorage();
                break;
        }
        showImage();
    }

    private void startDeleteLinkWork() {
        Data idData = new Data.Builder()
                .putLong(COLUMN_ID, id)
                .build();
        OneTimeWorkRequest deleteLinkWorkRequest = new OneTimeWorkRequest.Builder(DeleteLinkWorker.class)
                .setInputData(idData)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(deleteLinkWorkRequest);
    }

    private void addNewLink() {
        Uri addUri = getContentResolver().insert(URI_IMAGE, generateLink());
        id = ContentUris.parseId(addUri);
    }

    private void updateLink() {
        int updatedCount = getContentResolver().update(ContentUris.withAppendedId(URI_IMAGE, id), generateLink(), null, null);
    }

    private ContentValues generateLink() {
        ContentValues linkValue = new ContentValues();
        linkValue.put(COLUMN_ID, id);
        linkValue.put(COLUMN_URL, url);
        linkValue.put(COLUMN_STATUS, status);
        linkValue.put(COLUMN_OPEN_TIME, openTime);
        return linkValue;
    }

    private void showImage() {
        Glide.with(binding.imgPic.getContext())
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        status = STATUS_ERROR;
                        updateLink();
                        Toast.makeText(getApplicationContext(), "Error while loading image", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        status = STATUS_LOADED;
                        updateLink();
                        return false;
                    }
                })
                .into(binding.imgPic);
    }

    private void loadImageToStorage() {
        disposable.add(Observable.fromCallable(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) stringToURL(url).openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Objects.requireNonNull(connection).disconnect();
            }
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                            if (bitmap != null) {
                                Uri imageExternalUri = saveImageToExternalStorage(bitmap);
                                Log.d("load image", "Image saved " + imageExternalUri.toString());
                            } else {
                                Log.d("load image", "bitmap = null");
                            }
                        },
                        error -> Log.d("error", error.getMessage())));
    }

    protected Uri saveImageToExternalStorage(Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "Error, please check your SD card", Toast.LENGTH_SHORT).show();
            return null;
        }
        File bdTestDir = new File(FULL_PATH);
        if (!bdTestDir.exists()) {
            bdTestDir.mkdirs();
        }
        File image = new File(bdTestDir, id + ".jpeg");
        try {
            OutputStream stream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(image.getAbsolutePath());
    }

    protected URL stringToURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}