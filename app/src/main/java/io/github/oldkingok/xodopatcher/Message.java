package io.github.oldkingok.xodopatcher;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 便于向用户发送Toast消息
 */
public class Message {
    public Context instance;

    public void setContext(Context instance) {
        this.instance = instance;
    }

    public void sendToast(CharSequence text) {
        sendToast(text, Toast.LENGTH_SHORT);
    }

    public void sendToast(CharSequence text, int duration) {
        sendToast(instance, text, duration);
    }

    public void sendToast(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }
}
