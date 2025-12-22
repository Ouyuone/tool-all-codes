package com.oo.tools.spring.boot.netty.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/02/26 11:20:14
 */
public class BIOClientMain {
    
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            
            OutputStream outputStream = socket.getOutputStream();
            
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.write("hello 你好".getBytes());
//            dataOutputStream.writeUTF("hello 你好");
            outputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
}
