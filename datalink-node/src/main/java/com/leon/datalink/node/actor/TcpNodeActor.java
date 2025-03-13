package com.leon.datalink.node.actor;

import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.leon.datalink.core.utils.ExpressionUtil;
import com.leon.datalink.core.utils.HexUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TcpNodeActor extends AbstractNodeActor {

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        Integer port = properties.getInteger("port");
        if (null == port) throw new DataValidateException();
        bossGroup = new NioEventLoopGroup(properties.getInteger("bossTread", 1));
        workerGroup = new NioEventLoopGroup(properties.getInteger("workerTread", 5));
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast("decoder", new ByteToMessageDecoder() {
                            @Override
                            protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
                                byte[] bytes = new byte[in.readableBytes()];
                                in.getBytes(in.readerIndex(), bytes);
                                in.readRetainedSlice(bytes.length);
                                Map<String, Object> map = new HashMap<>();
                                map.put("bytes", bytes);
                                map.put("hex", HexUtil.bytesToHex(bytes));
                                out.add(map);
                            }
                        });
                        socketChannel.pipeline().addLast("encoder", new MessageToByteEncoder<String>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, String hex, ByteBuf byteBuf) throws Exception {
                                byteBuf.writeBytes(HexUtil.hexToBytes(hex));
                            }
                        });
                        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<Map<String, Object>>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Map<String, Object> map) throws Exception {
                                // 响应解析
                                String response = properties.getString("response");
                                if (!StringUtils.isEmpty(response)) {
                                    response = ExpressionUtil.analysis(response, GlobalVariableContent.getAllValue());
                                    ctx.writeAndFlush(response);
                                }
                                map.put("response", response);
                                output.out(map);
                            }
                        });
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        //绑定端口,开始接收进来的连接
        channel = bootstrap.bind(port).sync().channel();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "TCP node port");
    }


    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        bossGroup = null;
        workerGroup.shutdownGracefully();
        workerGroup = null;
        channel.closeFuture().syncUninterruptibly();
        channel = null;
        Integer port = properties.getInteger("port");
        if (null == port) return;
        ListenerContent.remove(port);
    }


}
