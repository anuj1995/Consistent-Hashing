import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*; 
import java.util.*;
import java.util.Map.Entry; 

public class BootStrapServer{
	int id;
	String serverIP;
	int serverPort;
	int predessorID;
	String predessorIp;
	int predessorPort;
	int successorID ;
	String successorIp;
	int successorPort;
	String slash;
	String OS;
	HashMap<Integer,String> data = new HashMap<Integer,String>();
	public BootStrapServer(String fileName) throws UnknownHostException{
		
		// TODO Auto-generated constructor stub
		OS = System.getProperty("os.name").toLowerCase();
		if(OS.indexOf("win") >= 0)
			slash ="\\";
		else if((OS.indexOf("mac") >= 0 || OS.indexOf("nux") >= 0))
			slash="/";
		
		File file = new File(System.getProperty("user.dir") +slash+  fileName); 
		try {
			Scanner sc = new Scanner(file);
			this.id  = Integer.parseInt(sc.nextLine());
			this.serverPort = Integer.parseInt(sc.nextLine());
			
		    while (sc.hasNextLine()) {
				String[] line = sc.nextLine().split(" "); 
				this.data.put(Integer.parseInt(line[0]),line[1]);
		    }
		    sc.close();
		    InetAddress inetAddress = InetAddress.getLocalHost();
		    this.serverIP = inetAddress.getHostAddress();
		    this.successorID = 0 ;
		    this.successorIp = null;
		    this.successorPort  = -1;
		    this.predessorID = 0;
		    this.predessorPort = -1;
		    this.predessorIp = null;
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
	  	}
	public String lookupKey (Integer key) {
		return null;
	}
	public void InsertKeyValue(Integer key, String Value) {
	
	}
	public void DeleteKey(Integer key) {
		
	}

	private void forwardEntryRequest(int requestID, String requestIp, int requestPort, int successorID, String successorIp ,
			int successorPort)
	{
		Socket s;
		try {
			s = new Socket(successorIp, successorPort);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Name server has forwarded a request id:" + requestID);
	        dos.writeUTF("Forwarding request");
	        dos.writeInt(requestID);
			dos.writeUTF(requestIp); 
			dos.writeInt(requestPort);
			dos.flush();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	 
	
	public static void main(String Args[])throws IOException  {
		BootStrapServer bs = new BootStrapServer("bnConfigFile.txt");
		ServerSocket ss = new ServerSocket(bs.serverPort); 
        // server is listening on port 5056 for other server
        // running infinite loop for getting 
        // client request
        // server request
        while (true)  
        { 
            Socket s = null;
            try { 
	            // socket object to receive incoming client requests 
	            s = ss.accept();
	            // obtaining input and out streams 
	            DataInputStream dis = new DataInputStream(s.getInputStream()); 
	            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	            String recieved = dis.readUTF();
	            System.out.println(recieved);
	            if(recieved.contains("client")) {
		            // create a new thread object 
	            	System.out.println("Assigning new thread for this client");
	            	 // Invoking the start() method 
		            Thread c = new ClientRequestHandler(s, dis, dos, bs);
		            c.start();
	            }
	            else {
	            	String message = dis.readUTF();
	            	System.out.println(message);
	            	switch(message)
	            	{
	            	case"Enter":
	            		if(bs.successorID == 0 && bs.predessorID == 0) {
	            			dos.writeInt(0);
	            			int requestID = dis.readInt();
		            		String requestIp = dis.readUTF();
		            		int requestPort = dis.readInt();
	            			//indicating first name server into the system
	            			//setting up the sucessors and predessors
	            			bs.successorID = requestID;
	            			bs.successorIp = requestIp;
	            			bs.successorPort = requestPort;
	            			bs.predessorID = requestID;
	            			bs.predessorIp =requestIp;
	            			bs.predessorPort = requestPort;
	            			dos.writeInt(bs.id);
	            			dos.writeUTF(bs.serverIP);
	            			dos.writeInt(bs.serverPort);
	            			// create a submap for the request
	            			HashMap<Integer,String> subMap = new HashMap<Integer,String>();
	            			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	            			for(Map.Entry<Integer, String> entry : bs.data.entrySet())
	            			{
	            				int currentID = entry.getKey();
	            				String currentValue = entry.getValue();
	            				if(currentID < requestID)
	            				{
	            					subMap.put(currentID,currentValue);
	            				}
	            			}
	            			os.writeObject(subMap);
	            			for(Map.Entry<Integer, String> entry : subMap.entrySet())
	            			{
	            				int currentID = entry.getKey();
	            				String currentValue = entry.getValue();
	            				if(bs.data.containsKey(currentID))
	            				{
	            					bs.data.remove(currentID,currentValue);
	            				}
	            			}
	            		}
	            		else{
	            			dos.writeInt(1);
	            			int requestID = dis.readInt();
		            		String requestIp = dis.readUTF();
		            		int requestPort = dis.readInt();
		            		/*if(requestID > bs.id && requestID < bs.successorID)
		            		{
		            			
		            		}*/
		            		bs.forwardEntryRequest(requestID, requestIp, requestPort, bs.successorID, bs.successorIp, bs.successorPort);
		            		
	            		}
	            		break;
	            	case "Exit":
	            		break;
	            	}
	            	
	            }
	           
	            	              
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
	}
}