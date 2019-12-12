package com.example.amruth.warfarinpredictorclient.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.amruth.warfarinpredictorclient.R;
import com.example.amruth.warfarinpredictorclient.patient.Patient;
import com.example.amruth.warfarinpredictorclient.patient.PatientMedicalData;
import com.example.amruth.warfarinpredictorclient.utils.RequestQueueSingleton;
import com.example.amruth.warfarinpredictorclient.patient.PatientWarfarinMedicalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
class NetworkCheck implements Runnable {

    NetworkCheck(long minTime) {
        this.minTime = minTime;
    }

    public void run() {
        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else
                    connected = false;
            }
            public  void onFinish(){
                textView.setText("FINISH!!");
            }
        }.start();

    }
}

    NetworkCheck nw = new NetworkCheck(143);
     new Thread(nw).start();
*/
public class MainActivity extends AppCompatActivity {


    /*
    String age= getIntent().getExtras().getString("age");
    EditText ageInput = (EditText)findViewById(R.id.patient_age);
    ageInput.setText();
*/
    public static int patientAge=0;
    public static long oldINRValue=0;
    public static long newINRValue=0;
    public static long oldDosage=0;

    int flag=0;
    private String logAppender = "WarfarinPredictorClient";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag=0;



        EditText ageInput = (EditText)findViewById(R.id.patient_age);

        if (ageInput.getText().equals("")){
            patientAge=0;
        }
        EditText oldINRInput = (EditText)findViewById(R.id.patient_oldINR);

        if (oldINRInput.getText().equals("")){
            oldINRValue=0;
        }

        EditText newINRInput = (EditText)findViewById(R.id.patient_newINR);

        if (newINRInput.getText().equals("")){
            newINRValue=0;
        }

        EditText oldDosageInput = (EditText)findViewById(R.id.patient_previous_dosage);

        if (oldDosageInput.getText().equals("")){
            oldDosage=0;
        }



