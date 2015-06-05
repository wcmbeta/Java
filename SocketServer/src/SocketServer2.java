import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket服务端
 * Created by water on 2015/6/5 15:48.
 * email water471871679@gmial.com(number is qq number)
 */
public class SocketServer2  {
    private static final int SERVER_PORT = Param.LAN_PORT;
    ExecutorService executor = Executors.newScheduledThreadPool(5);
    private static boolean isPrint = false;//是否输出消息标志
    private static List user_list = new ArrayList();//登录用户集合
    public SocketServer2() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器多线程任务
     */
    public class serverTask implements Runnable {
        private Socket socket;
        public serverTask(Socket socket){
            this.socket=socket;
            SocketAddress clientAddress = socket.getRemoteSocketAddress();
            System.out.println("" + clientAddress);
        }
        @Override
        public void run() {


        }
    }

    public static void main(String[] args) throws IOException {
        new SocketServer2();//启动服务端
    }
}
