package com.github.oldkingok.xodopatcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.xodo.pdf.reader")) return;
        hook(lpparam);
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XC_MethodHook.Unhook unhook = XposedHelpers.findAndHookMethod("viewer.CompleteReaderMainActivity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Toast.makeText((Activity) param.thisObject, "Xodo模块加载成功！", Toast.LENGTH_LONG).show();
                    }
                });

        XC_MethodHook.Unhook isPro = XposedHelpers.findAndHookMethod("com.xodo.utilities.misc.XodoProStatus",
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
