package android.behnamnasehi.chatApp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.databinding.ActivityLoginBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.User;
import android.behnamnasehi.chatApp.model.response.ResponseMain;
import android.behnamnasehi.chatApp.network.RetrofitClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthLoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    public static void start(Context context) {
        Intent starter = new Intent(context, AuthLoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLogin();
            }
        });
        binding.txtNotRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthRegisterActivity.start(AuthLoginActivity.this);
            }
        });

    }

    private void requestLogin() {
        final User user = new User();
        user.setUsername(binding.edUsername.getText().toString());
        user.setPassword(binding.edPassword.getText().toString());
         RetrofitClient.getNetworkConfiguration().requestPostLogin(user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        new SessionManager(AuthLoginActivity.this).setPublicKey(data.getData().getUser().getPublicKey());
                        new SessionManager(AuthLoginActivity.this).setUniqueIdentifier(data.getData().getUser().getId());
                        new SessionManager(AuthLoginActivity.this).setLogin(true);
                        MainActivity.start(AuthLoginActivity.this);
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                        Toast.makeText(AuthLoginActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Toast.makeText(AuthLoginActivity.this, "something went wrong :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
