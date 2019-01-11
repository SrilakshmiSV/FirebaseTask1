package com.srilakshmi.firebasetask1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    Query query;

    ListView listView;
    ArrayAdapter<User> userListAdapter;
    ArrayList<User> usersList=new ArrayList<>();
    ArrayList<String> userIdsList=new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mUsersDatabaseReference=mFirebaseDatabase.getReference().child("users");

        // Create RecyclerView List
        mRecyclerView=(RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Using a callback function to populate the Recycler view. This is because pulling data
        // from Firebase realtime database is an asynchronous operation.
        // This code will set the adapter only after data is available
        usersList=initUsers(new DataCallBack() {
            @Override
            public void onCallback(ArrayList<User> values) {
                if (usersList!=null) {
                    mAdapter=new RecyclerAdapter(usersList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });

    }

    /**
     * This method contains code to get data from Realtime database. Also contains listener methods
     * for changes in data to be reflected back in the UI (Recycler view)
     * @param dataCallBack
     * @return
     */
    private ArrayList<User> initUsers(final DataCallBack dataCallBack)
    {
        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("MainActivity", "onChildAdded:" + dataSnapshot.getKey());

                // A new user has been added, add it to the displayed list through callback method.
                User user = dataSnapshot.getValue(User.class);
                userIdsList.add(dataSnapshot.getKey());
                usersList.add(user);
                dataCallBack.onCallback(usersList);

                mAdapter.notifyItemInserted(usersList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("MainActivity", "onChildChanged:" + dataSnapshot.getKey());

                // A user data has changed, use the key to determine if this user is being displayed
                // if so display the changed user data.
                User newUser = dataSnapshot.getValue(User.class);
                String userKey=dataSnapshot.getKey();

                // Code to display updated user data
                int userIndex=userIdsList.indexOf(userKey);
                if (userIndex > -1)
                {
                    usersList.set(userIndex, newUser);
                    // Update the recycler view
                    mAdapter.notifyItemChanged(userIndex);
                }
                else
                {
                    Log.d("onChildChanged():", "Unknown user");
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d("MainActivity", "onChildRemoved:" + dataSnapshot.getKey());
                // A user data has been deleted, use the key to determine if we are displaying this
                // user and if so remove it.
                User user=dataSnapshot.getValue(User.class);

                String userKey=dataSnapshot.getKey();
                int userIndex=userIdsList.indexOf(userKey);

                if (userIndex > -1) {
                    userIdsList.remove(userIndex);
                    // Remove user from the list
                    usersList.remove(userIndex);
                    // Update the recycler view
                    mAdapter.notifyItemRemoved(userIndex);
                }
                else
                {
                    Log.d("onChildRemoved():", "Unknown user");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("MainActivity", "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("MainActivity", "UserData:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to load data.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mUsersDatabaseReference.addChildEventListener(childEventListener);
        return usersList;
    }
}