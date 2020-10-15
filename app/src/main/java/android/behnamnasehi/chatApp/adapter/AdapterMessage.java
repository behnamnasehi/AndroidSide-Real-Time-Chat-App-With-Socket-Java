package android.behnamnasehi.chatApp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.dataholder.SessionManager;
import android.behnamnasehi.chatApp.model.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter {
    private List<Message> MessageList;
    private Activity activity;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SERVER = 3;


    public AdapterMessage(List<Message> MessagesList, Activity activity) {
        this.MessageList = MessagesList;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        Message message = MessageList.get(position);

        if (message.getEventType() == 2) {
            return VIEW_TYPE_MESSAGE_SERVER;
        }
        if (message.getFrom().getId().equals(new SessionManager(activity).getUniqueIdentifier())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_message_send, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SERVER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_chat_server_message, parent, false);
                return new ServerMessageHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = MessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, position);
                break;
            case VIEW_TYPE_MESSAGE_SERVER:
                ((ServerMessageHolder) holder).bind(message);
                break;
        }

    }

    private class ServerMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent;

        ServerMessageHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
        }

        void bind(Message message) {
            txtContent.setText(message.getContent());

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtTime;

        SentMessageHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtMessageContent);
            txtTime = (TextView) itemView.findViewById(R.id.txtMessageTime);
        }

        void bind(Message message) {
            txtContent.setText(message.getContent());
            txtTime.setText(convertSecondsToHMmSs(message.getCreatedAt()));
            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }

        public String convertSecondsToHMmSs(long seconds) {
            long s = seconds % 60;
            long m = (seconds / 60) % 60;
            long h = (seconds / (60 * 60)) % 24;
            return String.format("%d:%02d:%02d", h, m, s);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtTime, txtUserName;
        ImageView imageView;
        ReceivedMessageHolder(View itemView) {
            super(itemView);

            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            imageView = (ImageView) itemView.findViewById(R.id.imgProfile);
        }

        void bind(Message message, int position) {
            if (position > 0) {
                Message previousMessage = MessageList.get(position - 1);
                if (previousMessage.getFrom() != null) {
                    if (previousMessage.getFrom().getId().equals(message.getFrom().getId())) {
                        txtUserName.setVisibility(View.GONE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
            txtContent.setText(message.getContent());
            txtUserName.setText(message.getFrom().getUsername());
            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));

            txtTime.setText(convertSecondsToHMmSs(message.getCreatedAt()));

        }

        public String convertSecondsToHMmSs(long seconds) {
            long s = seconds % 60;
            long m = (seconds / 60) % 60;
            long h = (seconds / (60 * 60)) % 24;
            return String.format("%d:%02d:%02d", h, m, s);
        }
    }


}