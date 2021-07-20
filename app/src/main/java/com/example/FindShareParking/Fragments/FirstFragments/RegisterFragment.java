package com.example.FindShareParking.Fragments.FirstFragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.FindShareParking.CallBacks.OpenParkingActivityCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.User;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.AuthUtils;
import com.example.FindShareParking.Utils.SharedPrefUtils;
import com.example.FindShareParking.databinding.FragmentRegisterBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    public static final String EMAIL = "email";


    private FirebaseAuth mAuth;
    private OpenParkingActivityCallBack openParkingActivityCallBack;
    private FragmentRegisterBinding binding;
    private Activity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initValues();
        initListeners();

        return view;
    }


    private void initListeners() {
        binding.createAccount.setOnClickListener(clickRegister);
    }

    private void initValues() {
        activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
    }


    public void initNewAccountCallBack(OpenParkingActivityCallBack openParkingActivityCallBack) {
        this.openParkingActivityCallBack = openParkingActivityCallBack;
    }

    private void createAccountAuth(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    // when Sign in success, update UI with the signed-in user's information
                    if (task.isSuccessful()) {
                        //save the name , email  , password of the new user;
                        saveNewUserInFireStore(getFullName(), email, AuthUtils.getCurrentUser());
                        saveLogInDetailSharedPref(email, password);

                        if (openParkingActivityCallBack != null) {
                            //insert to account
                            openParkingActivityCallBack.newAccountRegisterCallBack();
                        }
                        // when sign is fails
                    } else {
                        //  display a message to the user.
                        Toast.makeText(activity, R.string.authentication_failed,
                                Toast.LENGTH_SHORT).show();
                        handleProgressBar(View.GONE);

                    }
                });

    }

    private void saveNewUserInFireStore(String fullName, String email, String userId) {
        FireStoreManager.saveNewUserToFireStore(new User()
                .setName(fullName)
                .setUserId(userId)
                .setEmail(email));


    }

    private void saveLogInDetailSharedPref(String email, String password) {
        SharedPrefUtils.getInstance().setPassword(password);
        SharedPrefUtils.getInstance().putString(EMAIL, email);
    }


    void checkEmailExistsOrNot(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //when there isn't email account with this email
                if (Objects.requireNonNull(task.getResult().getSignInMethods()).size() == 0) {
                    binding.emailRegister.setErrorEnabled(false);
                    createAccountAuth(email, getPassword());

                    // when there is already account with this email
                } else {
                    setAndEnabledErrorMsgTextFiled(R.string.already_account, binding.emailRegister);
                    handleProgressBar(View.INVISIBLE);
                }
                //when the email that was inserted not Invalid
            } else {
                setAndEnabledErrorMsgTextFiled(R.string.invalid_email, binding.emailRegister);
                handleProgressBar(View.INVISIBLE);
            }
        }).addOnFailureListener(e -> handleProgressBar(View.INVISIBLE));
    }


    private void setAndEnabledErrorMsgTextFiled(int msg, TextInputLayout textField) {
        textField.setError(getString(msg));
        textField.setErrorEnabled(true);
    }


    private String getFullName() {
        return Objects.requireNonNull(binding.fullName.getEditText()).getText().toString();
    }

    private String getEmail() {
        return Objects.requireNonNull(binding.emailRegister.getEditText()).getText().toString();
    }

    private String getPassword() {
        return Objects.requireNonNull(binding.passwordRegister.getEditText()).getText().toString();
    }


    View.OnClickListener clickRegister = v -> {
        boolean isEmptyName = checkAndHandleEmptyFullName();
        boolean isEmptyEmail = checkAndHandleEmptyEmail();
        boolean isEmptyOrBelowSixCharacters = checkAndHandlePassword();
        // when the user inserted Good information
        if (!isEmptyName && !isEmptyEmail && !isEmptyOrBelowSixCharacters) {
            handleProgressBar(View.VISIBLE);
            hideKeyBoard();
            // check if email already exists
            checkEmailExistsOrNot(getEmail());


        }


    };

    private void handleProgressBar(int visibility) {
        binding.progressBarRegister.setVisibility(visibility);

    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Hide:
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private boolean checkAndHandleEmptyFullName() {
        String name = getFullName();
        boolean isEmptyName = true;
        if (name.equals("")) {
            setAndEnabledErrorMsgTextFiled(R.string.name_required, binding.fullName);

        } else {
            isEmptyName = false;
            binding.fullName.setErrorEnabled(false);
        }
        return isEmptyName;
    }

    private boolean checkAndHandleEmptyEmail() {
        String email = getEmail();
        boolean isEmptyEmail = true;
        if (email.equals("")) {
            setAndEnabledErrorMsgTextFiled(R.string.email_required, binding.emailRegister);

        } else {
            isEmptyEmail = false;
            binding.emailRegister.setErrorEnabled(false);
        }
        return isEmptyEmail;
    }

    public boolean checkAndHandlePassword() {

        String password = getPassword();
        boolean isEmptyOrBelowSixCharacters = true;
        if (password.equals("") || password.length() < 6) {
            setAndEnabledErrorMsgTextFiled(R.string.password_required, binding.passwordRegister);
        } else {
            isEmptyOrBelowSixCharacters = false;
            binding.passwordRegister.setErrorEnabled(false);
        }
        return isEmptyOrBelowSixCharacters;
    }


}
