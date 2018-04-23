package com.primeledger.higgs.pbft.common.network.netty;

import com.primeledger.higgs.pbft.common.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyMessageDecoder extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int n = byteBuf.readableBytes();
        if(n < 10){
            return;
        }
//        int dataLength = byteBuf.getInt(byteBuf.readerIndex());
//        if(byteBuf.readableBytes() < dataLength +4) return;

        RequestMessage request = new RequestMessage();
        request.read(byteBuf);

        list.add(request);
    }
}
