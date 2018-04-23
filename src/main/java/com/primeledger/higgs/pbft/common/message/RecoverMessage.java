package com.primeledger.higgs.pbft.common.message;

import java.io.*;

/**
 * @author hanson
 * recover from log
 */
public class RecoverMessage extends BaseMessage {


    private int startCp;

    private int endCp;

    private StateLog[] states;


    public int getStartCp() {
        return startCp;
    }

    public void setStartCp(int startCp) {
        this.startCp = startCp;
    }

    public int getEndCp() {
        return endCp;
    }

    public void setEndCp(int endCp) {
        this.endCp = endCp;
    }

    public StateLog[] getStates() {
        return states;
    }

    public void setStates(StateLog[] states) {
        this.states = states;
    }

    @Override
    public void read(DataInputStream inputStream) throws IOException {
        super.read(inputStream);
        startCp = inputStream.readInt();
        endCp = inputStream.readInt();
        int len = inputStream.readInt();
        if (len > 0) {
            states = new StateLog[len];
            for (int i = 0; i < len; i++) {

                states[i] = new StateLog();
                states[i].read(inputStream);
            }
        }
    }

    @Override
    public void write(DataOutputStream outputStream) throws IOException {
        super.write(outputStream);
        outputStream.writeInt(startCp);
        outputStream.writeInt(endCp);
        if (states != null && states.length > 0) {
            outputStream.writeInt(states.length);
            for (StateLog state : states) {
                state.write(outputStream);
            }
        } else {
            outputStream.writeInt(0);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(startCp);
        out.writeInt(endCp);
        if (states != null && states.length > 0) {
            out.writeInt(states.length);
            for (StateLog state : states) {
                state.writeExternal(out);
            }
        } else {
            out.writeInt(0);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        startCp = in.readInt();
        endCp = in.readInt();
        int len = in.readInt();
        if (len > 0) {
            states = new StateLog[len];
            for (int i = 0; i < len; i++) {
                states[i] = new StateLog();
                states[i].readExternal(in);
            }
        }
    }
}
