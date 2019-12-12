package com.example.amruth.warfarinpredictorclient.patient;

import org.json.JSONException;
import org.json.JSONObject;

class PatientMedicalDataWarfarinToJSONTransformer implements PatientMedicalDataTransformer {
    @Override
    public JSONObject transform(PatientMedicalData patientMedicalData) {
        JSONObject patientDataJSON = new JSONObject();
        try{
            patientDataJSON.put("age",patientMedicalData.getAge());
            patientDataJSON.put("gender",patientMedicalData.getGender());
            patientDataJSON.put("oldINRValue",patientMedicalData.getOldINRValue());
            patientDataJSON.put("newINRValue",patientMedicalData.getNewINRValue());
            patientDataJSON.put("procedure",patientMedicalData.getProcedure());
            patientDataJSON.put("oldDose",patientMedicalData.getOldDose());
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        finally {
            return patientDataJSON;
        }
    }
}
