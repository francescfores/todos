package com.iesebre.dam2.francesc.todos;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

public class TestAsyncTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private String mUrl;

    public TestAsyncTask(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String resultString = null;
        resultString = getJSON(mUrl);

        return resultString;
    }

    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);
    }

    private String getJSON(String url) {
    return url;
    }
}