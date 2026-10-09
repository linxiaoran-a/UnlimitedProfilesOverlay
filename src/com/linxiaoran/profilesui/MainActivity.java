package com.linxiaoran.profilesui;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText etLimit;
    private SeekBar seekLimit;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!RootShell.available()) {
            TextView tv = new TextView(this);
            tv.setText("未检测到 root 权限。\n本应用需要 root 才能读写模块配置。");
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dp(24), dp(24), dp(24), dp(24));
            setContentView(tv);
            return;
        }

        int pad = dp(20);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("UnlimitedProfiles");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView desc = new TextView(this);
        desc.setText("分身数量上限（1~999，默认 99）\n修改立即生效，无需重启。");
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        desc.setTextColor(Color.GRAY);
        desc.setPadding(0, dp(12), 0, dp(12));
        root.addView(desc, new LinearLayout.LayoutParams(-1, -2));

        etLimit = new EditText(this);
        etLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
        etLimit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        etLimit.setGravity(Gravity.CENTER);
        etLimit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        root.addView(etLimit, new LinearLayout.LayoutParams(-1, -2));

        seekLimit = new SeekBar(this);
        seekLimit.setMax(998); // 1~999
        root.addView(seekLimit, new LinearLayout.LayoutParams(-1, -2));

        Button btnApply = new Button(this);
        btnApply.setText("保存并应用");
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(-1, -2);
        btnLp.setMargins(0, dp(16), 0, dp(8));
        root.addView(btnApply, btnLp);

        tvStatus = new TextView(this);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvStatus.setTextColor(Color.GRAY);
        tvStatus.setPadding(0, dp(16), 0, 0);
        root.addView(tvStatus, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
        setTitle("UnlimitedProfiles");

        etLimit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    int v = Integer.parseInt(s.toString());
                    if (v >= 1 && v <= 999) {
                        seekLimit.setProgress(v - 1);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        seekLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser) {
                    etLimit.setText(String.valueOf(progress + 1));
                    etLimit.setSelection(etLimit.getText().length());
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        btnApply.setOnClickListener(v -> save());

        loadStatus();
    }

    private void loadStatus() {
        new AsyncTask<Void, Void, Ctl.Status>() {
            String err;
            @Override protected Ctl.Status doInBackground(Void... v) {
                try {
                    return Ctl.status();
                } catch (Exception e) {
                    err = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(Ctl.Status s) {
                if (s == null) {
                    tvStatus.setText("读取失败: " + err);
                    return;
                }
                etLimit.setText(String.valueOf(s.limit));
                etLimit.setSelection(etLimit.getText().length());
                seekLimit.setProgress(s.limit - 1);
                tvStatus.setText(
                    "fw.max_users=" + s.fw + "\n" +
                    "dumpsys: " + s.ds + "\n" +
                    "overlay: MaximumUsers=" + s.l1 + " MaxRunningUsers=" + s.l2 + "\n" +
                    "当前用户数: " + s.userCount + " (" + s.users + ")\n" +
                    "overlay列表: " + s.ovs
                );
            }
        }.execute();
    }

    private void save() {
        final String txt = etLimit.getText().toString();
        if (txt.isEmpty()) {
            toast("上限不能为空");
            return;
        }
        final int v;
        try {
            v = Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            toast("请输入 1~999 的数字");
            return;
        }
        if (v < 1 || v > 999) {
            toast("请输入 1~999 的数字");
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {
            String err;
            @Override protected Boolean doInBackground(Void... vv) {
                try {
                    Ctl.set(v);
                    return true;
                } catch (Exception e) {
                    err = e.getMessage();
                    return false;
                }
            }
            @Override protected void onPostExecute(Boolean ok) {
                if (ok) {
                    toast("已保存并应用: " + v);
                    loadStatus();
                } else {
                    toast("保存失败: " + err);
                }
            }
        }.execute();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                getResources().getDisplayMetrics());
    }
}
