package com.example.android.carmaintenance;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ServiceWidgetFactory(this.getApplicationContext(), intent));
    }


    public class ServiceWidgetFactory implements RemoteViewsFactory {
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;

        private Context context;
        private int appWidgetId;
        private String userUid;
        private int distance;
        private ArrayList<MaintenanceService> maintenanceServices = new ArrayList<>();

        public ServiceWidgetFactory(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            distance = intent.getIntExtra("distance",1);
            userUid = intent.getStringExtra("username");


        }


        @Override
        public int getCount() {
            if (maintenanceServices == null){

                return 0;
            }else {
                return maintenanceServices.size();}
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         *Similar to getView of Adapter where instead of View
         *we return RemoteViews
         *
         */
        @Override
        public RemoteViews getViewAt(int position) {

            final RemoteViews remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.widget_row);
            if (maintenanceServices == null){

            }
            else{
                String disString = "";
                Context context = getApplicationContext();
                remoteView.setTextViewText(R.id.tv_service_name_3,maintenanceServices.get(position).getServiceName());


                int due = (maintenanceServices.get(position).getPeriodicity()+ maintenanceServices.get(position).getLastTime()) - distance;
                if (due >= 0){
                    disString = context.getResources().getString(R.string.due_in) + " " + due + context.getResources().getString(R.string.unit);
                }else {
                    disString = (due * -1) +context.getResources().getString(R.string.unit)+ " " +context.getResources().getString(R.string.over_due);
                }
                remoteView.setTextViewText(R.id.tv_remaining_3,disString);


                String price = maintenanceServices.get(position).getPrice() + context.getResources().getString(R.string.unit2);
                remoteView.setTextViewText(R.id.tv_price_3,price);

            }


            return remoteView;
        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onCreate() {
            firebaseDatabase =FirebaseDatabase.getInstance();
            if (userUid != null ){
                if (!userUid.equals("")) {
                    databaseReference = firebaseDatabase.getReference().child(userUid);
                    attachDatabaseReadListener();
                }
            }

        }


        @Override
        public void onDataSetChanged() {





        }


        @Override
        public void onDestroy() {
        }

        private void attachDatabaseReadListener() {
            if (childEventListener == null) {
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        maintenanceServices.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            MaintenanceService maintenanceService = dataSnapshot1.getValue(MaintenanceService.class);
                            if (maintenanceService.isState())
                                maintenanceServices.add(maintenanceService);

                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        maintenanceServices.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            MaintenanceService maintenanceService = dataSnapshot1.getValue(MaintenanceService.class);
                            if (maintenanceService.isState())
                                maintenanceServices.add(maintenanceService);

                        }
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

    }
}