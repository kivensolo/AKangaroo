package com.zeke.kangaroo.utils;


import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 常用加密工具类
 */
public class EncryptTools {
	// <editor-fold defaultstate="collapsed" desc="变量定义">
	private static final String TAG = "EncryptTools";
	//RSA
	static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	//AES
	static final String AES_TRANSFORMATION = "AES/ECB/NoPadding"; //"AES/CBC/NoPadding";
	//DES & 3DES
	static final String DES_ALGORITHM = "DESede";
	static final String DES_TRANSFORMATION = "DESede/ECB/PKCS5Padding";

	// </editor-fold>
	public static PublicKey getPublicKey(String publicKeyString) {
		byte[] keyBytes;
		try {
			keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
		}
		return null;
	}

	public static PrivateKey getPrivateKey(String privateKeyString) {
		byte[] keyBytes;
		try {
			keyBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			return keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取指定长度的随机字符串
	 * @param length 生成字符串的长度
	 * @return	随机结果的字符串
	 */
	public static String getRandomString(int length) {
		String base = "abcdef0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
// <editor-fold defaultstate="collapsed" desc="AES">
	public static int encryptAES(byte[] dataBytes, String pass, String init, OutputStream outputStream) throws Exception {
		SecretKeySpec keyspec = new SecretKeySpec(HexUtils.hexString2Bytes(pass), "AES");
		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, keyspec);
		int blockSize = cipher.getBlockSize();
		int dataLength = 0;

		for (int i = 0; i < dataBytes.length; i += blockSize) {
			byte[] data;
			if (i + blockSize >= dataBytes.length) {
				byte[] padData = new byte[blockSize];
				Arrays.fill(padData, (byte) 0);

				System.arraycopy(dataBytes, i, padData, 0, dataBytes.length - i);
				data = cipher.doFinal(padData);
			} else {
				data = cipher.update(dataBytes, i, blockSize);
			}

			outputStream.write(data);
			dataLength += data.length;
		}
		return dataLength;
	}


	public static int decryptAES(InputStream dataStream, String pass, String init, OutputStream outputStream) throws Exception {
		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		SecretKeySpec keyspec = new SecretKeySpec(HexUtils.hexString2Bytes(pass), "AES");
		cipher.init(Cipher.DECRYPT_MODE, keyspec);

		final int blockSize = cipher.getBlockSize();
		final byte[] buf = new byte[blockSize];
		int dataLength = 0;
		while (true) {
			int len = 0;
			while (len != blockSize) {
				int readBytes = dataStream.read(buf, len, buf.length - len);
				if (readBytes <= 0) {
					break;
				}
				len += readBytes;
			}


			if (len <= 0) {
				final byte[] buffer = cipher.doFinal();
				if (buffer != null) {
					outputStream.write(buffer);
					dataLength += buffer.length;
				}
				break;
			} else {
				final byte[] buffer = cipher.update(buf);
				outputStream.write(buffer);
				dataLength += buffer.length;
			}
		}
		return dataLength;
	}
// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="RSA">
	public static byte[] RSA_ENCRYPT(String data, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return cipher.doFinal(data.getBytes());
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static String RAS_DECRYPT(byte[] data, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(cipher.doFinal(data));
		} catch (Exception e) {
			return "";
		}
	}
	// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="SHA-1/SHA-256">

	// 32 bytes from sha-256 -> 64 hex chars.
	private static final char[] SHA_256_CHARS = new char[64];
	// 20 bytes from sha-1 -> 40 chars.
	private static final char[] SHA_1_CHARS = new char[40];

	/**
	 * @param bytes  Given byte array
	 * @return The hex string of the given byte array representing a SHA256 hash.
	 */
	public static String sha256BytesToHex(byte[] bytes) {
		return HexUtils.bytesToHex(bytes, SHA_256_CHARS);
	}

    /**
     * Returns the hex string of the given byte array representing a SHA1 hash.
	 * @param bytes  byte array
	 * @return Hex string
     */
    public static String sha1BytesToHex(byte[] bytes) {
        return HexUtils.bytesToHex(bytes, SHA_1_CHARS);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="SHA1withRSA">
	public static byte[] sha1WithRsa(byte[] data, PrivateKey privateKey) {
		try {
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
		}
		return null;
	}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="MD5">
    //Md5 加密法一
    public synchronized static String MD5WithUpdate(String src) {
        try {
            byte[] btInput = src.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
            mdInst.update(btInput);
			// 获得密文字节数组
            byte[] md = mdInst.digest();
            return HexUtils.byte2HexFast(md);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //Md5 加密法二
	public static String MD5(String content) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("md5");
			byte[] md5 = md.digest(content.getBytes());
			return HexUtils.byte2HexFast(md5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="DES">
	/**
	 * DES加密
	 * @param message  加密信息
	 * @param key      加密密钥
	 * @return         加密后的返回数据
	 */
	private static String encryptDES(String message, String key){
		try {
			Log.i(TAG,"encrypt_DES message:" + message + ", key:" + key);
			byte[] arrayOfByte1 = HexUtils.hexString2Bytes(message);
			Cipher cipher = Cipher.getInstance(DES_ALGORITHM);

			byte[] keyBytes = getKeyBytes(key);
			SecretKeySpec desKeySpec = new SecretKeySpec(keyBytes, DES_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, desKeySpec);
			byte[] result = cipher.doFinal(arrayOfByte1);
			return new String(result);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decodeDes(byte[] b, String secretKey) throws Exception {
		DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes("UTF-8"));
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
		SecretKey deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		byte[] decryptData = cipher.doFinal(b);
		return new String(decryptData, "UTF-8");

	}

	/**
	 * 3DES-ECB encrypt.
	 * Before encryption, need to convert the target string into a byte array;
	 * After encryption, the result must be converted to a hex string;
	 *
	 * @param src String to be encrypted
	 *            In general, there are random numbers in the encrypted text;
	 *
	 * @param key Original key string; The key must contain at least 24 characters;
	 * @return Encrypted string
	 */
	public static String encrypt3DES(final String src, final String key) throws Exception {
		byte[] keyByte = getKeyBytes(key);
		DESedeKeySpec dks = new DESedeKeySpec(keyByte);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, securekey);
		byte[] encryptedBytes = cipher.doFinal(src.getBytes());
		return HexUtils.byte2HexExt(encryptedBytes);
	}

	/**
	 * 3DES-ECB decrypt.
	 *
	 * @param str Encrypted string;
	 * @param originalKey Original key string; The key must contain at least 24 characters;
	 * @return The decrypted string;
	 */
	public static String decrypt3DES(String str, String originalKey) {
		try {
			byte[] keyByte = getKeyBytes(originalKey);
			byte[] strByte = HexUtils.hexString2Bytes(str.toLowerCase());
			SecretKey deskey = new SecretKeySpec(keyByte, DES_ALGORITHM);
			Cipher c1 = Cipher.getInstance(DES_TRANSFORMATION);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			byte[] str2 = c1.doFinal(strByte);
			return new String(str2, "UTF-8");
		} catch (NoSuchAlgorithmException
				| NoSuchPaddingException
				| BadPaddingException
				| IllegalBlockSizeException
				| InvalidKeyException
				| UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取密钥的字节数组
	 * 密钥长度不足24字节时，右补ASCII字符“0”
	 * @param strKey 密钥
	 * @return 密钥的字节数组数据
	 */
	public static byte[] getKeyBytes(String strKey) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[24];
		byte[] srcBytes = strKey.getBytes("UTF-8");
		System.arraycopy(srcBytes, 0, keyBytes, 0, Math.min(keyBytes.length, srcBytes.length));
		return keyBytes;
		//int byteKeyLength = 24;
		//StringBuilder strKeyBuilder = new StringBuilder(strKey);
		//for (int i = 0; i < byteKeyLength; i++) {
		//	if (strKeyBuilder.length()-1<i){
		//		strKeyBuilder.append("0");
		//	}
		//}
		//strKey = strKeyBuilder.toString();
		//return strKey.getBytes("UTF-8");
	}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Base64">
    // 通过路径生成Base64文件
    public static String getBase64ForFilePath(String path) {
        String base64 = "";
        try {
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }
// </editor-fold>
}
