package main.java.CSCI.Final.Poject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.Random;
import java.util.Vector;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

import main.java.CSCI.Final.Poject.Player;

public class GameScene extends Application
{
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Socket socket;

    String serverIP;

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

    int timeFromServer;
    String serverIn;

    private Text p1Score;
    private Text p2Score;
    private Text timer;

    private boolean prevFired = false;

    private int whoWon;

    public GameScene()
    {
        //highScores = new int[5][5];
        currentFileName = "src/main/resources/saves/saves.dat";
    }

    public void SetIP(String ip)
    {
        serverIP = ip;
    }

    @Override
    public void start(Stage primaryStage)
    {
        Font gameFont = Font.loadFont(getClass().getResourceAsStream("/fonts/OCR A Std Regular.ttf"), 12);

        p1Score = new Text(50, 50, "K: 0 \nD: 0");
        p1Score.setFont(gameFont);
        p1Score.setScaleX(2.0);
        p1Score.setScaleY(2.0);

        p2Score = new Text(primaryStage.getWidth() - 75, 50, "K: 0 \nD: 0");
        p2Score.setFont(gameFont);
        p2Score.setScaleX(2.0);
        p2Score.setScaleY(2.0);
        p2Score.setFill(Color.RED);

        timer = new Text(primaryStage.getWidth() / 2.0, 50, "60");
        timer.setFont(gameFont);
        timer.setScaleX(3.0);
        timer.setScaleY(3.0);
        timer.setFill(Color.GREEN);


        try
        {
            // 3. Create a socket to connect to the server
              socket = new Socket(serverIP, 8000);

            // 4. Create an input stream to receive data from the server
              fromServer = new DataInputStream(socket.getInputStream());

            // 5. Create an output stream to send data to the server
              toServer = new DataOutputStream(socket.getOutputStream());


              //When you connect to the server, it will send you an int representing which player you are
              playerNum = fromServer.readInt();
              System.out.println(playerNum);
      
              if(playerNum == 1)
              {
                  p1Score.setFill(Color.LIGHTSEAGREEN);
                p2Score.setFill(Color.RED);
              }
            
            else
            {
                p1Score.setFill(Color.RED);
                p2Score.setFill(Color.LIGHTSEAGREEN);

            }        

          }
          catch (IOException ex)
          {
          }

        Random rand = new Random();
        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        for(int i = 0;i < 200;i++) {
            Rectangle r = new Rectangle(rand.nextInt(796)+2, rand.nextInt(596)+2, 2, 2);
            r.setFill(Color.rgb(255, 255, 255, rand.nextFloat()));
            pane.getChildren().add(r);
        }

        players = new Vector<Player>();
        projectiles = new Vector<Projectile>();

        kills = new Vector<Integer>();
        deaths = new Vector<Integer>();

        pane.getChildren().add(p1Score);
        pane.getChildren().add(p2Score);
        pane.getChildren().add(timer);

        //Initializing players
        if(playerNum == 1)
        {
            player = new Player(1, 100.0, 100.0, pane);
            player2 = new Player(2, 500.0, 500.0, pane);

            player.SetColor(Color.LIGHTSEAGREEN);
            player2.SetColor(Color.RED);
        }

        else
        {
            player = new Player(2, 100.0, 100.0, pane);
            player2 = new Player(1, 500.0, 500.0, pane);

            player.SetColor(Color.RED);
            player2.SetColor(Color.LIGHTSEAGREEN);
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
                    timeFromServer = fromServer.readInt();
                    timer.setText("" + timeFromServer);

                    if((timeFromServer <= 30) && timer.getFill() == Color.GREEN )
                        timer.setFill(Color.ORANGE);

                    
                    if((timeFromServer <= 15) && timer.getFill() == Color.ORANGE )
                        timer.setFill(Color.RED);
                        
                    if(timeFromServer <= 0) 
                        break;

                    //This is where we read data from the server
                    serverIn = fromServer.readUTF();
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
                                        //if the projectile is colliding, then delete all projectiles
                                        for(int k = 0;k < projectiles.size();k++)
                                          projectiles.get(k).deleteThis();
                                        projectiles.clear();
                                        for(int k = 0;k < players.size();k++) {
                                          players.get(k).Respawn(100.0 + 400.0*k, 100.0 + 400.0*k);
                                          players.get(k).Rotate(180.0*k);
                                        }
                                        deaths.setElementAt(deaths.get(i)+1, i);                                  

                                        if (i == 0)
                                        {
                                          kills.setElementAt(kills.get(1)+1, 1);
                                        }

                                        else if (i == 1)
                                        {
                                          kills.setElementAt(kills.get(0)+1, 0);
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

            save();
            Platform.runLater(() -> {
                primaryStage.setScene(getEndScene(kills.get(0), kills.get(1), playerNum));
            });

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
            FileOutputStream out = new FileOutputStream(f, true);
            PrintWriter p = new PrintWriter(out);

            if(playerNum == 1) p.println(kills.get(1) + "," + kills.get(0));
            else p.println(kills.get(0) + "," + kills.get(1));

            p.flush();
            p.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Scene getEndScene(int p1Kills, int p2Kills, int activePlayer) {

        //Make sure p1 is active user
        if(activePlayer != 1) {
          int buf = p1Kills;
          p1Kills = p2Kills;
          p2Kills = buf;
        }

        VBox endVBox = new VBox(20);
        endVBox.setAlignment(Pos.CENTER);
        endVBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Font endFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 40);
        Font endScoreFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 40);

        Text endText = new Text();
        endText.setFont(endFont);

        if(p1Kills < p2Kills) {
            endText.setFill(Color.RED);
            endText.setText("You lose");
        }
        else if(p1Kills > p2Kills) {
          endText.setFill(Color.LIGHTSEAGREEN);
          endText.setText("You win!");
        }
        else {
          endText.setFill(Color.GRAY);
          endText.setText("Tie game");
        }

        HBox scoreHBox = new HBox(80);
        scoreHBox.setAlignment(Pos.CENTER);

        Text p1KillTxt = new Text(String.valueOf(p1Kills));
        p1KillTxt.setFont(endScoreFont);
        p1KillTxt.setFill(Color.GRAY);

        Text p2KillTxt = new Text(String.valueOf(p2Kills));
        p2KillTxt.setFont(endScoreFont);
        p2KillTxt.setFill(Color.GRAY);

        scoreHBox.getChildren().addAll(p1KillTxt, p2KillTxt);

        endVBox.getChildren().addAll(endText, scoreHBox);

        Scene s = new Scene(endVBox, 800, 600);
        return s;

    }
}
