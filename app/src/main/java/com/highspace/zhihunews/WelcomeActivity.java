package com.highspace.zhihunews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bean.WelImgBean;
import constants.ApiConstant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.NetworkUtil;
import util.OkHttpUtil;
import util.SpUtil;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView mWelcomeImg;
    private View mBlackView;
    private Handler mHandler;
    private LinearLayout mBottonView;

    private static final int IMG_FLAG = 56;
    private static final int START_OTHER = 57;
    private WelImgBean mWelImgBean;

    private boolean isNetWorkConnect;


    private static final String IMG_TEXT = "img_text";
    private static final String IMG_URL = "img_url";
    private String localImgUrl = "";

    private boolean isGetFromNet = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        isNetWorkConnect = NetworkUtil.isConnected(this);

        mWelcomeImg = (ImageView) findViewById(R.id.welcome_img);
        mBlackView = findViewById(R.id.black_view);
        mBottonView = (LinearLayout) findViewById(R.id.wel_bottom_view);

        final Animation alphaAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_view);
        alphaAnim.setDuration(3000);

        final Animation traslAnim = AnimationUtils.loadAnimation(this, R.anim.anim_in_bottom);
        traslAnim.setDuration(3000);
        mBottonView.setVisibility(View.VISIBLE);
        mBottonView.setAnimation(traslAnim);
        traslAnim.start();


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == IMG_FLAG) {
                    if (isGetFromNet) {
                        Toast.makeText(WelcomeActivity.this, "从网络中加载图片", Toast.LENGTH_SHORT).show();
                        OkHttpUtil.getInstance().getFromNet(mWelImgBean.getImg(), new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(WelcomeActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.body().toString() != null) {

                                    InputStream inputStream = response.body().byteStream();
                                    File file = new File(getExternalCacheDir(), "/imgs.png");
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    byte[] b = new byte[1024];
                                    int n;

                                    while ((n = inputStream.read(b)) != -1) {
                                        fileOutputStream.write(b, 0, n);
                                        //fileOutputStream.write(b);
                                    }

                                    fileOutputStream.flush();
                                    inputStream.close();
                                    fileOutputStream.close();
                                    isGetFromNet = false;
                                    localImgUrl = getExternalCacheDir() + "/imgs.png";
                                    SpUtil.putString(IMG_TEXT, mWelImgBean.getText(), WelcomeActivity.this);
                                    SpUtil.putString(IMG_URL, localImgUrl, WelcomeActivity.this);
                                    mHandler.sendEmptyMessage(IMG_FLAG);
                                }
                            }
                        });


                    } else {

                        Toast.makeText(WelcomeActivity.this, "从本地中加载图片", Toast.LENGTH_SHORT).show();
                        localImgUrl = SpUtil.getString(IMG_URL, "", WelcomeActivity.this);
                        Bitmap bitmap = BitmapFactory.decodeFile(localImgUrl);
                        mWelcomeImg.setImageBitmap(bitmap);
                        // TODO: 2016/12/3 从本地中加载图片

                        mBlackView.setAnimation(alphaAnim);
                        alphaAnim.start();

                        mHandler.sendEmptyMessageDelayed(START_OTHER, 4000);
                    }

                   
                        /*
                             new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                saveImg(bitmap);
                                // this.stop();
                            }
                        }.start();
                         */


                } else if (msg.what == START_OTHER) {

                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                    finish();
                }

            }
        };
        if (isNetWorkConnect) {
            OkHttpUtil.getInstance().getFromNet(ApiConstant.APP_WEL_IMG, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(WelcomeActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body().toString() != null) {
                                Gson gson = new Gson();
                                mWelImgBean = gson.fromJson(response.body().string(), WelImgBean.class);
                                if (mWelImgBean.getText().equals(SpUtil.getString(IMG_TEXT, "", WelcomeActivity.this))) {
                                    isGetFromNet = false;// TODO: 2016/12/3
                                } else {
                                    isGetFromNet = true;
                                }
                                mHandler.sendEmptyMessage(IMG_FLAG);
                            }
                        }
                    }
            );
        } else {

            isGetFromNet = false;
            mHandler.sendEmptyMessage(IMG_FLAG);

        }


    }

    private void saveImg(Bitmap bitmap) {
        File welImg = new File(getCacheDir(), "/welImg.jpg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(welImg);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImg(String imgPath) throws FileNotFoundException {
        File img = new File(imgPath);
        FileInputStream fileInputStream = new FileInputStream(img);
        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
        return bitmap;

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }
    /*
    InputStream inputStream = response.body().byteStream();
                    File welImg = new File(getCacheDir(), "/welImg.jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(welImg);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();
                    Message msg = Message.obtain();
                    msg.what = IMG_GET;
                    msg.obj = bitmap;
                    mHandler.sendMessage(msg);
     */
}
