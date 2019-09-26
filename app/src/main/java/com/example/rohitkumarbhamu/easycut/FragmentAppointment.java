package com.example.rohitkumarbhamu.easycut;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentAppointment extends Fragment  {
    EditText editTextCustomerName,editTextTime,editTextServiceName,editTextEstimatedTime;
    Button addAppointmentButton;

    DatabaseReference databaseAppointments;

    ListView listViewAppointments;
    List<Appointment> appointments;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextCustomerName=(EditText) getView().findViewById(R.id.customerNameET);
        editTextTime=(EditText) getView().findViewById(R.id.timeET);
        editTextServiceName=(EditText) getView().findViewById(R.id.serviceNameET);
        editTextEstimatedTime=(EditText) getView().findViewById(R.id.estimatedTimeET);
        addAppointmentButton = (Button)getView().findViewById(R.id.addapointmentButton);

        listViewAppointments = (ListView) getView().findViewById(R.id.listViewAppointments);
        appointments = new ArrayList<>();

        databaseAppointments=FirebaseDatabase.getInstance().getReference("appointments");
        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAppoinment();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        databaseAppointments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                appointments.clear();
                for (DataSnapshot appointmentSnapshot: dataSnapshot.getChildren()){
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                    appointments.add(appointment);
                }
                //TODO here is problem because of getActivity it is doing fetching but when we change the data it crashes

                AppointmentList adapter = new AppointmentList(getActivity(),appointments);
                listViewAppointments.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addAppoinment(){
        String name= editTextCustomerName.getText().toString().trim();
        String time= editTextTime.getText().toString().trim();
        String service= editTextServiceName.getText().toString().trim();
        String estimatedTime= editTextEstimatedTime.getText().toString().trim();


        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(time)&&!TextUtils.isEmpty(service)&&!TextUtils.isEmpty(estimatedTime) ){


          String id=  databaseAppointments.push().getKey();
          Appointment appointment = new Appointment(id,name,time,service,estimatedTime);

          databaseAppointments.child(id).setValue(appointment);

          Toast.makeText(getActivity(),"Appointment added successfully",Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(getActivity(), "You should enter all the fields", Toast.LENGTH_SHORT).show();
        }

    }

}
