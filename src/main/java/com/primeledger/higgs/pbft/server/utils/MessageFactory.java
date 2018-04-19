package com.primeledger.higgs.pbft.server.utils;

import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.MessageType;

public class MessageFactory {

    public static BaseMessage getPrePrepareMessage(int id, int seq){
        BaseMessage message = new BaseMessage();
        message.setType(MessageType.PRE_PREPARE);
        message.setSender(id);

        return message;
    }

    public static BaseMessage getPrepareMessage(int id){
        BaseMessage message = new BaseMessage();
        message.setSender(id);
        message.setType(MessageType.PREPARE);
        return message;
    }

    public static BaseMessage getCommitMessage(int id){
        BaseMessage message = new BaseMessage();
        message.setType(MessageType.COMMIT);
        message.setSender(id);
        return message;
    }

    public static BaseMessage getReplyMessage(){
        return null;
    }
    public static BaseMessage getConnectMessage(){
        return null;
    }
}
