package utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class KeyUtils {


    // 生成AES加密的密钥
    public static String generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 可以使用128, 192, 或 256位的AES
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成密钥时出错", e);
        }
    }
//    public static void main(String[] args) {
//        String secretKey = generateSecretKey();
//        System.out.println("生成的密钥: " + secretKey);
//    }
}
