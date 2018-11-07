import sun.rmi.server.InactiveGroupException;

public class Point extends java.awt.Point {
    public Point point;
    public int cornerNum = 0;

    public Point(int x,int y,Point point){
        super(x,y);
        this.point = point;
    }

    public Point(int x,int y){
        super(x,y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "cornerNum=" + cornerNum +
                '}';
    }
}
