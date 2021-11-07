package a;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    //�ͻ���Socket
    private Socket socket;
    /**
     * ���췽�������ڳ�ʼ��
     */
    public Client(){
        try {
            socket = new Socket("localhost",8088);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * �ͻ��˹�������
     */
    public void start(){
        try {
            //����Scanner��ȡ�û���������
            Scanner scanner = new Scanner(System.in);
            //���������ǳ�
            inputNickName(scanner);

            //�����շ������Ϣ���߳�����
            ServerHander handler = new ServerHander();
            Thread t = new Thread(handler);
            t.setDaemon(true);
            t.start();

            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");
            PrintWriter pw = new PrintWriter(osw,true);
            while(true){
                pw.println(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * �����ǳ�
     */
    private void inputNickName(Scanner scanner)throws Exception{
        //�����ǳ�
        String nickName = null;
        //���������
        PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        socket.getOutputStream(),"UTF-8")
                ,true);
        //����������
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream(),"UTF-8")
        );
        /*
         * ѭ�����²���
         * �����û��������ϴ������������ȴ���������Ӧ�����ǳƿ��þͽ���ѭ��������֪ͨ�û���
         * ���������ǳ�
         */
        while(true){
            System.out.println("�������ǳ�:");
            nickName = scanner.nextLine();
            if(nickName.trim().equals("")){
                System.out.println("�ǳƲ���Ϊ��");
            }else{
                pw.println(nickName);
                String pass = br.readLine();
                if(pass!=null&&!pass.equals("OK")){
                    System.out.println("�ǳ��ѱ�ռ�ã��������");
                }else{
                    System.out.println("���!"+nickName+",��ʼ�����!");
                    break;
                }
            }
        }
    }

    /**
     * ���߳����ڽ��շ���˷��͹�������Ϣ
     */
    private class ServerHander implements Runnable{
        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                while(true){
                    System.out.println(br.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}