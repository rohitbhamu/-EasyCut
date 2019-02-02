package com.example.rohitkumarbhamu.easycut;

import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class BarberSignIn extends AppCompatActivity {

    EditText phoneNumber,varificationCode;  //editext for enter the number and varification code
   // String indiannumber;//for adding +91 in beginning of the number
    String number;
    private String varificationID;          //for assigning the code that is sent by the firebase to compare with entered code
    Button startButton,varifyButton,resendCodeButton;
    private FirebaseAuth mAuth ; //firebase auth object
    private ProgressBar progressBar ; //for showing progress during the varification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_sign_in);

        phoneNumber =(EditText)findViewById(R.id.phonenumber_editText);
        varificationCode=(EditText)findViewById(R.id.varification_code_editText);

        //indiannumber="+91" +number; //for indian number it need to start with +91

        mAuth= FirebaseAuth.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.authenticationProgressBar);

        startButton = (Button)findViewById(R.id.button_start_verification);
        varifyButton=(Button)findViewById(R.id.button_verify_phone);
       // resendCodeButton=(Button)findViewById(R.id.button_resend);  //WE will do resend part later




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = phoneNumber.getText().toString();
               /* if (number.length()<13){
                    phoneNumber.setError("Enter a valid number");
                }*/
                sendVarificationCode(number);
            }
        });

        varifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code= varificationCode.getText().toString().trim();

                if (code.isEmpty() || code.length()<6){
                    varificationCode.setError("Enter code...");
                    varificationCode.requestFocus();

                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                varifyCode(code);

            }
        });


    //End of oncreate
    }
    private void varifyCode(String code){
        PhoneAuthCredential credential  = PhoneAuthProvider.getCredential(varificationID,code);
        signInWithCredential(credential);
        

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            Intent intent= new Intent(BarberSignIn.this,BarberProfileActivity.class);
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            Toast.makeText(BarberSignIn.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendVarificationCode(String numbertobevarify){
        progressBar.setVisibility(View.VISIBLE);
        //Log.d(numbertobevarify,"hi");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numbertobevarify,       //number on that we are sending message
                60,             //timespan for code is valid
                 TimeUnit.SECONDS,  //unit of time
                         this, //task executer// paresh has written this instead of TaskExecutors.MainTHread
        mCallback
        );


    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            varificationID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code !=null){
                varificationCode.setText(code);
                varifyCode(code);
            }
            //Another waythis is much simpler
            /*
            signInWithCredential(phoneAuthCredential);
             */
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(BarberSignIn.this,e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent= new Intent(this,BarberProfileActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
