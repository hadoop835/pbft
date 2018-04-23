package com.primeledger.higgs.pbft.common.message;

import java.io.*;

public class ConsensusMessage extends BaseMessage {

    /**
     * the node cluster current view
     */
    private int view;

    /**
     * the digest of client request
     */
    private byte[] digest;

    /**
     * the sequence number of the consensus message assigned by master
     */
    private long sequnce;

    /**
     * the request sent by client
     */
    private byte[] request;

    /**
     * the time stamp received from client
     */
    private long timeStamp;

    /**
     * the id of the client who submit the consensus
     */
    private int clientId;


    /**
     * current consensus check poing
     */
    private int cp;

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    public long getSequnce() {
        return sequnce;
    }

    public void setSequnce(long sequnce) {
        this.sequnce = sequnce;
    }

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


    @Override
    public void read(DataInputStream inputStream) throws IOException {
        super.read(inputStream);

        view = inputStream.readInt();
        sequnce = inputStream.readLong();
        timeStamp = inputStream.readLong();
        clientId = inputStream.readInt();
        cp = inputStream.readInt();

        int digestLength = inputStream.readInt();
        digest = new byte[digestLength];
        inputStream.read(digest);

        int signatureLength = inputStream.readInt();
        signature = new byte[signatureLength];
        inputStream.read(signature);

//        int requestLength = inputStream.readInt();
//        request = new byte[requestLength];
//        inputStream.read(request);

    }

    @Override
    public void write(DataOutputStream outputStream) throws IOException {
        super.write(outputStream);
        outputStream.writeInt(view);
        outputStream.writeLong(sequnce);
        outputStream.writeLong(timeStamp);
        outputStream.writeInt(clientId);
        outputStream.writeInt(cp);

        outputStream.writeInt(digest.length);
        outputStream.write(digest);

        outputStream.writeInt(signature.length);
        outputStream.write(signature);
//        outputStream.writeInt(request.length);
//        outputStream.write(request);
    }

    public byte[] getSerializeMessage() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            super.write(dos);
            dos.writeInt(view);
            dos.writeLong(sequnce);
            dos.writeLong(timeStamp);
            dos.writeInt(clientId);
            dos.writeInt(cp);

            dos.writeInt(digest.length);
            dos.write(digest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return bos.toByteArray();
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(view);
        out.writeLong(sequnce);
        out.writeLong(timeStamp);
        out.writeInt(clientId);
        out.writeInt(cp);

        out.writeInt(digest.length);
        out.write(digest);

        out.writeInt(signature.length);
        out.write(signature);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        view = in.readInt();
        sequnce = in.readLong();
        timeStamp = in.readLong();
        clientId = in.readInt();
        cp = in.readInt();

        int digestLength = in.readInt();
        digest = new byte[digestLength];
        int read = 0;
        do {
            read += in.read(digest, read, digestLength - read);
        } while (read < digestLength);


        int signatureLength = in.readInt();
        signature = new byte[signatureLength];
        read = 0;
        do {
            read += in.read(signature, read, signatureLength - read);
        } while (read < signatureLength);

    }
}