        boolean netcheck=isNetworkConnected();
        if(!netcheck){
            Log.i(getLogAppender(),"No internet");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Net Connection Is Not Present");
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.i(getLogAppender(),"Dialog created n shown");
        }
        else{
            try{
            String user=getIntent().getExtras().getString("user");
            if (user.equals("null")) {
                Log.i(logAppender, "Null user value");
            }
            else{Log.i(logAppender,"Obtained user name "+user);

            Toast welcome = Toast.makeText(getApplicationContext(),"Welcome "+user,Toast.LENGTH_LONG );
            welcome.show();}}
            catch (Exception e){Log.i(logAppender,"Null user value");}

            ageInput = (EditText)findViewById(R.id.patient_age);
            if (patientAge==0){
                ageInput.setText("");
            }
            else{
                ageInput.setText(patientAge+"");
            }
            oldINRInput = (EditText)findViewById(R.id.patient_oldINR);
            if (oldINRValue==0){
                oldINRInput.setText("");
            }
            else{
                oldINRInput.setText(oldINRValue+"");
            }

            newINRInput = (EditText)findViewById(R.id.patient_newINR);
            if(newINRValue==0){
                newINRInput.setText("");
            }else{
            newINRInput.setText(newINRValue+"");}

            oldDosageInput = (EditText)findViewById(R.id.patient_previous_dosage);
            if (oldDosage==0){
                oldDosageInput.setText("");
            }else{
            oldDosageInput.setText(oldDosage+"");}


        }

    }


    public void predictDosage(View view) {

        boolean netcheck=isNetworkConnected();
        Log.i(getLogAppender(),"Net has been checked");
        if(!netcheck){
            Log.i(getLogAppender(),"No internet");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Net Connection Is Not Present");
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.i(getLogAppender(),"Dialog created n shown");
        }
        else {
            Log.i(getLogAppender(),"There is internet");

            Log.i(getLogAppender(), "Prediction request initialized");
            try {
                Patient patient = getPatient();
                JsonObjectRequest dosagePredictionRequest = makeDosagePredictionRequest(patient);
                RequestQueueSingleton.getInstance(this).addToRequestQueue(dosagePredictionRequest);
            } catch (JSONException e) {
                Log.i(logAppender, "Error parsing Input");
                e.printStackTrace();
            }

        }
    }

    private Patient getPatient() {
        PatientMedicalData patientMedicalData = getPatientDataFromUI();
        Patient.getInstance().setPatientMedicalData(patientMedicalData);
        return Patient.getInstance();
    }

    private JsonObjectRequest makeDosagePredictionRequest(final Patient patient) throws JSONException {

        String dosagePredictionServerURL = "https://warfarin-predictor.herokuapp.com/predictor";
        JsonObjectRequest dosagePredictionRequest = new JsonObjectRequest(Request.Method.POST,
                    dosagePredictionServerURL, patient.getPatientMedicalDataJSON(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(logAppender, "Response received");
                    String predictedDosage = response.optString("response");
                    Log.d(logAppender,"Predicted dosage is " + predictedDosage);
                    System.out.println(response.toString());
                    //check for negative predocted value of dosage
                   // int pd = Integer.parseInt(predictedDosage);
                    if (predictedDosage.charAt(0) == '-' && newINRValue>=2 && newINRValue<=5){
                       Toast negToast = Toast.makeText(getApplicationContext(),"The App cannot help you. Contact Physician",Toast.LENGTH_LONG );
                        //meetDocToast.setDuration(10);
                        negToast.show();
                        //Intent i=new Intent(MainActivity.this,MainActivity.class);
                        //finish();
                        //overridePendingTransition(0,0);
                        //startActivity(i);
                        //overridePendingTransition(0,0);
                        Log.i(logAppender,"Negative output");
                        flag=1;
                    }
                      Log.i(logAppender,"Going to check for flag "+flag);
                    // Insert flag variable here to check for error
                    if (flag==0){
                        Log.i(logAppender,"Checked flag");
                        Intent resultIntent = new Intent(getBaseContext(),ResultActivity.class);
                    resultIntent.putExtra("dosage",predictedDosage);
                    startActivity(resultIntent);}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(logAppender,"Prediction Error");
                    error.printStackTrace();
                    displayPredictionError(error);
                }
            }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> headers = new HashMap<String, String>();
                String authorizationToken = "Token "+patient.getAuthorizationToken();
                headers.put("Authorization",authorizationToken);
                return headers;
            }
        };

        return dosagePredictionRequest;

    }

    private void displayPredictionError(final VolleyError error){
        if(error instanceof TimeoutError){
            Toast timeoutToast = Toast.makeText(this,"Request timed out, " +
                    "please try again", Toast.LENGTH_LONG);
            timeoutToast.show();
        }
    }


    public void logout_main(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(logAppender,"Going back to login activity");
                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
                Log.i(logAppender,"Came back to login activity");
                Log.i(logAppender,"Changed to null vals");
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
             //   Intent intent = new Intent(getBaseContext(),MainActivity.class);
             //   startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        /* Log.i(logAppender,"Going back to login activity");
        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
        Log.i(logAppender,"Came back to login activity");
        //EditText pwd = (EditText)findViewById(R.id.password);
        //EditText uname = (EditText)findViewById(R.id.Username);
        //pwd.getText().clear();
        //uname.getText().clear();
        Log.i(logAppender,"CHanged to null vals");
        */
    }

    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(logAppender,"Going back to login activity");
                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
                Log.i(logAppender,"Came back to login activity");
                Log.i(logAppender,"Changed to null vals");
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              //  Intent intent = new Intent(getBaseContext(),MainActivity.class);
              //  startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private PatientMedicalData getPatientDataFromUI(){

        Toast ageToast = Toast.makeText(this,"Please enter age",Toast.LENGTH_LONG );
        Toast oldinrToast = Toast.makeText(this,"Please enter old INR value",Toast.LENGTH_LONG );
        Toast newinrToast = Toast.makeText(this,"Please enter new INR value",Toast.LENGTH_LONG );
        Toast olddosageToast = Toast.makeText(this,"Please enter old Dosage value",Toast.LENGTH_LONG );



        EditText ageInput = (EditText)findViewById(R.id.patient_age);
        boolean flag1=ageInput.getText().toString().isEmpty();
        if (!flag1){
            patientAge = Integer.parseInt(ageInput.getText().toString());
        }
        else{
            flag=1;
            Log.i(logAppender, "Please enter age");
        }


        EditText oldINRInput = (EditText)findViewById(R.id.patient_oldINR);
        boolean flag2=oldINRInput.getText().toString().isEmpty();
        if (!flag2){
            oldINRValue = (long)Float.parseFloat(oldINRInput.getText().toString());
        }
        else{
            flag=1;
            Log.i(logAppender, "Old INR value is null");
        }

        EditText newINRInput = (EditText)findViewById(R.id.patient_newINR);
        boolean flag3=newINRInput.getText().toString().isEmpty();
        if (!flag3){
            newINRValue = (long)Float.parseFloat(newINRInput.getText().toString());
            if (newINRValue<2 || newINRValue>5){
                Toast meetDocToast = Toast.makeText(this,"Contact Your Physician",Toast.LENGTH_LONG );
                //meetDocToast.setDuration(10);
                meetDocToast.show();
                //Intent i=new Intent(MainActivity.this,MainActivity.class);
                //finish();
                //overridePendingTransition(0,0);
                //startActivity(i);
                //overridePendingTransition(0,0);
                flag=1;
            }
        }
        else{
            flag=1;
            Log.i(logAppender, "New INR value is null");
        }

        EditText oldDosageInput = (EditText)findViewById(R.id.patient_previous_dosage);
        boolean flag4=oldDosageInput.getText().toString().isEmpty();

        if(!flag4){
            oldDosage = (long)Float.parseFloat(oldDosageInput.getText().toString());
        }
        else{
            flag=1;
            Log.i(logAppender, "Old Dosage value is null");
        }

        //Intent i=new Intent(MainActivity.this,MainActivity.class);
       /*
        if (!flag1){
            i.putExtra("age",ageInput.getText().toString());
        }
        else{
            i.putExtra("age","");
        }

        if (!flag2){
            i.putExtra("oldinr",oldINRInput.getText().toString());
        }
        else{
            i.putExtra("oldinr","");
        }

        if (!flag3){
            i.putExtra("newinr",newINRInput.getText().toString());
        }
        else{
            i.putExtra("newinr","");
        }

        if (!flag4){
            i.putExtra("olddose",oldDosageInput.getText().toString());
        }
        else{
            i.putExtra("olddose","");
        }

        //finish();
        //overridePendingTransition(0,0);
        //startActivity(i);
        //overridePendingTransition(0,0);       */
        if (flag1)
            ageToast.show();
        if (flag2)
            oldinrToast.show();
        if (flag3)
            newinrToast.show();
        if(flag4)
            olddosageToast.show();

