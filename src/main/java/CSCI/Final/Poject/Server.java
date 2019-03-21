package main.java.CSCI.Final.Poject;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.util.Vector;

public class Server extends Application {
    
    public Server()
    {

    }

    private int clientNo = 0;
    private int maxUsers = 2;
    private Vector<HandleAClient> clients = new Vector<HandleAClient>();
    TextArea ta = new TextArea();

    String playerInputs = "";

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
    // Text area for displaying contents

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread( () -> {
      try {   	  
    	  
        // 1. Create a server socket
    	  ServerSocket serverSocket = new ServerSocket(8000);

        ta.appendText("Server started at " + new Date() + '\n');

        while(true)
        {
            // 2. Listen for a connection request
            Socket clientSock = serverSocket.accept();

            clientNo++;

            Platform.runLater( () -> {
            
              ta.appendText("Starting thread for client " + clientNo + " at " + new Date() + '\n');
  
              // 3. Create an instance of InetAddress for the client on the socket
              InetAddress inetAddr = clientSock.getInetAddress();
              
              // 4. Find the client's host name
              ta.appendText("Client " + clientNo + "'s host name is " + inetAddr.getHostName() + '\n');
              
              // 5. Find the client's IP address
              ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddr.getHostAddress() + '\n'); 
  
            });

            new Thread(new HandleAClient(clientSock)).start();

            if(clientNo == maxUsers)
            {
              break;
            }
        }

        while(true)
        {
          try
          {
            Thread.sleep(10);
          }

          catch(InterruptedException ex){}
                               
          playerInputs = "";

          for(int i = 0; i < clients.size(); i++)
          {            
            playerInputs += i + clients.get(i).GetInputs();
          }

          for(int i = 0; i < clients.size(); i++)
          {
            clients.get(i).GetOutputStream().writeUTF(playerInputs);
            clients.get(i).ResetInputs();
          }
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  class HandleAClient implements Runnable {
    private Socket socket; 
    private String inputs = "";
    DataOutputStream outputToClient;

    public HandleAClient(Socket socket) {
      this.socket = socket;
    }

    public DataOutputStream GetOutputStream()
    {
      return outputToClient;
    }
    
    public String GetInputs()
    {
      return inputs;
    }

    public void ResetInputs()
    {
      inputs = "";
    }

    public void run() {
      try {

        DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
        outputToClient = new DataOutputStream(socket.getOutputStream());
        outputToClient.writeInt(clientNo);

        clients.add(this);

        while (true) {
          // 6. Receive radius from the client
          char inChar =  inputFromClient.readChar();
        	
          inputs += inChar;
          System.out.println(inputs);

          // 7. Send area back to the client
            //outputToClient.writeDouble(area);
          
            Platform.runLater(() -> {
            ta.appendText("radius received from client: " + inChar + '\n');
            ta.appendText("Area found: " + inChar + '\n');
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}