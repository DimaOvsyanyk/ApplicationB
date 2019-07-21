package com.dimaoprog.appb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.dimaoprog.appb.databinding.ActivityFalseBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FalseActivity extends AppCompatActivity {

    private ActivityFalseBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_false);
        startCountToFinish();
    }

    private void startCountToFinish() {
        disposable.add(Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .take(10)
                .map(time -> String.valueOf(10 - time))
                .subscribe(time -> binding.setTime(time),
                        onError -> Log.d("error", onError.getMessage()),
                        this::finishAffinity));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
