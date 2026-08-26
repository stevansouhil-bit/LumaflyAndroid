package com.lumafly.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
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
        root.setPadding(32, 40, 32, 32);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(25, 25, 30));
    }

    private void showHome() {
        setupRoot();

        TextView title = text("Lumafly Android", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = text("Hollow Knight Mod Manager", 17);
        subtitle.setGravity(Gravity.CENTER);

        root.addView(title);
        root.addView(subtitle);

        Button game = button("🎮  Hollow Knight");
        Button mods = button("📦  Mods");
        Button settings = button("⚙  Settings");

        root.addView(game);
        root.addView(mods);
        root.addView(settings);

        game.setOnClickListener(v -> showGame());
        mods.setOnClickListener(v -> showMods());
        settings.setOnClickListener(v -> showSettings());

        setContentView(root);
    }

    private void showGame() {
        setupRoot();

        TextView title = text("🎮 Hollow Knight", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView info = text(
                "مجلد اللعبة غير محدد.\n\n"
                + "في المرحلة القادمة سنضيف اختيار مجلد Hollow Knight.",
                17
        );
        info.setGravity(Gravity.CENTER);

        Button choose = button("📁 اختيار مجلد اللعبة");
        Button back = button("← رجوع");

        root.addView(title);
        root.addView(info);
        root.addView(choose);
        root.addView(back);

        choose.setOnClickListener(v ->
                showMessage("اختيار اللعبة",
                        "سيتم إضافة Android Folder Picker في الخطوة القادمة.")
        );

        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void showMods() {
        setupRoot();

        TextView title = text("📦 Mods", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView status = text(
                "لا توجد مودات مثبتة حاليًا.",
                17
        );
        status.setGravity(Gravity.CENTER);

        Button install = button("➕ Install Mod");
        Button refresh = button("🔄 Refresh");
        Button back = button("← رجوع");

        root.addView(title);
        root.addView(status);
        root.addView(install);
        root.addView(refresh);
        root.addView(back);

        install.setOnClickListener(v ->
                showMessage("Install Mod",
                        "هنا سنضيف اختيار ملف المود من الهاتف.")
        );

        refresh.setOnClickListener(v ->
                showMessage("Mods",
                        "تم تحديث قائمة المودات.")
        );

        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void showSettings() {
        setupRoot();

        TextView title = text("⚙ Settings", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView info = text(
                "Lumafly Android\nVersion 0.1",
                17
        );
        info.setGravity(Gravity.CENTER);

        Button back = button("← رجوع");

        root.addView(title);
        root.addView(info);
        root.addView(back);

        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void showMessage(String title, String message) {
        setupRoot();

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

        setContentView(root);
    }
}
