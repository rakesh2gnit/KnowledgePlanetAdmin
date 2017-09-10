package com.knowledgeplanet.admin.android;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgeplanet.admin.android.adapter.CourseAdapter;
import com.knowledgeplanet.admin.android.model.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 09-09-2017.
 */

public class AddCourseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddCourseActivity";
    private static final String REQUIRED = "Required";
    EditText et_courseName, et_subjectName, et_subSubjectName, et_imageName;
    Button btn_submit, btn_add_course;
    private DatabaseReference mDatabase;
    private DatabaseReference mCourseDb;
    private List<Course> mCourseList = new ArrayList<>();
    private RecyclerView mCourseRecycler;
    private ValueEventListener mCourseListener;
    private CourseAdapter mAdapter;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_submit:
                //addCourse();
                postData();
                break;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        et_courseName = (EditText) findViewById(R.id.et_courseName);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        mCourseRecycler = (RecyclerView) findViewById(R.id.recycler_courses);

        btn_submit.setOnClickListener(this);

        mCourseRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCourseRecycler.setLayoutManager(mLayoutManager);
        mCourseRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mCourseRecycler.setItemAnimator(new DefaultItemAnimator());

        //initialise database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCourseDb = FirebaseDatabase.getInstance().getReference()
                .child("course");

        mCourseRecycler.addOnItemTouchListener(new CourseAdapter.RecyclerTouchListener(this,
                mCourseRecycler, new CourseAdapter.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                final TextView txtCourse = (TextView)view.findViewById(R.id.course);
                Toast.makeText(AddCourseActivity.this, "Single Click on position        :" + position+" , "+ txtCourse.getText().toString().trim(),
                        Toast.LENGTH_SHORT).show();
                showDialog(txtCourse.getText().toString().trim());
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(AddCourseActivity.this, "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
                //showDialog();
            }
        }));

        //getValue();
        //getSingleValue();
        //getChildEvent();
    }

    private void showDialog(final String course) {
        et_courseName.clearFocus();
        mCourseDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("dataSnapshot", dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("What do you want to do?")
                .setTitle("Alert");

        // Add the buttons
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mCourseDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Course deleteSD = snapshot.getValue(Course.class);
                            if (course.equalsIgnoreCase(deleteSD.course)) {
                                mCourseDb.child(snapshot.getKey().toString()).removeValue();
                                break;
                            }
                        }
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //mCourseDb.child("-KteiDr2Isb9ftP1iYPT").removeValue();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener courseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Course object and use the values to update the UI
                Log.e("count", "" + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() == 0) {
                    ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.GONE);
                    // Listen for comments
                    mAdapter = new CourseAdapter(AddCourseActivity.this, mCourseDb);
                    mCourseRecycler.setAdapter(mAdapter);
                }
                //Course post = dataSnapshot.getValue(Course.class);
                //Log.e("Inside Start","onDataChange:"+post.Course);
                // [START_EXCLUDE]
                //mAuthorView.setText(post.author);
                //mTitleView.setText(post.title);
                //mBodyView.setText(post.body);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Course failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(AddCourseActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mCourseDb.addValueEventListener(courseListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mCourseListener = courseListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    private void setEditingEnabled(boolean enabled) {
        et_courseName.setEnabled(enabled);
        if (enabled) {
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            btn_submit.setVisibility(View.GONE);
        }
    }

    private void postData() {

        final String courseName = et_courseName.getText().toString().trim();

        // courseName is required
        if (TextUtils.isEmpty(courseName)) {
            et_courseName.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        mCourseDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExist = false;
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String existingUsername = (String) userSnapshot.child("course").getValue();
                        Log.e("existingUsername", existingUsername + "," + courseName);
                        if (existingUsername.equalsIgnoreCase(courseName)) {
                            isExist = true;
                        }
                    }

                    if (!isExist) {
                        postCourse(courseName);
                    } else {
                        Toast.makeText(AddCourseActivity.this, "Already exist!!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    postCourse(courseName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddCourseActivity.this, "databaseError", Toast.LENGTH_SHORT).show();
            }
        });

        setEditingEnabled(true);
    }

    private void postCourse(String courseName) {
        Toast.makeText(AddCourseActivity.this, "Posting....", Toast.LENGTH_SHORT).show();

        Course post = new Course(courseName, null, null, null);

        // Push the course, it will appear in the list
        mCourseDb.push().setValue(post);

        // Clear the field
        et_courseName.setText(null);
    }

    /*private void getChildEvent() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.e(TAG, "onChildAdded:"+dataSnapshot.getValue()+","+dataSnapshot.getChildrenCount()+","+dataSnapshot.getChildren()+","+dataSnapshot.getKey()+","+dataSnapshot.getRef()+s);
                Course post = dataSnapshot.getValue(Course.class);

                // Update RecyclerView
                //mCommentIds.add(dataSnapshot.getKey());
                mCourseList.add(post);
                Log.e("onChildAdded", "mCourseList:" + mCourseList.toString() + "" + mCourseList.size());
                //notifyItemInserted(mComments.size() - 1);
                //onChildAdded:{-Kt_jFnk3OjYqLQO-C6Y={course=tesr}, -Kt_jEjjwIiUOVX7DkmV={course=tesr}, -Kt_jFMfY8WTSHhMz31n={course=tesr}},
                // 3,
                // com.google.firebase.database.zza@d2c4e08,
                // course,
                // https://knowledgeplanetadmin-178916.firebaseio.com/course
                // null
                //   onChildAdded:{-Kt_jHzW1C_mTp38ft_0={subSubject=4yii, imageName=ryik, subject=tui, course=fuhg}},
                // 1,
                // com.google.firebase.database.zza@dd3d9a1,
                // posts,
                // https://knowledgeplanetadmin-178916.firebaseio.com/posts
                // course

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot.getValue() + "," + dataSnapshot.getChildrenCount() + "," + dataSnapshot.getChildren() + "," + dataSnapshot.getKey() + "," + dataSnapshot.getRef() + s);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot.getValue() + "," + dataSnapshot.getChildrenCount() + "," + dataSnapshot.getChildren() + "," + dataSnapshot.getKey() + "," + dataSnapshot.getRef());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot.getValue() + "," + dataSnapshot.getChildrenCount() + "," + dataSnapshot.getChildren() + "," + dataSnapshot.getKey() + "," + dataSnapshot.getRef() + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:" + databaseError.getMessage() + "," + databaseError.getDetails() + "," + databaseError.getCode());
            }
        };

        mDatabase.child("course").addChildEventListener(childEventListener);
    }*/

    /*private void getValue() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange:" + dataSnapshot.getValue() + "," + dataSnapshot.getChildrenCount() + "," + dataSnapshot.getChildren() + "," + dataSnapshot.getKey() + "," + dataSnapshot.getRef());
                //onDataChange:{posts={-Kt_jHzW1C_mTp38ft_0={subSubject=4yii, imageName=ryik, subject=tui, course=fuhg}}, course={-Kt_jFnk3OjYqLQO-C6Y={course=tesr}, -Kt_jEjjwIiUOVX7DkmV={course=tesr}, -Kt_jFMfY8WTSHhMz31n={course=tesr}}},
                // 2,
                // com.google.firebase.database.zza@308e7c6,
                // null,
                // https://knowledgeplanetadmin-178916.firebaseio.com
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelledinValue:" + databaseError.getMessage() + "," + databaseError.getDetails() + "," + databaseError.getCode());
            }
        };
        mDatabase.child("course").addValueEventListener(valueEventListener);
    }*/

    /*private void getSingleValue() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChangeSingle:" + dataSnapshot.getValue() + "," + dataSnapshot.getChildrenCount() + "," + dataSnapshot.getChildren() + "," + dataSnapshot.getKey() + "," + dataSnapshot.getRef());
                //onDataChangeSingle:{posts={-Kt_jHzW1C_mTp38ft_0={subSubject=4yii, imageName=ryik, subject=tui, course=fuhg}}, course={-Kt_jFnk3OjYqLQO-C6Y={course=tesr}, -Kt_jEjjwIiUOVX7DkmV={course=tesr}, -Kt_jFMfY8WTSHhMz31n={course=tesr}}},
                // 2,
                // com.google.firebase.database.zza@a8d3387,
                // null,
                // https://knowledgeplanetadmin-178916.firebaseio.com
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelledinValueSingle:" + databaseError.getMessage() + "," + databaseError.getDetails() + "," + databaseError.getCode());
            }
        };
        mDatabase.child("course").addListenerForSingleValueEvent(valueEventListener);
    }*/

    /*private void addCourse() {
        final String courseName = et_courseName.getText().toString().trim();

        // courseName is required
        if (TextUtils.isEmpty(courseName)) {
            et_courseName.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        Course item = new Course(courseName, null, null, null);

        DatabaseReference newRef = mDatabase.child("course").push();
        newRef.setValue(item);

        setEditingEnabled(true);
    }*/
}
