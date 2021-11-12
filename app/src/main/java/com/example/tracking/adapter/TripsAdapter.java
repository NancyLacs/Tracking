package com.example.tracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tracking.R;
import com.example.tracking.entities.Trip;

import java.util.List;

public class TripsAdapter  extends RecyclerView.Adapter<TripsAdapter.TripViewHolder>{
    private List<Trip> trips;
    private Context context;

    private ItemClickListener itemClickListener;

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trip_row, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        if (trips.get(0).status == 1){
            holder.tvTripDate.setText(trips.get(position).getDate());
        } else {
            holder.tvTripDate.setText(trips.get(position).endTime);
        }
        holder.tvTripNameInList.setText(trips.get(position).getTripName());

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvTripNameInList;
        private TextView tvTripDate;
        private ImageView ivTripDelete;


        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripNameInList = itemView.findViewById(R.id.tvTripNameInList);
            tvTripDate = itemView.findViewById(R.id.tvTripDate);
            ivTripDelete = itemView.findViewById(R.id.ivDeleteTrip);
            tvTripDate.setOnClickListener(this::onClick);
            tvTripNameInList.setOnClickListener(this::onClick);
            ivTripDelete.setOnClickListener(this::onClick);

        }

        @Override
        public void onClick(View view) {
            if(TripsAdapter.this.itemClickListener != null){
                int pos = getAbsoluteAdapterPosition();
                itemClickListener.onItemClick(view, pos);
            }
        }
    }
    public TripsAdapter(Context context, List<Trip> trips){
        this.trips = trips;
        this.context = context;
    }

    public void setClickListener(TripsAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
