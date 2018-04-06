package dauroi.photoeditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

import dauroi.photoeditor.config.DebugOptions;

/**
 * @author Hung Nguyen
 */
@SuppressLint({"DefaultLocale", "TrulyRandom"})
public class SecurityUtils {
    private static final String initializationVector = "1234567890!@#$%^";
    public static final String PASSPHRASE = "abc123$%^";
    private static final int PASSWORD_ITERATION = 2048;
    private static final int KEY_SIZE = 256;

    public static class Signature {
        public String date;
        public String signature;
        public String signedPath;
    }

    private static final byte[] DEFAULT_XOR_MASTER_PASSWORD = new byte[]{44, 26, 28, 6, 18, 1, 30, 79, 36, 5};
    private static final String XOR_KEY = "BigD Inc";
    private static final String characterEncoding = "UTF-8";
    public static final String aesEncryptionAlgorithm = "AES";
    public static final int IV_SIZE = 16;
    public static final int AES128_KEY_SIZE = 16;
    public static final int AES192_KEY_SIZE = 24;
    public static final int AES256_KEY_SIZE = 32;

    public static String genUUID16() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }

    public static String genUUID32() {
        String uuid = genUUID16() + genUUID16();
        return uuid;
    }

    @SuppressLint("TrulyRandom")
    public static String getRandomBase64String() {
        byte[] r = new byte[16];
        new SecureRandom().nextBytes(r);
        String s = org.apache.commons.codec.binary.Base64.encodeBase64String(r);
        return s.trim();
    }

    public static byte[] xorMessage(String message, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
            sb.append((char) (message.charAt(i) ^ key.charAt(i % key.length())));
        String result = sb.toString();
        return result.getBytes();
    }

    public static String unXorMessage(byte[] xorByte, String key) {
        String msg = new String(xorByte);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length(); i++)
            sb.append((char) (msg.charAt(i) ^ key.charAt(i % key.length())));
        String result = sb.toString();
        return result;
    }

    public static String getDefaultPassword() {
        return unXorMessage(DEFAULT_XOR_MASTER_PASSWORD, XOR_KEY);
    }

    private String cipherTransformation = "AES/CBC/PKCS7Padding";
    private int keySize = AES256_KEY_SIZE;
    private String blockType = "CBC";

    public SecurityUtils(String algorithm, String blockMethod, String paddingMethod, int keySize) {
        this.cipherTransformation = algorithm.concat("/").concat(blockMethod).concat("/").concat(paddingMethod);
        this.keySize = keySize;
        this.blockType = blockMethod;

    }

    public SecurityUtils() {
        this.cipherTransformation = "AES/CBC/PKCS7Padding";
        this.keySize = AES256_KEY_SIZE;
        this.blockType = "CBC";
    }

    public byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        if ("CBC".equalsIgnoreCase(this.blockType)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy);
        }

        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        if ("CBC".equalsIgnoreCase(this.blockType)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        }

        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    public byte[] getKeyBytes(String key, int keySize) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[keySize];
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = 0;
        }

        if (key != null && key.length() > 1) {
            byte[] parameterKeyBytes = key.getBytes(characterEncoding);
            System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        }

        return keyBytes;
    }

    public String encrypt(String plainText, String key, String iv)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key, keySize);
        byte[] initVector = getKeyBytes(iv, IV_SIZE);

        return org.apache.commons.codec.binary.Base64.encodeBase64String(encrypt(plainTextbytes, keyBytes, initVector));
    }

    public String encrypt(String plainText, String key)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key, keySize);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        return org.apache.commons.codec.binary.Base64.encodeBase64String(encrypt(plainTextbytes, keyBytes, initVector));
    }

    public String encrypt(String plainText, byte[] key, byte[] initVector) throws Exception {
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] res = encrypt(plainTextbytes, key, initVector);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(res);
    }

    public String encrypt(String plainText, byte[] key) throws Exception {
        if (plainText == null) {
            return null;
        }

        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        byte[] res = encrypt(plainTextbytes, key, initVector);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(res);
    }

    public byte[] encrypt(byte[] plainTextbytes, String hexKey, byte[] iv)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = HexUtil.hexToBytes(hexKey);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        if (iv != null) {
            System.arraycopy(iv, 0, initVector, 0, Math.min(iv.length, initVector.length));
        }
        return encrypt(plainTextbytes, keyBytes, initVector);
    }

    public String decrypt(String encryptedText, String key)
            throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] cipheredBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedText);
        byte[] keyBytes = getKeyBytes(key, keySize);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        return new String(decrypt(cipheredBytes, keyBytes, initVector), characterEncoding);
    }

    public String decrypt(String encryptedText, String key, String iv)
            throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] cipheredBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedText);
        byte[] keyBytes = getKeyBytes(key, keySize);
        byte[] initVector = getKeyBytes(iv, IV_SIZE);
        return new String(decrypt(cipheredBytes, keyBytes, initVector), characterEncoding);
    }

    public String decrypt(String encryptedText, byte[] key, byte[] initVector)
            throws UnsupportedEncodingException, Exception {
        byte[] cipheredBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedText);
        byte[] res = decrypt(cipheredBytes, key, initVector);
        return new String(res, characterEncoding);
    }

    public String decrypt(String encryptedText, byte[] key) throws UnsupportedEncodingException, Exception {
        if (encryptedText == null || encryptedText.length() < 1 || key == null) {
            return null;
        }

        byte[] cipheredBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedText);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        byte[] res = decrypt(cipheredBytes, key, initVector);

        return new String(res, characterEncoding);
    }

    public byte[] decrypt(byte[] cipheredBytes, String hexKey, byte[] iv)
            throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = HexUtil.hexToBytes(hexKey);
        byte[] initVector = new byte[IV_SIZE];
        for (int i = 0; i < initVector.length; i++) {
            initVector[i] = 0;
        }
        if (iv != null) {
            System.arraycopy(iv, 0, initVector, 0, Math.min(iv.length, initVector.length));
        }
        return decrypt(cipheredBytes, keyBytes, initVector);
    }

    /**
     * @param path   does not has date query.
     * @param method
     * @return signed path
     * @throws Exception
     */
    public static Signature signSimplePath(String path, String method) {
        Signature result = new Signature();
        String date = DateTimeUtils.getCurrentDateTimeGMT();
        String signature = "";
        if (path.endsWith("?")) {
            path = path.concat("date=").concat(date);
        } else {
            path = path.concat("&date=").concat(date);
        }

        try {
            signature = SecurityUtils.createSignature(getSecurityCode(), method, path);
            path = path.concat("&signature=").concat(signature);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        result.date = date;
        result.signature = signature;
        result.signedPath = path;

        return result;
    }

    public static String encodeHmacSHA256(String secret, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] data = sha256_HMAC.doFinal(message.getBytes());
            // String hash = Base64.encodeToString(data, Base64.DEFAULT);
            String hash = Base64.encodeToString(data, Base64.DEFAULT);
            return hash;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String createSignature(String key, String httpMethod, String canonicalizedResource) {
        Map<String, String> headers = new HashMap<String, String>();
        return createSignature(key, httpMethod, canonicalizedResource, headers);
    }

    public static String createSignature(String key, String httpMethod, String canonicalizedResource,
                                         Map<String, String> canonicalizedCustomizedHeaders) {
        StringBuilder headers = new StringBuilder();
        if (canonicalizedCustomizedHeaders != null && canonicalizedCustomizedHeaders.size() > 0) {
            List<String> keys = new ArrayList<String>();
            keys.addAll(canonicalizedCustomizedHeaders.keySet());
            Collections.sort(keys);
            final int size = keys.size();
            for (int idx = 0; idx < size; idx++) {
                headers.append(keys.get(idx).trim().toLowerCase());
                headers.append(":");
                headers.append(canonicalizedCustomizedHeaders.get(keys.get(idx).trim()));
                if (idx < size - 1) {
                    headers.append("\n");
                }
            }
        }

        String strToSign = httpMethod + "\n" + canonicalizedResource;
        if (headers.length() > 0) {
            strToSign = strToSign + "\n" + headers.toString();
        }

        final String signature = encodeHmacSHA256(key, strToSign);

        return signature;
    }

    @SuppressLint("TrulyRandom")
    public static String cipherAES128(String pass, String text, boolean encrypt) throws Exception {
        byte[] keyb = pass.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
        Cipher dcipher = Cipher.getInstance("AES");
        byte[] clearbyte = null;
        if (!encrypt) {
            dcipher.init(Cipher.DECRYPT_MODE, skey);
            clearbyte = dcipher.doFinal(HexUtil.hexToBytes(text));
            return new String(clearbyte);
        } else {
            dcipher.init(Cipher.ENCRYPT_MODE, skey);
            clearbyte = dcipher.doFinal(text.getBytes());
            return HexUtil.bytesToHex(clearbyte);
        }
    }

    public static String getSecurityCode() throws Exception {
        // CkUZDyX7JZZCsY71Z4EMjDW4B+Ys= or YzNlNWM1NDFkYzZkNzljNTEzNjYzNTFhNDU2MDkzNGE=
        return cipherAES128(getMasterSecurity(), getSecurity(), false);
    }

    public static String simpleCipher(final String text, final int pass) {
        int size = text.length();
        char[] arr = text.toCharArray();
        char[] out = new char[arr.length];
        for (int idx = 0; idx < size; idx++) {
            out[idx] = (char) (arr[idx] ^ pass);
        }

        return new String(out);
    }

    public static boolean cipher(final String inFilePath, final String outFilePath, final String password,
                                 boolean encrypt) {
        try {
            // start encrypt or decrypt
            byte[] saltBytes = new byte[16];// salt.getBytes("UTF-8");
            byte[] digest = new byte[32];// digest of password
            FileInputStream is = new FileInputStream(inFilePath);
            FileOutputStream os = new FileOutputStream(outFilePath);
            // Need to write the salt to the (encrypted) file. The
            // salt is needed when reconstructing the key for decryption.
            if (!encrypt) {
                is.read(saltBytes);
                is.read(digest);
                byte[] d = SecurityUtils.sha256b(password);
                for (int idx = 0; idx < d.length; idx++)
                    if (d[idx] != digest[idx]) {
                        is.close();
                        os.close();
                        File file = new File(outFilePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        return false;
                    }
            } else {
                SecureRandom random = new SecureRandom();
                random.nextBytes(saltBytes);
                os.write(saltBytes);
                digest = SecurityUtils.sha256b(password);
                os.write(digest);
            }
            byte[] ivBytes = initializationVector.getBytes("UTF-8");
            // Derive the key, given password and salt.
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, PASSWORD_ITERATION, KEY_SIZE);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (encrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            }
            // Read the file and encrypt its bytes.
            // create Cipher
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) > 0) {
                byte[] out = cipher.update(buff, 0, len);
                os.write(out);
            }
            byte[] out = cipher.doFinal();
            if (out != null) {
                os.write(out);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            File file = new File(outFilePath);
            file.delete();
            return false;
        }

        return true;
    }

    public static String cipherText(final String text, final String password, boolean encrypt) {
        try {
            // start encrypt or decrypt
            byte[] saltBytes = "Passla1234".getBytes("UTF-8");
            byte[] ivBytes = initializationVector.getBytes("UTF-8");
            // Derive the key, given password and salt.
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, PASSWORD_ITERATION, KEY_SIZE);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (encrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            }

            byte[] in;
            if (encrypt) {
                in = text.getBytes("UTF-8");
            } else {
                in = HexUtil.hexToBytes(text);
            }
            byte[] out = cipher.doFinal(in);
            String result = HexUtil.bytesToHex(out);
            if (!encrypt) {
                result = new String(out, "UTF-8");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 256 sha1
     *
     * @param input
     * @return digest of input
     * @throws java.security.NoSuchAlgorithmException
     */
    public static byte[] sha256b(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] result = digest.digest(input.getBytes());
        return result;
    }

    /**
     * sha1 with 256 bits
     *
     * @param input
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String sha256s(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static String loadAdsUnitId() {
        StringBuilder sb = new StringBuilder();
        sb.append("ca-app-pub-");
        sb.append("4015988808950288");
        sb.append("/");
        sb.append("1839861258");
        return sb.toString();
    }

    private static String getSecurity() {
        StringBuffer buf = new StringBuffer();
        if (DebugOptions.ENABLE_FOR_DEV) {
            buf.append("193f10127a83312f1559976486cb3d08226bed6d9948cd90efb2d9057ff01873");
        } else {
            buf.append("3ddc107ce01334e55499928f70953c8adeefa98f961370e5b2fc4cba8c90e68c6c6c2fb9061306699eb23c2768309ad3");
        }

        return buf.toString();
    }

    private static String getMasterSecurity() {
        StringBuffer buf = new StringBuffer();
        buf.append("e4b6def964");
        return buf.toString();
    }
}
