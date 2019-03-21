package main.java.CSCI.Final.Poject;

import java.lang.Math;

public class Vec2
{
    public Vec2(double _x, double _y)
    {
       x = _x;
       y = _y;
    }

    public double Magnitude()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public void Normalize()
    {
        x /= Magnitude();
        y /= Magnitude();
    }

    public Vec2 GetNormal()
    {
        return new Vec2(x / Magnitude(), y / Magnitude());
    } 

    public double x;
    public double y;

}