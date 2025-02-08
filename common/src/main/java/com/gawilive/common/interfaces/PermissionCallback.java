package com.gawilive.common.interfaces;

import java.util.HashMap;
import java.util.List;

public abstract class PermissionCallback {

    public abstract void onAllGranted();

    public void onResult(HashMap<String, Boolean> resultMap) {

    }

    public abstract void onDenied(List<String> deniedPermissions);
}
