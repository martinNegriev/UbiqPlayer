package com.example.ubiqplayer.ui.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.utils.CommonUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private HashMap<Integer, SettingInfo> settingsInfos = new HashMap<>();

    private static final int CHANGE_THEME = 0;
    private static final int COUNTDOWN_TIMER = 1;
    private static CountDownTimer countDownTimer = null;
    private static int minutesUntilFinished = 0;

    class ChangeThemePreference extends Preference {
        final int settingId;
        Context context;

        public ChangeThemePreference(Context context, int settingId) {
            super(context);
            this.context = context;
            this.settingId = settingId;
        }

        @Override
        public void onBindViewHolder(PreferenceViewHolder holder) {
            super.onBindViewHolder(holder);
            TextView titleView = (TextView) holder.findViewById(android.R.id.title);
            if (titleView != null)
                titleView.setTextColor(context.getResources().getColor(R.color.textColor, context.getTheme()));
            holder.itemView.setPadding(CommonUtils.dpToPx(16), holder.itemView.getPaddingTop(), CommonUtils.dpToPx(16), holder.itemView.getPaddingBottom());
        }
    }

    public static class SettingInfo
    {
        public SettingInfo(int settingId, int labelId,
                           int descriptionId, boolean checkable)
        {
            this.settingId = settingId;
            this.settingStringId = null;
            this.labelId = labelId;
            this.descriptionId = descriptionId;
            this.checkable = checkable;
        }

        public String sortKey;

        public boolean enabled = true;
        public boolean visible = true;
        public boolean checked = false;

        public String descriptionStr;
        public String labelStringId;
        public Preference pref;

        public final int  settingId;
        public final String settingStringId;
        public int labelId;
        public int descriptionId;
        public final boolean checkable;
    }

    public SettingsFragment() {
        settingsInfos.put(CHANGE_THEME, new SettingInfo(CHANGE_THEME, R.string.change_theme, 0, false));
        settingsInfos.put(COUNTDOWN_TIMER, new SettingInfo(COUNTDOWN_TIMER, R.string.sleep_timer, 0, true));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundColor, getContext().getTheme()));
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPreferenceManager() == null || getActivity() == null)
            return;

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());

        ArrayList<SettingInfo> visibleSettings = new ArrayList<>();
        List<Preference> preferences = new ArrayList<>();

        visibleSettings.add(settingsInfos.get(CHANGE_THEME));
        visibleSettings.add(settingsInfos.get(COUNTDOWN_TIMER));

        for (SettingInfo settingInfo : visibleSettings) {
            Preference pref = null;
            if (settingInfo.settingId == CHANGE_THEME) {
                pref = new ChangeThemePreference(getContext(), CHANGE_THEME);
                int isNightTheme = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (isNightTheme == Configuration.UI_MODE_NIGHT_YES)
                    pref.setIcon(R.drawable.ic_light_theme);
                else
                    pref.setIcon(R.drawable.ic_dark_theme);

                pref.setOnPreferenceClickListener(preference -> {

                    if (isNightTheme == Configuration.UI_MODE_NIGHT_YES)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    return true;
                });
            } else if (settingInfo.settingId == COUNTDOWN_TIMER) {
                pref = new SwitchPreferenceCompat(getContext());
                int isNightTheme = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (isNightTheme == Configuration.UI_MODE_NIGHT_YES)
                    pref.setIcon(R.drawable.ic_sleep_timer_dark);
                else
                    pref.setIcon(R.drawable.ic_sleep_timer);
                if (countDownTimer != null && minutesUntilFinished > 0)
                    pref.setSummary(App.get().getResources().getQuantityString(R.plurals.minutes_remaining, minutesUntilFinished, minutesUntilFinished));

                if (MediaPlayerService.getState() == Player.STATE_IDLE) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                }

                ((SwitchPreferenceCompat) pref).setChecked(countDownTimer != null);
            }
            if (pref == null)
                continue;

            pref.setTitle(settingInfo.labelId);
            pref.setKey(String.valueOf(settingInfo.settingId));
            if (settingInfo.descriptionId != 0)
                pref.setSummary(settingInfo.descriptionStr = getActivity().getString(settingInfo.descriptionId));
            else if (settingInfo.descriptionStr != null)
                pref.setSummary(settingInfo.descriptionStr);
            pref.setEnabled(settingInfo.enabled);

            pref.setOnPreferenceChangeListener(this);

            preferences.add(pref);
            settingInfo.pref = pref;
        }

        for (Preference p : preferences) {
            root.addPreference(p);
        }

        setPreferenceScreen(root);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(String.valueOf(COUNTDOWN_TIMER))) {
            boolean isEnabled = (Boolean) newValue;
            if (isEnabled) {
                if (MediaPlayerService.getState() == Player.STATE_IDLE) {
                    Toast.makeText(App.get(), R.string.player_not_active_msg, Toast.LENGTH_LONG).show();
                    return false;
                }
                final EditText editText = new EditText(this.getContext());
                AlertDialog dialog = new MaterialAlertDialogBuilder(this.getContext(), R.style.MultiChoiceAlertDialog)
                        .setTitle(App.get().getResources().getString(R.string.sleep_timer))
                        .setMessage(App.get().getResources().getString(R.string.set_sleep_timer_msg))
                        .setView(editText)
                        .setPositiveButton(App.get().getResources().getString(R.string.ok_button), (dialogInterface, i) -> {
                            String editTextInput = editText.getText().toString();
                            long minutesInput;
                            try {
                                minutesInput = Long.parseLong(editTextInput) * 1000 * 60;
                                countDownTimer = new CountDownTimer(minutesInput, 60000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        int minutes = (int) Math.ceil(millisUntilFinished / 1000F / 60F);
                                        minutesUntilFinished = minutes;
                                        if (preference.isVisible()) {
                                            preference.setSummary(App.get().getResources().getQuantityString(R.plurals.minutes_remaining, minutes, minutes));
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        if (MediaPlayerService.getState() == Player.STATE_IDLE)
                                            return;
                                        if (MediaPlayerService.isPlaying())
                                            MediaPlayerService.togglePlayPausePlayer();
                                        MediaPlayerService.killService();
                                        preference.setSummary(null);
                                        minutesUntilFinished = 0;
                                        ((SwitchPreferenceCompat) preference).setChecked(false);
                                    }
                                };
                                countDownTimer.start();
                            } catch (NumberFormatException e) {
                                Toast.makeText(App.get(), R.string.invalid_input_msg, Toast.LENGTH_LONG).show();
                                ((SwitchPreferenceCompat) preference).setChecked(false);
                            }
                        })
                        .setNegativeButton(App.get().getResources().getString(R.string.cancel_button), (dialog1, which) -> ((SwitchPreferenceCompat) preference).setChecked(false))
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (editText.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
                    p.setMargins(CommonUtils.dpToPx(20), p.topMargin, CommonUtils.dpToPx(20), p.bottomMargin);
                    editText.setLayoutParams(p);
                    editText.requestLayout();
                }
                return true;
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            preference.setSummary(null);
        }
        return true;
    }
}
