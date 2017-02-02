package org.computeforcancer.android.fbLogin;

import com.google.gson.annotations.SerializedName;

/**
 * Created by artem on 24.01.17.
 */

public class UserData {

    private String email;

    @SerializedName("wcg_username")
    private String username;

    @SerializedName("wcg_password")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
