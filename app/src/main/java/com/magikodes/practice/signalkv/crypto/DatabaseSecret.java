package com.magikodes.practice.signalkv.crypto;

import androidx.annotation.NonNull;

import com.magikodes.practice.signalkv.util.Hex;

import java.io.IOException;

public class DatabaseSecret {

    private final byte[] key;
    private final String encoded;

    public DatabaseSecret(@NonNull byte[] key) {
        this.key = key;
        this.encoded = Hex.toStringCondensed(key);
    }

    public DatabaseSecret(@NonNull String encoded) throws IOException {
        this.key        = Hex.fromStringCondensed(encoded);
        this.encoded    = encoded;
    }

    public String asString() { return encoded; }

    public byte[] asBytes() { return key; }
}
