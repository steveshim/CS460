package edu.csula.cs460.graph.search;

/**
 * Created by SteveShim on 11/15/2015.
 */
public class IntPair {
    private int x;
    private int y;

    IntPair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntPair)) return false;

        IntPair ip = (IntPair) o;

        return (appendIP().equals(ip.appendIP()));

    }


    public String toString(){
        return "x = " + getX() + ", y = " +  getY();
    }

    public String appendIP(){
        return (getX() + "00" + getY());
    }


    @Override
    public int hashCode(){
        String append = getX() + "00" + getY();
        return Integer.parseInt(append);
    }
}
