package a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    // �����Socket
    private ServerSocket serverSocket;
    // ���пͻ��������,keyΪ�û����ǳƣ�valueΪ���û��������
    private Map<String,PrintWriter> allOut;
    // �̳߳�
    private ExecutorService threadPool;
    /**
     * ���췽�������ڳ�ʼ��
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(8088);
            allOut = new HashMap<String,PrintWriter>();
            threadPool = Executors.newFixedThreadPool(40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ����˿�������
     */
    public void start() {
        try {
            //ѭ�������ͻ��˵�����
            while(true){
                System.out.println("�ȴ��ͻ�������...");
                // �����ͻ��˵�����
                Socket socket = serverSocket.accept();
                System.out.println("�ͻ���������!");

                //����һ���߳��������Ըÿͻ��˵Ľ���
                ClientHandler handler = new ClientHandler(socket);
                threadPool.execute(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ����������빲���ϣ������������������⣬��֤ͬ����ȫ
     * @param out
     */
    private synchronized void addOut(String nickName,PrintWriter out){
        allOut.put(nickName,out);
    }
    /**
     * ������������ӹ�����ɾ��
     * @param
     */
    private synchronized void removeOut(String nickName){
        allOut.remove(nickName);
    }
    /**
     * ����Ϣת�������пͻ���
     * @param message
     */
    private synchronized void sendMessage(String message){
        for(PrintWriter o : allOut.values()){
            o.println(message);
        }
    }
    /**
     * ����Ϣ���͸�ָ���ǳƵĿͻ���
     * @param nickName
     * @param message
     */
    private synchronized void sendMessageToOne(String nickName,String message){
        PrintWriter out = allOut.get(nickName);
        if(out!=null){
            out.println(message);
        }
    }
    /**
     * �߳��壬���ڲ�������ͬ�ͻ��˵Ľ���
     */
    private class ClientHandler implements Runnable {
        // ���߳����ڴ���Ŀͻ���
        private Socket socket;
        // ���ͻ��˵��ǳ�
        private String nickName;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            PrintWriter pw = null;
            try {
                //���ͻ��˵���������빲���ϣ��Ա�㲥��Ϣ
                OutputStream out = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");
                pw = new PrintWriter(osw,true);
                /*
                 * ���û���Ϣ���빲����
                 * ��Ҫͬ��
                 */
                //�Ȼ�ȡ���û��ǳ�
                nickName = getNickName();
                addOut(nickName,pw);
                Thread.sleep(100);
                /*
                 * ֪ͨ�����û����û�������
                 */
                sendMessage(nickName+"������");

                InputStream in = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);

                String message = null;
                // ѭ����ȡ�ͻ��˷��͵���Ϣ
                while ((message = br.readLine())!=null) {
                    //���Ȳ鿴�ǲ���˽��
                    if(message.startsWith("\\")){
                        /*
                         * ˽�ĸ�ʽ��\�ǳ�:����
                         */
                        //�ҵ�:��λ��
                        int index = message.indexOf(":");
                        if(index>=0){
                            //��ȡ�ǳ�
                            String name = message.substring(1,index);
                            //��ȡ����
                            String info = message.substring(index+1,message.length());
                            //ƴ������
                            info = nickName+"����˵:"+info;
                            //����˽����Ϣ��ָ���û�
                            sendMessageToOne(name, info);
                            //������˽�ĺ�Ͳ��ڹ㲥�ˡ�
                            continue;
                        }
                    }
                    /*
                     * ������������������ÿͻ��˷��͵���Ϣת�������пͻ���
                     * ��Ҫͬ��
                     */
                    sendMessage(nickName+"˵:"+message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                /*
                 * ���ͻ��˶��ߣ�Ҫ��������ӹ�������ɾ��
                 * ��Ҫͬ��
                 */
                removeOut(nickName);
                /*
                 * ֪ͨ�����û����û�������
                 */
                sendMessage(nickName+"������");
                System.out.println("��ǰ��������:"+allOut.size());
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /**
         * ��ȡ���û����ǳ�
         * @return
         */
        private String getNickName()throws Exception{
            try {
                //��ȡ���û��������
                OutputStream out = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");
                PrintWriter pw = new PrintWriter(osw,true);
                //��ȡ���û���������
                InputStream in = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                //��ȡ�ͻ��˷��͹������ǳ�
                String nickName = br.readLine();
                while(true){
                    //���ǳ�Ϊ�շ���ʧ�ܴ���
                    if(nickName.trim().equals("")){
                        pw.println("FAIL");
                    }
                    //���ǳ��Ѿ����ڷ���ʧ�ܴ���
                    if(allOut.containsKey(nickName)){
                        pw.println("FAIL");
                        //���ɹ������ͳɹ����룬�������ǳ�
                    }else{
                        pw.println("OK");
                        return nickName;
                    }
                    //�����ǳƱ�ռ�ã��ȴ��û��ٴ������ǳ�
                    nickName = br.readLine();
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
}