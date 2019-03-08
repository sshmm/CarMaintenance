package com.example.android.carmaintenance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class ArchiveFragment extends Fragment{


    private static final String USER_NAME = "user_name";

    private ArchiveRecyclerViewAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private String mUserUid;
    private ArrayList<KeyService> maintenanceServices = new ArrayList<>();
    private RecyclerView recyclerView;

    private MainActivity activity;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArchiveFragment() {
    }

    @SuppressWarnings("unused")
    public static ArchiveFragment newInstance(String userUid) {
        ArchiveFragment fragment = new ArchiveFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME,userUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_list, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference =firebaseDatabase.getReference().child(getArguments().getString(USER_NAME));
        Log.e("Testtttttttttttt",getArguments().getString(USER_NAME));
        attachDatabaseReadListener();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new  ArchiveRecyclerViewAdapter( );

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);/*
        if (context instanceof OnArchiveListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArchiveListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        attachDatabaseReadListener();

    }

    private void attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.e("ddddddddddd",maintenanceService.getServiceName());
                    maintenanceServices.clear();
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        MaintenanceService maintenanceService = dataSnapshot1.getValue(MaintenanceService.class);
                        String key = dataSnapshot1.getKey();

                        if (!maintenanceService.isState())
                            maintenanceServices.add(new KeyService(key,maintenanceService));

                    }
                   // Log.e("fffffffffff",maintenanceServices.get((maintenanceServices.size())-1).getServiceName());
                    Log.e("adpterDataSet","1");
                    adapter.setAdapterData(maintenanceServices, getArguments().getString(USER_NAME));
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    maintenanceServices.clear();
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        MaintenanceService maintenanceService = dataSnapshot1.getValue(MaintenanceService.class);
                        String key = dataSnapshot1.getKey();

                        if (!maintenanceService.isState())
                            maintenanceServices.add(new KeyService(key,maintenanceService));

                    }
                    // Log.e("fffffffffff",maintenanceServices.get((maintenanceServices.size())-1).getServiceName());
                    Log.e("adpterDataSet","1");
                    adapter.setAdapterData(maintenanceServices,getArguments().getString(USER_NAME));
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(childEventListener);


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener(){
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

}