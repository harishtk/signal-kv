package com.magikodes.practice.signalkv.keyvalue;

import androidx.annotation.NonNull;

import java.util.Collection;

public interface KeyValuePersistentStorage {
    void writeDataSet(@NonNull KeyValueDataSet dataSet, @NonNull Collection<String> removes);
    @NonNull KeyValueDataSet getDataSet();
}
