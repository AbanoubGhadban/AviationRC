package com.example.golden.aviationrc;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class CommandsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    CheckBox mIntValuesCB;
    HashMap<Integer, EditText> mCommandsTV;

    private static HashMap<Integer, EditText> getAllEditTexts(ViewGroup parent) {
        int count = parent.getChildCount();
        HashMap<Integer, EditText> views = new HashMap<>();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            if (view instanceof EditText)
                views.put(view.getId(), (EditText) view);
            else if (view instanceof ViewGroup)
                views.putAll(getAllEditTexts((ViewGroup) view));
        }
        return views;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);

        mIntValuesCB = findViewById(R.id.int_values_cb);
        mCommandsTV = getAllEditTexts((ViewGroup) findViewById(R.id.commands_table_layout));
        mIntValuesCB.setOnCheckedChangeListener(this);
        loadCommandsEditTexts();
    }

    private void loadCommandsEditTexts() {
        mCommandsTV.get(R.id.forward_tv).setText(Commands.getForward());
        mCommandsTV.get(R.id.backward_tv).setText(Commands.getBackward());
        mCommandsTV.get(R.id.right_tv).setText(Commands.getRight());
        mCommandsTV.get(R.id.left_tv).setText(Commands.getLeft());
        mCommandsTV.get(R.id.top_right_tv).setText(Commands.getTopRight());
        mCommandsTV.get(R.id.top_left_tv).setText(Commands.getTopLeft());
        mCommandsTV.get(R.id.back_right_tv).setText(Commands.getBackRight());
        mCommandsTV.get(R.id.back_left_tv).setText(Commands.getBackLeft());
        mCommandsTV.get(R.id.stop_tv).setText(Commands.getStop());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            InputFilter[] lenFilter = new InputFilter[]{new InputFilter.LengthFilter(3)};
            for (Map.Entry<Integer, EditText> entry : mCommandsTV.entrySet()) {
                entry.getValue().setInputType(InputType.TYPE_CLASS_NUMBER);
                entry.getValue().setFilters(lenFilter);
            }
        } else {
            InputFilter[] lenFilter = new InputFilter[]{new InputFilter.LengthFilter(1)};
            for (Map.Entry<Integer, EditText> entry : mCommandsTV.entrySet()) {
                entry.getValue().setInputType(InputType.TYPE_CLASS_TEXT);
                entry.getValue().setFilters(lenFilter);
            }
        }
    }

    private void saveCommands() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<Integer, EditText> entry : mCommandsTV.entrySet()) {
            String value = entry.getValue().getText().toString();
            if (mIntValuesCB.isChecked()) {
                try {
                    int c = Integer.parseInt(value);
                    if (c <= 0 || c > 255)
                        throw new NumberFormatException();
                    editor.putInt(getCommandIdByViewId(entry.getKey()), c);
                } catch (NumberFormatException ex) {
                    editor.clear();

                }
            }
        }
    }

    private boolean checkAddValues(SharedPreferences.Editor editor) {
        for (Map.Entry<Integer, EditText> entry : mCommandsTV.entrySet()) {
            EditText editText = entry.getValue();
            String value = editText.getText().toString();
            if (mIntValuesCB.isChecked()) {
                try {
                    int c = Integer.parseInt(value);
                    if (c <= 0 || c > 255)
                        throw new NumberFormatException();
                    editor.putInt(getCommandIdByViewId(editText.getId()), c);
                } catch (NumberFormatException ex) {
                    editor.clear();
                    editText.setText("");
                    return false;
                }
            } else {
                if (TextUtils.isEmpty(value)) {
                    editor.clear();
                    editText.setText("");
                    return false;
                }
                int c = value.charAt(0);
                editor.putInt(getCommandIdByViewId(editText.getId()), c);
                editText.setText(value.charAt(0));
            }
        }
        return true;
    }

    String getCommandIdByViewId(int viewId) {
        switch (viewId) {
            case R.id.forward_tv:
                return Commands.FORWARD_ID;
            case R.id.backward_tv:
                return Commands.BACKWARD_ID;
            case R.id.right_tv:
                return Commands.RIGHT_ID;
            case R.id.left_tv:
                return Commands.LEFT_ID;
            case R.id.top_left_tv:
                return Commands.TOP_LEFT_ID;
            case R.id.top_right_tv:
                return Commands.TOP_RIGHT_ID;
            case R.id.back_left_tv:
                return Commands.BACK_LEFT_ID;
            case R.id.back_right_tv:
                return Commands.BACK_RIGHT_ID;
            default:
                return Commands.STOP_ID;
        }
    }

    private interface OnPreferencesSavedListener {
        void onPreferencesSaved();
    }

    private static class SavePreferencesTask extends AsyncTask<SharedPreferences, Void, Void> {
        private OnPreferencesSavedListener listener;

        @Override
        protected Void doInBackground(SharedPreferences... sharedPreferences) {
            return null;
        }
    }
}
