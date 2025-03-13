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
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UdpNodeActor extends AbstractNodeActor {

    private EventLoopGroup bossGroup;

    private Channel channel;


    @Override
    public void create(NodeContext context, Output output) throws Exception {
        Integer port = properties.getInteger("port");
        if (null == port) throw new DataValidateException();
        bossGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                // 主线程处理
                .channel(NioDatagramChannel.class)
                // 广播
                .option(ChannelOption.SO_BROADCAST, false)
                // 设置读缓冲区为2M
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
                // 设置写缓冲区为1M
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NioEventLoopGroup(), new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                // 接收报文
                                ByteBuf in = packet.content();
                                byte[] bytes = new byte[in.readableBytes()];
                                in.getBytes(in.readerIndex(), bytes);
                                in.readRetainedSlice(bytes.length);
                                // 响应解析
                                String response = properties.getString("response");
                                if (!StringUtils.isEmpty(response)) {
                                    response = ExpressionUtil.analysis(response, GlobalVariableContent.getAllValue());
                                    byte[] responseBytes = HexUtil.hexToBytes(response);
                                    if (null != responseBytes) {
                                        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseBytes), packet.sender()));
                                    }
                                }
                                Map<String, Object> map = new HashMap<>();
                                map.put("bytes", bytes);
                                map.put("hex", HexUtil.bytesToHex(bytes));
                                map.put("response", response);
                                output.out(map);
                            }
                        });
                    }
                });
        channel = bootstrap.bind(port).sync().channel();
        ListenerContent.add(port, ListenerTypeEnum.UDP, "UDP node port");
    }


    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        bossGroup = null;
        channel.closeFuture().syncUninterruptibly();
        channel = null;
        Integer port = properties.getInteger("port");
        if (null == port) return;
        ListenerContent.remove(port);
    }


}
