package com.primeledger.higgs.pbft.server.worker;

import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.BlockingQueue;

@ChannelHandler.Sharable
public class ClientMessageHandler extends SimpleChannelInboundHandler<BaseMessage> {

    private BlockingQueue<BaseMessage> request;

    public ClientMessageHandler(BlockingQueue<BaseMessage> request){
        this.request = request;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMessage message) throws Exception {

//        System.out.println("receive client message from "+message.getSender());
        if(message.getType() == MessageType.REQUEST){
            if(!request.offer(message)){
                //TODO the message is full of the queue ,add to queue failed
                System.out.println("the request queue size is full");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        if(cause instanceof ClosedChannelException){
            System.out.println("Connection with client closed");
        }else{
            System.out.println(cause);
        }
    }
}
