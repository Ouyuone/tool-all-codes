package com.oo.tools.spring.boot.netty.server;

import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 自定义解码器，用于处理 writeUTF 格式的数据
 */
public class WriteUTFDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 确保至少有 2 字节的数据（用于读取长度）
        if (in.readableBytes() < 2) {
            return;  // 数据不完整，等待更多数据
        }

        // 读取 2 字节长度
        int length = in.readShort();  // length 是一个 2 字节的 short 类型
        
        if (length > 0 && in.readableBytes() >= length) {
            // 数据符合 writeUTF 格式，读取字符串并传递给下游
            ByteBuf stringBytes = in.readBytes(length);
//            String message = stringBytes.toString(StandardCharsets.UTF_8);
            out.add(stringBytes);
        } else {
            // 如果无法解析为 writeUTF 格式，重置读指针并假设是原始字节流
            in.resetReaderIndex();  // 重置到最初位置
            // 处理原始字节流，直接读取所有可用字节作为 ByteBuf
            ByteBuf rawBytes = in.readBytes(in.readableBytes());
            out.add(rawBytes);  // 将原始字节流作为 ByteBuf 添加到输出
        }
        
    }
}