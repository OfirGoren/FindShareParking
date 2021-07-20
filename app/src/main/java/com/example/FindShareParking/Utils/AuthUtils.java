package com.example.FindShareParking.Utils;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AuthUtils {

    public static String getCurrentUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }
    
}
