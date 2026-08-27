package com.lumafly.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.database.Cursor;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int PICK_GAME_FOLDER = 1001;
    private static final int PICK_MOD_FILE = 1002;

    private LinearLayout root;
    private Uri gameUri;
    private Uri modsUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // استرجاع مجلد اللعبة إذا كان محفوظاً
        String saved = getPreferences(MODE_PRIVATE)
                .getString("game_uri", null);

        if (saved != null) {
            gameUri = Uri.parse(saved);
        }

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
        root.addView(search);
        root.addView(subtitle);
        root.addView(game);
        root.addView(mods);
        root.addView(settings);

        game.setOnClickListener(v -> showGameManager());

        mods.setOnClickListener(v -> { showMessage("Mods", "جاري تحميل قائمة المودات..."); loadModsFromGitHub(); });

        settings.setOnClickListener(v -> showMessage(
                "Settings",
                "إعدادات Lumafly Android."
        ));

        setContentView(root);
    }

    private void showGameManager() {
        setupRoot();

        TextView title = text("🎮 إدارة اللعبة", 28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);
        root.addView(search);

        String gameStatus = gameUri == null
                ? "⚠️ لم يتم اختيار مجلد اللعبة"
                : "✅ مجلد اللعبة محدد";

        root.addView(text(gameStatus, 17));

        Button choose = button(gameUri == null
                ? "📁 اختيار مجلد اللعبة"
                : "🔄 تغيير مجلد اللعبة");
        choose.setOnClickListener(v -> chooseGameFolder());
        root.addView(choose);

        Button createMods = button("📦 إنشاء مجلد Mods");
        createMods.setOnClickListener(v -> {
            createModsFolder();
            showGameManager();
        });
        root.addView(createMods);

        String modsStatus = modsUri == null
                ? "⚠️ مجلد Mods غير جاهز"
                : "✅ مجلد Mods جاهز";
        root.addView(text(modsStatus, 17));

        Button back = button("← رجوع");
        back.setOnClickListener(v -> showHome());
        root.addView(back);

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

        if (requestCode == PICK_GAME_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (Exception ignored) {}

                gameUri = uri;
                modsUri = findOrCreateModsFolder(uri);
                getPreferences(MODE_PRIVATE).edit()
                        .putString("game_uri", uri.toString())
                        .apply();

                showMessage("Hollow Knight", "تم اختيار مجلد اللعبة بنجاح.");
            }
        }

        if (requestCode == PICK_MOD_FILE && resultCode == RESULT_OK && data != null) {
            Uri modUri = data.getData();
            if (modUri != null) {
                installModFile(modUri);
            }
        }
    }

    private Uri findFolder(Uri parentUri, String folderName) {
        try {
            Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                    parentUri,
                    DocumentsContract.getTreeDocumentId(parentUri)
            );

            Cursor cursor = getContentResolver().query(
                    childrenUri,
                    new String[]{
                            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                            DocumentsContract.Document.COLUMN_MIME_TYPE
                    },
                    null, null, null
            );

            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(1);
                        String mime = cursor.getString(2);
                        if (folderName.equals(name) &&
                                DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                            String id = cursor.getString(0);
                            return DocumentsContract.buildDocumentUriUsingTree(parentUri, id);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception ignored) {}

        return null;
    }


    private Uri findOrCreateModsFolder(Uri parentUri) {
        try {
            Uri existing = findFolder(parentUri, "Mods");
            if (existing != null) return existing;

            Uri newFolder = DocumentsContract.createDocument(
                    getContentResolver(),
                    parentUri,
                    DocumentsContract.Document.MIME_TYPE_DIR,
                    "Mods"
            );

            return newFolder;
        } catch (Exception e) {
            return null;
        }
    }


    private void createModsFolder() {
        if (gameUri == null) {
            showMessage("Mods", "اختر مجلد اللعبة أولاً.");
            return;
        }

        modsUri = findOrCreateModsFolder(gameUri);

        if (modsUri != null) {
            showMessage("Mods", "تم تجهيز مجلد Mods بنجاح.");
        } else {
            showMessage("Mods", "تعذر إنشاء مجلد Mods داخل المجلد المختار.");
        }
    }


    private void installModFile(Uri sourceUri) {
        if (modsUri == null) {
            showMessage("تثبيت المود", "افتح Mod Manager واختر مجلد اللعبة أولاً.");
            return;
        }

        try {
            String fileName = "mod_" + System.currentTimeMillis();

            Cursor cursor = getContentResolver().query(
                    sourceUri,
                    new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                    null, null, null
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    if (name != null && !name.isEmpty()) {
                        fileName = name;
                    }
                }
                cursor.close();
            }

            Uri targetUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    modsUri,
                    "*/*",
                    fileName
            );

            if (targetUri == null) {
                showMessage("خطأ", "تعذر إنشاء ملف المود داخل Mods.");
                return;
            }

            java.io.InputStream input =
                    getContentResolver().openInputStream(sourceUri);
            java.io.OutputStream output =
                    getContentResolver().openOutputStream(targetUri);

            if (input == null || output == null) {
                if (input != null) input.close();
                if (output != null) output.close();
                showMessage("خطأ", "تعذر قراءة أو كتابة ملف المود.");
                return;
            }

            byte[] buffer = new byte[8192];
            int count;

            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            showMessage(
                    "تم تثبيت المود",
                    "تم نسخ " + fileName + " إلى مجلد Mods."
            );

        } catch (Exception e) {
            showMessage("خطأ", "تعذر تثبيت ملف المود.");
        }
    }


    // =========================
    private void loadModsFromGitHub() {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://raw.githubusercontent.com/stevansouhil-bit/LumaflyAndroid/main/mods.json");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonText.append(line);
                }
                reader.close();

                JSONObject rootJson = new JSONObject(jsonText.toString());
                JSONArray modsArray = rootJson.getJSONArray("mods");

                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<String> downloadUrls = new ArrayList<>();

                for (int i = 0; i < modsArray.length(); i++) {
                    JSONObject mod = modsArray.getJSONObject(i);
                    names.add(mod.optString("name", "Unnamed Mod"));
                    descriptions.add(mod.optString("description", ""));
                    downloadUrls.add(mod.optString("download", ""));
                }

                new Handler(Looper.getMainLooper()).post(() -> showGitHubMods(names, descriptions, downloadUrls));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> showMessage("GitHub", "تعذر تحميل قائمة المودات من GitHub."));
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private void showGitHubMods(ArrayList<String> names, ArrayList<String> descriptions, ArrayList<String> downloadUrls) {
        setupRoot();

        TextView title = text("🌐 متجر المودات 🔍", 28);
        EditText search = new EditText(this);
        search.setHint("🔍 ابحث عن مود...");
        search.setSingleLine(true);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);
        root.addView(search);

        LinearLayout modsContainer = new LinearLayout(this);
        modsContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(modsContainer);
        if (names.size() == 0) {
            root.addView(text("لا توجد مودات في قاعدة البيانات.", 16));
        } else {
            for (int i = 0; i < names.size(); i++) {
                TextView name = text(names.get(i), 20);
                name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                modsContainer.addView(name);
                modsContainer.addView(text(descriptions.get(i), 15));
                Button download = button("📥 تحميل المود");
                final int modIndex = i;
                final String modName = names.get(modIndex);
                download.setOnClickListener(v -> downloadMod(downloadUrls.get(modIndex), modName));
                modsContainer.addView(download);
            }
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                for (int i = 0; i < modsContainer.getChildCount(); i += 3) {
                    boolean visible = query.isEmpty() || names.get(i / 3).toLowerCase().contains(query);
                    int end = Math.min(i + 3, modsContainer.getChildCount());
                    for (int j = i; j < end; j++) modsContainer.getChildAt(j).setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        Button refresh = button("🔄 تحديث المودات");
        refresh.setOnClickListener(v -> loadModsFromGitHub());

        Button back = button("← رجوع");
        back.setOnClickListener(v -> showHome());

        root.addView(refresh);
        root.addView(back);
        setContentView(root);
    }

    private void downloadMod(String urlString, String modName) {
        if (urlString == null || urlString.trim().isEmpty()) {
            showMessage("تحميل المود", "رابط التحميل غير موجود.");
            return;
        }

        if (modsUri == null) {
            showMessage("تحميل المود", "افتح مدير المودات أولاً وأنشئ مجلد Mods.");
            return;
        }

        new Thread(() -> {
            HttpURLConnection connection = null;
            java.io.InputStream input = null;
            java.io.OutputStream output = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP " + connection.getResponseCode());
                }

                String safeName = modName.replaceAll("[^a-zA-Z0-9._-]", "_");
                final String fileName = safeName.toLowerCase().endsWith(".zip")
                        ? safeName
                        : safeName + ".zip";

                Uri fileUri = DocumentsContract.createDocument(
                        getContentResolver(),
                        modsUri,
                        "application/zip",
                        fileName
                );

                if (fileUri == null) {
                    throw new Exception("تعذر إنشاء الملف.");
                }

                input = connection.getInputStream();
                output = getContentResolver().openOutputStream(fileUri);

                if (output == null) {
                    throw new Exception("تعذر فتح الملف للكتابة.");
                }

                byte[] buffer = new byte[8192];
                int count;

                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }

                output.flush();

                new Handler(Looper.getMainLooper()).post(() ->
                        showMessage(
                                "تم التحميل",
                                "تم حفظ " + fileName + " داخل مجلد Mods."
                        )
                );

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        showMessage(
                                "خطأ في التحميل",
                                "تعذر تحميل المود."
                        )
                );

            } finally {
                try {
                    if (output != null) output.close();
                } catch (Exception ignored) {}

                try {
                    if (input != null) input.close();
                } catch (Exception ignored) {}

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }



    // MOD MANAGER
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
