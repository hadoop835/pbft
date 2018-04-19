package com.primeledger.higgs.pbft.client;

import com.primeledger.higgs.pbft.common.message.BaseMessage;
import io.netty.channel.Channel;

public class C2SConnection {

    private int remoteId;
    private Channel channel = null;

    public C2SConnection(int remoteId,Channel channel){
        this.remoteId = remoteId;
        this.channel =  channel;
    }

    public void sendMessage(BaseMessage message){
        channel.write(message);
        channel.flush();

    }

    public Channel getChannel() {
        return channel;
    }

    public int getRemoteId(){
        return remoteId;
    }
}
