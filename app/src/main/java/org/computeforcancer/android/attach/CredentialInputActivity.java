/*******************************************************************************
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2012 University of California
 * 
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * 
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.computeforcancer.android.attach;

import java.util.ArrayList;
import org.computeforcancer.android.R;
import org.computeforcancer.android.fragments.AbstractBaseFragment;
import org.computeforcancer.android.fragments.FragmentsManager;
import org.computeforcancer.android.utils.*;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class CredentialInputActivity extends AppCompatActivity {

	private ProjectAttachService attachService = null;
	private boolean asIsBound = false;
	private FragmentsManager mFragmentsManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity onCreate"); 
        doBindService();
        setContentView(R.layout.credential_input_activity);
		mFragmentsManager = new FragmentsManager(this);
        
        /*CheckBox showPwdCb = (CheckBox) findViewById(R.id.show_pwd_cb);
        showPwdCb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					pwdET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
				} else {
					pwdET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					pwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
        });*/
    }
	
	@Override
	protected void onDestroy() {
		doUnbindService();
		super.onDestroy();
	}

	public void signIn(String email, String user, String pwd) {
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.continueClicked.");
        
		
		// set credentials in service
		if(asIsBound) {
	        // verfiy input, return if failed.
			if(!attachService.verifyInput(email, user, pwd)) return;
			// set credentials
			attachService.setCredentials(email, user, pwd);
		}
		else {
			if(Logging.ERROR) Log.e(Logging.TAG, "CredentialInputActivity.continueClicked: service not bound.");
			return;
		}

        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.continueClicked: starting BatchProcessingActivity...");
		startActivity(new Intent(this, BatchProcessingActivity.class));
	}	

	public void openFragment(final AbstractBaseFragment fragment, final boolean addToStack) {
		mFragmentsManager.openFragment(fragment, addToStack);
	}
	// triggered by individual button
	/*
	public void individualClicked(View v) {
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.individualClicked.");

		// set credentials in service, in case user typed before deciding btwn batch and individual attach
		if(asIsBound) attachService.setCredentials(emailET.getText().toString(), nameET.getText().toString(), pwdET.getText().toString());
		
		//startActivity(new Intent(this, IndividualAttachActivity.class));
		Intent intent = new Intent(this, BatchConflictListActivity.class);
		intent.putExtra("conflicts", false);
		startActivity(new Intent(this, BatchConflictListActivity.class));
	}*/
	
	private ServiceConnection mASConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been established, getService returns 
	    	// the Monitor object that is needed to call functions.
	        attachService = ((ProjectAttachService.LocalBinder)service).getService();
		    asIsBound = true;
		    
		    ArrayList<String> values = attachService.getUserDefaultValues();
			//TODO
	        //emailET.setText(values.get(0));
	        //nameET.setText(values.get(1));
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	// This should not happen
	    	attachService = null;
	    	asIsBound = false;
	    }
	};
	
	private void doBindService() {
		// bind to attach service
		bindService(new Intent(this, ProjectAttachService.class), mASConnection, Service.BIND_AUTO_CREATE);
	}

	private void doUnbindService() {
	    if (asIsBound) {
	        // Detach existing connection.
	        unbindService(mASConnection);
	        asIsBound = false;
	    }
	}
}
