package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class listings extends AppCompatActivity {

    private TextView name, condition, description, emailInfo, phoneNumber, likes;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listings);

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

        name = findViewById(R.id.listingName);
        condition = findViewById(R.id.listingCondition);
        description = findViewById(R.id.listingDescription);
        emailInfo = findViewById(R.id.listingEmailInformation);
        phoneNumber = findViewById(R.id.listingPhoneNumberInformation);
        img = findViewById(R.id.listingImage);
        likes = findViewById(R.id.likesCount);


        //Receive data from Main_Activity

        Intent intent = getIntent();
        String Name = intent.getExtras().getString("Listing Name");
        String Condition = intent.getExtras().getString("Listing Condition");
        String Description = intent.getExtras().getString("Listing Description");
        String email_Address = intent.getExtras().getString("Listing Email Address");
        String phone_Number = intent.getExtras().getString("Listing Phone Number");
        String imageURL = intent.getExtras().getString("Listing Image");
        int likesCount = intent.getExtras().getInt("Listing Likes");

        //Setting the values
        name.setText(Name);
        condition.setText(Condition);
        description.setText(Description);
        emailInfo.setText(email_Address);
        phoneNumber.setText(phone_Number);
        Glide.with(this).load(imageURL).into(img);
        likes.setText(String.valueOf(likesCount));
    }
}
