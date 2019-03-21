package main.java.CSCI.Final.Poject;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.input.KeyCode;


public class Player extends GameObject
{  
    Line topLeft;
    Line topRight;
    Line bottom;
    boolean keyStates[];
    Double moveSpeed = 1.0;
    Vec2 forward;
    Vec2 right;

    Double rotation = 0.0;
    Double turnSpeed = 3.0;

    Vec2 _velocity;
    Double _drag = 0.9;


    public Player(Double x, double y, Pane pane)
    {
        super(x, y, pane);
        
        keyStates = new boolean[KeyCode.W.ordinal() + 1];

        forward = new Vec2(0.0, 1.0);
        right = new Vec2(1.0, 0.0);

        size = 25.0;

        topLeft = new Line();
        topRight = new Line();

        components.add(topLeft);
        components.add(topRight);

        CreateTriangle();

        for(int i = 0; i < components.size(); i++)
        {
            pane.getChildren().add(components.get(i));
        }      
        
        _velocity = new Vec2(0.0, 0.0);
    }

    public void SetKey(KeyCode key, boolean b)
    {
        keyStates[key.ordinal()] = b;
    }

    @Override 
    public void Update()
    {
        if(keyStates[KeyCode.W.ordinal()])
        {
            _velocity.x += forward.x * moveSpeed;
            _velocity.y += forward.y * moveSpeed;
        } 

        if(keyStates[KeyCode.S.ordinal()])
        {            
            _velocity.x += forward.x * -moveSpeed;
            _velocity.y += forward.y * -moveSpeed;
        }

        if(keyStates[KeyCode.A.ordinal()])
            Rotate(-turnSpeed);

        if(keyStates[KeyCode.D.ordinal()])
            Rotate(turnSpeed);         

        Move(_velocity);

        _velocity.x *= _drag;
        _velocity.y *= _drag;
    }

    private void Rotate(Double theta)
    {
        rotation += theta;

        double x = Math.sin(Math.toRadians(rotation));
        double y = Math.cos(Math.toRadians(rotation));

        forward.x = x;
        forward.y = y;

        forward.Normalize();
        
        right.x = y;
        right.y = x;

        right.Normalize();

        CreateTriangle();
    }

    private void CreateTriangle()
    {
        Vec2 top = new Vec2(_position.x + (forward.x * size), _position.y + (forward.y * size));
        Vec2 bl = new Vec2(_position.x - (forward.x * size) - (right.x * size), _position.y - (forward.y * size) - (right.y * size));
        Vec2 br = new Vec2(_position.x - (forward.x * size) + (right.x * size), _position.y - (forward.y * size) + (right.y * size));


        components.get(0).setStartX(top.x);
        components.get(0).setStartY(top.y);
        components.get(0).setEndX(bl.x);
        components.get(0).setEndY(bl.y);

        components.get(1).setStartX(top.x);
        components.get(1).setStartY(top.y);
        components.get(1).setEndX(br.x);
        components.get(1).setEndY(br.y);

    }
}