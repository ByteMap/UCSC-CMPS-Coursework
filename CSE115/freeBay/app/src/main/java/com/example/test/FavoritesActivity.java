package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter myAdapter;
    private BottomNavigationView bottomNavView;
    private String userUid;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://freebay-cbb54.appspot.com");
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference().child("Listings");

    private List<listingObjects> listings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fireUser != null) {
            userUid = fireUser.getUid();
        }

        Toolbar toolBar = findViewById(R.id.topBar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottomNavView = findViewById(R.id.bottomBar);
        bottomNavView.getMenu().getItem(3).setIcon(R.mipmap.red_heart);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent mainIntent = new Intent(FavoritesActivity.this,MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.myListings:
                        Intent myListingIntent = new Intent(FavoritesActivity.this,MyListingsActivity.class);
                        startActivity(myListingIntent);
                        break;
                    case R.id.addListing:
                        Intent addListingIntent = new Intent(FavoritesActivity.this,AddListingActivity.class);
                        startActivity(addListingIntent);
                        break;
                    case R.id.accountSettings:
                        Intent accountSettingsIntent = new Intent(FavoritesActivity.this,AccountSettingsActivity.class);
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
                listings.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listingObjects listing = postSnapshot.getValue(listingObjects.class);

                    Map<String, favoriteObjects> map = listing.getFavorites();
                    for (String key : map.keySet()) {

                        if(key.equals(userUid) && listing.getFavorites().get(key).getIsFavorite()) {
                            listings.add(0, listing);
                        }
                    }
                }
                myAdapter = new RecyclerViewAdapter(FavoritesActivity.this, listings);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FavoritesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}