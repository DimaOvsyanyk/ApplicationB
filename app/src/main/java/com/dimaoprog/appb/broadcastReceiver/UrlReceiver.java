package com.dimaoprog.appb.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dimaoprog.appb.MainActivity;

import static com.dimaoprog.appb.utils.Constants.COLUMN_ID;
import static com.dimaoprog.appb.utils.Constants.COLUMN_OPEN_TIME;
import static com.dimaoprog.appb.utils.Constants.COLUMN_STATUS;
import static com.dimaoprog.appb.utils.Constants.COLUMN_URL;
import static com.dimaoprog.appb.utils.Constants.OPEN_START;

public class UrlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra(COLUMN_ID, intent.getLongExtra(COLUMN_ID, 0));
        activityIntent.putExtra(COLUMN_URL, intent.getStringExtra(COLUMN_URL));
        activityIntent.putExtra(COLUMN_STATUS, intent.getIntExtra(COLUMN_STATUS, 0));
        activityIntent.putExtra(COLUMN_OPEN_TIME, intent.getLongExtra(COLUMN_OPEN_TIME, 0));

        activityIntent.putExtra(OPEN_START, intent.getLongExtra(OPEN_START, 0));

        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}