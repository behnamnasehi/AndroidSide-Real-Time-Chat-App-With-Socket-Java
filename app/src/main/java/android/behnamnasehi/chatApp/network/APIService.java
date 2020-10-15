package android.behnamnasehi.chatApp.network;

import android.behnamnasehi.chatApp.application.AppConfig;
import android.behnamnasehi.chatApp.model.Room;
import android.behnamnasehi.chatApp.model.User;
import android.behnamnasehi.chatApp.model.response.ResponseMain;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @POST(AppConfig.URL_AUTH_LOGIN)
    Call<ResponseMain> requestPostLogin(@Body User user);

    @POST(AppConfig.URL_AUTH_REGISTER)
    Call<ResponseMain> requestPostRegister(@Body User user);

    @GET(AppConfig.URL_ROOM)
    Call<ResponseMain> requestGetRooms(@Header("public_key") String public_key);

    @GET(AppConfig.URL_ROOM_ONE)
    Call<ResponseMain> requestGetRoom(@Header("public_key") String public_key , @Path("room") String room);

    @GET(AppConfig.URL_USER_USERNAME)
    Call<ResponseMain> requestGetUserName(@Header("public_key") String public_key , @Path("username") String username);

    @POST(AppConfig.URL_ROOM)
    Call<ResponseMain> requestPostCreateRoom(@Header("public_key") String public_key , @Body Room room);

    @POST(AppConfig.URL_USER_FCM_KEY)
    Call<ResponseMain> requestGetUpdateFcmKey(@Header("public_key") String public_key , @Body User user);
}
