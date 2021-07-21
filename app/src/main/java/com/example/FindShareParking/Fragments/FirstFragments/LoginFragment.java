package com.example.FindShareParking.Fragments.FirstFragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.FindShareParking.CallBacks.OpenParkingActivityCallBack;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.SharedPrefUtils;
import com.example.FindShareParking.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class LoginFragment extends Fragment {


    private Context context;
    private Activity activity;
    private OpenParkingActivityCallBack openParkingActivityCallBack;
    private BiometricPrompt biometricPrompt;
    private FirebaseAuth mAuth;
    private BiometricPrompt.PromptInfo promptInfo;
    private FragmentLoginBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initValues();
        initListeners();
        checkIfThePhoneSupportFingerPrint();
        listenerFingerPrint();
        dialogFingerPrint();


        return view;
    }

    private void initValues() {
        mAuth = FirebaseAuth.getInstance();
        activity = getActivity();
    }

    private void initListeners() {
        binding.loginBtn.setOnClickListener(logIn);
        binding.fingerPrintBtn.setOnClickListener(fingerLogIn);

    }

    private void dialogFingerPrint() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.log_in))
                .setSubtitle(getString(R.string.use_finger_print))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();

    }

    private void listenerFingerPrint() {
        Executor executor = ContextCompat.getMainExecutor(this.context);
        biometricPrompt = new BiometricPrompt(LoginFragment.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                handleShowProgressBarAndFingerPrintIcon(View.VISIBLE, View.INVISIBLE);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                String email = SharedPrefUtils.getInstance().getString(RegisterFragment.EMAIL, null);
                String password = SharedPrefUtils.getInstance().getPassword();

                if (password != null && email != null) {
                    signInWithEmailAndPassword(email, password);
                    handleShowProgressBarAndFingerPrintIcon(View.INVISIBLE, View.VISIBLE);

                } else {
                }

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

    }

    private void handleShowProgressBarAndFingerPrintIcon(int visibilityFingerPrint, int visibilityProgressBar) {
        binding.fingerPrintBtn.setVisibility(visibilityFingerPrint);
        binding.progressBarLogIn.setVisibility(visibilityProgressBar);

    }

    private void checkIfThePhoneSupportFingerPrint() {
        BiometricManager biometricManager = BiometricManager.from(context.getApplicationContext());
        int i = biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        if (i == BiometricManager.BIOMETRIC_SUCCESS) {
            Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
        } else if (i == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            binding.fingerPrintBtn.setVisibility(View.GONE);
            Log.e("MY_APP_TAG", "No biometric features available on this device.");
        } else if (i == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            binding.fingerPrintBtn.setVisibility(View.GONE);
            Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
        } else if (i == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            binding.fingerPrintBtn.setVisibility(View.GONE);
        }


    }


    View.OnClickListener fingerLogIn = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (biometricPrompt != null) {
                biometricPrompt.authenticate(promptInfo);
            }
        }
    };

    public void initOpenParkingActivityCallBack(OpenParkingActivityCallBack openParkingActivityCallBack) {
        this.openParkingActivityCallBack = openParkingActivityCallBack;
    }

    View.OnClickListener logIn = v -> {

        boolean isPasswordProper = checkAndHandleEmptyPassword();
        boolean isEmptyEmail = checkAndHandleEmptyEmail(R.string.email_required);

        if (!isPasswordProper && !isEmptyEmail) {
            signInWithEmailAndPassword(getEmail(), getPassword());
            //show progress bar
            handleShowProgressBarAndFingerPrintIcon(View.INVISIBLE, View.VISIBLE);
            hideKeyBoard();
        }

    };


    private boolean checkAndHandleEmptyEmail(int msg) {
        String email = getEmail();
        boolean isEmptyEmail = true;
        if (email.equals("")) {
            binding.emailLogin.setErrorEnabled(true);
            setAndEnabledErrorMsgTextFiled(msg, binding.emailLogin);
        } else {
            binding.emailLogin.setErrorEnabled(false);
            isEmptyEmail = false;
        }
        return isEmptyEmail;
    }


    private boolean checkAndHandleEmptyPassword() {
        String password = getPassword();
        boolean isEmptyOrBelowSixCharacters = true;
        if (password.equals("") || password.length() < 6) {
            binding.passwordLogin.setErrorEnabled(true);
            setAndEnabledErrorMsgTextFiled(R.string.password_required, binding.passwordLogin);
        } else {
            binding.passwordLogin.setErrorEnabled(false);
            isEmptyOrBelowSixCharacters = false;
        }
        return isEmptyOrBelowSixCharacters;

    }


    private String getEmail() {
        return Objects.requireNonNull(binding.emailLogin.getEditText()).getText().toString();
    }

    private String getPassword() {
        return Objects.requireNonNull(binding.passwordLogin.getEditText()).getText().toString();
    }


    private void setAndEnabledErrorMsgTextFiled(int msg, TextInputLayout textField) {
        textField.setError(getString(msg));
        textField.setErrorEnabled(true);
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        if (openParkingActivityCallBack != null) {
                            //open the parkingActivity
                            openParkingActivityCallBack.newAccountRegisterCallBack();
                        }
                    } else {
                        //show finger print
                        handleShowProgressBarAndFingerPrintIcon(View.VISIBLE, View.INVISIBLE);
                        // If sign in fails, display a message to the user.
                        checkAndHandleEmptyEmail(R.string.invalid_email);

                    }
                });
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Hide:
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
