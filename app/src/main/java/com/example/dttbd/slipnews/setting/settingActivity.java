package com.example.dttbd.slipnews.setting;

import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dttbd.slipnews.R;

public class settingActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private Boolean bool;
    private TextView languageShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar settingToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        settingToolbar.setTitle(R.string.nav_settings);

        languageShow = (TextView) findViewById(R.id.languageShow);

        setSupportActionBar(settingToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        TextView languageChoose = (TextView) findViewById(R.id.languageChoose);
        languageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
                dialog.show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void  createDialog(){
        //解析
        View view = LayoutInflater.from(this).inflate(R.layout.languagedialog,null);
        RadioGroup languageChange = (RadioGroup) view.findViewById(R.id.languageChange);
        RadioButton chooseChinese = (RadioButton) view.findViewById(R.id.chooseChinese);
        RadioButton chooseEnglish = (RadioButton) view.findViewById(R.id.chooseEnglish);
        TextView baseSetting = (TextView) findViewById(R.id.baseSetting);
        if (baseSetting.getText().toString().equals("基本设置")) {
            chooseChinese.setChecked(true);
            bool = true;
        } else {
            chooseEnglish.setChecked(true);
            bool = false;
        }
        languageChange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.chooseChinese:
                        bool = true;
                        break;
                    case R.id.chooseEnglish:
                        bool = false;
                        break;
                    default:
                }
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bool) {
                            languageShow.setText(R.string.Chinese);
                            Toast.makeText(settingActivity.this, "中文", Toast.LENGTH_SHORT).show();

                        } else {
                            languageShow.setText(R.string.English);
                            Toast.makeText(settingActivity.this, "English", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .create();
    }

}
