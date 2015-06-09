import java.io.*;
import java.net.Socket;

/**
 * Socket客户端
 * Created by water on 2015/6/5 16:02.
 * email water471871679@gmial.com(number is qq number)
 */
public class SocketClient2 extends Socket {
    private static final String SERVER_IP = Param.LAN_ADRESS_LOCAL;
    private static final int SERVER_PORT = Param.LAN_PORT;

    private static Socket client;
    private static PrintWriter out;
    private static BufferedReader in;

    /**
     * 与服务器连接，并输入发送消息
     */
    public SocketClient2() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        System.out.println("客户端启动");
        client = this;
        out = new PrintWriter(this.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(this.getInputStream()));
        new readLineThread();
//        new sendThread();
//        Thread.sleep(500);
        out.println("file");
        while (true) {
            in = new BufferedReader(new InputStreamReader(System.in));
            String input = in.readLine();
            out.println(input);
        }
    }

    /**
     * 用于监听服务器端向客户端发送消息线程类
     */
    class readLineThread extends Thread {

        private BufferedReader buff;

        public readLineThread() {
            try {
                buff = new BufferedReader(new InputStreamReader(client.getInputStream()));
                start();
            } catch (Exception e) {
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String result = buff.readLine();
                    if ("byeClient".equals(result)) {//客户端申请退出，服务端返回确认退出
                        break;
                    } else if ("start".equals(result)) {//服务器返回确认文件传输
                        System.out.println("开始发送");
                        new sendThread();
                    } else {//输出服务端发送消息
                        System.out.println(result);
                    }
                }
                in.close();
                out.close();
                client.close();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        try {
            new SocketClient2();//启动客户端
        } catch (Exception e) {
        }
    }

    private static void sendFile() throws Exception {
        String path = "F:" + File.separator + "banner.png";
//        Socket socket = new Socket(Param.LAN_ADRESS_LOCAL,Param.LAN_PORT);
//        out.print("file\n");
        DataInputStream read = new DataInputStream(new FileInputStream(
                new File(path)));
        DataOutputStream os = new DataOutputStream(client.getOutputStream());
        byte[] data = new byte[1024];
        String start = in.readLine();
        int sendCountLen = 0;
        int len;
        if (start.equals("start")) {

            while ((len = read.read(data)) != -1) {

                os.write(data, 0, len);

                sendCountLen += len;


            }

            os.flush();

            os.close();

            read.close();
        }
    }

    class sendThread extends Thread {
        String path = "F:" + File.separator + "banner.png";
        byte[] data = new byte[1024];
        Socket socket ;
        public sendThread() throws IOException {
            socket = new Socket(Param.LAN_ADRESS_LOCAL,Param.LAN_PORT_FILE);
            start();
        }

        @Override
        public void run() {
            try {
                DataInputStream read = new DataInputStream(new FileInputStream(
                        new File(path)));
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
//                String start = in.readLine();
                int sendCountLen = 0;
                int len;
                while ((len = read.read(data)) != -1) {
                    os.write(data, 0, len);

                    sendCountLen += len;
                    System.out.println("发送长度"+sendCountLen);
                }
                System.out.println("发送完成");
                os.flush();

                os.close();

                read.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
