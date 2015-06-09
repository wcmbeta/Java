import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket服务端v2 与客户端v2对应 文本传输,文件传输(另起线程)
 * Created by water on 2015/6/5 15:48.
 * email water471871679@gmial.com(number is qq number)
 */
public class SocketServer2 {
    private static final int SERVER_PORT = Param.LAN_PORT;
    private static ExecutorService executor = Executors.newScheduledThreadPool(5);
    private static boolean isPrint = false;//是否输出消息标志
    private static Map userMap = new HashMap();//登录用户集合

    public SocketServer2() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("服务器启动");
            while (true) {
                Socket socket = serverSocket.accept();
                //单独处理一个客户端
                executor.execute(new ServerTask(socket));
            }

        } catch (Exception e) {
        e.printStackTrace();
    }
    }

    /**
     * 服务器多线程任务
     */
    public class ServerTask implements Runnable {
        private final SocketAddress clientAddress;
        private PrintWriter out;
        private BufferedReader in;
        private Socket socket;
        byte[] receiveBuf = new byte[1024];
        int receiveSize;

        public ServerTask(Socket socket) throws Exception {
            this.socket = socket;
            this.socket.getKeepAlive();
//            this.socket.setSoTimeout(5000);
            //添加user
            clientAddress = socket.getRemoteSocketAddress();
            if (null == userMap.get(clientAddress.toString())) {
                userMap.put(clientAddress.toString(), socket);
            }
            try {
                out = new PrintWriter(this.socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("新连接--" + clientAddress);
        }

        @Override
        public void run() {
            boolean done = true;
            while (done) {
                try {
                    try {
                        socket.sendUrgentData(0);
                    } catch (IOException e) {
                        done = false;
                        socket.close();
                        userMap.remove(clientAddress);
                        System.out.print(clientAddress + "断开连接");
                        break;//如果抛出了异常，那么就是断开连接了  跳出无限循环
                    }
                    while (done) {
                        String result = in.readLine();
                        if ("byeClient".equals(result)) {//客户端申请退出，服务端返回确认退出
                            break;
                        } else if ("file".equals(result)) {//客户端发来文件
                            System.out.println(clientAddress + ":" + result+"请求");
                            out.println("start");
                            executor.execute(new GetFile(null));
                        } else if(result!=null&&result.length()>0) {//输出服务端发送消息
                            System.out.println(clientAddress + "发来消息:" + result);
                        }
                    }
//                    receiveBuf = new byte[1024];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class GetFile extends Thread {
        private Socket socket;
        private ServerSocket server;
        public GetFile(Socket socket) throws Exception {
//            this.socket = socket;
            server = new ServerSocket(Param.LAN_PORT_FILE);
            start();
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                 socket = null;
                try {
                    socket = server.accept();
                    //单独处理一个客户端
                    getFile(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            try {
//                DataInputStream in = new DataInputStream(socket.getInputStream());
//
//                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
//
//                byte[] data = new byte[1024];
//
//                File file = new File("E:\\banner.png");
//                if (file.exists()) {
//
//                }else if (file.isFile()&&file.length()==0) {
//                    file.delete();
//                }
//                DataOutputStream out = new DataOutputStream(new FileOutputStream(
//                        file));
//
//                System.out.println("开始接收.....");
//
//                int countSize = 0;
//                int len = 0;
//                while((len=in.read(data))!=-1)
//
//                {
//                    out.write(data, 0, len);
//                    countSize += len;
//
//                }
//                System.out.println("接收完成");
//                os.close();
//
//                out.flush();
//
//                out.close();
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }


    }

    private void getFile(Socket socket) throws Exception {
        DataInputStream in = new DataInputStream(socket.getInputStream());

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        byte[] data = new byte[1024];

        File file = new File("E:\\banner.png");

        DataOutputStream out = new DataOutputStream(new FileOutputStream(
                file));

        System.out.println("开始接收.....");

        int countSize = 0;
        int len = 0;
        if(in.available()>0){
        //判断有数据过来，接收

        }
        while ((len = in.read(data)) != -1) {

            out.write(data, 0, len);

            countSize += len;
            System.out.println("接收长度:"+countSize);
        }
        System.out.println("接收完成");
        os.close();

        out.flush();

        out.close();
        in.close();
    }

    public static void main(String[] args) throws IOException {
        new SocketServer2();//启动服务端
    }
}
