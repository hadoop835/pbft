package com.primeledger.higgs.pbft.common.message;

import java.io.*;

public class StateLog implements Externalizable {

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

    public void read(DataInputStream inputStream) {
        try {
            cp = inputStream.readInt();
            int length = inputStream.readInt();
            operation = new byte[length];
            inputStream.read(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(DataOutputStream outputStream) {

        try {
            outputStream.writeInt(cp);
            outputStream.writeInt(operation.length);
            outputStream.write(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(cp);
        if (operation == null || operation.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(operation.length);
            out.write(operation);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cp = in.readInt();
        int len = in.readInt();
        operation = new byte[len];
        int read = 0;
        do {
            read += in.read(operation, read, len - read);
        } while (read < len);
    }
}
