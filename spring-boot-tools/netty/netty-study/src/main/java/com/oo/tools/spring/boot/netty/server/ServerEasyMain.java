package com.oo.tools.spring.boot.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/02/26 11:00:14
 */

public class ServerEasyMain {
    
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
         
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer(){
                        
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            System.out.println("客服端已连接到我这边。。。");
                            
                            channel.pipeline().addFirst(new WriteUTFDecoder());
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf =  (ByteBuf)msg;
                                    String readMsg = byteBuf.toString(StandardCharsets.UTF_8);
                                    System.out.println("客户端 channel读取数据："+ readMsg);
                             
                                }
                            });
                        }
                    });
            ChannelFuture cf = bootstrap.bind(9999).addListener(new ChannelFutureListener() {
                
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    System.out.println("我监听到了。。。");
                }
            }).sync();
            cf.addListener(new ChannelFutureListener() {
                
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("我监听到了2。。。");
                }
            });
            System.out.println("服务器启动成功。。。。");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        
    }
}
