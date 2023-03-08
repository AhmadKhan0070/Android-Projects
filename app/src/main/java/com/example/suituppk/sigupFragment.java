package com.example.suituppk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sigupFragment extends Fragment {




    public sigupFragment() {
        // Required empty public constructor
    }

    private EditText email;
    private EditText fullname;
    private EditText password;
    private EditText confirmpassword;
    private Button signupbtn;
    private ImageButton closebtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public TextView donthaveanaccount;

    private FrameLayout parenttFrameLayout;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    public static boolean disableCloseBtn = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sigup, container, false);
        donthaveanaccount =  view.findViewById(R.id.have_account);
        email = view.findViewById(R.id.signup_email);
        fullname = view.findViewById(R.id.sign_up_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmpassword = view.findViewById(R.id.sign_up_confirm_password);
        closebtn = view.findViewById(R.id.sign_up_cancel);
        signupbtn = view.findViewById(R.id.Sign_up_btn);
        progressBar = view.findViewById(R.id.signup_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        parenttFrameLayout = getActivity().findViewById(R.id.register_frame_layout);



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
                setfragment(new SigninFragment());
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
        fullname.addTextChangedListener(new TextWatcher() {
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
        confirmpassword.addTextChangedListener(new TextWatcher() {
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



        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    checkemailandpassword();


                    /*
                    Intent otpIntent = new Intent(getContext() , OTPvarificationActivity.class);
                    otpIntent.putExtra("mobile" , mobile);
                    startActivity(otpIntent);


                }
                getActivity().finish();
*/

            }
        });
    }



    private void setfragment(Fragment  fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parenttFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkinputs() {
    if(!TextUtils.isEmpty(email.getText())) {

        if(!TextUtils.isEmpty(fullname.getText())) {

            if(!TextUtils.isEmpty(password.getText())  && password.length() >= 8 ) {

                if(!TextUtils.isEmpty(confirmpassword.getText())  && confirmpassword.length() >= 8 ) {

                        signupbtn.setEnabled(true);
                        signupbtn.setTextColor(Color.rgb(255, 255, 255));

                }else {

                    signupbtn.setEnabled(false);
                    signupbtn.setTextColor(Color.argb(50,255,255,255));
                }

            }else {

                signupbtn.setEnabled(false);
                signupbtn.setTextColor(Color.argb(50,255,255,255));
            }

        }else {

            signupbtn.setEnabled(false);
            signupbtn.setTextColor(Color.argb(50,255,255,255));
        }

    }else {

        signupbtn.setEnabled(false);
        signupbtn.setTextColor(Color.argb(50,255,255,255));
    }
}

        public void checkemailandpassword(){

            if(email.getText().toString().matches(emailPattern)){
                if(password.getText().toString().equals(confirmpassword.getText().toString())){
                progressBar.setVisibility(View.VISIBLE);
                signupbtn.setEnabled(false);
                signupbtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Map<String , Object> userdata = new HashMap<>();
                                     userdata.put("Full Name" , fullname.getText().toString());
                                     userdata.put("email" , email.getText().toString());
                                     userdata.put("profile" , "");

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                                        ///////// Maps
                                                        Map<String,Object> wishlistMap = new HashMap<>();
                                                        wishlistMap.put("list_size" , (long) 0);

                                                        Map<String,Object> ratingMap = new HashMap<>();
                                                        ratingMap.put("list_size" , (long) 0);

                                                        Map<String,Object> cartMap = new HashMap<>();
                                                        cartMap.put("list_size" , (long) 0);

                                                        Map<String,Object> myAddressesMap = new HashMap<>();
                                                        myAddressesMap.put("list_size" , (long) 0);

                                                        Map<String,Object> notificationsMap = new HashMap<>();
                                                        notificationsMap.put("list_size" , (long) 0);

                                                        ///////// Maps



                                                        final List<String> documentNames = new ArrayList<>();
                                                        documentNames.add("MY_WISHLIST");
                                                        documentNames.add("MY_RATINGS");
                                                        documentNames.add("MY_CART");
                                                        documentNames.add("MY_ADDRESSES");
                                                        documentNames.add("MY_NOTIFICATIONS");

                                                        List<Map<String,Object>> documentFields = new ArrayList<>();
                                                        documentFields.add(wishlistMap);
                                                        documentFields.add(ratingMap);
                                                        documentFields.add(cartMap);
                                                        documentFields.add(myAddressesMap);
                                                        documentFields.add(notificationsMap);

                                                        for (int x = 0 ; x < documentNames.size() ; x++ ){
                                                            final int finalX = x;
                                                            userDataReference.document(documentNames.get(x)).set(documentFields.get(x))
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {
                                                                                if (finalX == documentNames.size() - 1){
                                                                                    mainintent();
                                                                            }
                                                                            }else {

                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                signupbtn.setEnabled(true);
                                                                                signupbtn.setTextColor(Color.rgb(255,255,255));
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    }else{
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signupbtn.setEnabled(true);
                                    signupbtn.setTextColor(Color.rgb(255,255,255));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }else {
                confirmpassword.setError("password doesn't match");

            }

        }else{

            email.setError("invalid email!");
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
            //comment
}

}