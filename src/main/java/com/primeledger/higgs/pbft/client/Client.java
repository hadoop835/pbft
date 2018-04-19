package com.primeledger.higgs.pbft.client;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.message.RequestMessage;
import com.primeledger.higgs.pbft.common.network.api.ISubmitConsensus;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import com.primeledger.higgs.pbft.common.network.netty.NettyMessageDecoder;
import com.primeledger.higgs.pbft.common.network.netty.NettyMessageEncoder;
import com.primeledger.higgs.pbft.common.utils.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@ChannelHandler.Sharable
public class Client extends SimpleChannelInboundHandler<BaseMessage> implements ISubmitConsensus {

    private Config config;

    private int id;

    private Map<Integer, C2SConnection> connections = null;

    private EventLoopGroup workerGroup = null;

    private PrivateKey privateKey = null;

    private boolean closed = false;

    private ReentrantReadWriteLock rl;

    public Client(Config config) {
        this.id = config.getId();
        this.config = config;
        this.connections = new HashMap<>();
        this.workerGroup = new NioEventLoopGroup();
        this.privateKey = config.getPrivateKey();

        init();
    }

    public void init() {
        ChannelFuture future = null;

        this.rl = new ReentrantReadWriteLock();

        for (NodeInfo nodeInfo : config.getNodeInfos()) {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new NettyMessageEncoder());
                    socketChannel.pipeline().addLast(new NettyMessageDecoder());
                    socketChannel.pipeline().addLast(Client.this);
                }
            });

            future = b.connect(new InetSocketAddress(nodeInfo.getHost(), nodeInfo.getClientPort()));

            connections.put(nodeInfo.getId(), new C2SConnection(nodeInfo.getId(), future.channel()));
            future.awaitUninterruptibly();

            if (!future.isSuccess()) {
                System.out.println("client Impossible to connect to " + nodeInfo.getId());
            } else {
                System.out.println("client success connect to node " + nodeInfo.getId());
            }
        }
    }

    public void postTask(Object object) throws IOException {
        RequestMessage message = new RequestMessage();
        message.setSender(id);
        message.setType(MessageType.REQUEST);
        message.setOperation(MessageUtils.objToBytes(object));

        byte[] sign = MessageUtils.signMessage(privateKey, message.getSerializeMessage());
        message.setSignature(sign);
        message.setTimeaStamp(System.currentTimeMillis());
        send(message);
    }

    public void send(BaseMessage message) {
        for (C2SConnection c2s : connections.values()) {
            c2s.sendMessage(message);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMessage message) throws Exception {
        if (message.getType() == MessageType.REPLY) {
            System.out.println("message reply");
        }
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        scheduleReconnect(ctx, 10);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        scheduleReconnect(ctx, 10);
    }

    @Override
    public void submit(Object obj) {

    }

    private void scheduleReconnect(final ChannelHandlerContext ctx, int time) {
        if (closed) {
            //TODO close chanel
            return;
        }
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                reconnect(ctx);
            }
        }, time, TimeUnit.SECONDS);
    }

    public void reconnect(final ChannelHandlerContext ctx) {
        rl.writeLock().lock();

        List<C2SConnection> sessions = new ArrayList<>(connections.values());

        for (C2SConnection c2s : sessions) {
            if (c2s.getChannel() == ctx.channel()) {
                if (workerGroup == null) {
                    workerGroup = new NioEventLoopGroup();
                }
                System.out.println("client try to reconnect to " + c2s.getRemoteId());
                ChannelFuture future = null;
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.option(ChannelOption.TCP_NODELAY, true);
                b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

                b.handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyMessageEncoder());
                        socketChannel.pipeline().addLast(new NettyMessageDecoder());
                        socketChannel.pipeline().addLast(Client.this);
                    }
                });
                NodeInfo nodeInfo = config.getNodeInfo(c2s.getRemoteId());
                future = b.connect(new InetSocketAddress(nodeInfo.getHost(), nodeInfo.getClientPort()));
                connections.put(nodeInfo.getId(), new C2SConnection(nodeInfo.getId(), future.channel()));
            }
        }
        rl.writeLock().unlock();
    }
}
