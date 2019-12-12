package com.example.amruth.warfarinpredictorclient.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.widget.Toast;


import com.example.amruth.warfarinpredictorclient.activities.ResultActivity;

public class DosagePredictionTaskRunner extends AsyncTask<JSONObject,Void,String> {

    private JSONObject postData;
    private static String logAppender = "WarfarinPredictorClient";
    private int remainingRequestCount = 0;
    private Context context;
    private static String predictionServerAddress = "http://prediciton-api-prototype.herokuapp.com/predictor/predict/";

    public DosagePredictionTaskRunner(Context context){
        this.postData = new JSONObject();
        this.context = context;
    }

    private void setPatientData(JSONObject jsonObject){
        this.postData = jsonObject;
    }

    @Override
    protected void onPreExecute(){
        Log.i(logAppender,"Starting to execute prediction task");
    }


    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(JSONObject... patientData) {
        remainingRequestCount++;
        try {
            setPatientData(patientData[0]);
            URL predictionServerURL = new URL(predictionServerAddress);
            HttpURLConnection predictorConnection = (HttpURLConnection) predictionServerURL.openConnection();
            predictorConnection.setRequestMethod("POST");
            predictorConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            predictorConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            predictorConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Log.i(logAppender,this.postData.toString());
            // Send post request
            predictorConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(predictorConnection.getOutputStream());
            wr.writeBytes(this.postData.toString());
            wr.flush();
            wr.close();

            int responseCode = predictorConnection.getResponseCode();
            Log.i(logAppender,"\nSending 'POST' request to URL : " + predictionServerAddress);
            Log.i(logAppender,"Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(predictorConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result

            Log.d(logAppender,response.toString());
            return response.toString();

        } catch (MalformedURLException e) {
            Log.d(logAppender,"Malformed exeception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(logAppender,"Cannot connect to server");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        remainingRequestCount--;
        if(s==null){
            Toast toast = Toast.makeText(this.context,"Server Error, please contact admin",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Log.d(logAppender, s);
        Intent intent = new Intent(this.context, ResultActivity.class);
        intent.putExtra("result",s);
        context.startActivity(intent);
        ((Activity)context).finish();

    }
}
