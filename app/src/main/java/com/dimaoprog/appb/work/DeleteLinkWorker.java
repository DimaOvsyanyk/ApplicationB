package com.dimaoprog.appb.work;

import android.content.ContentUris;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.dimaoprog.appb.utils.Constants.COLUMN_ID;
import static com.dimaoprog.appb.utils.Constants.URI_IMAGE;

public class DeleteLinkWorker extends Worker {

    public DeleteLinkWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        long id = getInputData().getLong(COLUMN_ID, 0);
        getApplicationContext().getContentResolver().delete(ContentUris.withAppendedId(URI_IMAGE, id), null, null);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getApplicationContext(), "Link deleted", Toast.LENGTH_SHORT).show());
        return Result.success();
    }
}
