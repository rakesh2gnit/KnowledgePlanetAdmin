package com.knowledgeplanet.admin.android;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.knowledgeplanet.admin.android.adapter.CourseAdapter;
import com.knowledgeplanet.admin.android.model.Course;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Admin on 10-09-2017.
 */

public class AddSubjectDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddSubjectDetail";
    private static final String REQUIRED = "Required";
    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;
    EditText et_subjectName, et_subSubjectName, et_imageName;
    Spinner spin_courseName;
    private List<Course> mComments = new ArrayList<>();
    //private DatabaseReference mFirDb;
    private DatabaseReference mCourseDb;
    Button btn_submit, btn_imgupload;
    int PICK_IMAGE_REQUEST = 111;
    private ImageView iv_docs;
    private TextView tv_imgname;
    Uri filePath;
    ProgressDialog pd;
    StorageReference storageRef;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.e(TAG, "filePath:" + filePath);
            uploadImage(filePath, true);
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                iv_docs.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                //if a file is selected
                if (data.getData() != null) {
                    //uploading the file
                    uploadImage(data.getData(),false);
                } else {
                    Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImage(Uri filePath, boolean isImage) {
        Log.e(TAG, "uploadImage:" + filePath);
        if (filePath != null) {

            pd.show();
            final String imageName;
            if(isImage) {
                imageName = "kp_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            }else {
                iv_docs.setImageDrawable(getResources().getDrawable(R.drawable.pdf));
                imageName = "kp_" + UUID.randomUUID().toString().replaceAll("-", "") + ".pdf";
            }


            StorageReference childRef = storageRef.child(imageName);

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    tv_imgname.setText(imageName);
                    Toast.makeText(AddSubjectDetailActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    tv_imgname.setText("");
                    Toast.makeText(AddSubjectDetailActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddSubjectDetailActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        et_subjectName = (EditText) findViewById(R.id.et_subjectName);
        btn_imgupload = (Button) findViewById(R.id.btn_imgupload);
        spin_courseName = (Spinner) findViewById(R.id.spin_courseName);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        iv_docs = (ImageView) findViewById(R.id.iv_docs);
        tv_imgname = (TextView) findViewById(R.id.tv_imgname);

        //mFirDb = FirebaseDatabase.getInstance().getReference();
        mCourseDb = FirebaseDatabase.getInstance().getReference()
                .child("course");

        //creating reference to firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://knowledgeplanetadmin-178916.appspot.com");    //change the url according to your firebase app


        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        btn_submit.setOnClickListener(this);
        btn_imgupload.setOnClickListener(this);
        spin_courseName.setOnItemSelectedListener(this);

        getCourse();
    }

    private void getCourse() {
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

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setAdapter(List<Course> mComments) {
        Log.e(TAG, "courseList1:" + mComments.toString());

        ArrayList<String> mCourseList = new ArrayList<>();
        mCourseList.add(0, "Select Course");
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
                showDialog();
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void addSubject() {

        final String courseName = spin_courseName.getSelectedItem().toString().trim();
        final String subjectName = et_subjectName.getText().toString().trim();
        final String imageName = tv_imgname.getText().toString().trim();

        Log.e("insideaddsub", "courseName:" + courseName);
        // courseName is required
        if (TextUtils.isEmpty(subjectName)) {
            et_subjectName.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(courseName) || courseName.equalsIgnoreCase("Select Course")) {
            Toast.makeText(AddSubjectDetailActivity.this, "Please Select Course", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(imageName)) {
            Toast.makeText(AddSubjectDetailActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
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
                postSubject(courseName, subjectName, imageName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddSubjectDetailActivity.this, "databaseError", Toast.LENGTH_SHORT).show();
            }
        });

        setEditingEnabled(true);
    }

    private void postSubject(String courseName, String subjectName, String imageName) {
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

        if (position != 0) {
            // Showing selected spinner item
            Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddSubjectDetailActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("What do you want to do?")
                .setTitle("Upload");

        // Add the buttons
        builder.setPositiveButton("PDF", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                getPDF();
            }
        });
        builder.setNegativeButton("IMAGE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                openGallery();
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }
}
