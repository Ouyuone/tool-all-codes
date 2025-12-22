package com.oo.tools.spring.boot.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/02/20 18:40:32
 */
public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer(){
                        
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    System.out.println("服务端 channel读取数据："+byteBuf.toString(CharsetUtil.UTF_8));
                                   
                                }
                                
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("客户端连接成功。。。", CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });
            
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9999).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        
    }
    
}
