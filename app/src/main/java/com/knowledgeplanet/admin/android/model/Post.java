package com.knowledgeplanet.admin.android.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 05-09-2017.
 */

public class Post {

    public String course;
    public String subject;
    public String subsubject;
    public String imagename;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String course, String subject, String subsubject, String imagename) {
        this.course = course;
        this.subject = subject;
        this.subsubject = subsubject;
        this.imagename = imagename;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("course", course);
        result.put("subject", subject);
        result.put("subsubject", subsubject);
        result.put("imagename", imagename);
        result.put("stars", stars);

        return result;
    }
}
