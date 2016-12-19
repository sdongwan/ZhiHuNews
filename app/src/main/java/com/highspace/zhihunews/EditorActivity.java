package com.highspace.zhihunews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

import adapter.EditorAdapter;
import bean.EditorsBean;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class EditorActivity extends SwipeBackActivity {
    public static final String EDITOR_BEAN = "editor_bean";
    private ImageButton mBackIB;
    private RecyclerView mEditorRcy;
    private EditorAdapter mEditorAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EditorsBean> mEditorsBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        Intent intent = getIntent();
        if (intent != null) {
            mEditorsBeanList = intent.getParcelableArrayListExtra(EDITOR_BEAN);
        }
        initView();
        initEvent();
    }

    private void initEvent() {

        mEditorAdapter = new EditorAdapter(this, mEditorsBeanList);
        mEditorRcy.setAdapter(mEditorAdapter);
        mEditorRcy.setLayoutManager(mLayoutManager);
        mEditorRcy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        mBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initView() {
        mBackIB = (ImageButton) findViewById(R.id.editor_toolbar_back);
        mEditorRcy = (RecyclerView) findViewById(R.id.editor_rcy);
        mLayoutManager = new LinearLayoutManager(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }
}
