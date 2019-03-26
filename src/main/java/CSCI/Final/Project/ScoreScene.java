package CSCI.Final.Project;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class displays top matches played, based off of player one's highest scores.
 * @author Matthew Demoe
 * @author Geerthan Srikantharajah
 * @author Gage Adam
 */
public class ScoreScene extends Application {

	//Only show n scores on high score display
	private double maxScoreDisplay = 7;

	public void start(Stage primaryStage) {

		//Main pane
		VBox p = new VBox(20);
		p.setAlignment(Pos.TOP_CENTER);
		p.setPadding(new Insets(20, 20, 20, 20));
        p.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

		Text titleText = new Text("High Scores");

		Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 35);
        titleText.setFont(titleFont);
        titleText.setFill(Color.RED);

        Font playerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 22);

        VBox p1VBox = new VBox(20);
        p1VBox.setAlignment(Pos.TOP_CENTER);

        VBox p2VBox = new VBox(20);
        p2VBox.setAlignment(Pos.TOP_CENTER);

        Text p1Text = new Text("Player 1");
        p1Text.setFont(playerFont);
        p1Text.setFill(Color.LIGHTSEAGREEN);

        Text p2Text = new Text("Player 2");
        p2Text.setFont(playerFont);
        p2Text.setFill(Color.LIGHTSEAGREEN);

        p1VBox.getChildren().add(p1Text);
        p2VBox.getChildren().add(p2Text);

        //Stores all scores read from file
        ArrayList<Pair<String, String>> scores = new ArrayList<Pair<String, String>>();

        try {
        	Scanner in = new Scanner(new File("src/main/resources/saves/saves.dat"));
	        while(in.hasNextLine()) {
	        	String[] str = in.nextLine().split(",");
	        	if(str.length != 2) break; //Invalid format

	        	scores.add(new Pair<String, String>(str[0], str[1]));
	        }
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }

        //Sort by player one's highest scores 
        Collections.sort(scores, Comparator.comparing(str -> Integer.valueOf(str.getKey())));

        //Either display maxScoreDisplay scores, or all scores if there are less scores than the maxScoreDisplay limit
        for(int i = scores.size()-1;i >= Math.max(0, scores.size()-maxScoreDisplay);i--) {
        	Text p1Score = new Text(scores.get(i).getKey());
			p1Score.setFont(playerFont);
			p1Score.setFill(Color.GRAY);

		    Text p2Score = new Text(scores.get(i).getValue());
		    p2Score.setFont(playerFont);
			p2Score.setFill(Color.GRAY);

		    p1VBox.getChildren().add(p1Score);
		    p2VBox.getChildren().add(p2Score);
        }
        
        HBox playerScores = new HBox(100);
	    playerScores.setAlignment(Pos.TOP_CENTER);
	    playerScores.getChildren().addAll(p1VBox, p2VBox);

        p.getChildren().addAll(titleText, playerScores);	

		Scene s = new Scene(p, 800, 600);
		primaryStage.setScene(s);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}