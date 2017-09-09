
package com.knowledgeplanet.admin.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Education implements Parcelable
{

    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("imagename")
    @Expose
    private String imagename;
    public final static Creator<Education> CREATOR = new Creator<Education>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Education createFromParcel(Parcel in) {
            Education instance = new Education();
            instance.subject = ((String) in.readValue((String.class.getClassLoader())));
            instance.imagename = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Education[] newArray(int size) {
            return (new Education[size]);
        }

    }
    ;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Education() {
    }

    /**
     * 
     * @param subject
     * @param imagename
     */
    public Education(String subject, String imagename) {
        super();
        this.subject = subject;
        this.imagename = imagename;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(subject);
        dest.writeValue(imagename);
    }

    public int describeContents() {
        return  0;
    }

}
