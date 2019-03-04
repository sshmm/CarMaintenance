package com.example.android.carmaintenance;

public class MaintenanceService {
    private String serviceName;
    private int periodicity;
    private int lastTime;
    private boolean state;
    private double price;


    public MaintenanceService(){

    }

    public MaintenanceService(String serviceName, int periodicity, int lastTime, boolean state, double price) {
        this.serviceName = serviceName;
        this.periodicity = periodicity;
        this.lastTime = lastTime;
        this.state = state;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
