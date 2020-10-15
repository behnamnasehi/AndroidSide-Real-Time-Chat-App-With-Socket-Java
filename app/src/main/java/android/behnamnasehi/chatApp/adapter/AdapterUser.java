package android.behnamnasehi.chatApp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.activity.ChatBoxActivity;
import android.behnamnasehi.chatApp.databinding.CustomRowUserBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Room;
import android.behnamnasehi.chatApp.model.User;
import android.behnamnasehi.chatApp.model.response.ResponseMain;
import android.behnamnasehi.chatApp.network.RetrofitClient;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {

    private List<User> list;
    private Activity activity;

    public AdapterUser(List<User> list, Activity activity) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomRowUserBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.custom_row_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User currentItem = list.get(position);
        holder.binding.txtName.setText(currentItem.getUsername());
        holder.binding.txtStatus.setText(getLastTime(currentItem.getLastSeen()));
    }

    private String getLastTime(long milis) {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomRowUserBinding binding;

        ViewHolder(@NonNull CustomRowUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            requestPostCreateRoom();
        }

        private void requestPostCreateRoom() {
            List<User> userList = new ArrayList<>();
            userList.add(list.get(getItemViewType()));
            Room room = new Room();
            room.setUsers(userList);
            RetrofitClient.getNetworkConfiguration().requestPostCreateRoom(new SessionManager(activity).getPublicKey(), room).enqueue(new Callback<ResponseMain>() {
                @Override
                public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        String room = gson.toJson(response.body().getData().getRoom() , Room.class);
                        ChatBoxActivity.start(activity, room);
                        activity.finish();
                    } else {
                        try {
                            Gson gson = new Gson();
                            ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
                            Toast.makeText(activity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMain> call, Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
