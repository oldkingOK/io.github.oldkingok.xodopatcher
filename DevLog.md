# Xposed Project Develop Guide

https://blog.ketal.icu/cn/Xposed%E6%A8%A1%E5%9D%97%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8%E4%BF%9D%E5%A7%86%E7%BA%A7%E6%95%99%E7%A8%8B/

## Add repository link

Edit project root file settings.gradle

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://api.xposed.info/' } // add this line
    }
}
```

Note: `dependencyResolutionManagement` instead of `pluginManagement`

## Add dependency

In `app/buile.gradle`

```groovy
dependencies {
    compileOnly 'de.robv.android.xposed:api:82' // add this line
}
```

## Add as debug

```shell
adb shell
su
magisk resetprop ro.debuggable 1
stop;start;
```

```shell
adb shell am set-debug-app -w --persistent com.xodo.pdf.reader
adb shell monkey -p com.xodo.pdf.reader -c android.intent.category.LAUNCHER 1
```

IDEA: Run -> Attach Debugger to Android process

Clear debug

```shell
adb shell am clear-debug-app
```