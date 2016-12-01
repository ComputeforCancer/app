package org.computeforcancer.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 30.11.2016.
 */
public class SignInActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_sign_layout);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_b) {
            startActivity(new Intent(this, CredentialInputActivity.class));
        } else if (v.getId() == R.id.get_started_b) {

        }
    }
}
