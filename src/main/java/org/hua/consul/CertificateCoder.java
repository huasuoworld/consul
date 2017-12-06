package com.hua.sso.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;

import org.apache.axiom.util.base64.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CertificateCoder {  
  
	private static final Logger LOG = LoggerFactory.getLogger(CertificateCoder.class);
    /** 
     * Java密钥库(Java Key Store，JKS)KEY_STORE 
     */  
    public static final String KEY_STORE = "JKS";  
  
    public static final String X509 = "X.509";  
  
    /** 
     * 由KeyStore获得私钥 
     *  
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     * @throws Exception 
     */  
    private static PrivateKey getPrivateKey(String keyStorePath, String alias,  
            String password) throws Exception {  
        KeyStore ks = getKeyStore(keyStorePath, password);  
        PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());  
        return key;  
    }  
  
    /** 
     * 由Certificate获得公钥 
     *  
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */  
    private static PublicKey getPublicKey(String certificatePath)  
            throws Exception {  
        Certificate certificate = getCertificate(certificatePath);  
        PublicKey key = certificate.getPublicKey();  
        return key;  
    }  
  
    /** 
     * 获得Certificate 
     *  
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */  
    private static Certificate getCertificate(String certificatePath)  
            throws Exception {  
        CertificateFactory certificateFactory = CertificateFactory  
                .getInstance(X509);  
        
        InputStream in = CertificateCoder.class.getClassLoader().getResourceAsStream(certificatePath);
//        FileInputStream in = new FileInputStream(certificatePath);  
  
        Certificate certificate = certificateFactory.generateCertificate(in);  
        in.close();  
  
        return certificate;  
    }  
  
    /** 
     * 获得Certificate 
     *  
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     * @throws Exception 
     */  
    private static Certificate getCertificate(String keyStorePath,  
            String alias, String password) throws Exception {  
        KeyStore ks = getKeyStore(keyStorePath, password);  
        Certificate certificate = ks.getCertificate(alias);  
  
        return certificate;  
    }  
  
    /** 
     * 获得KeyStore 
     *  
     * @param keyStorePath 
     * @param password 
     * @return 
     * @throws Exception 
     */  
    private static KeyStore getKeyStore(String keyStorePath, String password)  
            throws Exception {  
    	InputStream is1 = CertificateCoder.class.getClassLoader().getResourceAsStream(keyStorePath);
//        FileInputStream is = new FileInputStream(is1);  
        KeyStore ks = KeyStore.getInstance(KEY_STORE);  
        ks.load(is1, password.toCharArray());  
        is1.close();  
        return ks;  
    }  
  
    /** 
     * 私钥加密 
     *  
     * @param data 
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,  
            String alias, String password) throws Exception {  
        // 取得私钥  
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);  
  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
  
        return cipher.doFinal(data);  
  
    }  
  
    /** 
     * 私钥解密 
     *  
     * @param data 
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath,  
            String alias, String password) throws Exception {  
        // 取得私钥  
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);  
  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
  
        return cipher.doFinal(data);  
  
    }  
  
    /** 
     * 公钥加密 
     *  
     * @param data 
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPublicKey(byte[] data, String certificatePath)  
            throws Exception {  
  
        // 取得公钥  
        PublicKey publicKey = getPublicKey(certificatePath);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
  
        return cipher.doFinal(data);  
  
    }  
  
    /** 
     * 公钥解密 
     *  
     * @param data 
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPublicKey(byte[] data, String certificatePath)  
            throws Exception {  
        // 取得公钥  
        PublicKey publicKey = getPublicKey(certificatePath);  
  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicKey);  
  
        return cipher.doFinal(data);  
  
    }  
  
    /** 
     * 验证Certificate 
     *  
     * @param certificatePath 
     * @return 
     */  
    public static boolean verifyCertificate(String certificatePath) {  
        return verifyCertificate(new Date(), certificatePath);  
    }  
  
    /** 
     * 验证Certificate是否过期或无效 
     *  
     * @param date 
     * @param certificatePath 
     * @return 
     */  
    public static boolean verifyCertificate(Date date, String certificatePath) {  
        boolean status = true;  
        try {  
            // 取得证书  
            Certificate certificate = getCertificate(certificatePath);  
            // 验证证书是否过期或无效  
            status = verifyCertificate(date, certificate);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
  
    /** 
     * 验证证书是否过期或无效 
     *  
     * @param date 
     * @param certificate 
     * @return 
     */  
    private static boolean verifyCertificate(Date date, Certificate certificate) {  
        boolean status = true;  
        try {  
            X509Certificate x509Certificate = (X509Certificate) certificate;  
            x509Certificate.checkValidity(date);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
  
    /** 
     * 签名 
     *  
     * @param keyStorePath 
     * @param alias 
     * @param password 
     *  
     * @return 
     * @throws Exception 
     */  
    public static String sign(byte[] sign, String keyStorePath, String alias,  
            String password) throws Exception {  
        // 获得证书  
        X509Certificate x509Certificate = (X509Certificate) getCertificate(  
                keyStorePath, alias, password);  
        // 获取私钥  
        KeyStore ks = getKeyStore(keyStorePath, password);  
        // 取得私钥  
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password  
                .toCharArray());  
  
        // 构建签名  
        Signature signature = Signature.getInstance(x509Certificate  
                .getSigAlgName());  
        signature.initSign(privateKey);  
        signature.update(sign);  
        return BASE64Util.encodeByte(signature.sign());  
    }  
  
    /** 
     * 验证签名 
     *  
     * @param data 
     * @param sign 
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */  
    public static boolean verify(byte[] data, String sign,  
            String certificatePath) throws Exception {  
        // 获得证书  
        X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);  
        // 获得公钥  
        PublicKey publicKey = x509Certificate.getPublicKey();  
        // 构建签名  
        Signature signature = Signature.getInstance(x509Certificate  
                .getSigAlgName());  
        signature.initVerify(publicKey);  
        signature.update(data);  
  
        return signature.verify(BASE64Util.decodeByte(sign));  
  
    }  
    
   
  
    /** 
     * 验证Certificate 
     *  
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     */  
    public static boolean verifyCertificate(Date date, String keyStorePath,  
            String alias, String password) {  
        boolean status = true;  
        try {  
            Certificate certificate = getCertificate(keyStorePath, alias,  
                    password);  
            status = verifyCertificate(date, certificate);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
    
    /**
     * 公钥加密
     * @param data
     * @param certificatePath
     * @return
     */
    public static String encryptByPuKey(byte[] data, String certificatePath)  {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
    		byte[] encrypt = encryptByPublicKey(data,certificatePath);  
	        out.write(encrypt);
	        byte[] encryptedData = out.toByteArray();
	        String result = CertificateCoder.SafeBase64_encode(Base64Utils.encode(encryptedData));
	        return result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				out.close();
			} catch (Exception e2) {
				LOG.error(e2.getMessage(), e2);
			}
		}
    }
    
    /**
     * 私钥加密
     * @param data
     * @param keyStorePath
     * @param alias
     * @param password
     * @return
     */
    public static String encryptByPrKey(byte[] data, String keyStorePath, String alias, String password) { 
    	try {
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		byte[] encodedData = encryptByPrivateKey(data,  keyStorePath, alias, password); 
	        out.write(encodedData);
	        byte[] encryptedData = out.toByteArray();
	        String result = CertificateCoder.SafeBase64_encode(Base64Utils.encode(encryptedData));
	        return result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
    }
    
    /**
     * 私钥解密
     * @param result
     * @param keyStorePath
     * @param alias
     * @param password
     * @return
     */
    public static String decryptByPrKey(String result, String keyStorePath, String alias, String password) { 
    	try {
    		 byte[] encryptedDataByte = Base64Utils.decode(CertificateCoder.SafeBase64_decode(result));
    		 byte[] decrypt = CertificateCoder.decryptByPrivateKey(encryptedDataByte,  keyStorePath, alias, password);  
 	         String outputStr = new String(decrypt, "UTF-8"); 
 	         return outputStr;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
    }
    
    /**
     * 公钥解密
     * @param result
     * @param certificatePath
     * @return
     */
    public static String decryptByPuKey(String result, String certificatePath)  {
    	try {
    		byte[] encryptedDataByte = Base64Utils.decode(SafeBase64_decode(result));
	        byte[] decodedData = decryptByPublicKey(encryptedDataByte, certificatePath);  
	        String outputStr = new String(decodedData, "UTF-8");   
	        return outputStr;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
    }
    
    /**
     * 签名
     * @param result
     * @param keyStorePath
     * @param alias
     * @param password
     * @return
     */
    public static String sign(String result, String keyStorePath, String alias, String password) { 
    	try {
    		byte[] sign = Base64Utils.decode(SafeBase64_decode(result));
    		String str = sign(sign, keyStorePath, alias, password);
    		return str;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
    }
    
    /**
     * 签名
     * @param result
     * @param sign
     * @param certificatePath
     * @return
     */
    public static boolean verify(String result, String sign, String certificatePath) {
    	try {
    		byte[] resultByte = Base64Utils.decode(SafeBase64_decode(result));
    		boolean boo = verify(resultByte, sign, certificatePath);  
    		return boo;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
    }
  
    /** 
     * 验证Certificate 
     *  
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     */  
    public static boolean verifyCertificate(String keyStorePath, String alias,  
            String password) {  
        return verifyCertificate(new Date(), keyStorePath, alias, password);  
    }  
    
	public static String SafeBase64_encode(String base64str) {
		return base64str.replace("+", "-").replace("/", "_");
	}

	public static String SafeBase64_decode(String base64str) {
		return base64str.replace("-", "+").replace("_", "/");
	}
}  
