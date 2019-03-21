package main.java.CSCI.Final.Poject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.Vector;
import java.io.*;
import java.net.*;

import main.java.CSCI.Final.Poject.Player;

public class GameScene extends Application 
{
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Socket socket;
    

    private Player player;
    private Player player2;
    private Vector<Player> players;
    private Vector<Projectile> projectiles;

    private int playerNum = 0;

    public GameScene()
    {
        
    }

    @Override
    public void start(Stage primaryStage)
    {
        try 
        {
            // 3. Create a socket to connect to the server
              socket = new Socket("localhost", 8000);
      
            // 4. Create an input stream to receive data from the server
              fromServer = new DataInputStream(socket.getInputStream());
      
            // 5. Create an output stream to send data to the server
              toServer = new DataOutputStream(socket.getOutputStream());

              playerNum = fromServer.readInt();
              System.out.println(playerNum);
      
          }
          catch (IOException ex) 
          {
            //ta.appendText(ex.toString() + '\n');
          }

        Pane pane = new Pane();

        players = new Vector<Player>();
        projectiles = new Vector<Projectile>();

        if(playerNum == 1)
        {
            player = new Player(1, 100.0, 100.0, pane);
            player2 = new Player(2, 500.0, 500.0, pane);

            player.SetColor(Color.BLUE);
            player2.SetColor(Color.RED);
        }

        else
        {
            player = new Player(2, 100.0, 100.0, pane);
            player2 = new Player(1, 500.0, 500.0, pane);

            player.SetColor(Color.RED);
            player2.SetColor(Color.BLUE);
        }

        players.add(player);
        players.add(player2);

        Scene scene = new Scene(pane, 800, 600); 
        
        GetInput(scene);

        Thread thread = new Thread(() -> {
            while(true)
            {
                try {  
                    String serverIn = fromServer.readUTF();
                    HandleServerInput(serverIn);
                    
                    //JavaFX :)
                    Platform.runLater(() -> {

                        for(int j = 0; j < projectiles.size(); j++)
                        {
                            projectiles.get(j).Update();

                            for(int i = 0; i < players.size(); i++)
                            {
                                if(players.get(i).GetPlayerNum() != projectiles.get(j).GetPlayerNum())
                                {
                                    if(projectiles.get(j).CheckCollision(players.get(i)))
                                    {
                                        projectiles.removeElementAt(j);
                                        break;
                                    }

                                }
    
                            }
                        }

                        //Update all players
                        for(int i = 0; i < players.size(); i++)
                        {
                            players.get(i).Update();

                        }


                    }); 

                  }
                  catch (IOException ex) 
                  {
                    System.err.println(ex);
                  }
            }
        });

        thread.setDaemon(true);
        thread.start();

        primaryStage.setTitle("Teen Galaga"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage              
    }

    public static void main(String[] args) 
    {
        launch(args);
    }

    private void GetInput(Scene scene)
    {
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                try
                {
                   switch(event.getCode())
                   {
                        case W:
                           toServer.writeChar('W');
                           break;
                       
                        case S:
                           toServer.writeChar('S');
                           break;
   
                        case A:          
                           toServer.writeChar('A');
                           break;
   
                        case D:            
                           toServer.writeChar('D');
                           break;

                        case SPACE:
                            toServer.writeChar('X');
                            break; 
                   }
                   
               }
               catch (IOException ex) {
                System.err.println(ex);
              }
            }
        });
        
        scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                try
                {
                   switch(event.getCode())
                   {
                        case W:
                           toServer.writeChar('w');
                           break;
                       
                        case S:
                           toServer.writeChar('s');
                           break;
   
                        case A:          
                           toServer.writeChar('a');
                           break;
   
                        case D:            
                           toServer.writeChar('d');
                           break;

                        case SPACE:
                           toServer.writeChar('x');
                           break; 
                   }                   
               }
               catch (IOException ex) {
                System.err.println(ex);
              }
            }
        });
    }

    private void HandleServerInput(String in)
    {
        //I am creating projectile to add to the scene, so javaFX is making me do this
        Platform.runLater(() -> {
        int currentPlayer = 0;

        for(int i = 0; i < in.length(); i++)
        {

            switch(in.charAt(i))
            {
                case '0':
                    currentPlayer = 0;
                    break;

                case '1':
                    currentPlayer = 1;
                    break;

                case 'W':
                    players.get(currentPlayer).SetKey(KeyCode.W, true);
                    break;
                       
                case 'S':
                    players.get(currentPlayer).SetKey(KeyCode.S, true);
                   break;
 
                case 'A':                    
                    players.get(currentPlayer).SetKey(KeyCode.A, true);
                    break;
   
                case 'D':
                    players.get(currentPlayer).SetKey(KeyCode.D, true);
                    break;

                case 'X':
                    projectiles.add(players.get(currentPlayer).FireProjectile());
                    break;

                case 'w':
                    players.get(currentPlayer).SetKey(KeyCode.W, false);
                    break;
                       
                case 's':
                    players.get(currentPlayer).SetKey(KeyCode.S, false);
                   break;
 
                case 'a':                    
                    players.get(currentPlayer).SetKey(KeyCode.A, false);
                    break;
   
                case 'd':
                    players.get(currentPlayer).SetKey(KeyCode.D, false);
                    break;

                case 'x':
                    break;
            }
        }});
    }
}
