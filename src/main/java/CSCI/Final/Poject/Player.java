package main.java.CSCI.Final.Poject;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Player extends GameObject
{  
    Line line;

    public Player(Double x, double y, Pane pane)
    {
        super(x, y);

        line = new Line(_position.x + 50, _position.y + 50, _position.x - 50, _position.y - 50);
        components.add(line);

        for(int i = 0; i < components.size(); i++)
        {
            pane.getChildren().add(components.get(i));
        }
    }

    @Override 
    public void Update()
    {

    }

}