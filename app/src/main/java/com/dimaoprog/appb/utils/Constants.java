package com.dimaoprog.appb.utils;

import android.net.Uri;
import android.os.Environment;

public class Constants {

    public static final int STATUS_LOADED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_UNKNOWN = 3;

    public static final String URL_PICKED = "com.dimaoprog.action.URL_PICKED";

    public static final String OPEN_START = "openStart";

    public static final String AUTHORITY = "com.dimaoprog.appa.data";
    public static final String DATABASE_TABLE_NAME = "image_link";
    public static final String DATABASE_NAME = "imagesDatabase";
    public static final Uri URI_IMAGE = Uri.parse("content://" + AUTHORITY + "/" + DATABASE_TABLE_NAME);
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_OPEN_TIME = "open_time";

    public static final String FULL_PATH = Environment.getExternalStorageDirectory().toString() + "/BIGDIG/test";
}
