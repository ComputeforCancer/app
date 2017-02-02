package org.computeforcancer.android.fbLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by artem on 24.01.17.
 */

public interface NetApiService {

    @GET("/identity/wcg")
    Call<UserData> getUserData(@Query("token") String token);

}
