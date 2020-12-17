package com.yy.plugin;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * dex替换和合并
 * Created by andy on 2020/12/13.
 */
public class LoadUtils {

     //public static String apkPath = "/sdcard/out1.dex";
    public static String apkPath = "/sdcard/plugin4-debug.apk";

    public static void loadClass(Context context) {
        try {
            ClassLoader pathClassLoader = context.getClassLoader();

            Class<?> pathClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathList = pathClass.getDeclaredField("pathList");
            pathList.setAccessible(true);


            Class<?> dexPathList = Class.forName("dalvik.system.DexPathList");
            Field dexElements = dexPathList.getDeclaredField("dexElements");
            dexElements.setAccessible(true);

            Object hostPatList = pathList.get(pathClassLoader);
            Object[] hostElements = (Object[]) dexElements.get(hostPatList);

            File file = new File(apkPath);
            Log.i("sihai", "file.exit()=" + file.exists() + " length=" + file.length());

            DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                    context.getCacheDir().getAbsolutePath(), null, pathClassLoader);
            Object pluginPathList = pathList.get(dexClassLoader);
            Object[] pluginDexDelemnt = (Object[]) dexElements.get(pluginPathList);
            Object[] newElemnts =
                    (Object[]) Array.newInstance(hostElements.getClass().getComponentType(),
                            hostElements.length + pluginDexDelemnt.length);
            System.arraycopy(hostElements, 0, newElemnts, 0, hostElements.length);
            System.arraycopy(pluginDexDelemnt, 0, newElemnts, hostElements.length,
                    pluginDexDelemnt.length);
            dexElements.set(hostPatList, newElemnts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