/*
        int patientAge=0;
        EditText ageInput = (EditText)findViewById(R.id.patient_age);
        if (ageInput.getText().toString().isEmpty()){
            Toast ageToast = Toast.makeText(this,"Please enter age",Toast.LENGTH_LONG );
            Log.i(logAppender, "Please enter age");
            //meetDocToast.setDuration(10);
            //ageToast.show();
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            /*if (!flag2){
                i.putExtra("oldinr",oldINRInput.getText().toString());
            }
            else{
                i.putExtra("oldinr","");
            }
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);
            ageToast.show();
            flag=1;
        }

     else{
         patientAge = Integer.parseInt(ageInput.getText().toString());}


        EditText oldINRInput = (EditText)findViewById(R.id.patient_oldINR);
        long oldINRValue = 0;

        if (oldINRInput.getText().toString().isEmpty()){
            Toast oldinrToast = Toast.makeText(this,"Please enter old INR value",Toast.LENGTH_LONG );
            Log.i(logAppender, "Old INR value is null");
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);
            oldinrToast.show();
            flag=1;
        }

        else{
            oldINRValue = (long)Float.parseFloat(oldINRInput.getText().toString());}

        EditText newINRInput = (EditText)findViewById(R.id.patient_newINR);
        long newINRValue = 0;

        if (newINRInput.getText().toString().isEmpty()){
            Toast newinrToast = Toast.makeText(this,"Please enter new INR value",Toast.LENGTH_LONG );
            Log.i(logAppender, "New INR value is null");
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);
            newinrToast.show();
            flag=1;
        }

        else{
            newINRValue = (long)Float.parseFloat(newINRInput.getText().toString());}

        if (newINRValue<2 || newINRValue>5){
            Toast meetDocToast = Toast.makeText(this,"Contact Your Physician",Toast.LENGTH_LONG );
            //meetDocToast.setDuration(10);
            meetDocToast.show();
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);
            flag=1;
        }


        EditText oldDosageInput = (EditText)findViewById(R.id.patient_previous_dosage);
        long oldDosage = 0;

        if (oldDosageInput.getText().toString().isEmpty()){
            Toast olddosageToast = Toast.makeText(this,"Please enter old Dosage value",Toast.LENGTH_LONG );
            Log.i(logAppender, "Old Dosage value is null");
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);
            olddosageToast.show();
            flag=1;
        }

        else{
            oldDosage = (long)Float.parseFloat(oldDosageInput.getText().toString());}
*/
        Spinner genderInput = (Spinner)findViewById(R.id.patient_gender);
        String gender = genderInput.getItemAtPosition(genderInput.getSelectedItemPosition()).toString();

        Spinner procedureInput = (Spinner)findViewById(R.id.patient_procedure);
        String procedureType = procedureInput.getItemAtPosition(procedureInput.getSelectedItemPosition()).toString();

        PatientMedicalData patientWarfarinMedicalData = PatientWarfarinMedicalData.Builder.newInstance()
                .setAge(patientAge)
                .setGender(gender)
                .setOldINRValue(oldINRValue)
                .setNewINRValue(newINRValue)
                .setOldDose(oldDosage)
                .setProcedure(procedureType)
                .build();

        return patientWarfarinMedicalData;

    }

    protected String getLogAppender() {
        return logAppender;
    }

    private boolean isNetworkConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        flag=0;
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
                boolean netcheck=isNetworkConnected();
                Log.i(getLogAppender(),"Net has been checked");
                if(!netcheck){
                    Log.i(getLogAppender(),"No internet");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Net Connection Is Not Present");
                    builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.i(getLogAppender(),"Dialog created n shown");
                }
                else {
                    Log.i(getLogAppender(),"There is internet");

                    Log.i(getLogAppender(), "Prediction request initialized");
                    try {
                        Patient patient = getPatient();
                        JsonObjectRequest dosagePredictionRequest = makeDosagePredictionRequest(patient);
                        RequestQueueSingleton.getInstance(this).addToRequestQueue(dosagePredictionRequest);
                    } catch (JSONException e) {
                        Log.i(logAppender, "Error parsing Input");
                        e.printStackTrace();
                    }

                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}
