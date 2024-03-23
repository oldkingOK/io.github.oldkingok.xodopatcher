package com.github.oldkingok.xodopatcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    private Activity instance;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.xodo.pdf.reader")) return;
        hookPro(lpparam);
        hookLongClick(lpparam);
    }

    private void hookPro(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("viewer.CompleteReaderMainActivity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        instance = (Activity) param.thisObject;
                        Toast.makeText(instance, "Xodo模块加载成功！", Toast.LENGTH_SHORT).show();
                    }
                });

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

    private void hookLongClick(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent$n",
                lpparam.classLoader,
                "onLongClick",
                View.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Toast.makeText(instance, "我点击了！id是" + ((View)param.args[0]).getId(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
