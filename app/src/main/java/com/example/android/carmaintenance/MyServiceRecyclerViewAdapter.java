package com.example.android.carmaintenance;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.carmaintenance.ServiceFragment.OnListFragmentInteractionListener;
import com.example.android.carmaintenance.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyServiceRecyclerViewAdapter extends RecyclerView.Adapter<MyServiceRecyclerViewAdapter.ViewHolder> {

    private  ArrayList<MaintenanceService> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;


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
        holder.mIdView.setText(holder.mItem.getServiceName());
        Log.e("Valueeeeeeeeeeees",mValues.get(0).getServiceName());
        holder.mContentView.setText(mValues.get(position).getServiceName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbaks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                //    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mContentView;
        public MaintenanceService mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void setAdapterData(ArrayList<MaintenanceService> maintenanceServices){
        mValues = maintenanceServices;
        Log.e("setData2",mValues.get(0).getServiceName());

    }
}
