package com.uottawa.dutyhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ProfileSettingActivity extends AppCompatActivity {

    private Button mSelectImage;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private static final int GALLERY_INTENT = 2;
    private ImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        mProfileImage = (ImageView) findViewById(R.id.profilePic);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        mStorage = FirebaseStorage.getInstance().getReference();

        mSelectImage = (Button) findViewById(R.id.changePicture);
        loadPicture();
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPicture = new Intent(Intent.ACTION_PICK);
                selectPicture.setType("image/*");
                startActivityForResult(selectPicture, GALLERY_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String id = mAuth.getCurrentUser().getEmail();
            StorageReference filepath = mStorage.child("ProfilePicture").child(id);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ProfileSettingActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    loadPicture();
                }
            });
        }
    }

    public void loadPicture() {

        String id = mAuth.getCurrentUser().getEmail();
        StorageReference filepath = mStorage.child("ProfilePicture").child(id);
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(filepath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(mProfileImage);
    }

}
