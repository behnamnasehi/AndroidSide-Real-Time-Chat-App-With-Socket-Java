package android.behnamnasehi.chatApp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.databinding.ActivityRegisterBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.User;
import android.behnamnasehi.chatApp.model.response.ResponseMain;
import android.behnamnasehi.chatApp.network.RetrofitClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    String fcmToken = null;

    public static void start(Context context ) {
        Intent starter = new Intent(context, AuthRegisterActivity.class);
         context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRegister();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        fcmToken = task.getResult().getToken();
                    }
                });

    }

    private void requestRegister() {
        final User user = new User();
        user.setUsername(binding.edUsername.getText().toString());
        user.setName(binding.edName.getText().toString());
        user.setPassword(binding.edPassword.getText().toString());
        user.setDeviceModel(Build.MODEL);
        user.setFcmKey(fcmToken);
         RetrofitClient.getNetworkConfiguration().requestPostRegister(user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    new SessionManager(AuthRegisterActivity.this).setPublicKey(data.getData().getUser().getPublicKey());
                    new SessionManager(AuthRegisterActivity.this).setUniqueIdentifier(data.getData().getUser().getId());
                    new SessionManager(AuthRegisterActivity.this).setLogin(true);
                    MainActivity.start(AuthRegisterActivity.this);
                 } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                        Toast.makeText(AuthRegisterActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Toast.makeText(AuthRegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
