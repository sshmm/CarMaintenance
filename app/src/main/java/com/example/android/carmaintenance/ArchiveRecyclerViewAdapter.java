package com.example.android.carmaintenance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.carmaintenance.ServiceFragment.OnListFragmentInteractionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ArchiveRecyclerViewAdapter extends RecyclerView.Adapter<ArchiveRecyclerViewAdapter.ViewHolder> {

    private  ArrayList<KeyService> mValues = new ArrayList<>();
    private String userUid;


    public ArchiveRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context context = holder.mDisView.getContext();
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getMaintenanceService().getServiceName());
        holder.doneButton.setText(context.getResources().getString(R.string.delete));


        String disString = context.getResources().getString(R.string.serviced_at) +" " + holder.mItem.getMaintenanceService().getLastTime();
        holder.mDisView.setText(disString);

        String price = context.getResources().getString(R.string.cost) + holder.mItem.getMaintenanceService().getPrice() + context.getResources().getString(R.string.unit2);

        holder.mPriceView.setText(price);

        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference =  firebaseDatabase.getReference().child(userUid).child("services");
                databaseReference = firebaseDatabase.getReference().child(userUid).child("services").child(holder.mItem.key);
                databaseReference.removeValue();
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

    public void setAdapterData(ArrayList<KeyService> maintenanceServices, String userUid){
        this.userUid = userUid;
        mValues = maintenanceServices;

    }
}
