package com.knowledgeplanet.admin.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.knowledgeplanet.admin.android.adapter.CourseAdapter;
import com.knowledgeplanet.admin.android.model.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10-09-2017.
 */

public class AddSubjectDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddSubjectDetail";
    private static final String REQUIRED = "Required";
    EditText et_subjectName, et_subSubjectName, et_imageName;
    Spinner spin_courseName;
    private List<Course> mComments = new ArrayList<>();
    //private DatabaseReference mFirDb;
    private DatabaseReference mCourseDb;
    Button btn_submit, btn_imgupload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        et_subjectName = (EditText) findViewById(R.id.et_subjectName);
        btn_imgupload = (Button) findViewById(R.id.btn_imgupload);
        spin_courseName = (Spinner) findViewById(R.id.spin_courseName);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        //mFirDb = FirebaseDatabase.getInstance().getReference();
        mCourseDb = FirebaseDatabase.getInstance().getReference()
                .child("course");

        btn_submit.setOnClickListener(this);
        btn_imgupload.setOnClickListener(this);
        spin_courseName.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCourseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
          /* This method is called once with the initial value and again whenever data at this location is updated.*/
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Course course = dataSnapshot1.getValue(Course.class);
                    mComments.add(course);
                }
                setAdapter(mComments);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setAdapter(List<Course> mComments) {
        Log.e(TAG, "courseList1:" + mComments.toString());

        ArrayList<String> mCourseList = new ArrayList<>();
        mCourseList.add(0,"Select Course");
        for (Course course : mComments) {
            String courseName = course.course;
            mCourseList.add(courseName);
        }

        Log.e(TAG, "courseList:" + mCourseList.toString());
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mCourseList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_courseName.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                addSubject();
                break;

            case R.id.btn_imgupload:
                //submitPost();
                break;
        }
    }

    private void addSubject() {

        final String courseName = spin_courseName.getSelectedItem().toString().trim();
        final String subjectName = et_subjectName.getText().toString().trim();
        Log.e("insideaddsub","courseName:"+courseName);
        // courseName is required
        if (TextUtils.isEmpty(subjectName)) {
            et_subjectName.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);


        FirebaseDatabase.getInstance().getReference().child(courseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExist = false;
                /*if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String existingUsername = (String) userSnapshot.getValue();
                        Log.e("existingUsername", existingUsername + "," + courseName);
                        if (existingUsername.equalsIgnoreCase(courseName)) {
                            isExist = true;
                        }
                    }

                    if (!isExist) {
                        postSubject(courseName, subjectName,"abc.png");
                    } else {
                        Toast.makeText(AddSubjectDetailActivity.this, "Already exist!!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    postSubject(courseName, subjectName,"abc.png");
                }*/
                postSubject(courseName, subjectName,"abc.png");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddSubjectDetailActivity.this, "databaseError", Toast.LENGTH_SHORT).show();
            }
        });

        setEditingEnabled(true);
    }

    private void postSubject(String courseName,String subjectName, String imageName) {
        Toast.makeText(AddSubjectDetailActivity.this, "Posting....", Toast.LENGTH_SHORT).show();

        Course post = new Course(null, subjectName, null, imageName);

        // Push the course, it will appear in the list
        FirebaseDatabase.getInstance().getReference().child(courseName).push().setValue(post);

        // Clear the field
        et_subjectName.setText(null);
    }

    private void setEditingEnabled(boolean enabled) {
        et_subjectName.setEnabled(enabled);
        if (enabled) {
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            btn_submit.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(position).toString();

        if(position!=0) {
            // Showing selected spinner item
            Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
