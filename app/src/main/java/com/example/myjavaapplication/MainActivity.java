package com.example.myjavaapplication;
/* i not Configure ProGuard. simple not use it.
2)upload to git witout google-services.json file and this
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "my_app";

    private TextView textView;
    private Button button_send;
    private Button buttonSeePosts;
    private EditText editTextTitle;
    private EditText editTextBody;

    private String userId;
    private String username;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_send = (Button)findViewById(R.id.send);
        buttonSeePosts = (Button)findViewById(R.id.see_posts);
        editTextTitle = (EditText)findViewById(R.id.post_title);
        editTextBody = (EditText)findViewById(R.id.post_body);
        textView = (TextView)findViewById(R.id.textView2);
        buttonSeePosts.setOnClickListener(this);
        button_send.setOnClickListener(this);

        // Choose authentication providers
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {//user not sign-in

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build() );

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else{
            userSignIn();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                userSignIn();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this,"Sign in failed",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /** user sign-in immediately when activity start.
     * if is fail to sign-in activity is finish
     *
     */
    private void userSignIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.username = user.getDisplayName();
        this.userId = user.getUid();

        Toast.makeText(this,username+" Sign in success",Toast.LENGTH_LONG).show();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        myRef.child("posts").addValueEventListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSeePosts){
            Intent intent = new Intent(this,ActivityPosts.class);
            startActivity(intent);
        }else if(v == button_send) {
            String title = editTextTitle.getText().toString();
            String body = editTextBody.getText().toString();
            writeNewPost(title, body);
        }
    }

    // This method is called once with the initial value and again
    // whenever data at this location is updated.
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot:  dataSnapshot.getChildren()) {

            Post post = postSnapshot.getValue(Post.class);
            if(post != null) {
                textView.setText( textView.getText().toString() + post.toString() + "\n");
            }
        }

    }

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", error.toException());
    }

    private void writeNewPost(String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = myRef.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        myRef.updateChildren(childUpdates);
    }

}