package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder>{
    private Context myContext;
    private List<listingObjects> myData;
    private ImageButton favorite;
    private ImageButton thumbUpDown;
    private String userUid;
    private int count;

    public RecyclerViewAdapter(Context myContext, List<listingObjects> myData) {
        this.myContext = myContext;
        this.myData = myData;
    }

    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater myInflater = LayoutInflater.from(myContext);
        view = myInflater.inflate(R.layout.listings_cardview, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fireUser != null) {
            userUid = fireUser.getUid();
        }

        holder.name.setText(myData.get(position).getItemName());

        Glide.with(myContext).load(myData.get(position).getImg()).into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(myContext, listings.class);

                //Passes data to listings
                intent.putExtra("Listing Name", myData.get(position).getItemName());
                intent.putExtra("Listing Condition", myData.get(position).getItemCondition());
                intent.putExtra("Listing Description", myData.get(position).getItemDescription());
                intent.putExtra("Listing Email Address", myData.get(position).getEmailInfo());
                intent.putExtra("Listing Phone Number", myData.get(position).getPhoneNumberInfo());
                intent.putExtra("Listing Image", myData.get(position).getImg());
                intent.putExtra("Listing Likes", myData.get(position).getLikeCount());
                //Begin the activity on click
                myContext.startActivity(intent);
            }
        });

        Map<String, favoriteObjects> favoritesMap = myData.get(position).getFavorites();
        for (String key : favoritesMap.keySet()) {

            if (key.equals(userUid) && myData.get(position).getFavorites().get(userUid).getIsFavorite()) {
                favorite.setImageResource(R.mipmap.red_heart);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference favoriteRef = listingRef.child("favorites").child(userUid).child("isFavorite");

                        favoriteRef.setValue(false);
                    }
                });

                break;
            }
            else if (key.equals(userUid) && !myData.get(position).getFavorites().get(userUid).getIsFavorite()) {
                favorite.setImageResource(R.mipmap.heart_outline);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference favoriteRef = listingRef.child("favorites").child(userUid).child("isFavorite");

                        favoriteRef.setValue(true);
                    }
                });
                break;
            }

            if (!key.equals(userUid)) {
                favorite.setImageResource(R.mipmap.heart_outline);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference favoriteRef = listingRef.child("favorites").child(userUid).child("isFavorite");

                        favoriteRef.setValue(true);
                    }
                });
            }
        }

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareImage = myData.get(position).getImg();
                Uri image = Uri.parse(shareImage);
                String shareDescription = "Hey! I just saw a cool posting on freeBay of a %s that I thought you would like!";

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, String.format(shareDescription, myData.get(position).getItemName()));
                share.putExtra(Intent.EXTRA_STREAM, image);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                myContext.startActivity(Intent.createChooser(share, "Share using"));
            }
        });

        Map<String, likeObjects> likesMap = myData.get(position).getLikes();
        for (String key : likesMap.keySet()) {

            if (key.equals(userUid) && myData.get(position).getLikes().get(userUid).getIsLike()) {
                thumbUpDown.setImageResource(R.mipmap.thumb_up_down_filled);
                thumbUpDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference likeRef = listingRef.child("likes").child(userUid).child("isLike");

                        likeRef.setValue(false);

                        DatabaseReference likeCountRef = listingRef.child("likeCount");

                        count = myData.get(position).getLikeCount();
                        count--;
                        likeCountRef.setValue(count);
                    }
                });

                break;
            }
            else if (key.equals(userUid) && !myData.get(position).getLikes().get(userUid).getIsLike()) {
                thumbUpDown.setImageResource(R.mipmap.thumb_up_down_outline);
                thumbUpDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference likeRef = listingRef.child("likes").child(userUid).child("isLike");

                        likeRef.setValue(true);

                        DatabaseReference likeCountRef = listingRef.child("likeCount");

                        count = myData.get(position).getLikeCount();
                        count++;
                        likeCountRef.setValue(count);
                    }
                });
                break;
            }

            if (!key.equals(userUid)) {
                thumbUpDown.setImageResource(R.mipmap.thumb_up_down_outline);
                thumbUpDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(myData.get(position).getListingNum());
                        DatabaseReference likeRef = listingRef.child("likes").child(userUid).child("isLike");

                        likeRef.setValue(true);

                        DatabaseReference likeCountRef = listingRef.child("likeCount");

                        count = myData.get(position).getLikeCount();
                        count++;
                        likeCountRef.setValue(count);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        CardView cardView;
        ImageButton share;

        public myViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.listingNameCard);
            image = itemView.findViewById(R.id.listingImageCard);
            cardView = itemView.findViewById(R.id.cardview_id);
            favorite = itemView.findViewById(R.id.favoriteButton);
            share = itemView.findViewById(R.id.shareButton);
            thumbUpDown = itemView.findViewById(R.id.thumbUpDown);
        }
    }
}
