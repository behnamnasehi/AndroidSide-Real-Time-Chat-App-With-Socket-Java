package android.behnamnasehi.chatApp.dataholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER = "user";
    private static final String KEY_PUBLIC_KEY = "public_key";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_CHANNEL_CREATED = "channel_created";
    private static final String KEY_DEF_ADDRESS = "def_address";
    private static final String KEY_BRANCH_INFO = "branch_info_json";
    private static final String KEY_ID = "UniqueIdentifier";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        editor.apply();
    }

    public String getBranchInfo() {
        return pref.getString(KEY_BRANCH_INFO, "");
    }

    public void setBranchInfo(String value) {
        editor.putString(KEY_BRANCH_INFO, value);
        editor.apply();
        editor.commit();
    }

    public void setDefAddress(int id) {
        editor.putInt(KEY_DEF_ADDRESS, id);
        editor.apply();
        editor.commit();
    }

    public int getDefAddress() {
        return pref.getInt(KEY_DEF_ADDRESS, -1);
    }


    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public void setName(String Name) {
        editor.putString(KEY_NAME, Name);
        editor.apply();
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, "");
    }

    public void setMobile(String Mobile) {
        editor.putString(KEY_MOBILE, Mobile);
        editor.apply();
        editor.commit();
    }

    public String getPublicKey() {
        return pref.getString(KEY_PUBLIC_KEY, null);
    }

    public void setPublicKey(String apiKey) {
        editor.putString(KEY_PUBLIC_KEY, apiKey);
        editor.apply();
        editor.commit();
    }

    public String getUniqueIdentifier() {
        return pref.getString(KEY_ID, "");
    }

    public void setUniqueIdentifier(String UniqueIdentifier) {
        editor.putString(KEY_ID, UniqueIdentifier);
        editor.apply();
        editor.commit();
    }

    public String getFcmToken() {
        return pref.getString(KEY_FCM_TOKEN, "");
    }

    public void setFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
        editor.commit();
    }

    public boolean getChannelCreated() {
        return pref.getBoolean(KEY_CHANNEL_CREATED, false);
    }

    public void setChannelCreated(boolean value) {
        editor.putBoolean(KEY_CHANNEL_CREATED, value);
        editor.apply();
        editor.commit();
    }
}
