package com.quartz.demo.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 9:23 下午
 */
public class RSAUtil {

    private static final String CIPHER_ALGORITHM_RSA = "RSA";
    private static final String PLAIN_TEXT_CHARSET_NAME = "UTF-8";

    public RSAUtil() {
    }

    public static String rsaEncrypt(String str, String publicKey) throws Exception {
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    public static String rsaDecrypt(String str, String privateKey) throws Exception {
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    private static String getPublicKey(String slatKey) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(slatKey.getBytes());
        keyPairGenerator.initialize(1024, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
    }

    private static String getPrivateKey(String slatKey) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(slatKey.getBytes());
        keyPairGenerator.initialize(1024, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
    }

}
