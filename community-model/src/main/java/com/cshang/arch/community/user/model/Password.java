package com.cshang.arch.community.user.model;

import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.passay.PasswordData;

public class Password {

    private static final String SaltChars =
            "012456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SaltLengthForBCrypt = 22;

    // List of valid characters. Found in HashPbkdf2
    static final char[] VALID_PASSWORD_CHARS =
            ("! #$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "[]^_`abcdefghijklmnopqrstuvwxyz{|}~").toCharArray();

    private String salt;
    private String encryptedPassword;

    public Password() {

    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Password)) {
            return false;
        }

        Password otherPW = (Password) other;

        if (Objects.equals(salt, otherPW.salt)) {
            if (Objects.equals(encryptedPassword, otherPW.encryptedPassword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Not a perfect implementation since it doesn't look at the salt but one that is valid
        // nevertheless
        return Objects.hashCode(encryptedPassword);
    }

    public boolean checkClearPassword(String password) {
        String salt = getSalt();
        if (salt == null || !isValidPasswordAttempt(password)) {
            // this user hasn't gotten a properly set up ShopStyle password encryption
            // authentication always fails here
            return false;
        }

        String encryptedPassword = getEncryptedPassword();

        // first we try our current standard - BCrypt
        boolean result = false;
        String encrypted = null;
        if (salt.length() == SaltLengthForBCrypt) {
            // the salt has the expected length for the BCrypt algorithm, go ahead and try
            encrypted = encryptPasswordWithBCrypt(salt, password);
            result = encrypted.equals(encryptedPassword);
        }

        return result;
    }

    public void setClearPassword(String password) {
        if (!isValidClearPassword(password)) {
            throw new IllegalArgumentException(
                    "password must be at least 8 characters and must not contain any space.");
        }

        String salt = generateSalt();
        setSalt(salt);

        setEncryptedPassword(encryptPasswordWithBCrypt(salt, password));
    }

    public String generateSalt() {
        Random random = new Random();

        StringBuilder saltBuilder = new StringBuilder();
        for (int ii = 0; ii < SaltLengthForBCrypt; ii++) {
            int rand = random.nextInt(SaltChars.length());
            saltBuilder.append(SaltChars.charAt(rand));
        }

        return saltBuilder.toString();
    }

    public static String encryptPasswordWithBCrypt(String salt, String password) {
        // the "$2a$07$ makes us match the Sugar algorithm (2^7 rounds of blowfish)
        return BCrypt.hashpw(password, "$2a$07$" + salt);
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public static boolean isValidClearPassword(final String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        return validate(password);
    }

    public static boolean isValidPasswordAttempt(final String password) {
        return StringUtils.containsOnly(password, VALID_PASSWORD_CHARS) && password.length() >= 1;
    }

    public static boolean validate(String password) {
        return PasswordValidator.VALIDATOR.validate(new PasswordData(password)).isValid();
    }

}
