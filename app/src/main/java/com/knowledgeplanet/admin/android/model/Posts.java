
package com.knowledgeplanet.admin.android.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Posts implements Parcelable
{

    @SerializedName("Computer")
    @Expose
    private List<Computer> computer = null;
    @SerializedName("Education")
    @Expose
    private List<Education> education = null;
    public final static Creator<Posts> CREATOR = new Creator<Posts>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Posts createFromParcel(Parcel in) {
            Posts instance = new Posts();
            in.readList(instance.computer, (Computer.class.getClassLoader()));
            in.readList(instance.education, (Education.class.getClassLoader()));
            return instance;
        }

        public Posts[] newArray(int size) {
            return (new Posts[size]);
        }

    }
    ;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Posts() {
    }

    /**
     * 
     * @param computer
     * @param education
     */
    public Posts(List<Computer> computer, List<Education> education) {
        super();
        this.computer = computer;
        this.education = education;
    }

    public List<Computer> getComputer() {
        return computer;
    }

    public void setComputer(List<Computer> computer) {
        this.computer = computer;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(computer);
        dest.writeList(education);
    }

    public int describeContents() {
        return  0;
    }

}
