package com.yy.plugin;

import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // // DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/out1.dex",
        // //         getCacheDir().getAbsolutePath(), null, getClassLoader());
        // PathClassLoader pathClasLoader = new PathClassLoader("/sdcard/out1.dex", getClassLoader());
        // try {
        //     Class<?> aClass = pathClasLoader.loadClass("com.yy.plugin3.MainActivity");
        //     Method printMethod = aClass.getDeclaredMethod("print");
        //     printMethod.invoke(null);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        //
        //



        try {
            Class<?> aClass = Class.forName("com.yy.plugin3.MainActivity");
            Method method = aClass.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
