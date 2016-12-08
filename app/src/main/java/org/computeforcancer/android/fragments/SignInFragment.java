package org.computeforcancer.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 01.12.2016.
 */
public class SignInFragment extends AbstractBaseFragment {

    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_NAME = "key_name";
    private static final String TAG = SignInFragment.class.getSimpleName();

    private EditText emailET;
    private EditText nameET;
    private EditText pwdET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sign_in_layout, viewGroup, false);

        Button signInButton = (Button) mRootView.findViewById(R.id.sil_sign_in_b);
        TextView forgotPasswordTv = (TextView) mRootView.findViewById(R.id.tvForgotPassword);
        Bundle arguments = getArguments();
        String email = arguments.getString(KEY_EMAIL);
        String name = arguments.getString(KEY_NAME);
        emailET = (EditText) mRootView.findViewById(R.id.sil_email_et);
        nameET = (EditText) mRootView.findViewById(R.id.sil_name_et);
        pwdET = (EditText) mRootView.findViewById(R.id.sil_password_et);
        if (email != null && !email.isEmpty()) {
            emailET.setText(email);
        }
        if (name != null && !name.isEmpty()) {
            nameET.setText(name);
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CredentialInputActivity)getActivity()).signIn(emailET.getText().toString(), nameET.getText().toString(), pwdET.getText().toString());
            }
        });
        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.worldcommunitygrid.org/login/forgotPassword.do"));
                startActivity(browserIntent);
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
