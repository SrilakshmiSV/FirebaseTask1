package com.srilakshmi.firebasetask1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<User> users;

    public RecyclerAdapter(ArrayList<User> users)
    {
        this.users=users;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user=users.get(position);

        holder.userName.setText(user.getUserName());
        holder.age.setText(String.valueOf(user.getAge()));
    }

    @Override
    public int getItemCount() {
        if (users !=null)
        {
            return users.size();
        }
        else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView userName;
        public TextView age;

        public ViewHolder(View view)
        {
            super(view);
            this.view=view;
            this.userName=view.findViewById(R.id.username);
            this.age=view.findViewById(R.id.age);
        }
    }
}