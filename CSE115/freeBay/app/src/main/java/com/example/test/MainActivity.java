package com.example.test;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter myAdapter;
    private SearchView searchView;
    private ImageButton sort;
    private BottomNavigationView bottomNavView;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://freebay-cbb54.appspot.com");
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference().child("Listings");

    private List<listingObjects> listings;

    private DataSnapshot mostRecent;

    private int filterPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search);
        sort = findViewById(R.id.sort);
        bottomNavView = findViewById(R.id.bottomBar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(searchView.getQuery().toString(),listings);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                {
                    myAdapter = new RecyclerViewAdapter(MainActivity.this, listings);
                    recyclerView.setAdapter(myAdapter);
                }
                return false;
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.sort:
                        sortDialog();
                        break;
                }
            }
        });

        bottomNavView.getMenu().getItem(0).setIcon(R.mipmap.home_filled);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myListings:
                        Intent myListingIntent = new Intent(MainActivity.this,MyListingsActivity.class);
                        startActivity(myListingIntent);
                        break;
                    case R.id.addListing:
                        Intent addListingIntent = new Intent(MainActivity.this,AddListingActivity.class);
                        startActivity(addListingIntent);
                        break;
                    case R.id.favorite:
                        Intent favoriteIntent = new Intent(MainActivity.this,FavoritesActivity.class);
                        startActivity(favoriteIntent);
                        break;
                    case R.id.accountSettings:
                        Intent accountSettingsIntent = new Intent(MainActivity.this,AccountSettingsActivity.class);
                        startActivity(accountSettingsIntent);
                        break;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listings = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mostRecent = dataSnapshot;
                listings.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listingObjects listing = postSnapshot.getValue(listingObjects.class);
                    listings.add(0, listing);
                }
                sort("" + filterPosition,listings);
                myAdapter = new RecyclerViewAdapter(MainActivity.this, listings);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /***
     * Takes in a list of listing objects and a string checks if equals sets recyclerview based
     * on temp list
     */

    public void search(String searcher, List<listingObjects> adlistings)
    {
        List<listingObjects> temp = new ArrayList<>();
        if(adlistings.size()>0) {
            int listingSize = adlistings.size();
            for (int count = 0; count < adlistings.size(); count++) {
                if(adlistings.get(count).getItemName().toLowerCase().contains(searcher.toLowerCase()))
                {
                    temp.add(adlistings.get(count));
                }
            }
        }
        myAdapter = new RecyclerViewAdapter(MainActivity.this, temp);
        recyclerView.setAdapter(myAdapter);
    }
    //Insertion sort
    public List<listingObjects> sort(String filter, List<listingObjects> adlistings)
    {
        if(adlistings.size()>0)
        {
            if(filter.equals("Date - Newest to Oldest") || filter.equals("0"))
            {
                adlistings.clear();
                for (DataSnapshot postSnapshot : mostRecent.getChildren()) {
                    listingObjects listing = postSnapshot.getValue(listingObjects.class);
                    adlistings.add(0, listing);
                }
            }
            else if(filter.equals("Date - Oldest to Newest") || filter.equals("1"))
            {
                adlistings.clear();
                for (DataSnapshot postSnapshot : mostRecent.getChildren()) {
                    listingObjects listing = postSnapshot.getValue(listingObjects.class);
                    adlistings.add(listing);
                }
            }
            else if(filter.equals("Name - Alphabetical") || filter.equals("2")) {
                int listingSize = adlistings.size();
                for(int count = 0; count<adlistings.size();count++)
                {
                    for(int counter = count + 1; counter<adlistings.size();counter++)
                    {
                        if(adlistings.get(count).getItemName().compareToIgnoreCase(adlistings.get(counter).getItemName())>0){
                            listingObjects temp = adlistings.get(count);
                            adlistings.set(count, adlistings.get(counter));
                            adlistings.set(counter, temp);
                        }
                    }
                }
            }
            else if(filter.equals("Most Likes") || filter.equals("3"))
            {
                int listingSize = adlistings.size();
                for(int count = 0; count<adlistings.size();count++)
                {
                    for(int counter = count + 1; counter<adlistings.size();counter++)
                    {
                        if(adlistings.get(count).getLikeCount()<adlistings.get(counter).getLikeCount()){
                            listingObjects temp = adlistings.get(count);
                            adlistings.set(count, adlistings.get(counter));
                            adlistings.set(counter, temp);
                        }
                    }
                }
            }
            else if(filter.equals("Least Likes") || filter.equals("4"))
            {
                int listingSize = adlistings.size();
                for(int count = 0; count<adlistings.size();count++)
                {
                    for(int counter = count + 1; counter<adlistings.size();counter++)
                    {
                        if(adlistings.get(count).getLikeCount()>adlistings.get(counter).getLikeCount()){
                            listingObjects temp = adlistings.get(count);
                            adlistings.set(count, adlistings.get(counter));
                            adlistings.set(counter, temp);
                        }
                    }
                }
            }

        }
        return adlistings;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sortDialog() {
        final String[] sortByList = getResources().getStringArray(R.array.sortByArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sort by");
        builder.setSingleChoiceItems(sortByList, filterPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i != filterPosition)
                {
                    listings = sort(sortByList[i], listings);
                    filterPosition = i;
                    dialogInterface.dismiss();
                    myAdapter = new RecyclerViewAdapter(MainActivity.this, listings);
                    recyclerView.setAdapter(myAdapter);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Please select a different option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
