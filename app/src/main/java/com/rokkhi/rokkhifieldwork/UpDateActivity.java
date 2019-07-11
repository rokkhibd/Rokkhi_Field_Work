package com.rokkhi.rokkhifieldwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpDateActivity extends AppCompatActivity {

    EditText ownerName,ownerNumber,managerName,managerNumber,fieldDescription,address,todayDate,followUpDate;
    Button updateButton,deleteButton;
    FirebaseFirestore db;
    Buildings buildings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_date);

        buildings= (Buildings) getIntent().getSerializableExtra("buildings");
        db=FirebaseFirestore.getInstance();

        ownerName=findViewById(R.id.update_owner_name);
        ownerNumber=findViewById(R.id.update_owner_numbert);
        managerName=findViewById(R.id.update_manager_name);
        managerNumber=findViewById(R.id.update_manager_number);
        fieldDescription=findViewById(R.id.update_desc_box);
        address=findViewById(R.id.update_house_address);
        todayDate=findViewById(R.id.update_todays_date);
        followUpDate=findViewById(R.id.update_followUp_date);
        updateButton=findViewById(R.id.update_btn);
        deleteButton=findViewById(R.id.delete_btn);

        ownerName.setText(buildings.getOwner_name());
        ownerNumber.setText(buildings.getOwner_number());
        managerName.setText(buildings.getManager_name());
        managerNumber.setText(buildings.getManager_number());
        address.setText(buildings.getAddress());
        todayDate.setText(buildings. getTodaysDate());
        followUpDate.setText(buildings.getFollowUpDate());
        fieldDescription.setText(buildings.getField_description());


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownername=ownerName.getText().toString();
                String ownernumber=ownerNumber.getText().toString();
                String managername=managerName.getText().toString();
                String managernmbr=managerNumber.getText().toString();
                String flataddress=address.getText().toString();
                String todaysdate=todayDate.getText().toString();
                String followupdate=followUpDate.getText().toString();
                String fielddescription=fieldDescription.getText().toString();


               Map<String,Object> houseInfo=new HashMap<>();
                houseInfo.put("manager_name",managername);
                houseInfo.put("manager_number",managernmbr);
                houseInfo.put("owner_number",ownernumber);
                houseInfo.put("owner_name",ownername);
                houseInfo.put("field_description",fielddescription);
                houseInfo.put("address",flataddress);
               // houseInfo.put("imageUrl",downloadImageUri);
                houseInfo.put("todaysDate",todaysdate);
                houseInfo.put("followUpDate",followupdate);

                db.collection("HousesWeWent").document(buildings.getId())
                        .set(houseInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpDateActivity.this,"Info Updated Successfully",Toast.LENGTH_SHORT).show();
                                gotoInfoActivity();

                            }
                        });
/*
                db.collection("HousesWeWent").document(buildings.getId())
                        .update("manager_name",managername,"manager_number",managernmbr,"owner_number",ownernumber,
                               "owner_name",ownername, "field_description",fielddescription,"address",flataddress,"todaysDate",todaysdate,
                                "followUpDate",followupdate)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpDateActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(UpDateActivity.this);
                alertDialog.setTitle("Are your sure about this?");
                alertDialog.setMessage("Deletion is permanent");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInfo();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad=alertDialog.create();
                ad.show();
            }
        });
    }

    private void gotoInfoActivity() {

        Intent intent= new Intent(UpDateActivity.this,InfoActivity.class);
        startActivity(intent);
    }

    public void deleteInfo(){
        db.collection("HousesWeWent").document(buildings.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpDateActivity.this,"Data is deleted",Toast.LENGTH_SHORT).show();
                            gotoInfoActivity();
                        }
                    }
                });
    }
}
