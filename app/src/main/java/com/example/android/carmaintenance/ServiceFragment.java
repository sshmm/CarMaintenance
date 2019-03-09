package com.example.android.carmaintenance;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.carmaintenance.MainActivity.DISTANCE_KEY;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ServiceFragment extends Fragment {


    private static final String USER_NAME = "user_name";
    private OnListFragmentInteractionListener mListener;
    private MyServiceRecyclerViewAdapter adapter;
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
    public ServiceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ServiceFragment newInstance(String userUid) {
        ServiceFragment fragment = new ServiceFragment();
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
        attachDatabaseReadListener();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new  MyServiceRecyclerViewAdapter( mListener);
            Log.e("adpterDataSet","2");

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(KeyService keyService);
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
                        if (maintenanceService.isState())
                            maintenanceServices.add(new KeyService(key,maintenanceService));


                    }

                    Intent intentBroad = new Intent(getActivity().getApplicationContext(), NewAppWidget.class);
                    intentBroad.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    AppWidgetManager widgetManager = AppWidgetManager.getInstance(getActivity().getApplicationContext());
                    int[] ids = widgetManager.getAppWidgetIds(new ComponentName(getActivity().getApplicationContext(), NewAppWidget.class));
                    widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

                    intentBroad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    intentBroad.putExtra("distance",loadPreferences());
                    intentBroad.putExtra("username",getArguments().getString(USER_NAME));
                   // Log.e("fffffffffff",maintenanceServices.get((maintenanceServices.size())-1).getServiceName());
                    Log.e("adpterDataSet","1");
                    adapter.setAdapterData(maintenanceServices, getArguments().getString(USER_NAME),loadPreferences());
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    maintenanceServices.clear();
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        MaintenanceService maintenanceService = dataSnapshot1.getValue(MaintenanceService.class);
                        String key = dataSnapshot1.getKey();
                        if (maintenanceService.isState())
                            maintenanceServices.add(new KeyService(key,maintenanceService));

                    }
                    // Log.e("fffffffffff",maintenanceServices.get((maintenanceServices.size())-1).getServiceName());
                    Log.e("adpterDataSet","1");
                    adapter.setAdapterData(maintenanceServices, getArguments().getString(USER_NAME),loadPreferences());
                    recyclerView.setAdapter(adapter);

                    Intent intentBroad = new Intent(getActivity().getApplicationContext(), NewAppWidget.class);
                    intentBroad.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    AppWidgetManager widgetManager = AppWidgetManager.getInstance(getActivity().getApplicationContext());
                    int[] ids = widgetManager.getAppWidgetIds(new ComponentName(getActivity().getApplicationContext(), NewAppWidget.class));
                    widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

                    intentBroad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    intentBroad.putExtra("distance",loadPreferences());
                    intentBroad.putExtra("username",getArguments().getString(USER_NAME));
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

    private int loadPreferences() {
        SharedPreferences sharedPreferences = this.getActivity().getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt(DISTANCE_KEY, 0);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}