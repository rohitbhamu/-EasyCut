package com.example.rohitkumarbhamu.easycut;

public class Appointment {
    String appointmentId;
    String customerName;
    String timeofAppointment;
    String serviceName;
    String estimatedTime;

    public Appointment(){}

    public Appointment(String appointmentId, String customerName, String timeofAppointment, String serviceName, String estimatedTime) {
        this.appointmentId = appointmentId;
        this.customerName = customerName;
        this.timeofAppointment = timeofAppointment;
        this.serviceName = serviceName;
        this.estimatedTime = estimatedTime;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getTimeofAppointment() {
        return timeofAppointment;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }
}
