package com.example.quizza;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    Button addButton;
    EditText userInputClass;
    EditText userInputcourseID;
    EditText joinPageClassCode;
    User currentUser;
    FirebaseUser firebaseUser;
    TextView courseInfo;
    private FirebaseAuth fAuth;
    DatabaseReference currentDatabase;

    char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    TextView classJoinInfo;
    TextView classJoinCode;
    TextView joinSuccessView;
    Button addViewButton;
    Button joinViewButton;


    Button joinClassButton;

    ImageView backAddView;
    ImageView backJoinView;

    RelativeLayout initialAddView;
    RelativeLayout addCourseView;
    RelativeLayout joinCourseView;

    public AddFragment() {
        // Required empty public constructor
    }

    public String generateJoinCode(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add, container, false);
        addButton = (Button) view.findViewById(R.id.addCourse);
        userInputClass = (EditText) view.findViewById(R.id.classToAdd);
        fAuth = FirebaseAuth.getInstance();
        userInputcourseID = (EditText) view.findViewById(R.id.courseID);
        courseInfo = (TextView) view.findViewById(R.id.classInfo);
        initialAddView = (RelativeLayout) view.findViewById(R.id.addPage);
        addCourseView = (RelativeLayout) view.findViewById(R.id.courseAddView);
        joinCourseView = (RelativeLayout) view.findViewById(R.id.joinCourseView);

        joinClassButton = (Button) view.findViewById(R.id.joinClassButton);
        joinSuccessView = (TextView) view.findViewById(R.id.joinSuccessView);

        joinPageClassCode = (EditText) view.findViewById(R.id.joinEditView);

        addViewButton = (Button) view.findViewById(R.id.addViewButton);
        joinViewButton = (Button) view.findViewById(R.id.joinViewButton);
        classJoinInfo = (TextView) view.findViewById(R.id.classJoinCodeInfo);
        classJoinCode = (TextView) view.findViewById(R.id.classJoinCode);

        backAddView = (ImageView) view.findViewById(R.id.backAddView);
        backJoinView = (ImageView) view.findViewById(R.id.backJoinView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = firebaseUser.getUid();

        backAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialAddView.setVisibility(View.VISIBLE);
                addCourseView.setVisibility(View.INVISIBLE);
                joinCourseView.setVisibility(View.INVISIBLE);
            }
        });

        backJoinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialAddView.setVisibility(View.VISIBLE);
                addCourseView.setVisibility(View.INVISIBLE);
                joinCourseView.setVisibility(View.INVISIBLE);
            }
        });

        addViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialAddView.setVisibility(View.INVISIBLE);
                addCourseView.setVisibility(View.VISIBLE);
                joinCourseView.setVisibility(View.INVISIBLE);
            }
        });

        joinViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialAddView.setVisibility(View.INVISIBLE);
                addCourseView.setVisibility(View.INVISIBLE);
                joinCourseView.setVisibility(View.VISIBLE);
            }
        });

        final String addClassFail = "Error occurred in adding class";
        final String joinClassFail = "Error occurred in joining class";


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = userInputClass.getText().toString();
                Integer courseID = 0;
                String joinCode = generateJoinCode(CHARSET_AZ_09, 7);
                classJoinCode.setText(joinCode);
                Log.d("courseName", courseName);
                currentDatabase = FirebaseDatabase.getInstance().getReference("Users");
                currentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item_snapshot:snapshot.getChildren()){
                            if(item_snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                currentUser = item_snapshot.getValue(User.class);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //To implement5
                    }
                });

                if (TextUtils.isEmpty(courseName)) {
                    userInputClass.setError("Course Name is Required");
                    return;
                }


                if(!(currentUser == null)){
                    Course course1 = new Course(courseName, Uid, courseID, joinCode, Uid);
                    currentDatabase = FirebaseDatabase.getInstance().getReference();
                    currentDatabase.child("Courses").push().setValue(course1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    courseInfo.setText(course1.getCourseName());
                                    classJoinInfo.setVisibility(View.VISIBLE);
                                    classJoinCode.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getActivity(), addClassFail,
                                        Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }

            }
        });

        joinClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String classCode = joinPageClassCode.getText().toString();
                currentDatabase = FirebaseDatabase.getInstance().getReference("Courses");
                currentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item_snapshot : snapshot.getChildren()){
                            Course tempCourse = item_snapshot.getValue(Course.class);
                            if(classCode.equals(tempCourse.getJoinCode())) {

                                Set<String> myUsers = new HashSet<String>(tempCourse.getUserEnrolled());
                                myUsers.add(Uid);

                                tempCourse.setUserEnrolled(new ArrayList<String>(myUsers));
                                FirebaseDatabase.getInstance().getReference("Courses/" + item_snapshot.getKey()).setValue(tempCourse);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });





        return view;
    }
}