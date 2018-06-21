package com.example.angel.silentplaces.utils;

import com.master.permissionhelper.PermissionHelper;

public class Permissions {

    public static final String ALL_PERMISSION_GRANTED = "all_permision";

    public interface onRequestPermissions {
        void permissionGranted(Boolean allGranted, String[] string);

        void permissionDenied();


    }

    public static void RequestPermuissions(PermissionHelper permissionHelper, final onRequestPermissions onRequest) {


        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                onRequest.permissionGranted(true, null);
            }

            @Override
            public void onIndividualPermissionGranted(String[] strings) {
                onRequest.permissionGranted(false, strings);
            }

            @Override
            public void onPermissionDenied() {
                onRequest.permissionDenied();

            }

            @Override
            public void onPermissionDeniedBySystem() {
                onRequest.permissionDenied();
            }
        });
    }
}
