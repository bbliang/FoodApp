package com.hackgt17.foodapp.models;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data class to represent the JSON file passed in from MS Custom Vision Service
 */
public class CustomData implements Parcelable {

    //GSON deserialization annotations
    //Required for extracting information from JSON file and initialize Prediction object
    @SerializedName("Project")
    @Expose
    private String project;
    @SerializedName("Iteration")
    @Expose
    private String iteration;
    @SerializedName("Created")
    @Expose
    private String timestamp;
    @SerializedName("Predictions")
    @Expose
    private List<Prediction> predictions = null;

    //Accessor/mutator methods
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method to flatten the object into a parcel to pass to the Main Activity
     * Convert from CustomData object to parcel
     * @param out the parcel to create
     * @param flags additional flags on how the parcel should be created
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.project);
        out.writeString(this.iteration);
        out.writeString(this.timestamp);
        out.writeTypedList(this.predictions);
    }

    public CustomData() {
    }

    /**
     * Convert from parcel to CustomData object
     * @param in parcel as input
     */
    protected CustomData(Parcel in) {
        this.project = in.readString();
        this.iteration = in.readString();
        this.timestamp = in.readString();
        this.predictions = in.createTypedArrayList(Prediction.CREATOR);
    }

    /**
     * public CREATOR field that generates instances of your Parcelable class from a Parcel
     *
     * Required for converting a parcel into a CustomData object
     */
    public static final Parcelable.Creator<CustomData> CREATOR = new Parcelable.Creator<CustomData>() {
        @Override
        public CustomData createFromParcel(Parcel source) {
            return new CustomData(source);
        }

        @Override
        public CustomData[] newArray(int size) {
            return new CustomData[size];
        }
    };
}