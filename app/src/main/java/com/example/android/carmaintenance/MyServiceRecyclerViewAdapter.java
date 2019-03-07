package com.example.android.carmaintenance;

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
import com.example.android.carmaintenance.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyServiceRecyclerViewAdapter extends RecyclerView.Adapter<MyServiceRecyclerViewAdapter.ViewHolder> {

    private  ArrayList<KeyService> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;
    private String userUid;


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
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child(userUid).child("services").child(holder.mItem.key);
                    databaseReference.child("state").setValue(false);

                }
            });

        String disString = "Due in " + holder.mItem.getMaintenanceService().getPeriodicity();
        holder.mDisView.setText(disString);
        holder.mPriceView.setText(Double.toString(holder.mItem.getMaintenanceService().getPrice()));

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

    public void setAdapterData(ArrayList<KeyService> maintenanceServices , String userUid){
        this.userUid = userUid;
        mValues = maintenanceServices;
        notifyDataSetChanged();

    }
}
