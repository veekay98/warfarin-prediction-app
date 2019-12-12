package com.example.amruth.warfarinpredictorclient.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.amruth.warfarinpredictorclient.R;
import com.example.amruth.warfarinpredictorclient.patient.Patient;
import com.example.amruth.warfarinpredictorclient.utils.RequestQueueSingleton;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;


//TODO: Convert url encoding to JSON request
public class LoginActivity extends AppCompatActivity {

    public static final String lOGAPPENDER = "WarfarinPredictorClient";
    int flag=0;
    int clickCount=0;
    Button btn;

    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean netcheck=isNetworkConnected();
        Log.i(lOGAPPENDER,"Net has been checked");
        if(!netcheck){
            Log.i(lOGAPPENDER,"No internet");
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Net Connection Is Not Present");
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.i(lOGAPPENDER,"Dialog created n shown");
        }
    }

    public void requestLogin(View view) {

        boolean netcheck=isNetworkConnected();
        Log.i(lOGAPPENDER,"Net has been checked");
        if(!netcheck){
            Log.i(lOGAPPENDER,"No internet");
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Net Connection Is Not Present");
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.i(lOGAPPENDER,"Dialog created n shown");
        }

        else{

        clickCount++;
        if (clickCount==1){
            btn= (Button) findViewById(R.id.loginButton);
            btn.setVisibility(View.GONE);
        }
        if (flag==0){

        final ImmutableMap<String,String> loginCredentials = getLoginCredentials();

        RequestQueue loginRequestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        String authenticationServerURL = "https://warfarin-predictor.herokuapp.com/predictor/api-token-auth";
        StringRequest loginRequest = new StringRequest(Request.Method.POST,
                authenticationServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(lOGAPPENDER, response);
                Gson gson = new Gson();
                HashMap<String,String> loginResponse = gson.fromJson(response,new TypeToken<HashMap<String,String>>(){}.getType());
                Log.d(lOGAPPENDER,"Authorization token " + loginResponse.get("token"));
                Patient.getInstance().setAuthorizationToken(loginResponse.get("token"));
                Intent intent = new Intent(getBaseContext(),MainActivity.class);

                intent.putExtra("user",getUserNameFromUI());

                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(lOGAPPENDER,"Error logging in");
                displayInvalidLoginError(error);
                btn= (Button) findViewById(R.id.loginButton);
                btn.setVisibility(View.GONE);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            public Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", loginCredentials.get("username"));
                params.put("password", loginCredentials.get("password"));
                return params;
            }


        };


        loginRequestQueue.add(loginRequest);
    }}}



    private Toast mToastToShow;
    public void showToast(View view) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 10000;
        mToastToShow = Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    


    private void displayInvalidLoginError(VolleyError error){
        if(error instanceof TimeoutError){
            Toast toast = Toast.makeText(getBaseContext(),"Request timed out, please try again",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        else {
           Toast toast = Toast.makeText(getBaseContext(), "Invalid username or password", Toast.LENGTH_LONG);
           toast.show();

          //  View v=findViewById(android.R.id.content).getRootView();
          //  showToast(v);
            return;

        }
    }

    private ImmutableMap<String, String> getLoginCredentials() {
        String username = getUserNameFromUI();
        String password = getPasswordFromUI();
        if (username.isEmpty() || password.isEmpty()){
            flag=1;
            Toast toast1 = Toast.makeText(getBaseContext(), "NULL username or password", Toast.LENGTH_LONG);
            toast1.show();
            /*
            Intent i=new Intent(LoginActivity.this,LoginActivity.class);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);
            overridePendingTransition(0,0);

             */
            btn= (Button) findViewById(R.id.loginButton);
            btn.setVisibility(View.GONE);
        }
        ImmutableMap<String,String> credentails = buildCredentialsMap(username,password);
        return credentails;
    }

    private ImmutableMap<String, String> buildCredentialsMap(String username, String password) {
        ImmutableMap<String,String> credentials = new ImmutableMap.Builder<String,String>()
                .put("username",username)
                .put("password",password)
                .build();
        return  credentials;
    }

    private String getPasswordFromUI() {
        EditText passwordInput = (EditText)findViewById(R.id.Password);
        return passwordInput.getText().toString();
    }

    private String getUserNameFromUI() {
        EditText userNameInput = (EditText)findViewById(R.id.Username);
        return userNameInput.getText().toString();
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
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
                boolean netcheck=isNetworkConnected();
                Log.i(lOGAPPENDER,"Net has been checked");
                if(!netcheck){
                    Log.i(lOGAPPENDER,"No internet");
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Net Connection Is Not Present");
                    builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.i(lOGAPPENDER,"Dialog created n shown");
                }

                else{

                    clickCount++;
                    if (clickCount==1){
                        btn= (Button) findViewById(R.id.loginButton);
                        btn.setVisibility(View.GONE);
                    }
                    if (flag==0){

                        final ImmutableMap<String,String> loginCredentials = getLoginCredentials();

                        RequestQueue loginRequestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                                .getRequestQueue();

                        String authenticationServerURL = "https://warfarin-predictor.herokuapp.com/predictor/api-token-auth";
                        StringRequest loginRequest = new StringRequest(Request.Method.POST,
                                authenticationServerURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(lOGAPPENDER, response);
                                Gson gson = new Gson();
                                HashMap<String,String> loginResponse = gson.fromJson(response,new TypeToken<HashMap<String,String>>(){}.getType());
                                Log.d(lOGAPPENDER,"Authorization token " + loginResponse.get("token"));
                                Patient.getInstance().setAuthorizationToken(loginResponse.get("token"));
                                Intent intent = new Intent(getBaseContext(),MainActivity.class);

                                intent.putExtra("user",getUserNameFromUI());

                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Log.d(lOGAPPENDER,"Error logging in");
                                displayInvalidLoginError(error);
                                btn= (Button) findViewById(R.id.loginButton);
                                btn.setVisibility(View.GONE);
                            }
                        })
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type","application/x-www-form-urlencoded");
                                return params;
                            }

                            @Override
                            public Map<String,String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("username", loginCredentials.get("username"));
                                params.put("password", loginCredentials.get("password"));
                                return params;
                            }


                        };


                        loginRequestQueue.add(loginRequest);}}
                return true;


            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}
