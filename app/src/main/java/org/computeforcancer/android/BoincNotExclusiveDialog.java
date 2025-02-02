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
package org.computeforcancer.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Shows dialog to exit, if another BONIC based application detected on device.
 */
public class BoincNotExclusiveDialog extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		 Answers.getInstance().logCustom(new CustomEvent("Unable to Compute")
		 .putCustomAttribute("reason", "other_app"));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage(getString(R.string.nonexcl_dialog_text))
		    .setCancelable(false)
		    .setTitle(getString(R.string.nonexcl_dialog_header))
		    .setNeutralButton(getString(R.string.nonexcl_dialog_exit), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            finish();
		        }
		    }).show();
	 }
}
