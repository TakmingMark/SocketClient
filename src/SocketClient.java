import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
 
public class SocketClient {
	private JFrame fame=null;
	private JPanel panel=null;
	private JLabel LBmsg=null;
	private JTextField TFmsg=null;
	private JLabel LBIpAddress=null;
	private JTextArea TAcontent=null;
	private JScrollPane SPcontent=null;
	private JComboBox<String> CBuser=null;
	
	public static final String IP_ADDRESS="120.127.14.91";
	public static final int LISTEN_PORT = 2525;
 
    public static void main(String args[]) {
    	SocketClient socketClient=new SocketClient();
    	socketClient.initLayout();
    	socketClient.initListener();
    	socketClient.setIPAdrress();
    	socketClient.clientListener();
    }
    
    private void initLayout() {	
		fame=new JFrame("Socket Client");
		fame.setBounds(0,0,600,500);
		fame.setVisible(true);
		fame.setResizable(false);
		
		LBmsg=new JLabel("由此傳送訊息：");
		LBmsg.setLocation(10,10);
		LBmsg.setSize(100,30);
		
		TFmsg=new JTextField();
		TFmsg.setLocation(110,10);
		TFmsg.setSize(450,30);
		
		LBIpAddress=new JLabel("IP Address:");
		LBIpAddress.setLocation(10,50);
		LBIpAddress.setSize(400,30);
		
		TAcontent=new JTextArea("");
		TAcontent.setLineWrap(true);
		
		SPcontent=new JScrollPane(TAcontent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		SPcontent.setLocation(10,90); 
		SPcontent.setSize(400,350);
		
		
		
		CBuser=new JComboBox<String>();
		CBuser.setLocation(420,90);
		CBuser.setSize(150, 20);
		
		panel=new JPanel(null);
		panel.add(LBmsg);
		panel.add(TFmsg);
		panel.add(LBIpAddress);
		panel.add(SPcontent);
		panel.add(CBuser);
		
	    DefaultCaret caret = (DefaultCaret)TAcontent.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	   
		fame.add(panel);
		fame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fame.revalidate() ;	
	}
    
    private void initListener(){
		
	}
    
    private void setIPAdrress(){
		InetAddress adr = null;
		try {
			adr = InetAddress.getLocalHost(); //IPv6
			adr =Inet4Address.getLocalHost();//IPv4
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LBIpAddress.setText("IP Address:"+adr.getHostAddress());
	}
    
    private void clientListener(){
    	Socket socket;
    	try {
			socket=new Socket(IP_ADDRESS,LISTEN_PORT);
			socket.setSoTimeout(60000);
		  	TAcontent.append("Client connect..."+"\r\n");
		  	new Thread(new ClientThread(socket,TAcontent)).start();
		  	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
class ClientThread implements Runnable{
	private Socket socket=null;
	private JTextArea TAcontent=null;
	public String ipAddress="";
	public int listenPort=0;
	private DataOutputStream socketOutput=null;
	private DataInputStream socketInput=null;
	private String inputMsg="",outputMsg="";
	ClientThread(Socket socket,JTextArea TAcontent){
		try {
			this.socket=socket;
			this.TAcontent=TAcontent;
			ipAddress=socket.getLocalAddress().getHostAddress();
			listenPort=socket.getPort();
			socketOutput=new DataOutputStream(socket.getOutputStream());
			socketInput=new DataInputStream(socket.getInputStream());
			
			while(true){
				outputMsg=Protocol.C_USER_LOGIN.toString()+ipAddress+":"+listenPort+Protocol.C_USER_LOGIN.toString();
				socketOutput.writeUTF(outputMsg);
				inputMsg=socketInput.readUTF();
				
				if(inputMsg.startsWith(Protocol.S_USER_REPEAT.toString()) &&inputMsg.endsWith(Protocol.S_USER_REPEAT.toString())){
					TAcontent.append("user IP repeat...check your IP\r\n");
				}else if(inputMsg.startsWith(Protocol.S_USER_REPEAT.toString()) &&inputMsg.endsWith(Protocol.S_USER_SUCCESS.toString())){
					TAcontent.append("user login success\r\n");
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}