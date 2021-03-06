/*
 * Copyright (C) 2017 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.dutweaks.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Ticker extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_SHOW_TICKER = "status_bar_show_ticker";
    private static final String ALWAYS_HEADSUP_DIALER = "always_headsup_dialer";

    private ListPreference mShowTicker;
    private SwitchPreference mAlwaysHeadsupDialer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ticker);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mShowTicker = (ListPreference) findPreference(STATUS_BAR_SHOW_TICKER);
        mShowTicker.setOnPreferenceChangeListener(this);
        int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER,
                0, UserHandle.USER_CURRENT);
        mShowTicker.setValue(String.valueOf(tickerMode));
        mShowTicker.setSummary(mShowTicker.getEntry());

        mAlwaysHeadsupDialer = (SwitchPreference) findPreference(ALWAYS_HEADSUP_DIALER);

        Context context = getActivity();
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {

            mAlwaysHeadsupDialer.setChecked(Settings.System.getInt(resolver,
                    Settings.System.ALWAYS_HEADSUP_DIALER, 0) == 1);
            mAlwaysHeadsupDialer.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mAlwaysHeadsupDialer);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DIRTYTWEAKS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mShowTicker)) {
            int tickerMode = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode,
                    UserHandle.USER_CURRENT);
            int index = mShowTicker.findIndexOfValue((String) newValue);
            mShowTicker.setSummary(mShowTicker.getEntries()[index]);
            return true;
        } else if (preference == mAlwaysHeadsupDialer) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), ALWAYS_HEADSUP_DIALER,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
