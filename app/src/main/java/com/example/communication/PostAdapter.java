package com.example.communication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<Post> posts;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private onProfileClick listener;

    public PostAdapter(List<Post> posts, onProfileClick listener) {
        this.posts = posts;
        this.listener = listener;
    }

    public interface onProfileClick {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {
        final Post post = posts.get(position);

        holder.username.setText(post.getUsername());
        holder.postContent.setText(post.getPostcontent());

        Glide.with(holder.itemView.getContext())
                .load(post.getUserprofile())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .centerCrop()
                .into(holder.userImage);

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("posts")
                        .document(post.getPostid())
                        .collection("likes")
                        .document(mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Likes likes = task.getResult().toObject(Likes.class);

                                if (likes == null) {
                                    firestore.collection("posts")
                                            .document(post.getPostid())
                                            .collection("likes")
                                            .document(mAuth.getUid())
                                            .set(new Likes(mAuth.getUid()));
                                    holder.likeIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                                    notifyDataSetChanged();
                                } else {
                                    firestore.collection("posts")
                                            .document(post.getPostid())
                                            .collection("likes")
                                            .document(mAuth.getUid())
                                            .delete();
                                    holder.likeIcon.setImageResource(R.drawable.like);
                                    notifyDataSetChanged();
                                }
                            }
                        });


            }
        });
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
                Intent i = new Intent(holder.userImage.getContext(), ProfileActivity.class);
                holder.userImage.getContext().startActivity(i);
            }
        });
        try {
            firestore.collection("posts")
                    .document(post.getPostid())
                    .collection("likes")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int likes = Objects.requireNonNull(task.getResult()).size();
                                String message = "Like (" + likes + ")";
                                holder.count.setText(message);
                            }

                        }
                    });
        } catch (Exception e) {
            Toast.makeText(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView userImage, likeIcon;
        TextView postContent, username, count;
        LinearLayout likeLayout, commentLayout;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_icon);
            likeIcon = itemView.findViewById(R.id.like_icon);
            postContent = itemView.findViewById(R.id.content);
            username = itemView.findViewById(R.id.tv_user);
            count = itemView.findViewById(R.id.count);
            likeLayout = itemView.findViewById(R.id.like_linear);
            commentLayout = itemView.findViewById(R.id.comment_linear);

        }
    }
}
