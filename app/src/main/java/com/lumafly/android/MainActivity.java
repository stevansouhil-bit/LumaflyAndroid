package com.lumafly.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int PICK_GAME_FOLDER = 1001;
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

        Button game = button("🎮  Hollow Knight");
        Button mods = button("📦  Mods");
        Button settings = button("⚙  Settings");

        root.addView(title);
        root.addView(subtitle);
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
                "اختار مجلد Hollow Knight من الهاتف.",
                17
        );
        info.setGravity(Gravity.CENTER);

        Button choose = button("📁 اختيار مجلد اللعبة");
        Button back = button("← رجوع");

        root.addView(title);
        root.addView(info);
        root.addView(choose);
        root.addView(back);

        choose.setOnClickListener(v -> chooseGameFolder());
        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void chooseGameFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        );

        startActivityForResult(intent, PICK_GAME_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_GAME_FOLDER
                && resultCode == RESULT_OK
                && data != null) {

            Uri uri = data.getData();

            if (uri != null) {
                try {
                    int flags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    getContentResolver().takePersistableUriPermission(uri, flags);
                } catch (Exception ignored) {
                }

                getSharedPreferences("lumafly", MODE_PRIVATE)
                        .edit()
                        .putString("game_folder", uri.toString())
                        .apply();

                showGameSelected(uri);
            }
        }
    }

    private void showGameSelected(Uri uri) {
        setupRoot();

        TextView title = text("✅ Hollow Knight", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView info = text(
                "تم اختيار مجلد اللعبة بنجاح.\n\n"
                + "المسار محفوظ داخل Lumafly Android.",
                17
        );
        info.setGravity(Gravity.CENTER);

        Button change = button("📁 تغيير المجلد");
        Button back = button("← رجوع");

        root.addView(title);
        root.addView(info);
        root.addView(change);
        root.addView(back);

        change.setOnClickListener(v -> chooseGameFolder());
        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void showMods() {
        setupRoot();

        TextView title = text("📦 Mods", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView status = text(
                "مدير المودات جاهز للمرحلة القادمة.",
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
                showMessage(
                        "Install Mod",
                        "اختيار ملف المود سيتم إضافته في المرحلة القادمة."
                )
        );

        refresh.setOnClickListener(v ->
                showMessage("Mods", "تم تحديث قائمة المودات.")
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
                "Lumafly Android\nVersion 0.2",
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
