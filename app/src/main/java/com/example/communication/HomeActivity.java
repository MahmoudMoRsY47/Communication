package com.example.communication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    EditText post_edt;
    ImageView submit;
    RecyclerView rc;
    TextView username;
    SharedPreferences Myshared;
    List<Post> posts = new ArrayList<>();
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //.........................................
        username = findViewById(R.id.tv_user);
        post_edt = findViewById(R.id.home_add_post);
        submit = findViewById(R.id.post_submit);
        rc = findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(this));
        //......................................
        Myshared = getSharedPreferences("userData", MODE_PRIVATE);
        getUserData();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUi();
            }
        });
        postAdapter = new PostAdapter(posts, new PostAdapter.onProfileClick() {
            @Override
            public void onItemClick(int position) {
            }
        });
        rc.setAdapter(postAdapter);
        getPostsFromCloud();
    }

    private void getDataFromUi() {
        String content = post_edt.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "please write post", Toast.LENGTH_LONG).show();
            return;
        }
        Post post = new Post();
        post.setUsername(Myshared.getString("username", ""));
        post.setUserprofile(Myshared.getString("userProfileLink", ""));
        post.setPostcontent(content);
        post.setUserid(FirebaseAuth.getInstance().getUid());
        post.setPostid(String.valueOf(System.currentTimeMillis()));
        pushPostToFirebase(post);
    }

    private void pushPostToFirebase(Post post) {
        firestore.collection("posts")
                .document(post.getPostid())
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Posted", Toast.LENGTH_LONG).show();
                            post_edt.setText("");
                        } else {
                            Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, SignIn.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getPostsFromCloud() {

        firestore.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                posts.clear();
                for (DocumentSnapshot snapshot : value) {
                    Post post = snapshot.toObject(Post.class);
                    posts.add(post);
                }
                if (!posts.isEmpty()) {
                    postAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void getUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            return;
        }
        firestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            if (user == null) {
                                return;
                            }
                            Myshared.edit().putString("username", user.getUsername()).apply();
                            Myshared.edit().putString("userProfileLink", user.getProfileUrl()).apply();
                        }
                    }
                });
    }
}
