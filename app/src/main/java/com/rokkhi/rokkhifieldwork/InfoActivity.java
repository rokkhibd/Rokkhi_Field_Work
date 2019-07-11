package com.rokkhi.rokkhifieldwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    FirebaseFirestore db;
    List<Buildings> buildingsList;
    BuildingAdapter buildingAdapter;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    Toolbar toolbar;
    ImageView searchImage,areaCoveredMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        progressBar=findViewById(R.id.progress_bar);
        //searchView=findViewById(R.id.info_search_view);
        floatingActionButton=findViewById(R.id.floating_button);
        recyclerView=findViewById(R.id.recycler_view);
        searchImage=findViewById(R.id.action_search);
        areaCoveredMap=findViewById(R.id.covered_area_map);
        //toolbar=findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(" ");
        //getSupportActionBar().setIcon(R.drawable.logotext);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        buildingsList = new ArrayList<>();
        buildingAdapter = new BuildingAdapter(buildingsList );

        recyclerView.setAdapter(buildingAdapter);

        db=FirebaseFirestore.getInstance();

        db.collection("HousesWeWent").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Buildings p = d.toObject(Buildings.class);
                                p.setId(d.getId());
                                buildingsList.add(p);
                            }

                            buildingAdapter.notifyDataSetChanged();
                        }

                    }
                });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InfoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

       searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent=new Intent(InfoActivity.this,SearhActivity.class);
                startActivity(searchIntent);
            }
        });

       areaCoveredMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent area_intent=new Intent(InfoActivity.this,AreaCoveredMapActivity.class);
               startActivity(area_intent);
           }
       });

    }



}


