package com.highspace.zhihunews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import adapter.ImageAdapter;
import bean.NewsDetailBean;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ImageActivity extends SwipeBackActivity {
    public static final String IMG_URL = "img_url";
    public static final String THEME_FLAG = "theme_flag";
    public static final String NEWS_DEAIL_BEAN = "img_detail_bean";


    private ImageButton mBackIB;
    private ImageButton mDownLoadIB;
    private TextView mImgPotision;
    private String imgUrl;

    private NewsDetailBean mNewsDetailBean;
    private ViewPager mImgViewPager;
    private boolean isTheme = false;

    private ImageAdapter mImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        // Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        Intent intent = getIntent();
        if (intent != null) {
            imgUrl = intent.getStringExtra(IMG_URL);
            isTheme = intent.getBooleanExtra(THEME_FLAG, false);
            //mNewsDetailBean = intent.getParcelableExtra(NEWS_DEAIL_BEAN);
        }
        initView();
        initEvents();
    }

    private void initEvents() {
        int imgPosition = isTheme ? ThemeDetailActivity.mImgUrlList.indexOf(imgUrl) : HomeDetailActivity.mImgUrlList.indexOf(imgUrl);
        if (imgPosition != -1) {
            mImgViewPager.setCurrentItem(imgPosition);
            mImgPotision.setText((imgPosition + 1) + "/" + mImageAdapter.getCount());
        }


        mBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        mImgViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mImgPotision.setText(position + 1 + "/" + mImageAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDownLoadIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String imgUrl = (isTheme ? ThemeDetailActivity.mImgUrlList : HomeDetailActivity.mImgUrlList).get(mImgViewPager.getCurrentItem());

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();


                        Handler handler = new Handler(Looper.myLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 0) {
                                    if (msg.obj != null)
                                        downLoadImg(ImageActivity.this, (Bitmap) msg.obj);
                                }
                            }
                        };


                        try {
                            Bitmap img = Picasso.with(ImageActivity.this).load(imgUrl).get();
                            Message msg = Message.obtain();
                            msg.obj = img;
                            msg.what = 0;
                            handler.sendMessage(msg);

                        } catch (IOException e) {

                            Toast.makeText(ImageActivity.this, "图片下载失败", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                }.start();


            }
        });
    }

    private void initView() {
        mImgPotision = (TextView) findViewById(R.id.img_potion_tv);
        mBackIB = (ImageButton) findViewById(R.id.img_back_ib);
        mDownLoadIB = (ImageButton) findViewById(R.id.img_download_ib);
        mImgViewPager = (ViewPager) findViewById(R.id.img_viewpager);

        mImageAdapter = new ImageAdapter(isTheme ? ThemeDetailActivity.mImgUrlList : HomeDetailActivity.mImgUrlList, this);
        mImgViewPager.setAdapter(mImageAdapter);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    private void downLoadImg(Context context, Bitmap bitmap) {

        String path = Environment.getExternalStorageDirectory().getPath() + "/imgs/";


        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String imgName = System.currentTimeMillis() + ".jpg";
        File img = new File(fileDir, imgName);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            Toast.makeText(context, "图片下载完成", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "图片下载失败", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "图片下载失败", Toast.LENGTH_SHORT).show();
        }

    }


}
