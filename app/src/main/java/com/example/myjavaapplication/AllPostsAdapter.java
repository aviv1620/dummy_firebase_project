package com.example.myjavaapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class AllPostsAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> posts;

    public AllPostsAdapter(Context context, int textViewResourceId, List<Post> posts) {
        super(context, textViewResourceId,posts);
        this.context=context;
        this.posts=posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.post, parent, false);
        TextView tvTitle = (TextView)view.findViewById(R.id.title1);
        TextView tvAuthor = (TextView)view.findViewById(R.id.author);
        TextView tvBody = (TextView)view.findViewById(R.id.body);
        Post post = posts.get(position);
        tvTitle.setText(post.title);
        tvAuthor.setText(post.author);
        tvBody.setText(post.body);
        return view;

    }

    @Override
    public String toString() {
        return "AllPostsAdapter{" +
                "posts=" + posts +
                '}';
    }
}
