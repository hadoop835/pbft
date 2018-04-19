package com.primeledger.higgs.pbft.server.worker;

import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.server.main.ServerViewController;
import com.primeledger.higgs.pbft.server.communication.MessageBroadcaster;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SendThread extends Thread {

    private BlockingQueue<BaseMessage> outQueue = null;
    private ServerViewController controller = null;
    private MessageBroadcaster broadcaster = null;
    private boolean doWork = true;

    public SendThread(BlockingQueue<BaseMessage> outQueue,ServerViewController controller,MessageBroadcaster broadcaster){
        this.outQueue = outQueue;
        this.controller = controller;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run(){
        while (doWork){
            try {
                BaseMessage message = outQueue.poll(100, TimeUnit.MILLISECONDS);
                if( message == null){
                   continue;
                }
                broadcaster.boadcastToServer(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
