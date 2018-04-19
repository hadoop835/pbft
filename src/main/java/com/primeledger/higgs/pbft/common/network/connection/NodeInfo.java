package com.primeledger.higgs.pbft.common.network.connection;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class NodeInfo {


    /**
     * the client listen port
     */
    private int clientPort;

    /**
     * ip address
     */
    private String host;

    /**
     * id of the backup
     */
    private int id;


    /**
     * the server listen port
     */
    private int serverPort;

    /**
     * the public key string of the node
     */
    private String publicKeyString;

    /**
     * the public key of the node
     */
    private PublicKey publicKey = null;


    public String getPublicKeyString() {
        return publicKeyString;
    }

    public void setPublicKeyString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public PublicKey getPublicKey() {
        if (publicKey == null) {
            KeyFactory keyFactory = null;
            try {
                keyFactory = KeyFactory.getInstance("RSA");
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
                publicKey = keyFactory.generatePublic(publicKeySpec);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

        }

        return publicKey;
    }

}
