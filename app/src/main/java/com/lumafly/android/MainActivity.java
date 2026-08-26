package com.lumafly.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.content.Intent;
import android.net.Uri;
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

        game.setOnClickListener(v -> chooseGameFolder());

        mods.setOnClickListener(v -> showMessage(
                "Mods",
                "مدير المودات سيتم تطويره في المرحلة القادمة."
        ));

        settings.setOnClickListener(v -> showMessage(
                "Settings",
                "إعدادات Lumafly Android."
        ));

        setContentView(root);
    }

    private void chooseGameFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        startActivityForResult(intent, PICK_GAME_FOLDER);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_GAME_FOLDER &&
                resultCode == RESULT_OK &&
                data != null) {

            Uri uri = data.getData();

            if (uri != null) {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );

                showMessage(
                        "Hollow Knight",
                        "تم اختيار مجلد اللعبة بنجاح."
                );
            }
        }
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
