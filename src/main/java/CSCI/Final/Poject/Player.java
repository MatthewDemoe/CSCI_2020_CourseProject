package main.java.CSCI.Final.Poject;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;


public class Player extends GameObject
{
    Line topLeft;
    Line topRight;
    Line bottom;

    //Array of booleans to track which keys a player is holding
    boolean keyStates[];
    Double moveSpeed = 1.0;

    //Keeps track of the direction the player is looking
    Vec2 forward;
    Vec2 right;

    //Keeps track of the player's rotation
    Double rotation = 0.0;
    Double turnSpeed = 3.0;

    //Used for decreasing player speed every update
    Double _drag = 0.9;

    int playerNum;

    Double projectileSpeed = 8.0;
    boolean fired = false;

    public Player(int num, Double x, double y, Pane pane)
    {
        super(x, y, pane);

        playerNum = num;

        //Initialize array to hold W elements. W is the biggest ASCII character we are receiving
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

    @Override
    public void Update()
    {
        //Accelerate in the direction of the forward vector
        if(keyStates[KeyCode.W.ordinal()])
        {
            _velocity.x += forward.x * moveSpeed;
            _velocity.y += forward.y * moveSpeed;
        }

        //Accelerate in the direction opposite the forward vector
        if(keyStates[KeyCode.S.ordinal()])
        {
            _velocity.x += forward.x * -moveSpeed;
            _velocity.y += forward.y * -moveSpeed;
        }

        //Rotate the player's lines and vectors
        if(keyStates[KeyCode.A.ordinal()])
            Rotate(turnSpeed);

        if(keyStates[KeyCode.D.ordinal()])
            Rotate(-turnSpeed);

        //I should be doing this based on dt...
        Move(_velocity);

        _velocity.x *= _drag;
        _velocity.y *= _drag;
    }

    public void Rotate(Double theta)
    {
        rotation += theta;

        double x = Math.sin(Math.toRadians(rotation));
        double y = Math.cos(Math.toRadians(rotation));

        //Forward and right vectors are perpendicular to each other
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
        //Three points that create the triangle
        Vec2 top = new Vec2(_position.x + (forward.x * size), _position.y + (forward.y * size));
        Vec2 bl = new Vec2(_position.x - (forward.x * size) - (right.x * size), _position.y - (forward.y * size) - (right.y * size));
        Vec2 br = new Vec2(_position.x - (forward.x * size) + (right.x * size), _position.y - (forward.y * size) + (right.y * size));

        //Composing triangle from two lines. It's hard to tell which direction you're facing if you include the bottom line
        components.get(0).setStartX(top.x);
        components.get(0).setStartY(top.y);
        components.get(0).setEndX(bl.x);
        components.get(0).setEndY(bl.y);

        components.get(1).setStartX(top.x);
        components.get(1).setStartY(top.y);
        components.get(1).setEndX(br.x);
        components.get(1).setEndY(br.y);
    }

    //Create and return a projectile, which will be added to a list in the GameScene class
    public Projectile FireProjectile()
    {
        Projectile proj = new Projectile(playerNum, _position.x, _position.y, _pane);
        proj.SetVelocity(new Vec2(forward.x * projectileSpeed, forward.y * projectileSpeed));

        //Change color to fit the player who fired it
        if(playerNum == 1)
            proj.SetColor(Color.LIGHTSEAGREEN);

        else
            proj.SetColor(Color.RED);

        fired = true;

        return proj;
    }

    public void SetKey(KeyCode key, boolean b)
    {
        keyStates[key.ordinal()] = b;
    }

    public int GetPlayerNum()
    {
        return playerNum;
    }

    public void Respawn(Double x, Double y)
    {
      _velocity = new Vec2(0.0, 0.0);
      _position = new Vec2(x, y);
      Rotate(-rotation);
      CreateTriangle();
    }
}
