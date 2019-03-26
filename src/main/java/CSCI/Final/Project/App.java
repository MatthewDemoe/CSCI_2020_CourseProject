package CSCI.Final.Project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.scene.control.TextField;
import java.io.File;

public class App extends Application 
{
    @Override
    public void start(Stage primaryStage)
    {
        GridPane pane = new GridPane();
        pane.setHgap(25);
        pane.setVgap(25);
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Text titleText = new Text("Teen Galaga");

        Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 50);
        titleText.setFont(titleFont);
        titleText.setFill(Color.RED);       

        Button startButton = new Button("Start");
        Button serverButton = new Button("Host Server");
        Button scoresButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        formatButtons(startButton, serverButton, scoresButton, exitButton);
        
        Font inputFont = Font.loadFont(getClass().getResourceAsStream("/fonts/OCR A Std Regular.ttf"), 12);

        TextField serverInput = new TextField();
        serverInput.setPromptText("Enter Server IP");
        serverInput.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        serverInput.setFont(inputFont);
        serverInput.setAlignment(Pos.CENTER);
        serverInput.setPrefColumnCount(serverInput.getPromptText().length()+5);

        HBox serverInputPane = new HBox(20);
        serverInputPane.setAlignment(Pos.CENTER_LEFT);
        serverInputPane.getChildren().addAll(startButton, serverInput);
        
        pane.add(titleText, 0, 0);
        //pane.add(serverInput, 1, 1);
        pane.add(serverInputPane, 0, 1);
        pane.add(serverButton, 0, 2);
        pane.add(scoresButton, 0, 3);
        pane.add(exitButton, 0, 4);

        //Application objects. When you click a button, it will run the start function in one of these 
        GameScene game = new GameScene();
        Server server = new Server();
        ScoreScene scoreView = new ScoreScene();


        startButton.setOnAction(e -> {
            //Start Main Game
            game.SetIP(serverInput.getText());
            game.start(primaryStage);         
        });

        serverButton.setOnAction(e -> {
            server.start(primaryStage);
        });

        scoresButton.setOnAction(e -> {
            //Show list of High scores
            scoreView.start(primaryStage);
        });

        exitButton.setOnAction(e -> {
            //Exit app
            Platform.exit();
        });

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Teen Galaga Menu"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }
    
    public void formatButtons(Button... bts) {
        Font buttonFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 20);

        EventHandler<MouseEvent> btHoverHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Button bt = (Button) (e.getSource());
                bt.setTextFill(Color.LIGHTSEAGREEN);
            }
        };

        EventHandler<MouseEvent> btReleaseHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Button bt = (Button) (e.getSource());
                bt.setTextFill(Color.GRAY);
            }
        };

        for(Button bt : bts) {
            bt.setFont(buttonFont);
            bt.setBackground(Background.EMPTY);
            bt.setTextFill(Color.GRAY);
            bt.setOnMouseEntered(btHoverHandler);
            bt.setOnMouseExited(btReleaseHandler);
        }

    }

    public static void main(String[] args) 
    {
        launch(args);

    }
}
