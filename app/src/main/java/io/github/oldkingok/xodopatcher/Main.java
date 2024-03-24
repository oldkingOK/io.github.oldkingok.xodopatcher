package io.github.oldkingok.xodopatcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    private Activity instance;
    private Pen pen;
    public Message message = new Message();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.xodo.pdf.reader")) return;
        pen = new Pen(lpparam, message);
        hookStartup(lpparam);
        hookPro(lpparam);
        pen.penHook(lpparam);
    }

    private void hookStartup(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("viewer.CompleteReaderMainActivity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        message.setContext((Activity) param.thisObject);
                        message.sendToast("Xodo模块加载成功！", Toast.LENGTH_SHORT);
                    }
                });
    }

    private void hookPro(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.xodo.utilities.misc.XodoProStatus",
                lpparam.classLoader,
                "isPro",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return true;
                    }
                });
    }
}
