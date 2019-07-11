package com.rokkhi.rokkhifieldwork;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.LoadBalancer;

public class MainActivity extends AppCompatActivity {

    EditText ownerName,ownerNumber,managerName,managerNumber,fieldDescription,address,todayDate,followUpDate,person_weTalked;
    Button saveInfo,showInfo;
    CircleImageView circleImageView;
    FirebaseFirestore db;
    ProgressBar progressBar;
    Bitmap bitmap;
    Uri pickedImageUri;
    StorageReference houseImageRef;
    String downloadImageUri,today_date,followUp_Date,spinner_item;
    ImageView todaysDate,nextFollowingDate,googleMapImage;
    DatePickerDialog datePickerDialog;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        houseImageRef= FirebaseStorage.getInstance().getReference().child("House Image");

        ownerName=findViewById(R.id.owner_name);
        ownerNumber=findViewById(R.id.owner_numbert);
        managerName=findViewById(R.id.manager_name);
        managerNumber=findViewById(R.id.manager_number);
        fieldDescription=findViewById(R.id.desc_box);
        progressBar=findViewById(R.id.progress);
        address=findViewById(R.id.house_address);
        todayDate=findViewById(R.id.todays_date);
        followUpDate=findViewById(R.id.followUp_date);
        todaysDate=findViewById(R.id.todays_date_calendar);
        nextFollowingDate=findViewById(R.id.nxt_following_date_calendar);
        googleMapImage=findViewById(R.id.map_image);
        person_weTalked=findViewById(R.id.person_we_talked);

        saveInfo=findViewById(R.id.save_info_btn);
        showInfo=findViewById(R.id.show_info_btn);
        circleImageView=findViewById(R.id.house_photo);
        spinner=findViewById(R.id.spinner_id);

        //TODO: Set Spinner, Get Spinner Items
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(this,R.array.name,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_item=parent.getItemAtPosition(position).toString();
                person_weTalked.setText(spinner_item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        googleMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SeeLocationActivity.class);
                startActivity(intent);
            }
        });

        //TODO:Set pick up image dialogue
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PickImageDialog.build(new PickSetup()).show(MainActivity.this);

                PickSetup setup = new PickSetup().setWidth(100).setHeight(100)
                        .setTitle("Choose Photo")
                        .setBackgroundColor(Color.WHITE)
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setGalleryButtonText("Gallery")
                        .setCameraIcon(R.mipmap.camera_colored)
                        .setGalleryIcon(R.mipmap.gallery_colored);

                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            pickedImageUri = r.getUri();
                            bitmap = r.getBitmap();
                            circleImageView.setImageBitmap(r.getBitmap());

                        } else {
                            Toast.makeText(MainActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }).show(MainActivity.this);

            }
        });

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                postImageStorage();


            }
        });

        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInfoActivity();
            }
        });

        todaysDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                todayDate.setText(day + "/" + (month+1) + "/" + year);

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        nextFollowingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                followUpDate.setText(day + "/" + (month+1) + "/" + year);

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });



    }

    private void gotoInfoActivity() {

        Intent intent= new Intent(MainActivity.this,InfoActivity.class);
        startActivity(intent);
    }

    public void postImageStorage(){
        final StorageReference filePath=houseImageRef.child(pickedImageUri.getLastPathSegment()+".jpg");
        final UploadTask uploadTask=filePath.putFile(pickedImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Image Uploaded successfully ",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUri=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){

                            downloadImageUri=task.getResult().toString();
                            //Toast.makeText(MainActivity.this,"Got image URL",Toast.LENGTH_SHORT).show();

                            String ownername=ownerName.getText().toString();
                            String ownernumber=ownerNumber.getText().toString();
                            String managername=managerName.getText().toString();
                            String managernumber=managerNumber.getText().toString();
                            String fieldnote=fieldDescription.getText().toString();
                            String houseAddress=address.getText().toString();
                            String today_date=todayDate.getText().toString();
                            String followUp_Date=followUpDate.getText().toString();
                            String personWeTalked=person_weTalked.getText().toString();

                            Map<String,Object> houseInfo=new HashMap<>();
                            houseInfo.put("manager_name",managername);
                            houseInfo.put("manager_number",managernumber);
                            houseInfo.put("owner_number",ownernumber);
                            houseInfo.put("owner_name",ownername);
                            houseInfo.put("field_description",fieldnote);
                            houseInfo.put("address",houseAddress);
                            //houseInfo.put("search",houseAddress.toLowerCase());
                            houseInfo.put("imageUrl",downloadImageUri);
                            houseInfo.put("todaysDate",today_date);
                            houseInfo.put("followUpDate",followUp_Date);
                            houseInfo.put("person_We_Talked",personWeTalked);



                            db.collection("HousesWeWent").add(houseInfo)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(MainActivity.this,"DataSaved Successfully",Toast.LENGTH_SHORT).show();
                                            //postImageStorage();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }



}
