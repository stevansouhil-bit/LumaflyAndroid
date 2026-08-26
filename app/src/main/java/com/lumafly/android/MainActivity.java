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
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setGravity(Gravity.CENTER);
        v.setPadding(16, 16, 16, 16);
        return v;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(16);
        b.setAllCaps(false);
        return b;
    }

    private void setupRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(Color.rgb(25, 25, 30));
    }

    private void showHome() {
        setupRoot();

        TextView title = text("Lumafly Android", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView subtitle = text(
                "Hollow Knight Mod Manager",
                17
        );

        Button game = button("🎮  Hollow Knight");
        Button mods = button("📦  Mods");
        Button settings = button("⚙  Settings");

        root.addView(title);
        root.addView(subtitle);
        root.addView(game);
        root.addView(mods);
        root.addView(settings);

        game.setOnClickListener(v ->
                showMessage(
                        "Hollow Knight",
                        "اختيار مجلد اللعبة سيتم إضافته لاحقًا."
                )
        );

        mods.setOnClickListener(v ->
                showMessage(
                        "Mods",
                        "مدير المودات سيتم تطويره في المرحلة القادمة."
                )
        );

        settings.setOnClickListener(v ->
                showMessage(
                        "Settings",
                        "إعدادات Lumafly Android."
                )
        );

        setContentView(root);
    }

    private void showMessage(String title, String message) {
        setupRoot();

        TextView t = text(title, 26);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView m = text(message, 17);

        Button back = button("← رجوع");
        back.setOnClickListener(v -> showHome());

        root.addView(t);
        root.addView(m);
        root.addView(back);

        setContentView(root);
    }
}
