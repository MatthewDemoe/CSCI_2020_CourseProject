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
import java.util.Scanner;
import javafx.scene.text.Text;

import main.java.CSCI.Final.Poject.Player;

public class GameScene extends Application
{
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Socket socket;

    private Player player;
    private Player player2;
    private int k1 = 0;
    private int k2 = 0;
    private int d1 = 0;
    private int d2 = 0;
    private Vector<Integer> kills;
    private Vector<Integer> deaths;
    private int highScores[][];
    private Vector<Player> players;
    private Vector<Projectile> projectiles;
    private int playerNum = 0;
    private String currentFileName;

    private Text p1Score;
    private Text p2Score;

    private boolean prevFired = false;

    private int whoWon;

    public GameScene()
    {
        highScores = new int[5][5];
        currentFileName = "saves.dat";
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


              //When you connect to the server, it will send you an int representing which player you are
              playerNum = fromServer.readInt();
              System.out.println(playerNum);

              p1Score = new Text(50, 50, "K: 0 \nD: 0");
              p1Score.setScaleX(2.0);
              p1Score.setScaleY(2.0);
              p2Score = new Text(primaryStage.getWidth() - 75, 50, "K: 0 \nD: 0");
              p2Score.setScaleX(2.0);
              p2Score.setScaleY(2.0);
              p2Score.setFill(Color.RED);
      
              if(playerNum == 1)
              {
                  p1Score.setFill(Color.BLUE);
                p2Score.setFill(Color.RED);
              }
            
            else
            {
                p1Score.setFill(Color.RED);
                p2Score.setFill(Color.BLUE);

            }        

          }
          catch (IOException ex)
          {
          }

        Pane pane = new Pane();

        players = new Vector<Player>();
        projectiles = new Vector<Projectile>();

        kills = new Vector<Integer>();
        deaths = new Vector<Integer>();

        pane.getChildren().add(p1Score);
        pane.getChildren().add(p2Score);

        //Initializing players
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
        player2.Rotate(180.0);

        players.add(player);
        players.add(player2);

        kills.add(k1);
        kills.add(k2);
        deaths.add(d1);
        deaths.add(d2);

        Scene scene = new Scene(pane, 800, 600);

        GetInput(scene);

        //Creating a new thread to perform operations in
        Thread thread = new Thread(() -> {
            while(true)
            {
                try {
                    //This is where we read data from the server
                    String serverIn = fromServer.readUTF();
                    HandleServerInput(serverIn);

                    //Doing operations in JavaFX thread, because we are adjusting nodes
                    Platform.runLater(() -> {

                        for(int j = 0; j < projectiles.size(); j++)
                        {
                            //Move each projectile
                            projectiles.get(j).Update();

                            if(projectiles.get(j).CheckBounds())
                            {
                                projectiles.removeElementAt(j);
                                break;
                            }

                            for(int i = 0; i < players.size(); i++)
                            {
                                if(players.get(i).GetPlayerNum() != projectiles.get(j).GetPlayerNum())
                                {
                                    //Check if any projectiles are colliding with a player who's number is not the same as the projectile's
                                    if(projectiles.get(j).CheckCollision(players.get(i)))
                                    {
                                        //if the projectile is colliding, then delete it
                                        projectiles.removeElementAt(j);
                                        players.get(i).Respawn(100.0 + 400.0*i, 100.0 + 400.0*i);
                                        deaths.setElementAt(deaths.get(i)+1, i);                                  


                                        System.out.println(i+1 + " died " + deaths.get(i) + " times."); //Debugging comment to remove later

                                        if (i == 0)
                                        {
                                          kills.setElementAt(kills.get(1)+1, 1);
                                          System.out.println(2 + " killed " + kills.get(1) + " times."); //Debugging comment to remove later
                                        }

                                        else if (i == 1)
                                        {
                                          kills.setElementAt(kills.get(0)+1, 0);

                                          System.out.println(1 + " killed " + kills.get(0) + " times."); //Debugging comment to remove later
                                        }

                                        p1Score.setText("K: " + kills.get(0) + " \nD: " + deaths.get(0));

                                        p2Score.setText("K: " + kills.get(1) + " \nD: " + deaths.get(1));


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

    //Check the keystates of a player
    private void GetInput(Scene scene)
    {
        //When any key is pressed down
        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                try
                {
                    //Sending to the server which key has been pressed
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
                            if(!prevFired)
                              toServer.writeChar('X');
                            prevFired = true;
                            break;
                   }
               }
               catch (IOException ex) {
                System.err.println(ex);
              }
            }
        });



        //Whenever a key is released
        scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                try
                {
                    //Sending to the server which key has been released
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
                           prevFired = false;
                           break;
                   }
               }
               catch (IOException ex) {
                System.err.println(ex);
              }
            }
        });
    }

    //Argument is string sent from server
    private void HandleServerInput(String in)
    {
        //I am creating projectile to add to the scene, so javaFX is making me do this
        Platform.runLater(() -> {
        int currentPlayer = 0;

        //Iterate through each character in the string sent from the server. This corresponds to each keystate from each player
        for(int i = 0; i < in.length(); i++)
        {
            switch(in.charAt(i))
            {
                //0 means the following inputs are for player 0
                case '0':
                    currentPlayer = 0;
                    break;

                //1 means the following inputs are for player 1
                case '1':
                    currentPlayer = 1;
                    break;

                //Sets the corresponding keystate for the corresponding player
                //Capital letter means button down, lowercase means button up
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

 public void save() {
        File f = new File(currentFileName);

        try {

            PrintWriter p = new PrintWriter(f);

            for(int i = 0;i < highScores.length;i++) {
              if (kills.get(whoWon) >= highScores[i][0])
              {
                if (deaths.get(whoWon) <= highScores[i][1])
                {
                  if (i + 1 < 5)
                  {
                    highScores[i+1][0] = highScores[i][0];
                    highScores[i+1][1] = highScores[i][1];
                  }
                  highScores[i][0] = kills.get(whoWon);
                  highScores[i][1] = deaths.get(whoWon);
                }
              }
                p.println(kills.get(whoWon) + "," + deaths.get(whoWon));
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
                highScores[i][0] = Integer.valueOf(str[0]);
                highScores[i][1] = Integer.valueOf(str[1]);
                i++;
            }

            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
