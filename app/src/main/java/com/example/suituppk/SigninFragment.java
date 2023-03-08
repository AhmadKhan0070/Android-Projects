 package com.example.suituppk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.suituppk.RegisterActivity.resetpasswordfragment;

 public class SigninFragment extends Fragment {




    public SigninFragment() {
        // Required empty public constructor
    }


    public TextView donthaveanaccount;
    private FrameLayout parenttFrameLayout;
    private EditText email;
    private EditText password;
    private ImageButton closebtn;
    private Button signinbtn;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private ProgressBar progressBar;
    private TextView forgotpassword;
    public static boolean disableCloseBtn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        donthaveanaccount = (TextView) view.findViewById(R.id.no_account);
        parenttFrameLayout = getActivity().findViewById(R.id.register_frame_layout);
        email = view.findViewById(R.id.sign_in_EmailAddress);
        password = view.findViewById(R.id.Sign_in_password);
        progressBar = view.findViewById(R.id.signin_progressBar);
        signinbtn = view.findViewById(R.id.Sign_in_btn);
        closebtn = view.findViewById(R.id.sign_in_close_btn);
        forgotpassword = view.findViewById(R.id.Sign_in_forgot);
        firebaseAuth = FirebaseAuth.getInstance();

        if (disableCloseBtn){
            closebtn.setVisibility(View.GONE);
        }else {
            closebtn.setVisibility(View.VISIBLE);
        }

        return view;

    }

     @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         donthaveanaccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 setfragment(new sigupFragment());
             }

         });
         forgotpassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 resetpasswordfragment = true;
                 setfragment(new Resetpassword());
             }
         });

         closebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mainintent();
             }
         });

         email.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 checkinputs();
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
         password.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 checkinputs();
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });

         signinbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 checkemailandpassword();
             }
         });




     }


    private void setfragment(Fragment  fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parenttFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }


    private void checkinputs(){

        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(password.getText())){

                signinbtn.setEnabled(true);
                signinbtn.setTextColor(Color.rgb(255,255,255));

            }else {
                signinbtn.setEnabled(false);
                signinbtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            signinbtn.setEnabled(false);
            signinbtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

     private void checkemailandpassword(){
        if ((email.getText().toString().matches(emailPattern))){
            if(password.length() >= 8){

                progressBar.setVisibility(View.VISIBLE);
                signinbtn.setEnabled(false);
                signinbtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                  mainintent();
                              }else{
                                  progressBar.setVisibility(View.INVISIBLE);
                                  signinbtn.setEnabled(true);
                                  signinbtn.setTextColor(Color.rgb(255,255,255));
                                  String error = task.getException().getMessage();
                                  Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                              }
                            }
                        });

            }else{
                Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();

        }
     }


     private void mainintent(){

        if (disableCloseBtn){
            disableCloseBtn = false;
        }else {
            Intent mainintent = new Intent(getActivity(), Home_Activity.class);
            startActivity(mainintent);
        }
         getActivity().finish();

     }
}