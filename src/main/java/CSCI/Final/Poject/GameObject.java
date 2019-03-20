package main.java.CSCI.Final.Poject;

import javafx.scene.layout.Pane;
import java.lang.Object;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.Vector;

public abstract class GameObject
{
    GameObject(Double x, Double y)
    {
        _position = new Vec2(x, y);
        components = new Vector<Line>();
    }

    public void Move(Vec2 amount)
    {
        for(int i = 0; i < components.size(); i++)
        {
            components.get(i).setStartX(components.get(i).getStartX() + amount.x);
            components.get(i).setEndX(components.get(i).getEndX() + amount.x);
            components.get(i).setStartY(components.get(i).getStartY() + amount.y);
            components.get(i).setEndY(components.get(i).getEndY() + amount.y);           
        }
    }

    public void Update()
    {

    }

    protected Vector<Line> components;
    protected Vec2 _position;    

}