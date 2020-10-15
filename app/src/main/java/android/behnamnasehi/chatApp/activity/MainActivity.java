package android.behnamnasehi.chatApp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.adapter.AdapterRoom;
import android.behnamnasehi.chatApp.databinding.ActivityMainBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Room;
import android.behnamnasehi.chatApp.model.response.ResponseMain;
import android.behnamnasehi.chatApp.network.RetrofitClient;
import android.behnamnasehi.chatApp.service.SocketIOService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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


public class MainActivity extends AppCompatActivity {
    public static final String ON_CHANGE_DATA_RECEIVER = "android.zeroprojects.mafia.activity.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "android.zeroprojects.mafia.activity.ON_SOCKET_CONNECTION";

    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    private TextView textView;
    private Button button;
    private static final String EVENT_JOIN = "join";
    private long startTime;
    private long latency = 0;
    private RecyclerView recyclerRoom;
    private AdapterRoom adapterRoom;
    private List<Room> roomList;
    private List<Room> roomListDB;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    private BroadcastReceiver onChangeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();
            Room r = gson.fromJson(intent.getExtras().getString("room"), Room.class);
            for (int i = 0; i < adapterRoom.getRoomList().size(); i++) {
                if (adapterRoom.getRoomList().get(i).getId().equals(r.getId())) {
                    Room room = adapterRoom.getRoomList().get(i);
                    room.setLastMessage(r.getLastMessage());
                    adapterRoom.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    private BroadcastReceiver onSocketConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getExtras().getBoolean("status");
            binding.txtToolbarTitle.setText(isConnected ? "Connected" : "Connecting...");

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(onChangeDataReceiver, new IntentFilter(ON_CHANGE_DATA_RECEIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(onSocketConnect, new IntentFilter(ON_SOCKET_CONNECTION));
        if (adapterRoom != null) adapterRoom.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onChangeDataReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSocketConnect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        recyclerConfiguration();
        requestRooms();

        Intent service = new Intent(this, SocketIOService.class);
        startService(service);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), 1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {

        }
    }

    private void recyclerConfiguration() {
        recyclerRoom = binding.recyclerRooms;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerRoom.setLayoutManager(mLayoutManager);
        recyclerRoom.setItemAnimator(new DefaultItemAnimator());
        roomList = new ArrayList<>();

    }

    private void setChatAdapter(List<Room> rooms) {
        adapterRoom = new AdapterRoom(rooms, this);
        recyclerRoom.setAdapter(adapterRoom);
    }

    private void requestRooms() {
        RetrofitClient.getNetworkConfiguration().requestGetRooms(new SessionManager(this).getPublicKey()).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    roomList.addAll(response.body().getData().getRoomList());
                    setChatAdapter(response.body().getData().getRoomList());
                } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                        Toast.makeText(MainActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}