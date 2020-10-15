package android.behnamnasehi.chatApp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.adapter.AdapterMessage;
import android.behnamnasehi.chatApp.databinding.ActivityChatBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Message;
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

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxActivity extends AppCompatActivity {
    private static final String TAG = ChatBoxActivity.class.getSimpleName();
    public static final String ON_MESSAGE_RECEIVED = "android.zeroprojects.mafia.activity.ON_MESSAGE_RECEIVED";
    public RecyclerView myRecylerView;
    public List<Message> MessageList;
    public AdapterMessage chatBoxAdapter;
    private IO.Options IOOption;
    private Socket socket;
    ActivityChatBinding binding;
    public String roomId;
    private Room room;

    public static void start(Context context, String room) {
        Intent starter = new Intent(context, ChatBoxActivity.class);
        starter.putExtra("room", room);
        context.startActivity(starter);
    }

    private BroadcastReceiver onMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();
            Message m = gson.fromJson(intent.getExtras().getString("message"), Message.class);
            MessageList.add(m);
            // add the new updated list to the adapter
            // notify the adapter to update the recycler view
            chatBoxAdapter.notifyItemInserted(MessageList.size());
            //set the adapter for the recycler view
            myRecylerView.smoothScrollToPosition(MessageList.size());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessageReceiver, new IntentFilter(ON_MESSAGE_RECEIVED));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        Gson gson = new Gson();
        room = gson.fromJson(getIntent().getExtras().getString("room"), Room.class);
        joinRoom();
        binding.txtToolbarTitle.setText(room.getUsers().get(0).getUsername());
        binding.txtToolbarStatus.setText(getStatus());

        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.recyclerMessageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        chatBoxAdapter = new AdapterMessage(MessageList, ChatBoxActivity.this);
        myRecylerView.setAdapter(chatBoxAdapter);
        requestRoom();
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if (!binding.edMessage.getText().toString().isEmpty()) {
                    sendMessage();
                    binding.edMessage.setText("");
                }
            }
        });
    }

    private void joinRoom() {
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_ROOM_ID, room.getId());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        startService(service);
    }

    private void sendMessage() {
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("from", new SessionManager(ChatBoxActivity.this).getUniqueIdentifier());
            mainObj.put("room", room.getId());
            mainObj.put("content", binding.edMessage.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE);
        service.putExtra(SocketIOService.EXTRA_DATA, mainObj.toString());
        startService(service);
    }

    private StringBuilder getStatus() {

        StringBuilder title = new StringBuilder();
        if (room.getUsers().size() > 1) {
            for (int i = 0; i < room.getUsers().size(); i++) {
                title.append(room.getUsers().get(i).getUsername()).append(",");
            }
        } else {
            title.append(getLastTime(room.getUsers().get(0).getLastSeen()));
        }
        return title;
    }

    private String getLastTime(long milis) {
        if (milis == 0) {
            return "Online";
        }
        int day = 0;
        int hour = 0;
        long now = System.currentTimeMillis();
        long diff = now - milis;
        int minutes = (int) (diff / 1000) / 60;
        if (minutes > 60) {
            hour = minutes / 60;
            if (hour > 24) {
                day = hour / 24;
                return "last seen " + day + " day ago";
            } else {
                return "last seen " + hour + " hour ago";
            }
        } else {
            return "last seen " + minutes + " minutes ago";
        }

    }

    private void requestRoom() {
        RetrofitClient.getNetworkConfiguration().requestGetRoom(new SessionManager(this).getPublicKey(), room.getId()).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    Room room = response.body().getData().getRoom();
                    List<Message> messages = response.body().getData().getMessageList();
                    MessageList.addAll(messages);
                    // add the new updated list to the adapter
                    // notify the adapter to update the recycler view
                    chatBoxAdapter.notifyItemInserted(MessageList.size());
                    //set the adapter for the recycler view
                    myRecylerView.smoothScrollToPosition(MessageList.size());

                } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                        Toast.makeText(ChatBoxActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Toast.makeText(ChatBoxActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMessageReceiver);
    }
}
