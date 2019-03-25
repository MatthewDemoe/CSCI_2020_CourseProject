package CSCI.Final.Poject;

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

public class ScoreScene extends Application {

	public void start(Stage primaryStage) {

		VBox p = new VBox(20);
		p.setAlignment(Pos.TOP_CENTER);
		p.setPadding(new Insets(20, 20, 20, 20));
        p.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

		Text titleText = new Text("High Scores");

		Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 35);
        titleText.setFont(titleFont);
        titleText.setFill(Color.RED);

        HBox playerNames = new HBox(100);
        playerNames.setAlignment(Pos.TOP_CENTER);

        Font playerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/emulogic.ttf"), 22);

        Text p1Text = new Text("Player 1");
        p1Text.setFont(playerFont);
        p1Text.setFill(Color.LIGHTSEAGREEN);

        Text p2Text = new Text("Player 2");
        p2Text.setFont(playerFont);
        p2Text.setFill(Color.LIGHTSEAGREEN);

        playerNames.getChildren().addAll(p1Text, p2Text);

		p.getChildren().addAll(titleText, playerNames);

		Scene s = new Scene(p, 800, 600);
		primaryStage.setScene(s);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}