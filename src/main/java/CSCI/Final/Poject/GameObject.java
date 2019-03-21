package main.java.CSCI.Final.Poject;

import javafx.scene.layout.Pane;
import java.lang.Object;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.Vector;
import javafx.scene.paint.Color;

public abstract class GameObject
{
    GameObject(Double x, Double y, Pane pane)
    {
        _position = new Vec2(x, y);
        _pane = pane;
        components = new Vector<Line>();
    }

    public void Move(Vec2 amount)
    {
        Double newX = _position.x + amount.x;
        Double newY = _position.y + amount.y;

        if((newX > 0) && (newX < _pane.getWidth()))
            _position.x += amount.x;

        if((newY > 0) && (newY < _pane.getHeight()))
            _position.y += amount.y;

        for(int i = 0; i < components.size(); i++)
        {
            if((newX > 0) && (newX < _pane.getWidth()))
            {
                components.get(i).setStartX(components.get(i).getStartX() + amount.x);
                components.get(i).setEndX(components.get(i).getEndX() + amount.x);
            }

            if((newY > 0) && (newY < _pane.getHeight()))
            {
                components.get(i).setStartY(components.get(i).getStartY() + amount.y);
                components.get(i).setEndY(components.get(i).getEndY() + amount.y);  
            }                    
        }
    }

    public void Update()
    {

    }

    public Double GetSize()
    {
        return size;
    }

    public void SetColor(Color c)
    {
        for(int i = 0; i < components.size(); i++)
        {
            components.get(i).setStroke(c);
            components.get(i).setStrokeWidth(5);
        }
    }

    protected Vector<Line> components;
    protected Vec2 _position;  
    protected Double size;
    protected Vec2 _velocity;

    protected Pane _pane;
}