package com.example.amruth.warfarinpredictorclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.amruth.warfarinpredictorclient.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private  Map<Date,Integer> dailyDosageList;
    private static final Integer SEQUENCE_LENGTH = 7;
    private static String logAppender = "WarfarinPredictorClient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(logAppender,"ResultActivity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Getting value passed from the main activity
        double predictedDosage = getPredictedDosageFromIntent();

        // The predicted dosage values for all 30 days
        setDailyDosageList(predictedDosage);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        setCurrentDate(calendarView);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                displayDosage(eventDay);
            }
        });

        displayDosage(getCurrentDate());
    }

    // Getting the predicted dosage value passed from the main activity
    private double getPredictedDosageFromIntent() {
        String predictedDosageText = getIntent().getExtras().getString("dosage");
        return Double.parseDouble(predictedDosageText);
    }

    //
    private void setDailyDosageList(Double predictedDosage) {
        dailyDosageList = getDailyDosage(predictedDosage, SEQUENCE_LENGTH);
    }

    private int initializePredictionListSum(List<Integer> predictionList) {
        int sum = 0;
        for(int number: predictionList){
            sum += number;
        }
        return sum;
    }

    private List<Integer> initializePredictionList(int frequency, int lowerBound) {
        List<Integer> predictionList = new ArrayList<>();
        for(int counter = 0;counter<frequency;counter++){
            predictionList.add(lowerBound);
        }
        return predictionList;
    }

    // Returns the predction list with last value as ceil instead of floor
    private List<Integer> getPredictedDosageSequence(double predictedDosage, int frequency) {
        int dosageUpperBound = (int) Math.ceil(predictedDosage);
        int dosageLowerBound = (int) Math.floor(predictedDosage);
        // Includes the floor of decimal values of dosage for 7 days
        List<Integer> predictionList = initializePredictionList(frequency, dosageLowerBound);
        // Includes the sum of decimal dosages for 7 days
        double targetTotalDosage = predictedDosage*frequency;
        // The sum of the floor values for dosage for 7 days
        int predictionListSum = initializePredictionListSum(predictionList);
        // Changes the seventh value of dosage to ceil rather than floor
        while (targetTotalDosage - predictionListSum >0){
            predictionListSum = 0;
            int lowerBoundLastIndex = predictionList.lastIndexOf(dosageLowerBound);
            predictionList.set(lowerBoundLastIndex,dosageUpperBound);
            for(int number : predictionList){
                predictionListSum += number;
            }
        }
        return predictionList;
    }

    private Calendar getCurrentCalender() {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MILLISECOND,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.ZONE_OFFSET,0);
        return cal;
    }

    private Date getCurrentDate() {
        return getCurrentCalender().getTime();
    }

    private Map<Date,Integer> getDailyDosage(double predictedDosage, int sequenceLength){
        // Returns new sequence with last value as ceil not floor
        List<Integer> predictedDosageSequence = getPredictedDosageSequence(predictedDosage, sequenceLength);
        Map<Date,Integer> dailyDosagePrediction = new HashMap<Date, Integer>();
        // Returns calendar with todays date highlighted
        Calendar cal = getCurrentCalender();
        // Puts the daily prediction values in an array of 30 days
        for(int counter = 0;counter<30;counter++){
            dailyDosagePrediction.put(cal.getTime(),predictedDosageSequence.get(counter%predictedDosageSequence.size()));
            cal.add(Calendar.DATE,1);
        }
        Log.i(logAppender, "Dosage for next 30 days is " + dailyDosagePrediction.toString());
        return dailyDosagePrediction;

    }

    private void displayDosage(EventDay eventDay) {
        if (eventDay != null && eventDay.getCalendar() != null) {
            displayDosage(eventDay.getCalendar().getTime());
        }
    }

    private void displayDosage(Date date) {
        String predictedDosageText = "unavailable";
        Map<String,Integer> sevenDayDosagePrediction = new HashMap<String, Integer>();
        Calendar cal = getCurrentCalender();
     //   for(int counter = 0;counter<7;counter++){
     //       sevenDayDosagePrediction.put(cal.getTime(),dailyDosageList.get(counter));
     //       cal.add(Calendar.DATE,1);
     //   }
        // Integer dosage = dailyDosageList.
        for (int i=0;i<7;i++){
           sevenDayDosagePrediction.put(DateFormat.format("EEEE", cal.getTime())+"",getPredictedDosageSequence(getPredictedDosageFromIntent(), 7).get(i));
            cal.add(Calendar.DATE,1);

        }

       // String dayOfTheWeek = (String) DateFormat.format("EEEE", date);
         /*
        Collection<String> arrlist = new ArrayList<String>(7);
        for ( Date date1 : sevenDayDosagePrediction.keySet() ) {
            arrlist.add((DateFormat.format("EEEE", date1))+"");
            Log.i(logAppender,DateFormat.format("EEEE", date1)+" ");
        }

        Log.i(logAppender,sevenDayDosagePrediction.toString());
        Collection<Integer> a =sevenDayDosagePrediction.values();
        */

        String pred=sevenDayDosagePrediction.toString();

        // get(date);
      //  if (dosage != null) {
      //      predictedDosageText = dosage.toString();
      //      Log.i(logAppender,"Predicted dosage is : " + predictedDosageText);
      //  }
        TextView predictedDosageView = (TextView)findViewById(R.id.predictedDosage);
        predictedDosageView.setText("Warfarin dosage for next week is " + pred);
       // predictedDosageView.setText("Warfarin dosage is " + predictedDosageText);

    }

    /*
      private void displayDosage(Date date) {
        String predictedDosageText = "unavailable";
        Integer dosage = dailyDosageList.get(date);
        if (dosage != null) {
            predictedDosageText = dosage.toString();
            Log.i(logAppender,"Predicted dosage is : " + predictedDosageText);
        }
        TextView predictedDosageView = (TextView)findViewById(R.id.predictedDosage);
        predictedDosageView.setText("Warfarin dosage is " + predictedDosageText);

    }
    */

    private void setCurrentDate(CalendarView calendarView) {
        Date currentDate = new Date();
        try {
            calendarView.setDate(currentDate);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed(){


                Log.i(logAppender,"Going back to main activity");
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                Log.i(logAppender,"Came back to main activity");

            }

    public void logout_result(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
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
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
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

}
