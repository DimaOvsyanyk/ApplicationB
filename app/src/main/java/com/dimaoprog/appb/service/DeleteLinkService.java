package com.dimaoprog.appb.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.dimaoprog.appb.utils.Constants.COLUMN_ID;
import static com.dimaoprog.appb.utils.Constants.URI_IMAGE;

public class DeleteLinkService extends Service {


    private CompositeDisposable disposable = new CompositeDisposable();

    public DeleteLinkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long id = intent.getLongExtra(COLUMN_ID, 0);
        disposable.add(Observable.timer(15, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {},
                        e -> Log.d("service", e.getMessage()),
                        () -> {
                            getContentResolver().delete(ContentUris.withAppendedId(URI_IMAGE, id), null, null);
                            Toast.makeText(getApplicationContext(), "Link deleted", Toast.LENGTH_SHORT).show();
//                            this.stopSelf();
                        }));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
