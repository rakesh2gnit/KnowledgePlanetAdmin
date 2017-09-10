
package com.knowledgeplanet.admin.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@IgnoreExtraProperties
public class Course {

    public String course;
    public String subject;
    public String SubSubject;
    public String imageName;

    public Course(){

    }

    public Course(String course, String subject, String SubSubject, String imageName) {
        this.course = course;
        this.subject = subject;
        this.SubSubject = SubSubject;
        this.imageName = imageName;

    }

    /*public String getCourse() {
        return Course;
    }

    public void setCourse(String Course) {
        this.Course = Course;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String Subject) {
        this.Subject = Subject;
    }

    public String getSubSubject() {
        return SubSubject;
    }

    public void setSubSubject(String SubSubject) {
        this.SubSubject = SubSubject;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String ImageName) {
        this.ImageName = ImageName;
    }*/
}
