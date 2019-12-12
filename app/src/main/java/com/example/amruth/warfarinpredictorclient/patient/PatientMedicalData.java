package com.example.amruth.warfarinpredictorclient.patient;

import org.json.JSONException;
import org.json.JSONObject;

public interface PatientMedicalData {

    public String getGender();

    public String getProcedure();

    public int getAge();

    public long getOldINRValue();

    public long getNewINRValue();

    public long getOldDose();

}
