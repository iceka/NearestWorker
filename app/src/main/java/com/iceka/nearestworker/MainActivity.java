package com.iceka.nearestworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iceka.nearestworker.adapters.WorkerAdapter;
import com.iceka.nearestworker.models.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;

    private EditText mEtMyLocation;
    private Button mBtFind;
    private Spinner mSpServices;
    private RecyclerView mRecyclerView;
    private TextView mTvWorkerNotFound;

    private DatabaseReference mDriverReference;

    private List<Worker> workerList = new ArrayList<>();
    private LatLng myLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtMyLocation = findViewById(R.id.et_my_loation);
        mBtFind = findViewById(R.id.bt_find_worker);
        mSpServices = findViewById(R.id.sp_jobdesk);
        mRecyclerView = findViewById(R.id.rv_worker);
        mTvWorkerNotFound = findViewById(R.id.tv_no_worker_existed);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDriverReference = firebaseDatabase.getReference().child("worker");

        mEtMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), REQUEST_CODE);
            }
        });

        mBtFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWorker();
            }
        });
    }

    private void getWorker() {
        mDriverReference.child(mSpServices.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workerList.clear();
                if (dataSnapshot.exists()) {
                    mTvWorkerNotFound.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Worker worker = snapshot.getValue(Worker.class);
                        workerList.add(worker);

                        Collections.sort(workerList, new SortPlaces(myLatLng));

                        WorkerAdapter adapter = new WorkerAdapter(getApplicationContext(), workerList, myLatLng);
                        mRecyclerView.setAdapter(adapter);
                    }
                } else {
                    workerList.clear();
                    mTvWorkerNotFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class SortPlaces implements Comparator<Worker> {
        LatLng currentLoc;

        public SortPlaces(LatLng current) {
            currentLoc = current;
        }

        @Override
        public int compare(final Worker place1, final Worker place2) {
            double lat1 = place1.getLat();
            double lon1 = place1.getLng();
            double lat2 = place2.getLat();
            double lon2 = place2.getLng();

            Location location1 = new Location("1");
            location1.setLatitude(lat1);
            location1.setLongitude(lon1);
            Location location2 = new Location("2");
            location2.setLatitude(lat2);
            location2.setLongitude(lon2);

            double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
            double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
            return (int) (distanceToPlace1 - distanceToPlace2);
        }

        public double distance(double fromLat, double fromLon, double toLat, double toLon) {
            double radius = 6378137;   // approximate Earth radius, *in meters*
            double deltaLat = toLat - fromLat;
            double deltaLon = toLon - fromLon;
            double angle = 2 * Math.asin(Math.sqrt(
                    Math.pow(Math.sin(deltaLat / 2), 2) +
                            Math.cos(fromLat) * Math.cos(toLat) *
                                    Math.pow(Math.sin(deltaLon / 2), 2)));
            return radius * angle;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            String address = data.getStringExtra("address");
            mEtMyLocation.setText(address);
            myLatLng = data.getExtras().getParcelable("latlng");
        }
    }
}
