package model;


import viewModel.TimeSeries;

public interface SimulatorModel {
    boolean connectToServer(String ip, double port);
    void displayFlight(boolean isConnected);
    void setTimeSeries(TimeSeries ts, String tsType);
}
