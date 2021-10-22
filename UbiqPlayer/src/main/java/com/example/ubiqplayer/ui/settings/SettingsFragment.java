package com.example.ubiqplayer.ui.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private HashMap<Integer, SettingInfo> settingsInfos = new HashMap<>();

    private static final int CHANGE_THEME = 0;

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
        return true;
    }
}
