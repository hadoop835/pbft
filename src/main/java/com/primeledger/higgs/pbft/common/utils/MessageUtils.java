package com.primeledger.higgs.pbft.common.utils;

import io.netty.handler.codec.base64.Base64Encoder;

import java.io.*;
import java.security.*;

public class MessageUtils {

    /**
     * sign message
     * @param key
     * @param message
     * @return
     */
    public static byte[] signMessage(PrivateKey key,byte[] message){
        byte[] result = null;
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(key);
            signature.update(message);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean verifySignature(PublicKey key,byte[] message,byte[] signature){
        boolean result = false;

        if(key == null||message==null||signature==null){
            return false;
        }

        try {
            Signature signatureEngin = Signature.getInstance("SHA1withRSA");
            signatureEngin.initVerify(key);
            result = verifySignature(signatureEngin,message,signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean verifySignature(Signature signatureEngin,byte[] message,byte[] signatrue) throws SignatureException {
        signatureEngin.update(message);
        return signatureEngin.verify(signatrue);
    }

    public static byte[] objToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream dos = new ObjectOutputStream(bos);
        dos.writeObject(object);

        return bos.toByteArray();
    }

    public static Object byteToObj(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public static byte[] computeDigest(byte[] context) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(context);
    }

}
