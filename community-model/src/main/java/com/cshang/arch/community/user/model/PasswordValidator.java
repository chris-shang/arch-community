package com.cshang.arch.community.user.model;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.passay.LengthRule;
import org.passay.WhitespaceRule;

public class PasswordValidator {

    private final String newPassword;
    private final String newPasswordConfirm;

    private boolean currentPasswordValid;
    private final boolean isValidNewPassword;
    private final boolean isValidPasswordConfirm;

    public static final org.passay.PasswordValidator VALIDATOR
            = new org.passay.PasswordValidator(Arrays.asList(
            // length must between 8 and 50 characters
            // see: https://security.stackexchange.com/questions/39849/does-bcrypt-have-a-maximum-password-length)
            new LengthRule(8, 50),
            // no whitespace
            new WhitespaceRule()));

    @Deprecated
    public PasswordValidator(final User user, final boolean passwordRequired,
                             final String newPassword, final String newPasswordConfirm) {
        this(passwordRequired, newPassword, newPasswordConfirm);
    }

    public PasswordValidator(final boolean passwordRequired, final String newPassword,
                             final String newPasswordConfirm) {
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;

        final boolean isEmptyNewPassword = StringUtils.isEmpty(newPassword);

        if (passwordRequired) {
            if (isEmptyNewPassword) {
                isValidNewPassword = false;
                isValidPasswordConfirm = true;
            } else {
                isValidNewPassword = Password.isValidClearPassword(newPassword);
                isValidPasswordConfirm = newPassword.equals(newPasswordConfirm);
            }
        } else {
            // for update, newPasswordString may be empty, but if not must pass muster.
            if (isEmptyNewPassword) {
                isValidNewPassword = true;
                isValidPasswordConfirm = true;
            } else {
                isValidNewPassword = Password.isValidClearPassword(newPassword);
                isValidPasswordConfirm = newPassword.equals(newPasswordConfirm);
            }
        }
        currentPasswordValid = true;
    }

    public PasswordValidator(final User user, final String currentPassword,
                             final boolean passwordRequired, final String newPassword,
                             final String newPasswordConfirm) {
        this(passwordRequired, newPassword, newPasswordConfirm);

        // check that the provided current password matches the one stored in the database
        currentPasswordValid = user.getPassword().checkClearPassword(currentPassword);
    }

    public boolean isValid() {
        return isValidNewPassword && isValidPasswordConfirm;
    }

    public boolean isValidNewPassword() {
        return isValidNewPassword;
    }

    public boolean isValidPasswordConfirm() {
        return isValidPasswordConfirm;
    }

    public boolean isCurrentPasswordValid() {
        return currentPasswordValid;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

}
