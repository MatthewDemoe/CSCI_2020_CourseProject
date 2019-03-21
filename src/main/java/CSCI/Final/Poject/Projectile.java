package main.java.CSCI.Final.Poject;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Projectile extends GameObject
{
    int playerNum;
    Line h;
    Line v;

    Projectile(int num, Double x, Double y, Pane pane)
    {
        super(x, y, pane);
        size = 5.0;

        h = new Line(_position.x + (size / 2.0), _position.y, _position.x - (size / 2.0), _position.y);
        v = new Line(_position.x , _position.y + (size / 2.0), _position.x, _position.y - (size / 2.0));

        components.add(h);
        components.add(v);

        for(int i = 0; i < components.size(); i++)
        {
            pane.getChildren().add(components.get(i));
        }      

        playerNum = num;
    }

    @Override
    public void Update()
    {
        Move(_velocity);
    }

    public int GetPlayerNum()
    {
        return playerNum;
    }

    public void SetVelocity(Vec2 vel)
    {
        _velocity = vel;
    }

    public boolean CheckCollision(GameObject obj)
    {
        Vec2 toObj = new Vec2(obj._position.x - _position.x, obj._position.y - _position.y);

        if(toObj.Magnitude() < (GetSize() + obj.GetSize()))
        {
            deleteThis();
            return true;
        }

        else if((_position.x <= 0 + _velocity.Magnitude()) || (_position.x >= _pane.getWidth() - _velocity.Magnitude()))
        {
            deleteThis();

            return true;
        }

        else if((_position.y <= 0 + _velocity.Magnitude()) || (_position.y >= _pane.getHeight() - _velocity.Magnitude()))        
        {
            deleteThis();
            return true;
        }

        return false;
    }

    public void deleteThis()
    {
        for(int i = 0; i < components.size(); i++)
        {
            _pane.getChildren().remove(components.get(i));
        }
    }
}