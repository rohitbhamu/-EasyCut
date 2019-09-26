package com.example.rohitkumarbhamu.easycut;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppointmentList extends ArrayAdapter<Appointment> {
    private Activity context;
    private List<Appointment> appointmentList;

    public AppointmentList (Activity context,List<Appointment> appointmentList){

        super(context,R.layout.appointment_list_layout,appointmentList);
        this.context =context;
         this.appointmentList=appointmentList;


    }

    @NonNull
    @Override
    public View getView(int position,  @Nullable View convertView,  @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem= inflater.inflate(R.layout.appointment_list_layout,null,true);

        TextView tvName=(TextView)listViewItem.findViewById(R.id.tvName);
        TextView tvTime=(TextView)listViewItem.findViewById(R.id.tvTime);
        TextView tvService=(TextView)listViewItem.findViewById(R.id.tvService);
        TextView tvEstimatedTime=(TextView)listViewItem.findViewById(R.id.tvEstimatedTime);


        Appointment appointment =appointmentList.get(position);

        tvName.setText(appointment.getCustomerName());
        tvTime.setText(appointment.getTimeofAppointment());
        tvService.setText(appointment.getServiceName());
        tvEstimatedTime.setText(appointment.getEstimatedTime());


        return listViewItem;
    }
}
