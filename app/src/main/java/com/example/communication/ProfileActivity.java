package com.example.communication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageView;
    EditText edt_email, edt_user, edt_phone;
    TextView change;
    Button save;
    Uri ProfilePath = null;
    String ProfileUrl = null;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = findViewById(R.id.profile_img);
        edt_email = findViewById(R.id.profile_email);
        edt_user = findViewById(R.id.profile_username);
        edt_phone = findViewById(R.id.profile_phone);
        save = findViewById(R.id.profile_btn_save);
        change = findViewById(R.id.change);
        // .....................................
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        getUserDataFromFirestore();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ProfileActivity.this,ViewProfile.class);
                startActivity(i);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUi();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ProfilePath = result.getUri();
                uploadProfileImage(ProfilePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadProfileImage(Uri profilePath) {
        storageReference.child("ProfileImage")
                .child(mAuth.getUid())
                .putFile(profilePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile image updata", Toast.LENGTH_LONG).show();
                            getProfileUrl();

                        } else {
                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void getProfileUrl() {
        storageReference.child("ProfileImage").child(mAuth.getUid()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            ProfileUrl = task.getResult().toString();
                            Glide.with(ProfileActivity.this).load(ProfileUrl)
                                    .centerCrop()
                                    .into(imageView);
                            Map<String, Object> map = new HashMap<>();
                            map.put("profileUrl", ProfileUrl);
                            firestore.collection("Users").document(mAuth.getUid()).update(map);
                            sharedPreferences.edit().putString("userProfileLink",ProfileUrl).apply();

                        } else {
                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getDataFromUi() {
        String username = edt_user.getText().toString();
        String phone = edt_phone.getText().toString();
        if (username.isEmpty() || phone.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "please fill all data", Toast.LENGTH_LONG).show();
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        if (ProfileUrl != null) {
            user.setProfileUrl(ProfileUrl);
        }
        uploadUserDataToFireStore(user);

    }

    private void uploadUserDataToFireStore(final User user) {
        firestore.collection("Users").document(mAuth.getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "User data updated",
                            Toast.LENGTH_SHORT).show();

                    sharedPreferences.edit().putString("username", user.getUsername()).apply();


                } else {
                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserDataFromFirestore() {
        firestore.collection("Users").document(mAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    edt_email.setText(mAuth.getCurrentUser().getEmail());

                    if (user == null) return;

                    edt_phone.setText(user.getPhone());
                    edt_user.setText(user.getUsername());

                    Glide.with(ProfileActivity.this).load(user.getProfileUrl())
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageView);



                }
            }
        });
    }

}