package com.iceka.nearestworker.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.iceka.nearestworker.R;
import com.iceka.nearestworker.models.Worker;

import java.text.DecimalFormat;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.MyViewHolder> {

    private List<Worker> workerList;
    private LatLng myLatLng;
    private Context mContext;

    public WorkerAdapter(Context context, List<Worker> workers, LatLng latLng) {
        this.mContext = context;
        this.workerList = workers;
        this.myLatLng = latLng;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Worker worker = workerList.get(position);

        holder.mUsername.setText(worker.getName());
        holder.mJobs.setText(worker.getServices());
        holder.mRating.setRating(worker.getRating());

        Location myLocation = new Location("My Location");
        myLocation.setLatitude(myLatLng.latitude);
        myLocation.setLongitude(myLatLng.longitude);

        Location workerLocation = new Location("Worker Location");
        workerLocation.setLatitude(worker.getLat());
        workerLocation.setLongitude(worker.getLng());

        float distance = myLocation.distanceTo(workerLocation) / 1000;
        holder.mDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mUsername;
        private TextView mJobs;
        private TextView mClosed;
        private TextView mDistance;
        private RatingBar mRating;
        private ImageView mPhoneCall;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.tv_username_worker);
            mJobs = itemView.findViewById(R.id.tv_jobs);
            mClosed = itemView.findViewById(R.id.tv_open_close);
            mDistance = itemView.findViewById(R.id.tv_distance);
            mRating = itemView.findViewById(R.id.rb_worker);
            mPhoneCall = itemView.findViewById(R.id.img_phone_call);
        }
    }
}
