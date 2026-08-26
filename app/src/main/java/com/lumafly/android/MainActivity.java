package com.lumafly.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showHome();
    }

    private TextView text(String value, float size) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(Color.WHITE);
        view.setPadding(16, 16, 16, 16);
        return view;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(16);
        b.setAllCaps(false);
        return b;
    }

    private void showHome() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 40, 32, 32);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(25, 25, 30));

        TextView title = text("Lumafly Android", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = text(
                "Hollow Knight Mod Manager",
                17
        );
        subtitle.setGravity(Gravity.CENTER);

        root.addView(title);
        root.addView(subtitle);

        Button game = button("🎮  Hollow Knight");
        Button mods = button("📦  Mods");
        Button settings = button("⚙  Settings");

        root.addView(game);
        root.addView(mods);
        root.addView(settings);

        game.setOnClickListener(v -> showMessage(
                "Hollow Knight",
                "لم يتم ربط مجلد اللعبة بعد."
        ));

        mods.setOnClickListener(v -> showMessage(
                "Mods",
                "نظام المودات سيتم إضافته في المرحلة القادمة."
        ));

        settings.setOnClickListener(v -> showMessage(
                "Settings",
                "إعدادات Lumafly Android."
        ));

        setContentView(root);
    }

    private void showMessage(String title, String message) {
        root.removeAllViews();

        TextView t = text(title, 26);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);

        TextView m = text(message, 17);
        m.setGravity(Gravity.CENTER);

        Button back = button("← رجوع");
        back.setOnClickListener(v -> showHome());

        root.addView(t);
        root.addView(m);
        root.addView(back);
    }
}
