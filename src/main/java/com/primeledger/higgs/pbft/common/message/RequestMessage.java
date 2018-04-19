package com.primeledger.higgs.pbft.common.message;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestMessage extends BaseMessage {

    /**
     * client time stamp
     */
    private long timeaStamp;

    /**
     * client operation need to do
     */
    private byte[] operation;


    public long getTimeaStamp() {
        return timeaStamp;
    }

    public void setTimeaStamp(long timeaStamp) {
        this.timeaStamp = timeaStamp;
    }

    public byte[] getOperation() {
        return operation;
    }

    public void setOperation(byte[] operation) {
        this.operation = operation;
    }


//    public byte[] getSignature() {
//        return signature;
//    }
//
//    public void setSignature(byte[] signature) {
//        this.signature = signature;
//    }

    @Override
    public void read(ByteBuf byteBuf){
        super.read(byteBuf);
        timeaStamp = byteBuf.readLong();

        int opLength = byteBuf.readInt();
        operation = new byte[opLength];
        byteBuf.readBytes(operation);

        int signLength = byteBuf.readInt();
        signature = new byte[signLength];
        byteBuf.readBytes(signature);
    }

    @Override
    public void write(ByteBuf byteBuf){
        super.write(byteBuf);
        byteBuf.writeLong(timeaStamp);

        byteBuf.writeInt(operation.length);
        byteBuf.writeBytes(operation);

        byteBuf.writeInt(signature.length);
        byteBuf.writeBytes(signature);
    }

    @Override
    public byte[] getSerializeMessage() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            super.write(dos);
            dos.writeInt(operation.length);
            dos.write(operation);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return bos.toByteArray();
    }

}
