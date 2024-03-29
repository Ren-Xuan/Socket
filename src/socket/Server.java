package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

 
public class Server {
	private static List<Channel> all=new ArrayList<Channel>();
	private static List<String> names=new ArrayList<String>();
	public static void main(String[] args)throws Exception{
		System.out.println("----Server----");
		ServerSocket server = new ServerSocket(9991);
		while(true) {
			Socket client = server.accept();
			System.out.println("一个客户端建立了连接");
			Channel c=new Channel(client);
			all.add(c);
			new Thread(c).start();
		}
	}
	//一个客户端代表一个Channel
	static class Channel implements Runnable{
		private DataInputStream dis;
		private DataOutputStream dos;
		private Socket client;
		private boolean isRunning;
		private String name;
		
		
		public Channel(Socket client) {
			
			this.client = client;
			try {
				//定义socket客户端的输入流
				dis = new DataInputStream(client.getInputStream());
				//定义socket客户端的输出流
				dos=new DataOutputStream(client.getOutputStream());
				isRunning=true;
				//接收消息
				this.name=receive();
				for(String s:names) {
					if(s.equals(name)) {
						//把收到的消息发送给指定的客户端
						this.send("false");
						this.name="";
						return;
					}	
				}
				this.send("true");
				names.add(name);
				System.out.println("name:"+name);
				sendOthers("------"+this.name+"进入聊天室------",true);
			} catch (IOException e1) {
				release();
			}
		}	
		//接收消息
		private String receive() throws IOException {
			String msg="";
			msg=dis.readUTF();
			return msg;
		}
		//发送消息
		private void send(String msg) {
			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException e) {
				release();
			}
		}
		/**
		 * 群聊：获取自己的消息，发给其他人
		 * 
		 */
		private void sendOthers(String msg,boolean isSys) {
			for(Channel other: all) {
				if(!isSys) {
					if(other==this)  //自己
						other.send("我："+msg);
					else
						other.send(this.name +"："+msg);//群聊消息
				}else 
					other.send(msg); //系统消息
			}
		}
		//释放资源
		private void release() {
			this.isRunning=false;
			Utils.close(dis,dos,client);
			all.remove(this);
			if(this.name!="")
				sendOthers("------"+this.name+"离开聊天室------",true);
		}
		//线程
		public void run() {
			while(isRunning) {
				String msg="";
				try {
					msg = receive();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!msg.equals("")) 
					sendOthers(msg,false);
			}
		}
	}
}
