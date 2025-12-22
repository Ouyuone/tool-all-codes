package com.oo.tools.spring.boot.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/02/20 18:17:56
 */
public class ServerMain {
    
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        System.out.println("客户端 channel连接初始化");
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("客户端 channel连接激活");
                                super.channelActive(ctx);
                            }
                        }).addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                System.out.println("客户端 channel读取数据："+byteBuf.toString(CharsetUtil.UTF_8));
                                
                                // 回复消息
                                ctx.writeAndFlush(Unpooled.copiedBuffer("客户端你好，我收到你的消息了",CharsetUtil.UTF_8));
                            }
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(Unpooled.copiedBuffer("客户端你好，我是服务端", CharsetUtil.UTF_8));
                            }
                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("客户端 channel读取数据完成");
                            }
                        });
                    }
                });
        
        ChannelFuture channelFuture = bootstrap.bind(9999).sync();
        System.out.println("服务器启动成功。。。。");
        
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("监听端口---"+channelFuture.isSuccess());
            }});
        
        channelFuture.channel().closeFuture().sync();
    }
}
