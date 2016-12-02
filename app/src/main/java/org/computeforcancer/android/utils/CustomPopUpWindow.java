package org.computeforcancer.android.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import org.computeforcancer.android.R;

/**
 * Created by Peter on 02.12.2016.
 */

public class CustomPopUpWindow {
    ProgressDialog dialog;
    public CustomPopUpWindow(Context context, String status){
        dialog = new ProgressDialog(context);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
        dialog.setContentView(R.layout.main_popup_window);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(true);
    }
    public void stop(){
        dialog.dismiss();
    }
}
