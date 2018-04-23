package com.primeledger.higgs.pbft.server.communication;

import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.message.RecoverMessage;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.ConsensusMessage;
import com.primeledger.higgs.pbft.common.utils.MessageUtils;
import com.primeledger.higgs.pbft.server.recover.IRecoverable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerConnectionSession extends Thread {

    private Socket socket = null;

    private int remoteId;

    private DataOutputStream outputStream;

    private DataInputStream inputStream;

    private boolean doWork = true;

    private BlockingQueue<BaseMessage> inQueue;

    private IRecoverable recoverable = null;

    private Lock connectLck = new ReentrantLock();

    private NodeInfo nodeInfo;

    private int myId;


    public ServerConnectionSession(NodeInfo nodeInfo, Socket socket, boolean ifConnect, int id, BlockingQueue<BaseMessage> inQueue, IRecoverable recoverable) {
        this.recoverable = recoverable;
        this.socket = socket;
        if (nodeInfo != null) {
            this.remoteId = nodeInfo.getId();
            this.nodeInfo = nodeInfo;
        }
        this.myId = id;
        this.inQueue = inQueue;

        if (ifConnect && socket == null) {
            try {
                this.socket = new Socket(nodeInfo.getHost(), nodeInfo.getServerPort());
                this.socket.setTcpNoDelay(true);
                inputStream = new DataInputStream(this.socket.getInputStream());
                outputStream = new DataOutputStream(this.socket.getOutputStream());
                BaseMessage message = new BaseMessage();
                message.setType(MessageType.CONNECT);
                message.setSender(id);
                send(message);
            } catch (IOException e) {
                System.out.println("impossible connect to " + remoteId);
//                e.printStackTrace();
            }
        }


        if (!ifConnect && this.socket != null) {
            try {
                inputStream = new DataInputStream(this.socket.getInputStream());
                outputStream = new DataOutputStream(this.socket.getOutputStream());

            } catch (IOException e) {
                System.out.println("error connecting to" + remoteId);
                e.printStackTrace();
            }
        }
        start();
    }

    public void send(BaseMessage message) {
        if (socket == null || outputStream == null || !socket.isConnected()) {
            closeSocket();
            reconnect(null);
            return;
        }
        try {
//            message.write(outputStream);
            byte[] data = MessageUtils.objToBytes(message);
            outputStream.writeInt(data.length);
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("--------send message failed----------");
            e.printStackTrace();
        }
    }

    public void send(byte[] b) {

        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        byte[] receiveMsg = null;
        while (doWork) {
            if (socket != null && inputStream != null) {
                try {
//                int sender = inputStream.readInt();
                    int length = inputStream.readInt();
                    byte[] data = new byte[length];

                    int read = 0;
                    do{
                        read += inputStream.read(data,read,length - read);
                    }while(read < length);
                    Object obj = MessageUtils.byteToObj(data);
                    if(obj instanceof ConsensusMessage){
                        inQueue.offer((ConsensusMessage)obj);
                    }else if(obj instanceof  RecoverMessage){
                        recoverable.recover((RecoverMessage)obj);
                    }else{
                        System.out.println("error!");
                    }
//                    int t = inputStream.readInt();
//
//                    if (t < 0 || t > MessageType.values().length) {
//                        inputStream.reset();
//                    }
//                    MessageType type = MessageType.values()[t];
//                    switch (type) {
//                        case COMMIT:
//                        case PREPARE:
//                        case PRE_PREPARE:
//                            ConsensusMessage consensusMessage = new ConsensusMessage();
//                            consensusMessage.read(inputStream);
//                            consensusMessage.setType(type);
//                            inQueue.offer(consensusMessage);
//                            break;
//                        case BACK_SYC_LOG:
//                        case ASK_SYN_LOG:
//                            RecoverMessage recoverMessage = new RecoverMessage();
//                            recoverMessage.setType(type);
//                            recoverMessage.read(inputStream);
//                            recoverable.recover(recoverMessage);
//                            break;
//                        default:
//                            System.out.println("did not support message Type");
//                            break;
//                    }


//                System.out.println(sender);
                } catch (IOException e) {
                    System.out.println("---------------read received message from " + remoteId + " error!-------------");
                    closeSocket();
                    reconnect(null);
                    continue;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (doWork) {
                    try {
                        Thread.sleep(5000);
                        reconnect(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    private void closeSocket() {
        if (socket != null) {

            try {
                outputStream.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
            outputStream = null;
            inputStream = null;
        }
    }

    public void reconnect(Socket newSocket) {
        connectLck.lock();
        if (this.socket == null || !socket.isConnected()) {
            if (nodeInfo != null) {
                try {
                    System.out.println("try to reconnect to " + nodeInfo.getId());
                    this.socket = new Socket(nodeInfo.getHost(), nodeInfo.getServerPort());
                    this.socket.setTcpNoDelay(true);
                    inputStream = new DataInputStream(this.socket.getInputStream());
                    outputStream = new DataOutputStream(this.socket.getOutputStream());
                    BaseMessage message = new BaseMessage();
                    message.setType(MessageType.CONNECT);
                    message.setSender(myId);
                    send(message);
                    System.out.println("success connect to " + nodeInfo.getId());
                } catch (IOException e) {
                    System.out.println("import possible connect to " + nodeInfo.getId());
//                    e.printStackTrace();
                }
            } else {
                socket = newSocket;
            }
        }
//        if (socket != null) {
//            try {
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
        connectLck.unlock();
    }


}
