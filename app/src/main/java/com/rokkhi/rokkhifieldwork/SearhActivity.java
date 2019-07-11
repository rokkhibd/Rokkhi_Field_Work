package com.rokkhi.rokkhifieldwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearhActivity extends AppCompatActivity {

    Toolbar toolbar;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView notFoundTxt;
    FirebaseFirestore db;
    List<Buildings> buildingsList;
    BuildingAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searh);

        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        searchView=findViewById(R.id.search_view);
        recyclerView=findViewById(R.id.house_search_recycler_view);
        notFoundTxt=findViewById(R.id.textView);

        //recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        buildingsList=new ArrayList<>();

        Adapter adapter=new Adapter(buildingsList);

        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                //TODO: When user click on search button
                progressDialog.setTitle("Searching");
                //progressDialog.setMessage("loading");
                progressDialog.setCancelable(true);
                progressDialog.show();

                buildingsList.clear();

                String[] tags=s.toLowerCase().split(" ");
                for (final String tag: tags){
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("HousesWeWent").whereArrayContains("tags",tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                progressDialog.dismiss();
                                for (DocumentSnapshot doc:task.getResult().getDocuments()){
                                    Buildings buildings=doc.toObject(Buildings.class);
                                    buildings.setId(doc.getId());
                                    buildingsList.add(buildings);
                                }
                            }else {

                                String message=task.getException().getMessage();
                                Toast.makeText(SearhActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //TODO: When user type any single word in search box
                return false;
            }
        });
    }


    class Adapter extends BuildingAdapter implements Filterable {

        List<Buildings> filteredList;

        public Adapter(List<Buildings> buildingsList) {
            super(buildingsList);
            filteredList=new ArrayList<>();

        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    //Filter logic
                    FilterResults filterResults=new FilterResults();
                    String[] tags=constraint.toString().toLowerCase().split(" ");

                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
    }

}
