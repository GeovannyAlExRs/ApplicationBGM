package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthFirebaseProvider {

    private FirebaseAuth firebaseAuth;

    public AuthFirebaseProvider() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login(String email, String username) {
        return firebaseAuth.signInWithEmailAndPassword(email, username);
    }

    public Task<AuthResult> register (String email, String username) {
        return firebaseAuth.createUserWithEmailAndPassword(email, username);
    }

    public String getUidFirebase() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    public String getFirebaseEmail() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    public FirebaseUser getUserSession() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    public void logout() {
        if (firebaseAuth != null){
            firebaseAuth.signOut();
        }
    }
}
