package android.behnamnasehi.chatApp.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.activity.ChatBoxActivity;
import android.behnamnasehi.chatApp.databinding.CustomRowChatBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Room;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AdapterRoom extends RecyclerView.Adapter<AdapterRoom.ViewHolder> {

    private List<Room> list;
    private Activity activity;

    public AdapterRoom(List<Room> list, Activity activity) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomRowChatBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.custom_row_chat, parent, false);
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
        Room currentItem = list.get(position);
        StringBuilder title = new StringBuilder();
        for (int i = 0; i < currentItem.getUsers().size(); i++) {
            title.append(currentItem.getUsers().get(i).getUsername()).append(",");
        }
        holder.binding.txtTitle.setText(title);
        holder.binding.txtLastMessage.setText(currentItem.getLastMessage().getContent());
        int[] androidColors = activity.getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[2];
        holder.binding.imgAvatar.setBackgroundColor(randomAndroidColor);
        if (!currentItem.getLastMessage().getId().equals(new SessionManager(activity).getUniqueIdentifier())){
            holder.binding.viewNewMessage.setVisibility(currentItem.getLastMessage().getReadStatus() == 1 ? View.VISIBLE : View.GONE);
        }else {
            holder.binding.viewNewMessage.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.imgAvatar.setClipToOutline(true);
        }
    }

    public List<Room> getRoomList() {
        return this.list;
    }

    public void updateReceiptsList(List<Room> newlist) {
        list.clear();
        Toast.makeText(activity, "" + list.size(), Toast.LENGTH_SHORT).show();
        list = newlist;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomRowChatBinding binding;

        ViewHolder(@NonNull CustomRowChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Gson gson = new Gson();
            String room = gson.toJson(list.get(getItemViewType()), Room.class);
            ChatBoxActivity.start(activity, room);
        }
    }
}
