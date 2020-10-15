package android.behnamnasehi.chatApp.network;

import android.behnamnasehi.chatApp.application.AppConfig;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    @NonNull
    public static Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    @NonNull
    private static OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.RESTRICTED_TLS, ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .readTimeout(AppConfig.REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(AppConfig.REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    public static APIService getNetworkConfiguration() {
        return RetrofitClient.getInstance().create(APIService.class);
    }
}
