package com.example.suituppk;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class Resetpassword extends Fragment {


    private EditText regiteredmail;
    private Button resetpassword;
    private TextView goback;
    private FrameLayout parenttFrameLayout;
    private FirebaseAuth firebaseAuth;
    private ViewGroup email_icon_container;
    private ImageView emailicon;
    private TextView emailicontext;
    private ProgressBar progressBar;


    public Resetpassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_resetpassword, container, false);
        regiteredmail = view.findViewById(R.id.forgot_password_email);
        resetpassword = view.findViewById(R.id.Reset_password_btn);
        goback = view.findViewById(R.id.forget_password_goback);
        parenttFrameLayout = getActivity().findViewById(R.id.register_frame_layout);
        email_icon_container = view.findViewById(R.id.forgot_password_linearLayout);
        emailicon = view.findViewById(R.id.forgot_password_icon);
        emailicontext = view.findViewById(R.id.forgot_password_icon_text);
        progressBar = view.findViewById(R.id.forgot_password_progressbar);


        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        regiteredmail.addTextChangedListener(new TextWatcher() {
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

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(email_icon_container);
                emailicontext.setVisibility(View.GONE);


                TransitionManager.beginDelayedTransition(email_icon_container);
                emailicon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetpassword.setEnabled(false);
                resetpassword.setTextColor(Color.argb(50,255,255,255));
                firebaseAuth.sendPasswordResetEmail(regiteredmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){

                               ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailicon.getWidth()/2,emailicon.getHeight()/2);
                               scaleAnimation.setDuration(100);
                               scaleAnimation.setInterpolator(new AccelerateInterpolator());
                               scaleAnimation.setRepeatMode(Animation.REVERSE);
                               scaleAnimation.setRepeatCount(1);

                               scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                                   @Override
                                   public void onAnimationStart(Animation animation) {

                                   }

                                   @Override
                                   public void onAnimationEnd(Animation animation) {

                                       emailicontext.setText("Recovery email sent successfully! check your inbox");
                                       emailicontext.setTextColor(getResources().getColor(R.color.green));

                                       TransitionManager.beginDelayedTransition(email_icon_container);
                                       emailicontext.setVisibility(View.VISIBLE);
                                   }

                                   @Override
                                   public void onAnimationRepeat(Animation animation) {

                                       emailicon.setImageResource(R.drawable.green_mail);
                                   }




                               });

                               emailicon.startAnimation(scaleAnimation);

                           }else {
                               String error = task.getException().getMessage();


                               emailicontext.setText(error);
                               emailicontext.setTextColor(getResources().getColor(R.color.colorPrimary));
                               TransitionManager.beginDelayedTransition(email_icon_container);
                               emailicontext.setVisibility(View.VISIBLE);
                           }
                                progressBar.setVisibility(view.GONE);
                                resetpassword.setEnabled(true);
                                resetpassword.setTextColor(Color.rgb(255,255,255));
                            }
                        });

            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setfragment(new SigninFragment());
            }
        });



    }

private void checkinputs(){

        if(TextUtils.isEmpty(regiteredmail.getText())){

            resetpassword.setEnabled(false);
            resetpassword.setTextColor(Color.argb(50,255,255,255));

        }else {

            resetpassword.setEnabled(true);
            resetpassword.setTextColor(Color.rgb(255,255,255));

        }


}
    private void setfragment(Fragment  fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parenttFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }



}
