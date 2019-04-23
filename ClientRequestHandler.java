import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*; 
import java.util.*; 

class ClientRequestHandler extends Thread  
{ 

    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s;
    BootStrapServer bs;
    NameServer ns;
      
  
    // Constructor for bootstrap client
    public ClientRequestHandler(Socket s, DataInputStream dis, DataOutputStream dos , BootStrapServer bs)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.bs = bs;
    }
    
    // Constructor for NameServer client
    public ClientRequestHandler(Socket s, DataInputStream dis, DataOutputStream dos , NameServer ns)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.ns = ns;
    }


    @Override
    public void run()  
    { 
        String received; 
        while (true)  
        { 
            try { 
                // Ask user what he wants
           /* 	DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());*/
                // receive the answer from client 
                received = dis.readUTF(); 
                if(received.equals("Exit")) 
                {  
                	ns.Exit(ns);
                    System.out.println("Client " + this.s + " sends exit..."); 
                    this.s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
                  
                // write on output stream based on the 
                // answer from the client 
                switch (received) { 
                    case "lookup" : 
                    	break; 
                          
                    case "Isert key value" : 
                        break; 
                          
                    case "enter" :
                    	ns.Enter(ns.bootStrapIp,ns.bootStrapPort,ns);
                    	Thread.sleep(1000);
                    	dos.writeUTF("sucessful entry");
                    	dos.writeUTF("The key range is: "+ (ns.predessorID + 1)  +" - "+ ns.id);
                    	dos.writeUTF("predessor ID: "+ns.predessorID +" Sucessor ID:" + ns.successorID);
                    	ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
                    	os.writeObject(ns.trail);
                    	
                    default: 
                        break; 
                } 
            } catch (IOException | ClassNotFoundException e) { 
                e.printStackTrace(); 
            } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        } 
          
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
            
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 