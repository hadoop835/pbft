package com.primeledger.higgs.pbft.common.message;

public enum MessageType {
    REQUEST,  // client request
    PRE_PREPARE , //
    PREPARE,
    COMMIT,
    REPLY,
    CONNECT,
    VIEW_CHANGE,
    NEW_VIEW,
    ASK_CHECKPOINT,
    BACK_CHECKPOINT,
    ASK_SYN_LOG,
    BACK_SYC_LOG
}
