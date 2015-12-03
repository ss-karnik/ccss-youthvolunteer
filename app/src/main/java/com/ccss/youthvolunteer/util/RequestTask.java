package com.ccss.youthvolunteer.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;

public abstract class RequestTask<Params,Result> extends AsyncTask<Params,Void,Result> {

    public static int CONNECT_TIMEOUT = 20 * 1000;
    public static int RESPONSE_TIMEOUT = 60 * 1000;
    private final Type clz;
    private final String requestURI;
    private Exception ex;

    public RequestTask(String requestURI,Type clz) {
        this.clz = clz;
        this.requestURI = requestURI;
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            URL google = new URL(requestURI);
            URLConnection tc = google.openConnection();
            tc.setConnectTimeout(CONNECT_TIMEOUT);
            tc.setReadTimeout(RESPONSE_TIMEOUT);
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            Gson gson = new Gson();
            return gson.fromJson(in, clz);
        } catch (Exception ex) {
            Log.w(this.getClass().getName(), "Error while trying to execute request " + requestURI, ex);
            this.ex = ex;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result t) {
        if (t!=null) {
            onSuccess(t);
        }
        else if (ex!=null) {
            onError(ex);
        }
    }

    protected abstract void onSuccess(Result t);

    protected abstract void onError(Exception ex);
}
