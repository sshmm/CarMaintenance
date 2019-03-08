package com.example.android.carmaintenance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.carmaintenance.ServiceFragment.OnListFragmentInteractionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyServiceRecyclerViewAdapter extends RecyclerView.Adapter<MyServiceRecyclerViewAdapter.ViewHolder> {

    private  ArrayList<KeyService> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;
    private String userUid;
    private int currentDistance;


    public MyServiceRecyclerViewAdapter( OnListFragmentInteractionListener listener) {
            mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getMaintenanceService().getServiceName());
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaintenanceService finished = holder.mItem.getMaintenanceService();

                    MaintenanceService maintenanceService = new MaintenanceService(finished.getServiceName(),
                            finished.getPeriodicity(),currentDistance,true,finished.getPrice());
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference =  firebaseDatabase.getReference().child(userUid).child("services");
                    databaseReference.push().setValue(maintenanceService);
                    databaseReference = firebaseDatabase.getReference().child(userUid).child("services").child(holder.mItem.key);
                    databaseReference.child("state").setValue(false);
                    databaseReference.child("lastTime").setValue(currentDistance);

                }
            });

        String disString = "";
        Context context = holder.mDisView.getContext();
        int due = (holder.mItem.getMaintenanceService().getPeriodicity() + holder.mItem.getMaintenanceService().getLastTime()) - currentDistance;
        if (due >= 0){
            disString = context.getResources().getString(R.string.due_in) + " " + due + context.getResources().getString(R.string.unit);
        }else {
            disString = (due * -1) +context.getResources().getString(R.string.unit)+ " " +context.getResources().getString(R.string.over_due);
        }
        holder.mDisView.setText(disString);

        String price = holder.mItem.getMaintenanceService().getPrice() + context.getResources().getString(R.string.unit2);
        holder.mPriceView.setText(price);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbaks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                   mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mDisView;
        public final TextView mPriceView;
        public final Button doneButton;
        public KeyService mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tv_service_name_2);
            mDisView = (TextView) view.findViewById(R.id.tv_remaining);
            mPriceView = (TextView) view.findViewById(R.id.tv_price_2);
            doneButton = (Button) view.findViewById(R.id.button);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

    public void setAdapterData(ArrayList<KeyService> maintenanceServices , String userUid, int currentDistance){
        this.userUid = userUid;
        mValues = maintenanceServices;
        this.currentDistance = currentDistance;
        notifyDataSetChanged();

    }
}
