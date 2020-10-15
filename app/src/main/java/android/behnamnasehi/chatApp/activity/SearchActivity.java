package android.behnamnasehi.chatApp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.adapter.AdapterUser;
import android.behnamnasehi.chatApp.databinding.ActivitySearchUserBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.User;
import android.behnamnasehi.chatApp.model.response.ResponseMain;
import android.behnamnasehi.chatApp.network.RetrofitClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements TextWatcher {
   private ActivitySearchUserBinding binding;
   private List<User> userList;
   private RecyclerView myRecylerView;
   private AdapterUser adapterUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_search_user);
        binding.edSearch.addTextChangedListener(this);
        recyclerConfiguration();
    }

    private void recyclerConfiguration(){
        myRecylerView = binding.recyclerUser;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void requestGetUsers(String query){
        RetrofitClient.getNetworkConfiguration().requestGetUserName(new SessionManager(this).getPublicKey() , query).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body().getData().getUsersList();
                    userList = new ArrayList<>();
                    userList.addAll(users);
                    // add the new updated list to the adapter
                    // notify the adapter to update the recycler view
                    adapterUser = new AdapterUser(userList, SearchActivity.this);
                    myRecylerView.setAdapter(adapterUser);
                  } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                        Toast.makeText(SearchActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = binding.edSearch.getText().toString();
        if (text.length() > 2){
            requestGetUsers(text);
        }
    }
}
