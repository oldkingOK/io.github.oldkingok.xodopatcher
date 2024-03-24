package io.github.oldkingok.xodopatcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 笔相关
 */
public class Pen {
    /**
     * 用来发送Toast消息
     */
    Message message;
    /**
     * 当前选中的按钮
     */
    int selectedButtonId = 0;
    CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish() {
            message.sendToast("已超时");
            selectedButtonId = 0;
        }
    };
    /**
     * 存储枚举键值对
     */
    Map<Integer, Object> buttonIdMap;

    public Pen(XC_LoadPackage.LoadPackageParam lpparam, Message message) throws Throwable {
        this.message = message;
        // 从枚举中加载对应值
        Class<?> clz = XposedHelpers.findClass("com.pdftron.pdf.widget.toolbar.component.DefaultToolbars$ButtonId",
                lpparam.classLoader);
        Object[] objects = clz.getEnumConstants();
        Method value = clz.getMethod("value");
        buttonIdMap = new HashMap<>();
        for (Object obj : objects) {
            buttonIdMap.put((int) value.invoke(obj), obj);
        }
    }

    public void penHook(XC_LoadPackage.LoadPackageParam lpparam) {
        // Hook 长按按钮方法
        XposedHelpers.findAndHookMethod("com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent$n",
                lpparam.classLoader,
                "onLongClick",
                View.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View)param.args[0];
                        showKeySettingPopup(view);

                        if (selectedButtonId != 0) {
                            // 重置定时器
                            countDownTimer.cancel();
                        }
                        selectedButtonId = view.getId();
                        countDownTimer.start();
                    }
                });

        // Hook 获取按钮枚举
        XposedHelpers.findAndHookMethod("com.pdftron.pdf.utils.ShortcutHelper",
                lpparam.classLoader,
                "getButtonId",
                XposedHelpers.findClass("com.pdftron.pdf.tools.ToolManager", lpparam.classLoader),
                int.class, KeyEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        int id = (int) param.args[1];
                        SharedPreferences sp = message.instance.getSharedPreferences(Consts.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
                        int buttonId = sp.getInt(id + "", 0);
                        if (buttonId != 0) {
                            param.setResult(buttonIdMap.get(buttonId));
                        }
                    }
                });

        // Hook按钮监听
        XposedHelpers.findAndHookMethod("com.pdftron.pdf.controls.PdfViewCtrlTabFragment2",
                lpparam.classLoader,
                "handleKeyUp",
                int.class, KeyEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int id = (int) param.args[0];
                        SharedPreferences sp = message.instance.getSharedPreferences(Consts.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);

                        if (selectedButtonId != 0) {
                            if (isPageUp(id) || isPageDown(id)) {
                                // Cancel
                                param.args[0] = 0;
                                // 提示
                                message.sendToast("设置按键成功！", Toast.LENGTH_SHORT);
                                // 存储设置
                                sp.edit().putInt(id + "", selectedButtonId).apply();
                                // 重置
                                selectedButtonId = 0;
                                countDownTimer.cancel();
                            }
                        }
                    }
                });
    }


    public void showKeySettingPopup(View view) {
        // 提示
        SharedPreferences sp = view.getContext().getSharedPreferences(Consts.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(Consts.SHOW_KEY_ALARM, true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示")
                    .setMessage("长按按钮，3秒内按下触控笔按键即可设置快捷方式")
                    .setPositiveButton("不再弹出", (dialog, which) -> {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Consts.SHOW_KEY_ALARM, false);
                        editor.apply();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static boolean isPageDown(int i) {
        return (i == 93 || i == 62);
    }

    public static boolean isPageUp(int i) {
        return (i == 92);
    }
}