package com.yx.p2p.ds.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:RAS非对称加密解密
 * 来自：Spring Boot RSA 非对称加密，https://blog.csdn.net/zhangFG77/article/details/84307733，
 * @author: yx
 * @date: 2020/04/15/12:50
 */
public class RASUtil {

    private static final String RSA_ALGORITHM = "RSA";

    private static final String CHARSET = "UTF-8";

   /**
       * @description: 产生RSA公钥和私钥
       * @author:  YX
       * @date:    2020/04/15 14:02
       * @param: keySize
       * @return: java.util.Map<java.lang.String,java.lang.String>
       * @throws: 
       */
    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        RSAPublicKey rsp= (RSAPublicKey)keyPair.getPublic();
        BigInteger bit= rsp.getModulus();
        byte[] b=bit.toByteArray();
        byte[] deBase64Value=Base64.encodeBase64(b);
        String retValue= new String(deBase64Value);
        keyPairMap.put("model",retValue);

        return keyPairMap;
    }


    private static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
        * @description: RSA分割code
        * @author:  YX
        * @date:    2020/04/15 14:05
        * @param: cipher
    * @param: opmode
    * @param: datas
    * @param: keySize
        * @return: byte[]
        * @throws:
        */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        byte[] resultDatas = null;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            resultDatas = out.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }finally {
            if(out != null){
                try{
                    out.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return resultDatas;
    }

    //公钥加密
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * @Title: privateDecrypt
     * @Description: 私钥解密
     * @DateTime 2018年11月19日 下午7:49:36
     * @param data
     * @param privateKey
     * @return
     */
    public static String privateDecrypt2(String data, String privateKey){
        try{
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE,
                    Base64.decodeBase64(data), rsaPrivateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    public static void main(String[] args) throws Exception {
        //main1();//String加密解密
        main2();//Object加密解密
    }

    private static void main1() throws Exception {
        Map<String, String> keyPairMap = createKeys(512);
        //String publicKey = keyPairMap.get("publicKey");
        //String privateKey = keyPairMap.get("privateKey");
        // System.out.println("公钥" + publicKey);
        // System.out.println("私钥" + privateKey);
        //公钥：
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJqvwt9ZrgqteyKXLtzQJVJaLmyfW8QKpyn1Ru1vHEZRrxLn_P4bG5zhDxgDF5YjlXKbLAjiNqoaxWuWtp5pC3cCAwEAAQ";
        //私钥：
        String privateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmq_C31muCq17Ipcu3NAlUloubJ9bxAqnKfVG7W8cRlGvEuf8_hsbnOEPGAMXliOVcpssCOI2qhrFa5a2nmkLdwIDAQABAkApx5ElzE4SujqI5DaXE20RKiRh2ETr1UjDL2lh1eHYJ-nAy5QbuUClScWQ_b-HyZzYDWp_CxLDGJFUry4qX2tBAiEA3O7eRO5VcAJybfq6IDtqmU7cVLUnm_RMSezcvOr4r2ECIQCzPRxvT6IWW3iU5aS1YiPH8w-ijAIyQoWDW-S6hVph1wIgXJ_4p8WUJEWiW-GGLwU6B9Q8I3dfh87APS4EzV9lq4ECICQlDrbjuCwKBicAqFZrlAueWGjPEyJPh90ViuqSOcfBAiEA2Yd95fAI6M--0vCGlvmMK4aXBQ_PwVICRTObhumzUIM";

        String data = "abc122";

        //1.用公钥加密

        String encode= publicEncrypt(data, publicKey);
        System.out.println("-----加密结果----" + encode);

        //2.用私钥解密

        String decodeResult= privateDecrypt2(encode,privateKey);
        System.out.println("-----解密结果----" + decodeResult);
    }



    //公钥加密
    public static String publicEncrypt(Object data, String publicKey){
        try{
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE,
                    ObjectUtil.objToByteArray(data), rsaPublicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }



    //解密
    public static Object privateDecrypt(String data, String privateKey){
        try{
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] bytes = rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), rsaPrivateKey.getModulus().bitLength());
            return ObjectUtil.bytesToObject(bytes);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    //Object加密解密
    private static void main2() throws Exception{
        //公钥：
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJqvwt9ZrgqteyKXLtzQJVJaLmyfW8QKpyn1Ru1vHEZRrxLn_P4bG5zhDxgDF5YjlXKbLAjiNqoaxWuWtp5pC3cCAwEAAQ";
        //私钥：
        String privateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmq_C31muCq17Ipcu3NAlUloubJ9bxAqnKfVG7W8cRlGvEuf8_hsbnOEPGAMXliOVcpssCOI2qhrFa5a2nmkLdwIDAQABAkApx5ElzE4SujqI5DaXE20RKiRh2ETr1UjDL2lh1eHYJ-nAy5QbuUClScWQ_b-HyZzYDWp_CxLDGJFUry4qX2tBAiEA3O7eRO5VcAJybfq6IDtqmU7cVLUnm_RMSezcvOr4r2ECIQCzPRxvT6IWW3iU5aS1YiPH8w-ijAIyQoWDW-S6hVph1wIgXJ_4p8WUJEWiW-GGLwU6B9Q8I3dfh87APS4EzV9lq4ECICQlDrbjuCwKBicAqFZrlAueWGjPEyJPh90ViuqSOcfBAiEA2Yd95fAI6M--0vCGlvmMK4aXBQ_PwVICRTObhumzUIM";

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("orderSn","invest1213123123");
        map.put("status","ok");

        //1.用公钥加密
        String encode= publicEncrypt(map, publicKey);
        System.out.println("-----加密结果----" + encode);

        //2.用私钥解密
        Object decodeResult= privateDecrypt(encode,privateKey);
        System.out.println("-----解密结果----" + decodeResult);
    }

}
