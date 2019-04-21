import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*; 
import java.util.*; 

public class NameServer{
	int id;
	int serverPort;
	String serverIP;
	int predessorID;
	String predessorIp;
	int predessorPort;
	int successorID;
	String successorIp;
	int successorPort;
	String bootStrapIp;
	int bootStrapPort;
	String slash;
	String OS;
	ArrayList<Integer> trail = new ArrayList<>();
	HashMap<Integer,String> data = new HashMap<Integer,String>();
	
	public NameServer(String fileName) throws UnknownHostException, IOException{
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
				bootStrapIp = line[0];
				bootStrapPort = Integer.parseInt(line[1]);
		    }
		    sc.close();
		    InetAddress inetAddress = InetAddress.getLocalHost();
		    this.serverIP = inetAddress.getHostAddress();
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	  	}
	public void Enter (String bootStrapIp, int bootStrapPort ,NameServer ns) throws ClassNotFoundException {
		try {
			Socket s = new Socket(bootStrapIp, bootStrapPort);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF("Name server has been connected with a id:" + this.id);
			dos.writeUTF("Enter");
			int situation  = dis.readInt();
			if(situation == 0) {
				//sending the request id to bootstrap server
				dos.writeInt(ns.id);
				dos.writeUTF(ns.serverIP); 
				dos.writeInt(ns.serverPort);
				//recieving the responses from bootstrap server
				ns.successorID = dis.readInt();
				ns.successorIp = dis.readUTF();
				ns.successorPort = dis.readInt();
				ns.predessorID = successorID;
				ns.predessorIp = successorIp;
				ns.predessorPort = successorPort;
				//revieveing the data from bootstrap server
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
    			ObjectInputStream is = new ObjectInputStream(s.getInputStream());
    			data = (HashMap<Integer,String>) is.readObject();
    			trail.add(predessorID);
			}
			else {
				
			}
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		
	}
	public void exit() {
	
	}
	
	public static void main(String Args[])throws IOException  {
		// server is listening on port 6000 
		NameServer ns = new NameServer("nsConfigFile.txt");
        @SuppressWarnings("static-access")
		ServerSocket ss = new ServerSocket(ns.serverPort); 
        
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
            try { 
	            // socket object to receive incoming client requests 
	            s = ss.accept(); 
	            System.out.println("A new client is connected : " + s); 
	
	            // obtaining input and out streams 
	            DataInputStream dis = new DataInputStream(s.getInputStream()); 
	            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	              
	            String recieved = dis.readUTF();
	            System.out.println(recieved);
	            if(recieved.contains("client")) {
		            // create a new thread object 
	            	System.out.println("Assigning new thread for this client");
	            	 // Invoking the start() method 
		            Thread c = new ClientRequestHandler(s, dis, dos, ns);
		            c.start();
	            }
	            else {
	            	String message = dis.readUTF();
	            	System.out.println(message);
	            	switch(message)
	            	{
	            	case"Forwarding request":
	            		
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