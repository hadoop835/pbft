package com.primeledger.higgs.pbft.common.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StateLog {

    private int cp;
    private byte[] operation;

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public byte[] getOperation() {
        return operation;
    }

    public void setOperation(byte[] operation) {
        this.operation = operation;
    }

    public void read(DataInputStream inputStream){
        try {
            cp = inputStream.readInt();
            int length = inputStream.readInt();
            operation = new byte[length];
            inputStream.read(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(DataOutputStream outputStream){

        try {
            outputStream.writeInt(cp);
            outputStream.writeInt(operation.length);
            outputStream.write(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
