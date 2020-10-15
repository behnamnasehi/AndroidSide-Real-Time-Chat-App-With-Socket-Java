package android.behnamnasehi.chatApp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.behnamnasehi.chatApp.activity.ChatBoxActivity;
import android.behnamnasehi.chatApp.activity.MainActivity;
import android.behnamnasehi.chatApp.application.AppConfig;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Message;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;


/**
 * Created by planet on 6/8/2017.
 */

public class SocketIOService extends Service implements SocketEventListener.Listener, HeartBeat.HeartBeatListener {
    public static final String KEY_BROADCAST_MESSAGE = "b_message";
    public static final int EVENT_TYPE_JOIN = 1, EVENT_TYPE_MESSAGE = 2, EVENT_TYPE_TYPING = 3;
    private static final String EVENT_MESSAGE = "message";
    private static final String EVENT_CHANGE = "change";
    private static final String EVENT_JOIN = "join";
    private static final String EVENT_RECEIVED = "received";
    private static final String EVENT_TYPING = "typing";
    public static final String EXTRA_DATA = "extra_data_message";
    public static final String EXTRA_ROOM_ID = "extra_room_id";
    public static final String EXTRA_EVENT_TYPE = "extra_event_type";
    private static final String TAG = SocketIOService.class.getSimpleName();
    private Socket mSocket;
    private Boolean isConnected = true;
    private boolean mTyping;
    private Queue<Message> chatQueue;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HeartBeat heartBeat;
    private String room_id;
    private ConcurrentHashMap<String, SocketEventListener> listenersMap;

    //-------------------------------------------------------------------------------------------
    private IO.Options IOOption;
    public static final String EXTRA_EVENT_SEND_MESSAGE = "message_detection";

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    Log.w(TAG, "Connected");

                    break;
                case 2:
                    Log.w(TAG, "Disconnected");

                    break;
                case 3:
                    Log.w(TAG, "Error in Connection");
         
                    break;
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IOOption = new IO.Options();
        IOOption.query = "public_key=" + new SessionManager(getApplicationContext()).getPublicKey();
        chatQueue = new LinkedList<>();
        listenersMap = new ConcurrentHashMap<>();
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread(TAG + "Args",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        try {
            mSocket = IO.socket(AppConfig.BASE_URL, IOOption);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        getSocketListener();

        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.on(entry.getKey(), entry.getValue());
        }
        /*mSocket.on("user joined", new SocketEventListener("user joined", this));
        mSocket.on("user left", new SocketEventListener("user left", this));
        mSocket.on("typing", new SocketEventListener("typing", this));
        mSocket.on("stop typing", new SocketEventListener("stop typing", this));*/
        if (!isConnected && !mSocket.connected()) {
            mSocket.connect();
        }
        heartBeat = new HeartBeat(this);
        heartBeat.start();
    }

    private void getSocketListener() {
        listenersMap.put(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
        listenersMap.put(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
        listenersMap.put(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
        listenersMap.put(Socket.EVENT_CONNECT_TIMEOUT, new SocketEventListener(Socket.EVENT_CONNECT_TIMEOUT, this));
        listenersMap.put(EVENT_MESSAGE, new SocketEventListener(EVENT_MESSAGE, this));
         listenersMap.put(EVENT_CHANGE, new SocketEventListener(EVENT_CHANGE, this));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (intent != null) {
            int eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, EVENT_TYPE_JOIN);

            switch (eventType) {
                case EVENT_TYPE_JOIN:
                    room_id = intent.getStringExtra(EXTRA_ROOM_ID);
                    if (!mSocket.connected()) {
                        mSocket.connect();
                    }
                    joinChat();
                    break;
                case EVENT_TYPE_MESSAGE:
                    String chat = intent.getExtras().getString(EXTRA_DATA);
                    if (isSocketConnected()) {
                        sendMessage(chat);
                    }
                    break;
                case EVENT_TYPE_TYPING:
                    if (isSocketConnected()) {
//                        sendMessage(chat, eventType);
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private boolean isSocketConnected() {
        if (null == mSocket) {
            return false;
        }
        if (!mSocket.connected()) {
            mSocket.connect();
            Log.i(TAG, "reconnecting socket...");
            return false;
        }
        return true;
    }

    @Override
    public void onHeartBeat() {
        Log.e(TAG, "onHeartBeat: " );
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
        }
    }

    private void joinChat() {
        if (TextUtils.isEmpty(room_id)) {
            //null can not join chat
            return;
        }
        mSocket.emit(EVENT_JOIN, room_id);
    }

    private void sendMessage(String messageObject) {
        JSONObject chat = null;
        try {
            chat = new JSONObject(messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(EXTRA_EVENT_SEND_MESSAGE, chat);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        heartBeat.stop();
        room_id = null;
        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.off(entry.getKey(), entry.getValue());
        }
    }


    @Override
    public void onEventCall(String event, Object... args) {
        JSONObject data;
        Intent intent;
        switch (event) {
            case Socket.EVENT_CONNECT:
                android.os.Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = 1;
                mServiceHandler.sendMessage(msg);
                isConnected = true;
                intent = new Intent(MainActivity.ON_SOCKET_CONNECTION);
                intent.putExtra("status", true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case Socket.EVENT_DISCONNECT:
                Log.w(TAG, "socket disconnected");
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 2;
                mServiceHandler.sendMessage(msg);
                intent = new Intent(MainActivity.ON_SOCKET_CONNECTION);
                intent.putExtra("status", false);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case Socket.EVENT_CONNECT_ERROR:
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 3;
                mServiceHandler.sendMessage(msg);
                // reconnect
                mSocket.connect();
                break;
            case Socket.EVENT_CONNECT_TIMEOUT:
                if (!mTyping) return;
                mTyping = false;
                mSocket.emit("stop typing");
                break;
            case EVENT_MESSAGE:
                data = (JSONObject) args[0];
                intent = new Intent(ChatBoxActivity.ON_MESSAGE_RECEIVED);
                intent.putExtra("message", data.toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case EVENT_CHANGE:
                data = (JSONObject) args[0];
                intent = new Intent(MainActivity.ON_CHANGE_DATA_RECEIVER);
                intent.putExtra("room", data.toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
        }
    }
}
