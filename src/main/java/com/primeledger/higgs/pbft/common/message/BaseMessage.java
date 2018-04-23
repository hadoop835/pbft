package com.primeledger.higgs.pbft.common.message;

import io.netty.buffer.ByteBuf;

import java.io.*;

public class BaseMessage implements Externalizable{

    protected byte[] signature;

    private MessageType type;

    private int sender;

    private byte[] serializeMessage;



    public byte[] getSerializeMessage() {
        return serializeMessage;
    }

    public void setSerializeMessage(byte[] serializeMessage) {
        this.serializeMessage = serializeMessage;
    }



    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }


    public BaseMessage readFrom(byte[] btf) {

        return null;
    }

    public void read(ByteBuf byteBuf) {
        type = MessageType.values()[byteBuf.readInt()];
        sender = byteBuf.readInt();
    }

    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(type.ordinal());
        byteBuf.writeInt(sender);
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(type.ordinal());
        outputStream.writeInt(sender);
    }

    public void read(DataInputStream inputStream) throws IOException {
//        type = MessageType.values()[inputStream.readInt()];
        sender = inputStream.readInt();
    }

    public static BaseMessage readFrom(ByteBuf byteBuf) {
        BaseMessage msg = new BaseMessage();
        msg.read(byteBuf);
        return msg;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(type.ordinal());
        out.writeInt(sender);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = MessageType.values()[in.readInt()];
        sender = in.readInt();
    }
}
