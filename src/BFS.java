import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    //每一个(tx,ty)代表一个点的上下右左相邻的点
    static int[] tx = {-1, 0, 1, 0};
    static int[] ty = {0, 1, 0, -1};
    static int[][] deep = new int[Map.ROW][Map.COL];

    public static Point bfs(Point p1, Point p2) {
        Queue<Point> q = new LinkedList<>();
        q.offer(p1);
        //设置一个相同规格的二位数组，除了第一个选择的点为0，其他都为-1,防止多次加入队列
        for (int i = 0; i < Map.ROW; i++)
            for (int j = 0; j < Map.COL; j++)
                deep[i][j] = -1;
        deep[p1.x][p1.y] = 0;
        return find(p1, p2, q);
    }

    public static Point find(Point p1, Point p2, Queue<Point> q) {
        while (q.size() > 0) {
            Point p = q.poll();
            //如果从队列取出的点为目标点，则退出while循环
            // if (p.x == p2.x && p.y == p2.y)
            //     break;
            //循环判断当前点相邻的点是否符合条件
            for (int i = 0; i < 4; i++) {
                int x = p.x + tx[i];
                int y = p.y + ty[i];
                //不超出边界,并且没访问过的点
                if (x >= 0 && x < Map.ROW && y >= 0 && y < Map.COL && deep[x][y] == -1) {
                    //选定的两个点值相同，并且当前点等于第二个点，返回经过的点数
                    if (Map.buttons[p1.x][p1.y].getText().equals(Map.buttons[p2.x][p2.y].getText()) && x == p2.x && y == p2.y) {
                        deep[x][y] = deep[p.x][p.y] + 1;
                        Point point = new Point(x, y, p);
                        return point;

                    } else if (Map.buttons[x][y].getText().equals(" ")) {
                        deep[x][y] = deep[p.x][p.y] + 1;
                        q.offer(new Point(x, y, p));
                    }
                }
            }
        }
        return new Point(0, 0);
    }
}
