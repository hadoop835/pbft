package com.primeledger.higgs.pbft.server.communication;

import com.primeledger.higgs.pbft.common.network.netty.NettyMessageDecoder;
import com.primeledger.higgs.pbft.common.network.netty.NettyMessageEncoder;
import com.primeledger.higgs.pbft.server.worker.ClientMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class S2CConnectionLayer{

    private Channel mainChannel ;
    private String host;
    private int port;
    private int nWorkers;

    public S2CConnectionLayer(String host,int port,int nWorkers,ClientMessageHandler handler){
        this.host = host;
        this.port = port;
        this.nWorkers = nWorkers;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = (nWorkers > 0 ? new NioEventLoopGroup(nWorkers) : new NioEventLoopGroup());

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder());
                            ch.pipeline().addLast(new NettyMessageEncoder());
                            ch.pipeline().addLast(handler);
                        }
                    })	.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = b.bind(new InetSocketAddress(host, port)).sync();
            mainChannel = f.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeChannelAndEventLoop(Channel c){
        c.flush();
        c.deregister();
        c.close();
        c.eventLoop().shutdownGracefully();
    }


}
