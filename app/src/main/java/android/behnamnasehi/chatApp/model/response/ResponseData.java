package android.behnamnasehi.chatApp.model.response;

import android.behnamnasehi.chatApp.model.Message;
import android.behnamnasehi.chatApp.model.Room;
import android.behnamnasehi.chatApp.model.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseData {

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("room")
    @Expose
    private Room room;

    @SerializedName("rooms")
    @Expose
    private List<Room> roomList;

    @SerializedName("messages")
    @Expose
    private List<Message> messageList;

    @SerializedName("users")
    @Expose
    private List<User> usersList;

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
