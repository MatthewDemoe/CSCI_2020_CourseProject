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

            //Stop listening for new clients when 2 have connected 
            if(clientNo == maxUsers)
            {
              break;
            }
        }

        while(true)
        {
          try
          {
            //Send all received data after every 10 milliseconds 
            Thread.sleep(10);
          }

          catch(InterruptedException ex){}
                         
          //Reset player input string
          playerInputs = "";

          //Append player input information to the string
          for(int i = 0; i < clients.size(); i++)
          {            
            playerInputs += i + clients.get(i).GetInputs();
          }

          //Send input to both players, who are running the game synchronously 
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
          // 6. Receive key input from client
          char inChar =  inputFromClient.readChar();
          
          //Write received keystate to input string
          inputs += inChar;
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