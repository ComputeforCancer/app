package org.computeforcancer.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 01.12.2016.
 */
public class SignInFragment extends AbstractBaseFragment {

    private static final String TAG = SignInFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sign_in_layout, viewGroup, false);

        Button signInButton = (Button) mRootView.findViewById(R.id.sil_sign_in_b);
        final EditText emailET = (EditText) mRootView.findViewById(R.id.sil_email_et);
        final EditText nameET = (EditText) mRootView.findViewById(R.id.sil_name_et);
        final EditText pwdET = (EditText) mRootView.findViewById(R.id.sil_password_et);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CredentialInputActivity)getActivity()).signIn(emailET.getText().toString(), nameET.getText().toString(), pwdET.getText().toString());
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
