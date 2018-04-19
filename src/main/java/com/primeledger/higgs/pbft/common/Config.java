package com.primeledger.higgs.pbft.common;

import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

/**
 * @author hanson
 */

@Component
@ConfigurationProperties(prefix = "config")
@EnableConfigurationProperties
public class Config {

    /**
     * every server node info
     */

    private List<NodeInfo> nodeInfos;



    /**
     * server id
     */
    private int id;

    /**
     * 入队列大小，默认200
     */
    private int inQueueSize = 200;


    /**
     * the number of tolerate fault nodes
     */
    private int fault;

    /**
     * the size of client request queue
     */
    private int requestQueueSize = 200;

    /**
     * the private key string of this node
     */
    private String privateKeyString;

    /**
     * the private key of this node
     */
    private PrivateKey privateKey = null;



    /**
     * log path
     */
    private String logPath = null;

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public void setPrivateKeyString(String privateKeyString) {
        this.privateKeyString = privateKeyString;
    }


    public int getInQueueSize() {
        return inQueueSize;
    }

    public void setInQueueSize(int inQueueSize) {
        this.inQueueSize = inQueueSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public List<NodeInfo> getNodeInfos() {
        return nodeInfos;
    }

    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    public NodeInfo getMyNodeInfo() {
        for (NodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.getId() == id) {
                return nodeInfo;
            }
        }
        return null;
    }

    public NodeInfo getNodeInfo(int id) {
        for (NodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.getId() == id) {
                return nodeInfo;
            }
        }
        return null;
    }

    public int[] getInitViewAcceptors() {
        int[] process = new int[nodeInfos.size()];
        for (int i = 0; i < nodeInfos.size(); i++) {
            process[i] = nodeInfos.get(i).getId();
        }
        return process;
    }

    /**
     * @return the private key of the node
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public PrivateKey getPrivateKey() {

        if (privateKey == null) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
                privateKey = keyFactory.generatePrivate(privateKeySpec);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

        }


        return privateKey;
    }

    public PublicKey getPublicKey(int i) {
        if (i < 0 || i > nodeInfos.size()) {
            return null;
        }
        return nodeInfos.get(i).getPublicKey();
    }

    /**
     * @return the initial id of the leader in the cluster
     */
    public int getInitLeader() {
        int leader = Integer.MAX_VALUE;
        for (NodeInfo nodeInfo : nodeInfos) {
            leader = leader > nodeInfo.getId() ? nodeInfo.getId() : leader;
        }
        return leader;
    }

    public int getNodeCount() {
        return nodeInfos.size();
    }

    public int getRequestQueueSize() {
        return requestQueueSize;
    }

    public void setRequestQueueSize(int requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }


    public int getFault() {
        return fault;
    }

    public void setFault(int fault) {
        this.fault = fault;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
