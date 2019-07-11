package com.rokkhi.rokkhifieldwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

   private Context mCtx;
    private List<Buildings> buildingsList;


    public BuildingAdapter(List<Buildings> buildingsList) {
       // this.mCtx = mCtx;
        this.buildingsList = buildingsList;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BuildingViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_houses,viewGroup,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder buildingViewHolder, int i) {

        Buildings buildings=buildingsList.get(i);

        buildingViewHolder.ownername.setText(buildings.getOwner_name());
        buildingViewHolder.ownernmbr.setText(buildings.getOwner_number());
        buildingViewHolder.manageranme.setText(buildings.getManager_name());
        buildingViewHolder.managernmbr.setText(buildings.getManager_number());
        buildingViewHolder.address.setText(buildings.getAddress());
        buildingViewHolder.todaysdate.setText(buildings.getTodaysDate());
        buildingViewHolder.followupdate.setText(buildings.getFollowUpDate());
        buildingViewHolder.description.setText(buildings.getField_description());

        Picasso.get().load(buildings.getImageUrl()).into(buildingViewHolder.houseimage);

    }

    @Override
    public int getItemCount() {
        return buildingsList.size();
    }

    public class BuildingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView ownername,ownernmbr,manageranme,managernmbr,address,todaysdate,followupdate,description;
        ImageView houseimage;


        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);

            ownername=itemView.findViewById(R.id.show_owner_name);
            ownernmbr=itemView.findViewById(R.id.show_owner_number);
            manageranme=itemView.findViewById(R.id.show_manager_name);
            managernmbr=itemView.findViewById(R.id.show_manager_number);
            address=itemView.findViewById(R.id.show_house_address);
            houseimage=itemView.findViewById(R.id.show_flat_photo);
            todaysdate=itemView.findViewById(R.id.show_today_date);
            followupdate=itemView.findViewById(R.id.show_followUp_date);
            description=itemView.findViewById(R.id.shoe_note_info);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Buildings buildings=buildingsList.get(getAdapterPosition());
            Intent intent=new Intent(v.getContext(),UpDateActivity.class);
            intent.putExtra("buildings",buildings);
            v.getContext().startActivity(intent);
        }
    }

    public void updateList(List<Buildings> newBuildingList){

        buildingsList=new ArrayList<>();
        buildingsList.addAll(newBuildingList);
        notifyDataSetChanged();

    }
}
