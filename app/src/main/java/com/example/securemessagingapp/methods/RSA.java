package com.example.securemessagingapp.methods;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSA {

    private static final String ALGORITHM = "RSA";

//    public static void main(String[] args) {
//        try {
//            KeyPair keyPair = generateKeyPair();
//            String publicKeyStr = keyToString(keyPair.getPublic());
//            String privateKeyStr = keyToString(keyPair.getPrivate());
//
//            System.out.println("Public Key: " + publicKeyStr);
//            System.out.println("Private Key: " + privateKeyStr);
//
//            String originalMessage = "Hello, RSA!";
//            String encryptedMessage = encrypt(originalMessage, publicKeyStr);
//            String decryptedMessage = decrypt(encryptedMessage, privateKeyStr);
//
//            System.out.println("Original Message: " + originalMessage);
//            System.out.println("Encrypted Message: " + encryptedMessage);
//            System.out.println("Decrypted Message: " + decryptedMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    /*public static String encrypt(String message, String publicKeyStr) throws Exception {
        PublicKey publicKey = stringToPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        //System.out.println(Base64.getEncoder().encodeToString(encryptedBytes));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }*/
    //To modify the function to avoid throwing an error, you can catch the exception and handle it gracefully. Here's the modified function:

           // ```java
    public static String encrypt(String message, String publicKeyStr)
        throws Exception {
            PublicKey publicKey = stringToPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
    }
///```

    public static String decrypt(String encryptedMessage, String privateKeyStr) throws Exception {
        PrivateKey privateKey = stringToPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    public static PublicKey stringToPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        return java.security.KeyFactory.getInstance(ALGORITHM).generatePublic(new java.security.spec.X509EncodedKeySpec(keyBytes));
    }

    public static PrivateKey stringToPrivateKey(String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        return java.security.KeyFactory.getInstance(ALGORITHM).generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(keyBytes));
    }

    public static String keyToString(java.security.Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
