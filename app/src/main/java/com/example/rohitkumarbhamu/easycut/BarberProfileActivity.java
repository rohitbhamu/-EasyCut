package com.example.rohitkumarbhamu.easycut;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class BarberProfileActivity extends AppCompatActivity {

    //TODO Bug1 save information
    //TODO bug2 loading information is not working yet

    private  static final int CHOOSE_IMAGE= 101;
    TextView editedbarberName,editedbarberAddress,editedbarberOpeningTime;//for dialog builder layout
    TextView barberName,barberAddress,barberOpeningTime,barberNumber;//for barberprofile layout
    ImageView editedbarberImage,barberImage;
    LinearLayout editInfoLinearLayout;
    ProgressBar progressBar;//for showing after saving the picture from dialog builder
    String profileImageUrl;
    FirebaseAuth mAuth;



    Uri uriProfileImage;//for profile image
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_profile);

        //initlization for profile activity
        barberImage=findViewById(R.id.barberProfileImageView);
        barberName=findViewById(R.id.barberNameTextView);
        barberAddress=findViewById(R.id.address_textView);
        barberOpeningTime=findViewById(R.id.opening_time_textview);
        barberNumber=findViewById(R.id.mobilenumber_textview);

        Toolbar toolbar = findViewById(R.id.toolbarprofileActivity);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressbarUpdatedone);
        editInfoLinearLayout=(LinearLayout)findViewById(R.id.linearlayoutEditProfile);


        //for laoding information from firebase
        loadUserInformation();



        editInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog();
            }



        });
    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            if (user.getPhotoUrl() !=null) {

                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(barberImage);
            }
            if (user.getDisplayName()!=null){

                barberName.setText(user.getDisplayName());

            }


        }



    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.edit_info_dialog,null);//this is the view that gonna shwo on dialog

        builder.setView(view)
                .setTitle("Update Info")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveUserInformation();
                    }
                });

        editedbarberName=view.findViewById(R.id.editedBarberName);
        editedbarberOpeningTime=view.findViewById(R.id.editedBarberOpeningTime);
        editedbarberAddress=view.findViewById(R.id.editedBarberaddress);
        editedbarberImage=view.findViewById(R.id.editedbarberImage);


         builder.create();
        builder.show();     //showing the dialog
        //now we need to choose iamge from storage

        editedbarberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();

            }
        });


    }

    private void saveUserInformation() {
        //all the editText data that user have entered
        String  barber_name = editedbarberName.getText().toString();
        String barber_opening_Time= editedbarberOpeningTime.getText().toString();
        String barber_Address = editedbarberAddress.getText().toString();


        barberName.setText(barber_name);
        barberAddress.setText(barber_Address);
        barberOpeningTime.setText(barber_opening_Time);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null &&profileImageUrl!=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(barber_name)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(BarberProfileActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




        }

    }

    //for chosing image from storage we need a method and we will compare that choose image code

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode==RESULT_OK && data !=null && data.getData()!=null){

            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                editedbarberImage.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        final StorageReference profielImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis() + ".jpg");

        if (uriProfileImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            profielImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);

                            profielImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadUrl = uri;
                                }
                            });

                            //TODO see the third video why .getDownloadURl is not working here (Note: I have written alternative code for this)
                          //  profileImageUrl = taskSnapshot.get

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BarberProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select profile Image"),CHOOSE_IMAGE);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
            }
    }
}
