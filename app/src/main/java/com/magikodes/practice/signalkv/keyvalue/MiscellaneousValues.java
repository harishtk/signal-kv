package com.magikodes.practice.signalkv.keyvalue;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public final class MiscellaneousValues extends SignalKvStoreValues {

    private static final String USER_TYPED_VALUE   = "misc_user_typed_value";

    MiscellaneousValues(@NonNull KeyValueStore store) { super(store); }

    public void setUserTypedValue(String value) {
        putString(USER_TYPED_VALUE, value);
    }

    public String getUserTypedValue() {
        return getString(USER_TYPED_VALUE, "");
    }

    @Override
    void onFirstEverAppLaunch() { putString(USER_TYPED_VALUE, ""); }

    @NonNull
    @Override
    List<String> getKeysToIncludeInBackup() { return Collections.emptyList(); }
}
