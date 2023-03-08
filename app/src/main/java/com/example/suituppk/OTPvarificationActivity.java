package com.example.suituppk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPvarificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo ;
    public static String verificationId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pvarification);

        userNo = getIntent().getStringExtra("mobile");




        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verify_btn);

        phoneNo.setText("Verification code has been send to +92 " + userNo);




      PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + userNo
                ,60
                , TimeUnit.SECONDS
                , OTPvarificationActivity.this
                , new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPvarificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                       // DeliveryActivity.orderId.setText("Order Id : "  + verificationId);


                    }
                }
        );








        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().trim().isEmpty()){
                    Toast.makeText(OTPvarificationActivity.this, "please enter valid code", Toast.LENGTH_SHORT).show();
                return;
                }

                String code = otp.getText().toString();

                if (verificationId1 != null){


                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId1, code);

                    FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(phoneAuthCredential)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {


                                }
                            });
                }
            }
        });
    }

}