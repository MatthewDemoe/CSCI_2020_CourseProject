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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
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
    private int kills;
    private int deaths;
    private int highScores[5][5];

    private int playerNum = 0;

    public GameScene()
    {
        try {
            // 3. Create a socket to connect to the server
              socket = new Socket("localhost", 8000);

            // 4. Create an input stream to receive data from the server
              fromServer = new DataInputStream(socket.getInputStream());

            // 5. Create an output stream to send data to the server
              toServer = new DataOutputStream(socket.getOutputStream());

              playerNum = fromServer.readInt();
              System.out.println(playerNum);

          }
          catch (IOException ex) {
            //ta.appendText(ex.toString() + '\n');
          }


    }

    @Override
    public void start(Stage primaryStage)
    {
        Pane pane = new Pane();

        player = new Player(100.0, 100.0, pane);
        player2 = new Player(500.0, 500.0, pane);

        Scene scene = new Scene(pane, 1600, 900);

        GetInput(scene);

        new Thread(() -> {
            while(true)
            {
                try {
                    // Get the radius from the text field
                    //String test = "Memes";

                    //System.out.println(test);

                    // 1. Send the radius to the server

                    //toServer.writeUTF(test);

                    //toServer.flush();

                    // 2. Get area from the server
                    String serverIn = fromServer.readUTF();
                    HandleServerInput(serverIn);

                    //System.out.println(serverIn);


                    // Display to the text area
                    /*ta.appendText("Radius is " + radius + "\n");
                    ta.appendText("Area received from the server is " + area + '\n');*/
                  }
                  catch (IOException ex) {
                    System.err.println(ex);
                  }


            }
        }).start();
        /*Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                Runnable updater = new Runnable(){
                    @Override
                    public void run(){
                        //System.out.println("Memes");

                    }
                };

                while(true){
                    try{
                        Thread.sleep(10);
                    }catch(InterruptedException ex){
                    }

                    Platform.runLater(updater);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();*/

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
        System.out.println(in);
        int currentPlayer = 0;

        for(int i = 0; i < in.length(); i++)
        {
            switch(in.charAt(i))
            {
                case '1':
                    currentPlayer = 1;
                    break;

                case '2':
                    currentPlayer = 2;
                    break;

                case 'W':
                    if(currentPlayer == 1)
                        player.Move(new Vec2(0.0, -100.0));

                    if(currentPlayer == 2)
                        player2.Move(new Vec2(0.0, -100.0));

                    break;

                case 'S':
                    if(currentPlayer == 1)
                        player.Move(new Vec2(0.0, 100.0));

                    if(currentPlayer == 2)
                        player2.Move(new Vec2(0.0, 100.0));
                   break;

                case 'A':
                    if(currentPlayer == 1)
                        player.Move(new Vec2(-100.0, 0.0));

                    if(currentPlayer == 2)
                        player2.Move(new Vec2(-100.0, 0.0));
                    break;

                case 'D':
                    if(currentPlayer == 1)
                        player.Move(new Vec2(100.0, 0.0));

                    if(currentPlayer == 2)
                        player2.Move(new Vec2(100.0, 0.0));
                    break;
            }
        }
    }

    public void save() {
        File f = new File(currentFileName);

        try {

            PrintWriter p = new PrintWriter(f);

            for(int i = 0;i < l.size();i++) {
              if (kills >= highScores[i][0])
              {
                if (deaths <= highScores[i][1])
                {
                  if (i + 1 < 5)
                  {
                    highScores[i+1][0] = highScores[i][0];
                    highScores[i+1][1] = highScores[i][1];
                  }
                  highScores[i][0] = kills;
                  highScores[i][1] = deaths;
                }
              }
                p.println(kills + "," + deaths);
            }

            p.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load() {



        try {
            Scanner in = new Scanner(new File(currentFileName));
            int i = 0;
            while(in.hasNextLine()) {
                String[] str = in.nextLine().split(",");
                highScores[i][0] = str[0];
                highScores[i][1] = str[1];
                i++;
            }

            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
