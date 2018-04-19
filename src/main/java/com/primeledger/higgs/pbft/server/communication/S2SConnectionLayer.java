package com.primeledger.higgs.pbft.server.communication;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import com.primeledger.higgs.pbft.server.communication.ServerConnectionSession;
import com.primeledger.higgs.pbft.server.recover.IRecoverable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;


public class S2SConnectionLayer extends Thread {

    private BlockingQueue inQueue;

    private Config config;
    private ServerSocket serverSocket = null;
    private boolean doWork = true;

    private Hashtable<Integer, ServerConnectionSession> connections = new Hashtable<Integer, ServerConnectionSession>();

    private IRecoverable recoverable = null;

    public S2SConnectionLayer(Config config, BlockingQueue inQueue, IRecoverable recoverable) throws IOException {
        this.inQueue = inQueue;
        this.config = config;
        this.recoverable = recoverable;

        for (NodeInfo nodeInfo : config.getNodeInfos()) {
            if (nodeInfo.getId() < config.getId()) {
                getConenction(nodeInfo);
            }
        }

        NodeInfo nodeInfo = config.getMyNodeInfo();

        this.serverSocket = new ServerSocket(nodeInfo.getServerPort());
        serverSocket.setSoTimeout(10000);
        serverSocket.setReuseAddress(true);

        start();
    }

    public void broadcast(BaseMessage message) {
        for(ServerConnectionSession session:connections.values()){
            session.send(message);
        }
    }

    public boolean authticationExamin(BaseMessage message) {
        return true;
    }

    public ServerConnectionSession getConenction(NodeInfo nodeInfo) {
        if (connections.get(nodeInfo.getId()) == null) {
            ServerConnectionSession connectionSession = new ServerConnectionSession(nodeInfo, null, true, config.getId(), inQueue,recoverable);
            connections.put(nodeInfo.getId(), connectionSession);
            return connectionSession;
        }
        return connections.get(nodeInfo.getId());
    }

    public ServerConnectionSession getConenction(int remoteId) {
        return connections.get(remoteId);
    }

    @Override
    public void run() {
        while (doWork) {
            try {
                Socket socket = this.serverSocket.accept();
                socket.setTcpNoDelay(true);
                BaseMessage message = new BaseMessage();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                int t = inputStream.readInt();
                message.read(inputStream);
                message.setType(MessageType.values()[t]);
                if (message.getType() == MessageType.CONNECT && authticationExamin(message)) {
                    ServerConnectionSession connectionSession = new ServerConnectionSession(null, socket, false, config.getId(), inQueue,recoverable);
                    connections.put(message.getSender(), connectionSession);
                    System.out.println("node "+message.getSender()+" connected");
                } else {
                    socket.close();
                }
            } catch (SocketTimeoutException ex) {
                //timeout on the accept... do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
