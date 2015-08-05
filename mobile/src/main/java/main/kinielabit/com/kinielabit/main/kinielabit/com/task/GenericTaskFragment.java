package main.kinielabit.com.kinielabit.main.kinielabit.com.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;

/**
 * Created by juanelo on 01/08/15.
 */
public class GenericTaskFragment extends Fragment {

    static public interface TaskCallBacks{
        void onPreExcute();
        void onProgressUpdate();
        void onCancelled();
        void onPostExcute(String result);
    }


    public static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskCallBacks mCallBacks;
    private GenericAsyncTask mTask;
    private boolean mRunning;

    private String mURL;
    private String mRequestMethod;
    private String mData;


    public void setData(String mURL, String mRequestMethod, String mData){

        this.mURL = mURL;
        this.mRequestMethod = mRequestMethod;
        this.mData = mData;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if ( !(activity instanceof TaskCallBacks)){
            throw new IllegalStateException("Activity debera implementar interfaz taskCallBacks");
        }
        mCallBacks = (TaskCallBacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }

    public void start(){
        if (!mRunning) {
            mTask  = new GenericAsyncTask();
            mTask.execute("");
            mRunning = true;
        }
    }

    public void cancel(){
        if (mRunning) {
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    public boolean isRunning(){
        return mRunning;
    }


    private class GenericAsyncTask extends AsyncTask<String, Object, String>{


        @Override
        protected void onPreExecute() {
            mCallBacks.onPreExcute();
            mRunning = true;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)(new java.net.URL(mURL)).openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(mRequestMethod);
                InputStream is =null;
                if (mRequestMethod.equals("PUT")){
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(mData.getBytes());
                    outputStream.flush();

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    is = conn.getInputStream();
                }else if (mRequestMethod.equals("GET")){
                    conn.setDoInput(true);
                    conn.connect();
                    is = conn.getInputStream();

                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                String json = sb.toString();
                Log.d(GenericTaskFragment.class.getSimpleName(), "Respuesta: "+ (json == null ? "Sin json" : json));


                return json;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                disposeConnection(conn);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mCallBacks.onPostExcute(s);
            mRunning = false;

        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            mCallBacks.onProgressUpdate();
        }

        @Override
        protected void onCancelled() {
            mCallBacks.onCancelled();
            mRunning = false;
        }
    }

    private void disposeConnection(HttpURLConnection conn) {

        if ( conn != null ){
            conn.disconnect();
        }

        conn = null;
    }
}
