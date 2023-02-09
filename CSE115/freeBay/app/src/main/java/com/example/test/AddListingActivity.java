package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddListingActivity extends AppCompatActivity {

    Button takePic;
    String pathToFile;
    Button chooseImg;
    ImageView imgView;
    ProgressDialog pd;
    static final int PICK_IMAGE_REQUEST = 2;
    Uri filePath;
    EditText itemName;
    Spinner condition;
    EditText itemDescription;
    EditText emailInformation;
    EditText phoneNumberInformation;
    String fileUrl;
    Button submit;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_z");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://freebay-cbb54.appspot.com");
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        Toolbar toolBar = findViewById(R.id.topBar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        takePic = findViewById(R.id.cameraButton);
        chooseImg = findViewById(R.id.chooseImg);
        imgView = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        condition = findViewById(R.id.conditionArray);
        itemDescription = findViewById(R.id.itemDescription);
        emailInformation = findViewById(R.id.emailInformation);
        phoneNumberInformation = findViewById(R.id.phoneNumberInformation);
        submit = findViewById(R.id.submit);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(takePic)) {
                    dispatchPictureTakerAction();
                }
                else if (v.equals(chooseImg)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                }
            }
        };

        itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        itemDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        itemDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);
        itemDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        emailInformation.setImeOptions(EditorInfo.IME_ACTION_DONE);
        emailInformation.setRawInputType(InputType.TYPE_CLASS_TEXT);
        emailInformation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        phoneNumberInformation.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phoneNumberInformation.setRawInputType(InputType.TYPE_CLASS_TEXT);
        phoneNumberInformation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        takePic.setOnClickListener(onClickListener);
        chooseImg.setOnClickListener(onClickListener);

        pd = new ProgressDialog(this);
        pd.setMessage("Submitting...");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    pd.show();

                    String currentTimestamp = formatter.format(new Date());

                    final StorageReference childRef = storageRef.child("IMG_" + currentTimestamp + ".jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get the image Url of the file uploaded
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

                                            String userUid = fireUser.getUid();
                                            String itemNameText = itemName.getText().toString();
                                            String conditionText = condition.getSelectedItem().toString();
                                            String itemDescriptionText = itemDescription.getText().toString();
                                            String emailInformationText = emailInformation.getText().toString();
                                            String phoneNumberInformationText = phoneNumberInformation.getText().toString();

                                            // getting image uri and converting into string
                                            fileUrl = uri.toString();

                                            HashMap<String, favoriteObjects> favorites = new HashMap<>();
                                            favoriteObjects favorite = new favoriteObjects(false);
                                            favorites.put(userUid, favorite);
                                            HashMap<String, likeObjects> likes = new HashMap<>();
                                            likeObjects like = new likeObjects(false);
                                            likes.put(userUid, like);
                                            int likeCount = 0;
                                            listingObjects listing = new listingObjects(userUid, itemNameText, conditionText, itemDescriptionText, emailInformationText, phoneNumberInformationText, fileUrl, favorites, likes, likeCount);

                                            DatabaseReference databaseReference = database.getReference().child("Listings");
                                            String uniqueListingKey = databaseReference.child("Listings").push().getKey();
                                            DatabaseReference listingsRef = databaseReference.child(uniqueListingKey);

                                            listing.setListingNum(uniqueListingKey);

                                            listingsRef.setValue(listing);
                                        }
                                    });
                                }
                            }
                            pd.dismiss();
                            Toast.makeText(AddListingActivity.this, "Submission successful", Toast.LENGTH_SHORT).show();
                            Intent goToMain = new Intent(AddListingActivity.this,MainActivity.class);
                            startActivity(goToMain);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddListingActivity.this, "Submission Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AddListingActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                filePath = FileProvider.getUriForFile(AddListingActivity.this, "admin", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                startActivityForResult(takePic, 1);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = null;
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//            return image;
//        }
        image = new File(storageDir, name);
        return image;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            try {
                //getting image from in-app camera
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 2) {
            filePath = data.getData();
            try {
                //getting image from local storage
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}