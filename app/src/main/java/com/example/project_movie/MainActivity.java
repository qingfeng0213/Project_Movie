package com.example.project_movie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Disposable mDisposable;
    //是否是第一次使用
    private boolean isFirstUse;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("isFirstUse", MODE_WORLD_READABLE);
        isFirstUse = preferences.getBoolean("isFirstUse", true);

        // 倒计时 3s
        mDisposable = Flowable.intervalRange(0, 3, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (isFirstUse) {
                            startActivity(new Intent(MainActivity.this, LeadActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                        finish();
                        //实例化Editor对象
                        SharedPreferences.Editor editor = preferences.edit();
                        //存入数据
                        editor.putBoolean("isFirstUse", false);
                        //提交修改
                        editor.commit();
                    }
                })
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

}
