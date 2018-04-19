package com.primeledger.higgs.pbft.server.communication;

import java.nio.channels.Channel;

public class ClientConnetionSession {

    private final Channel channel;
    private final int sender;

    public ClientConnetionSession(Channel channel, int sender){

        this.channel = channel;
        this.sender = sender;
    }

    public void send(){

    }
}
