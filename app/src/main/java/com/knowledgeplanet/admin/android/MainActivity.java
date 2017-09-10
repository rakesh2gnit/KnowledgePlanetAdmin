package com.knowledgeplanet.admin.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgeplanet.admin.android.model.Course;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 04-09-2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final String REQUIRED = "Required";
    private FloatingActionButton fabLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private GoogleApiClient mGoogleApiClient;
    Button btn_add_course, btn_add_subject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fabLogout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fabLogout.setOnClickListener(this);

        btn_add_course = (Button) findViewById(R.id.btn_add_course);
        btn_add_subject = (Button) findViewById(R.id.btn_add_subject);

        btn_add_course.setOnClickListener(this);
        btn_add_subject.setOnClickListener(this);

        //getCategories();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_subject:
                goToAddSubjectDetailAct();
                //submitPost();
                break;

            case R.id.fab_logout:
                signOut();
                break;

            case R.id.btn_add_course:
                goToMainPageActivity();
                break;
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        goToLoginActivity(null);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void goToLoginActivity(FirebaseUser user) {
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void goToMainPageActivity() {
        Intent mainIntent = new Intent(MainActivity.this, AddCourseActivity.class);
        startActivity(mainIntent);
        //finish();
    }

    private void goToAddSubjectDetailAct() {
        Intent mainIntent = new Intent(MainActivity.this, AddSubjectDetailActivity.class);
        startActivity(mainIntent);
        //finish();
    }

    /*private void submitPost() {
        final String courseName = et_courseName.getText().toString().trim();
        final String subjectName = et_subjectName.getText().toString().trim();
        final String subSubjectName = et_subSubjectName.getText().toString().trim();
        final String imageName = et_imageName.getText().toString().trim();

        // courseName is required
        if (TextUtils.isEmpty(courseName)) {
            et_courseName.setError(REQUIRED);
            return;
        }

        // subjectName is required
        if (TextUtils.isEmpty(subjectName)) {
            et_subjectName.setError(REQUIRED);
            return;
        }

        // subSubjectName is required
        if (TextUtils.isEmpty(subSubjectName)) {
            et_subSubjectName.setError(REQUIRED);
            return;
        }

        // imageName is required
        if (TextUtils.isEmpty(imageName)) {
            et_imageName.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        Course item = new Course(courseName,subjectName,subSubjectName,imageName);

        DatabaseReference newRef = mDatabase.child("posts").push();
        newRef.setValue(item);

        setEditingEnabled(true);
    }*/

    /*private void setEditingEnabled(boolean enabled) {
        et_courseName.setEnabled(enabled);
        et_subjectName.setEnabled(enabled);
        et_subSubjectName.setEnabled(enabled);
        et_imageName.setEnabled(enabled);
        if (enabled) {
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            btn_submit.setVisibility(View.GONE);
        }
    }*/

    private void writeNewPost(String course, String subject, String subsubject, String image) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        //Course post = new Course(course, subject, subsubject, image);
        //Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + key, postValues);
        //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void getCategories(){
        mDatabase.child("posts").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get value
                        Course post = dataSnapshot.getValue(Course.class);
                       // Log.e(TAG,post.getPosts().getComputer().toString());
                        //String[] value = (String[]) dataSnapshot.getValue();
                        if (post == null) {
                            // post is null, error out
                            Log.e(TAG, "User " + post + " is unexpectedly null");
                            Toast.makeText(MainActivity.this,
                                    "Error: could not fetch data.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                           //Log.e(TAG,post.course);

                            /*if(post.course.equalsIgnoreCase(courseName)) {
                                Toast.makeText(MainActivity.this,"Already Exist",Toast.LENGTH_SHORT).show();
                                //writeNewPost(courseName, subjectName, subSubjectName, imageName);
                            }else{
                                // Write new post
                                // writeNewPost(courseName, subjectName, subSubjectName, imageName);
                            }*/
                        }

                        // Finish this Activity, back to the stream
                       // setEditingEnabled(true);
                        //finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                       // setEditingEnabled(true);
                    }
                });
    }
}
