package com.example.myjavaapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class ActivityPosts extends AppCompatActivity implements ChildEventListener {
    private static final String TAG = "my_app";
    HashMap<String,Post> posts = new HashMap<>();
    AllPostsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        itemsAdapter = new AllPostsAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Post>());
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(itemsAdapter);

        /*AllPostsAdapter itemsAdapter = new AllPostsAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Post>());
        ListView listView = (ListView) findViewById(R.id.listview);
        Post p = new Post("a","b","c","d");
        itemsAdapter.add(p);
        itemsAdapter.add(new Post("aa","bb","cc","dd"));
        itemsAdapter.add(new Post("aaa","bbb","ccc","ddd"));
        itemsAdapter.remove(p);
        listView.setAdapter(itemsAdapter);*/

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }else{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message/posts");
            myRef.addChildEventListener(this);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        String key = dataSnapshot.getKey();
        Post post = dataSnapshot.getValue(Post.class);
        Log.d(TAG, "onChildAdded:" + key);

        // A new post has been added, add it to the displayed list
        itemsAdapter.add(post);
        posts.put(key,post);

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        String key = dataSnapshot.getKey();
        Post post = dataSnapshot.getValue(Post.class);
        Log.d(TAG, "onChildChanged:" + key);

        // A comment has changed, use the key to determine if we are displaying this
        // comment and if so displayed the changed comment.
        Post oldPost = posts.get(key);
        posts.put(key,post);
        itemsAdapter.remove(oldPost);
        itemsAdapter.add(post);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        Log.d(TAG, "onChildRemoved:" + key);

        // A comment has changed, use the key to determine if we are displaying this
        // comment and if so remove it.
        Post post = posts.get(key);
        itemsAdapter.remove(post);
        posts.remove(key);

        // ...
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

        // A comment has changed position, use the key to determine if we are
        // displaying this comment and if so move it.

        // ...
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
        Toast.makeText(this, "Failed to load comments.",
                Toast.LENGTH_SHORT).show();
    }
}