package com.example.amruth.warfarinpredictorclient.patient;


import org.json.JSONException;
import org.json.JSONObject;

public class PatientWarfarinMedicalData implements PatientMedicalData {

        private String gender,procedure;
        private int age;
        private long oldINRValue, newINRValue,oldDose;

        public PatientWarfarinMedicalData(Builder builder){
            this.gender = builder.gender;
            this.procedure = builder.procedure;
            this.age = builder.age;
            this.oldINRValue = builder.oldINRValue;
            this.newINRValue = builder.newINRValue;
            this.oldDose = builder.oldDose;
        }

    public String getGender() {
        return gender;
    }

    public String getProcedure() {
        return procedure;
    }

    public int getAge() {
        return age;
    }

    public long getOldINRValue() {
        return oldINRValue;
    }

    public long getNewINRValue() {
        return newINRValue;
    }

    public long getOldDose() {
        return oldDose;
    }

    public static class Builder{
            private String gender,procedure;
            private int age;
            private long oldINRValue, newINRValue,oldDose;

            private Builder() {}

            public static Builder newInstance(){
                return new Builder();
            }

            public Builder setGender(String gender){
                this.gender = gender;
                return this;
            }

            public Builder setAge(int age){
                this.age = age;
                return this;
            }

            public Builder setProcedure(String procedure){
                this.procedure = procedure;
                return this;
            }

            public Builder setOldINRValue(long oldINRValue){
                this.oldINRValue = oldINRValue;
                return this;
            }

            public Builder setNewINRValue(long newINRValue){
                this.newINRValue = newINRValue;
                return this;
            }

            public Builder setOldDose(long oldDose){
                this.oldDose = oldDose;
                return this;
            }

            public PatientWarfarinMedicalData build(){
                return new PatientWarfarinMedicalData(this);
            }
        }
}
