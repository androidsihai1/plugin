package com.yy.plugin;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1.dex 加载

        // ClassLoader classLoader = getClassLoader();
        // System.out.println("MainActivity classlOader=" + classLoader.toString());
        //
        // ClassLoader classLoader1 = Activity.class.getClassLoader();
        //
        // System.out.println(" Actiivty classlOader=" + classLoader1.toString());
        // File file = new File("/sdcard/out1.dex");
        // if (file.exists()) {
        //     System.out.println("file存在的");
        // }
        // DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/out1.dex",
        //         getCacheDir().getAbsolutePath(), null, getClassLoader());
        // PathClassLoader pathClasLoader = new PathClassLoader(LoadUtils.apkPath, getClassLoader());
        // try {
        //     Class<?> aClass = pathClasLoader.loadClass("com.yy.plugin3.MainActivity");
        //     Method printMethod = aClass.getDeclaredMethod("print");
        //     printMethod.invoke(null);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        //
        //

        // 2.dex合并修改后

        try {
            Class<?> aClass = Class.forName("com.yy.plugin4.MainActivity");
            Method method = aClass.getMethod("hahaTest");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }



        Log.d("sihai", "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        // findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         //3.hook替换
        //         Intent intent = new Intent();
        //         intent.setComponent(new ComponentName("com.yy.plugin3", "com.yy.plugin3.MainActivity"));
        //         startActivity(intent);
        //     }
        // });


    }

}
