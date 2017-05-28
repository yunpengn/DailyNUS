package ind.hailin.dailynus.utils;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import ind.hailin.dailynus.exception.DesException;

/**
 * Created by hailin on 2017/5/27.
 * This is a util class for DES encryption and decryption
 */

public class DesEncryption {
    public static final String TAG = "DesEncryption";

    private static String key = "ind.hailin.helix@latibrodd";

    public static byte[] encryption(String encryStr) throws DesException {
        try {
            SecretKey secretKey = generateSecretKey();
            Cipher desCipher = Cipher.getInstance("DESede");

            byte[] content = encryStr.getBytes();

            desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] textEncryted = desCipher.doFinal(content);

            return Base64.encodeBase64(textEncryted);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        throw new DesException();
    }

    public static String decryption(String decryStr) throws DesException {
        try {
            SecretKey secretKey = generateSecretKey();
            Cipher desCipher = Cipher.getInstance("DESede");

            byte[] content = Base64.decodeBase64(decryStr.getBytes());

            desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] textDncryted = desCipher.doFinal(content);

            return new String(textDncryted, "utf-8");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        throw new DesException();
    }

    private static SecretKey generateSecretKey() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        byte[] keyBytes = key.getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return secretKey;
    }
}
