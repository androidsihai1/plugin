package com.yy.plugin;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Created by andy on 2020/12/13.
 */
public class LoadUtils {

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


            DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/out1.dex",
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
