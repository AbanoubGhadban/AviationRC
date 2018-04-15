package com.example.golden.aviationrc.bluetooth;

import java.util.UUID;

/**
 * Created by Golden on 3/27/2018.
 */

public class ListeningInfo {
    private String mName;
    private UUID mUUID;

    public ListeningInfo(String name, UUID uuid) {
        mName = name;
        mUUID = uuid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(String uuid) {
        this.mUUID = UUID.fromString(uuid);
    }

    public void setUUID(UUID uuid) {
        this.mUUID = uuid;
    }
}
