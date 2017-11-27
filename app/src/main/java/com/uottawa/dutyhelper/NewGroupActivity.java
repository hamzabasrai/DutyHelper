package com.uottawa.dutyhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends AppCompatActivity {

    private EditText mGroupName;
    private ListView mUserList;
    private ArrayAdapter<String> mAdapter;
    private DatabaseReference mDatabaseUsers;
    private List<String> mUsers;
    private Button mCreateGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        mGroupName = (EditText) findViewById(R.id.group_name);
        mUserList = (ListView) findViewById(R.id.user_list);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        mUsers = new ArrayList<>();
        mCreateGroupBtn = (Button) findViewById(R.id.btn_create_group);
        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mUserList.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++) {
                    Log.d("NewGroupAct", String.valueOf(checked.get(i)));
                }
                ArrayList<String> selectedItems = new ArrayList<>();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    String name = user.getFirstName() + " " + user.getLastName();
                    mUsers.add(name);
                }
                mAdapter = new ArrayAdapter<String>(NewGroupActivity.this, android.R.layout.simple_list_item_multiple_choice, mUsers);
                mUserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mUserList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
