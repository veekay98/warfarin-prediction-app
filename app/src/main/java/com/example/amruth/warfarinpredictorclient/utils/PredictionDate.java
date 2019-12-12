package com.example.amruth.warfarinpredictorclient.utils;

import java.util.Objects;

public class PredictionDate {
    int day,month,year;


    public PredictionDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public int hashCode(){
        return Objects.hash(day,month,year);
    }

    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;

        if(!(o instanceof PredictionDate))
            return false;

        PredictionDate predictionDate = (PredictionDate) o;
        if (predictionDate.day == this.day &&
                predictionDate.month == this.month && predictionDate.year == this.year){
            return true;
        }
        return false;
    }
}
