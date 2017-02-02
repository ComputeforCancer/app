package org.computeforcancer.android.fbLogin;

import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.computeforcancer.android.R;
import org.computeforcancer.android.fragments.AbstractBaseFragment;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by artem on 24.01.17.
 */

public class FbLoginManager implements FacebookCallback<LoginResult>, Callback<UserData> {

    public static String END_POINT = "http://pure-dawn-88767.herokuapp.com";

    private final NetApiService mNetApiService;
    private Converter<ResponseBody, NetworkError> mErrorConverter;

    private ResultDispatcher mUiResultDispatcher;

    public FbLoginManager() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(END_POINT)
                .client(client)
                .build();

        mNetApiService = retrofit.create(NetApiService.class);
        mErrorConverter = retrofit.responseBodyConverter(NetworkError.class, new Annotation[0]);

    }

    public void setDispatcher(ResultDispatcher resultDispatcher) {
        mUiResultDispatcher = resultDispatcher;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Call<UserData> call= mNetApiService.getUserData(loginResult.getAccessToken().getToken());
        call.enqueue(FbLoginManager.this);
        logOut();
    }

    @Override
    public void onCancel() {
        if (mUiResultDispatcher != null && mUiResultDispatcher instanceof AbstractBaseFragment) {
            String result = ((AbstractBaseFragment) mUiResultDispatcher).getActivity().getString(R.string.fb_login_canceled);
            mUiResultDispatcher.dispatchError(result);
        }

    }

    @Override
    public void onError(FacebookException error) {
        if (mUiResultDispatcher != null && mUiResultDispatcher instanceof AbstractBaseFragment) {
            String result = ((AbstractBaseFragment) mUiResultDispatcher).getActivity().getString(R.string.message_smth_wrong);
            mUiResultDispatcher.dispatchError(result);
        }
    }

    public void logOut() {
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onResponse(Call<UserData> call, Response<UserData> response) {


        if (!response.isSuccessful() && response.errorBody() != null) {
            NetworkError error;
            try {
                error = mErrorConverter.convert(response.errorBody());
                if (mUiResultDispatcher != null) {
                    mUiResultDispatcher.dispatchError(error.getErrorMsg());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            UserData userData = response.body();
            Log.e(FbLoginManager.class.getName(), "email " + userData.getEmail() + " name " + userData.getUserName() + " password " +userData.getPassword());
            mUiResultDispatcher.dispatchSuccessResult(response.body());
        }

    }

    @Override
    public void onFailure(Call<UserData> call, Throwable t) {
        if (mUiResultDispatcher != null && mUiResultDispatcher instanceof AbstractBaseFragment) {
            String result = ((AbstractBaseFragment) mUiResultDispatcher).getActivity().getString(R.string.message_smth_wrong);
            mUiResultDispatcher.dispatchError(result);
        }
    }
}