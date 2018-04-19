package com.primeledger.higgs.pbft.server.communication;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.server.recover.IRecoverable;
import com.primeledger.higgs.pbft.server.worker.ClientMessageHandler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author hanson
 */
public class ServerCommunicationSystem extends Thread implements MessageBroadcaster {

    private S2SConnectionLayer s2SConnectionLayer = null;

    private S2CConnectionLayer s2CConnectionLayer = null;

    private BlockingQueue<BaseMessage> requestQueue = null;

//    private IRecoverable recoverable = null;



    public ServerCommunicationSystem(Config config,BlockingQueue<BaseMessage> inQueue,BlockingQueue<BaseMessage> requestQueue,IRecoverable recoverable){
        super("CS Server");


        try {
            s2SConnectionLayer = new S2SConnectionLayer(config,inQueue,recoverable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientMessageHandler handler = new ClientMessageHandler(requestQueue);

        NodeInfo nodeInfo = config.getMyNodeInfo();
        s2CConnectionLayer = new S2CConnectionLayer(nodeInfo.getHost(),nodeInfo.getClientPort(),1,handler);

    }



    public void init(){

    }

    @Override
    public void run(){

    }

    @Override
    public void boadcastToServer(BaseMessage message) {
        s2SConnectionLayer.broadcast(message);
    }

    @Override
    public void send(int id, BaseMessage message) {
        ServerConnectionSession connection = s2SConnectionLayer.getConenction(id);
        connection.send(message);
    }

    @Override
    public void boadcastToServer(byte[] message) {

    }

    @Override
    public void sendToClient(int clientId, BaseMessage message) {

    }
}
