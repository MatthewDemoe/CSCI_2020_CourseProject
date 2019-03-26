package CSCI.Final.Project;

import java.lang.Math;

/**
 * This 2D vector class is used for Teen Galaga's player movement.
 * @author Matthew Demoe
 * @author Geerthan Srikantharajah
 * @author Gage Adam
 */
public class Vec2
{
    public double x;
    public double y;

    public Vec2(double _x, double _y)
    {
       x = _x;
       y = _y;
    }

    //Returns magnitude of vector
    public double Magnitude()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    //Normalized the x and y members 
    public void Normalize()
    {
        x /= Magnitude();
        y /= Magnitude();
    }

    //Returns a new normal vector 
    public Vec2 GetNormal()
    {
        return new Vec2(x / Magnitude(), y / Magnitude());
    } 
}