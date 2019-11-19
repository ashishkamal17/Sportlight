package com.roninaks.sportlight.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;


import com.roninaks.sportlight.R;
import com.roninaks.sportlight.activities.NoInternetActivity;
import com.roninaks.sportlight.interfaces.SqlDelegate;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nihalpradeep on 10/08/18.
 */

public class SqlHelper {
    private String MasterUrl;
    private Context context;
    private String ExecutePath;
    private JSONObject JSONResponse;
    private String StringResponse;
    private String ActionString;
    private SqlDelegate sqlDelegate;
    private ContentValues params;
    private String Method;
    private boolean showLoading;
    private boolean isService;
    private boolean isMandatory = false;
    private String UploadFilePath;
    private HashMap<String, String> Extras;

    //Constructors
    public SqlHelper(Context context){
        this(context, null);
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate){
        this(context, sqlDelegate, "");

    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String urlTarget){
        this(context, sqlDelegate, urlTarget, "");

    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String urlTarget, String executePath){
        this(context, sqlDelegate, urlTarget, executePath, false);
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String urlTarget, String executePath, Boolean isMandatory){
        this.context = context;
        this.sqlDelegate = sqlDelegate;
        this.ExecutePath = executePath;
        isService = false;
        isMandatory = isMandatory;
        switch (urlTarget){
            case "database":MasterUrl = context.getString(R.string.database_url);
                break;
            case "booking":MasterUrl = context.getString(R.string.booking_url);
                break;
            case "checkout":MasterUrl = context.getString(R.string.checkout_url);
                break;
            case "social":MasterUrl = context.getString(R.string.socialmedia_url);
                break;
            default:MasterUrl = context.getString(R.string.database_url);
                break;
        }
    }

    //Getters
    public Context getContext() {
        return context;
    }

    public String getExecutePath() {
        return ExecutePath;
    }

    public String getMasterUrl() {
        return MasterUrl;
    }

    public JSONObject getJSONResponse() {
        return JSONResponse;
    }

    public String getActionString() {
        return ActionString;
    }

    public String getStringResponse(String key) {
        try {
            return JSONResponse.getString(key);
        } catch (Exception e){
            Log.e("SqlHelper:getStringResp", e.getMessage());
            return "exception";
        }
    }

    public SqlDelegate getSqlDelegate() {
        return sqlDelegate;
    }

    public ContentValues getParams() {
        return params;
    }

    public String getMethod() {
        return Method;
    }

    public String getUploadFilePath() {
        return UploadFilePath;
    }

    public HashMap<String, String> getExtras() {
        return Extras;
    }

    public boolean isShowLoading(){
        return showLoading;
    }

    public boolean isService() {
        return isService;
    }

    public boolean isMandatory(){ return  isMandatory; }

    //Setters
    public void setSqlDelegate(SqlDelegate sqlDelegate) {
        this.sqlDelegate = sqlDelegate;
    }

    public void setContext(Context context) {
        context = context;
    }

    public void setExecutePath(String executePath) {
        ExecutePath = executePath;
    }

    public void setMasterUrl(String masterUrl) {
        MasterUrl = masterUrl;
    }

    public void setActionString(String actionString) {
        ActionString = actionString;
    }

    public void setJSONResponse(JSONObject JSONResponse) {
        this.JSONResponse = JSONResponse;
    }

    public void setStringResponse(String stringResponse) {
        StringResponse = stringResponse;
    }

    public void setParams(ContentValues params) {
        this.params = params;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public void setUploadFilePath(String uploadFilePath) {
        UploadFilePath = uploadFilePath;
    }

    public void setExtras(HashMap<String, String> extras) {
        Extras = extras;
    }

    public void setService(boolean isService){
        this.isService = isService;
    }


    //Public methods
    public void executeUrl(Boolean showLoading){
        this.showLoading = showLoading;
        LoadResponse loadResponse = new LoadResponse();
        loadResponse.execute();
    }

    //Private methods
    private Boolean isOnline() {
        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal>=0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private String getQuery(ContentValues parameters) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for(Map.Entry<String,Object> entry : parameters.valueSet()){
            if (first)
                first = false;
            else
                result.append("&");
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue().toString(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return result.toString();
    }

    //Async Tasks
    public class LoadResponse extends AsyncTask<Void, Void, Void>{
        TransparentProgressDialog pDialog;
        Boolean canceled = false;

        @Override
        protected Void doInBackground(Void... voids) {
//            try {
//                if(!(isNetworkAvailable() && isOnline())){
//                    canceled = true;
//                }else {
//                    JSONObject jsonObject = null;
//                    if (Method.equals("GET"))
//                        jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "GET", params);
//                    else if (Method.equals("POST"))
//                        jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "POST", params);
//                    JSONResponse = jsonObject;
//                }
//            }catch (Exception e){
//                Log.e("SqlHelper:Background", e.getMessage());
//            }
//            return null;
            HttpURLConnection httpURLConnection = null;
            InputStream IS = null;
            int temp;
            try {
                if(!(isNetworkAvailable())){
                    canceled = true;
                }else {
                    URL url = null;
                    if (Method.equals("GET")) {
                        url = new URL(MasterUrl + ExecutePath + "?" + getQuery(params));
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setDoOutput(false);
                        httpURLConnection.setRequestMethod(Method);
                        httpURLConnection.setConnectTimeout(30000);
                        httpURLConnection.setReadTimeout(30000);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                    } else if (Method.equals("POST")) {
                        url = new URL(MasterUrl + ExecutePath);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setRequestMethod(Method);
                        httpURLConnection.setConnectTimeout(30000);
                        httpURLConnection.setReadTimeout(30000);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        OutputStream OS = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                        String data = getQuery(params);
                        bufferedWriter.write(data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        OS.close();
                    }
                    IS = httpURLConnection.getInputStream();
                    String response = "";
                    while ((temp = IS.read()) != -1) {
                        response += (char) temp;
                    }
                    JSONResponse = new JSONObject(response);
                    return null;
                }
            } catch (Exception e){
                canceled = true;
            } finally {
                if (httpURLConnection != null)
                {
                    httpURLConnection.disconnect();
                }
                try {
                    if (IS != null) {
                        IS.close();
                    }
                } catch (IOException e) {

                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(showLoading) {
                pDialog = new TransparentProgressDialog(context);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(showLoading)
                pDialog.dismiss();
            if(canceled && !isService){
                NoInternetActivity.sqlHelper = SqlHelper.this;
                context.startActivity(new Intent(context, NoInternetActivity.class));
            }else {
                if(sqlDelegate != null) {
                    sqlDelegate.onResponse(SqlHelper.this);
                }
            }
        }

    }
}
