package unl.edu.cc.workunity.util;

import unl.edu.cc.workunity.exception.EncryptorException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidad para encriptación de contraseñas
 */
public class EncryptorManager {

    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_PRIVATE_KEY = "_tuClaveEnBase64"; // Las claves deben contener 16 caracteres

    private static SecretKey getSecretKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(DEFAULT_PRIVATE_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    /**
     * Encripta un texto usando AES
     */
    public static String encrypt(String text) throws EncryptorException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            // Convertimos el mensaje a bytes y lo encriptamos
            byte[] encryptText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            // Convertimos el resultado a Base64 (para que sea texto legible)
            return Base64.getEncoder().encodeToString(encryptText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                 | InvalidKeyException e) {
            throw new EncryptorException(e.getMessage(), e);
        }
    }

    /**
     * Desencripta un texto encriptado con AES
     */
    public static String decrypt(String textEncrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        // Convertimos el Base64 a bytes normales
        byte[] bytesEncrypted = Base64.getDecoder().decode(textEncrypted);
        // Desencriptamos los bytes
        byte[] bytesOriginals = cipher.doFinal(bytesEncrypted);
        // Convertimos los bytes a texto normal
        return new String(bytesOriginals, StandardCharsets.UTF_8);
    }
}

