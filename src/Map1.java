import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Map1 {
    static final int WIDTH = 800;
    static final int HEIGHT = 800;
    static final int ROW = 10;
    static final int COL = 10;
    //用于存放被点击按钮下标的队列
    static Queue<Point> queue = new ArrayDeque<>();
    //创建按钮
    static JButton[][] buttons = new JButton[ROW][COL];
    static int eachBtnSize = WIDTH / ROW;
    static Point tmpPoint = new Point(0, 0);
    static JPanel panel = new JPanel();
    static Graphics g;

    public static void main(String[] args) {
        JFrame frame = new JFrame("连连看");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel.setBackground(Color.white);
        frame.add(panel);
        //创建网格布局
        GridLayout gridLayout = new GridLayout(ROW, COL);
        panel.setLayout(gridLayout);

        //按钮下标对应的图片编号列表
        List<Integer> list = new ArrayList<>();
        //暂时设定每(ROW-2)个为一组图片
        for (int i = 0; i < ROW - 2; i++) {
            for (int j = 0; j < COL - 2; j++) {
                // int index = i*(ROW-2)+j;
                list.add(i + 1);
            }
        }
        //打乱顺序
        Collections.shuffle(list);
        int index = 0;

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                int x = row;
                int y = col;
                //外围设空
                if (row != 0 && row != ROW - 1 && col != 0 && col != COL - 1) {
                    ImageIcon imageIcon = new ImageIcon("pic/" + list.get(index) + ".jpg");
                    imageIcon.setImage(imageIcon.getImage().getScaledInstance(WIDTH/ROW, HEIGHT/COL, Image.SCALE_DEFAULT));
                    buttons[x][y] = new JButton(list.get(index) + "",imageIcon);
                    buttons[x][y].setActionCommand(x + "," + y);
                    buttons[x][y].setBackground(Color.lightGray);
                    // buttons[x][y].setVerticalTextPosition(SwingConstants.BOTTOM);
                    // buttons[x][y].setHorizontalTextPosition(SwingConstants.CENTER);
                    buttons[x][y].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton button = (JButton) e.getSource();
                            String str = button.getActionCommand();
                            String[] xy = str.split(",");
                            int x = Integer.parseInt(xy[0]);
                            int y = Integer.parseInt(xy[1]);
                            //清空上一次的连线
                            while (tmpPoint.point != null) {
                                panel.repaint();
                                tmpPoint = tmpPoint.point;
                            }
                            button.setBackground(Color.CYAN);
                            button.setEnabled(false);
                            Point currentPoint = new Point(x, y);
                            queue.offer(currentPoint);
                            if (queue.size() == 2) {
                                Point firstPoint = queue.poll();
                                int[] tx1 = {-1, 0, 1, 0};
                                int[] ty1 = {0, 1, 0, -1};
                                Point bfs = bfs(firstPoint, currentPoint, tx1, ty1);
                                System.out.println("bfs:" + bfs);
                                tmpPoint = bfs;
                                //第一次判断能否消除，若不能消除，则此时已经将第一次点击的按钮状态还原
                                canClean(bfs, firstPoint, currentPoint, true);
                                //若从第一个点到第二个点的宽度优先算法拐点超过2个，则将第一个点作为第二个点，第二个点作为第一个点再次判断。
                                if (bfs.x == 0 && bfs.y == 0) {
                                    bfs = bfs(currentPoint, firstPoint, tx1, ty1);
                                    tmpPoint = bfs;
                                    //第二次判断，此时不对第一次点击的按钮状态进行操作。
                                    canClean(bfs, currentPoint, firstPoint, false);
                                }
                                // int[] tx2 = {1, 0, -1, 0};
                                // int[] ty2 = {0, -1, 0, 1};
                                // //若从第一个点到第二个点的宽度优先算法拐点超过2个，则将第一个点作为第二个点，第二个点作为第一个点再次判断。
                                // if (bfs.x == 0 && bfs.y == 0) {
                                //     bfs = bfs(currentPoint, firstPoint, tx2, ty2);
                                //     tmpPoint = bfs;
                                //     //第3次判断，此时不对第一次点击的按钮状态进行操作。
                                //     canClean(bfs, currentPoint, firstPoint, false);
                                // }
                                // if (bfs.x == 0 && bfs.y == 0) {
                                //     bfs = bfs(firstPoint, currentPoint, tx2, ty2);
                                //     tmpPoint = bfs;
                                //     //第4次判断，此时不对第一次点击的按钮状态进行操作。
                                //     canClean(bfs, firstPoint, currentPoint, false);
                                // }
                            }
                        }
                    });
                    index++;
                } else {
                    buttons[x][y] = new JButton(" ");
                    buttons[x][y].setEnabled(false);
                    buttons[x][y].setVisible(false);
                }
                panel.add(buttons[x][y]);
            }
        }
        frame.setVisible(true);
    }

    /**
     * 宽度优先算法。
     * @param p1 第一次点击的点
     * @param p2 第二次点击的点
     * @param tx 上右下左的x运算位
     * @param ty 上右下左的y运算位
     * @return 两个点可达并且值相同，返回第二次点击的点，该点为一个包含上一个点的链表数据结构；若不可达，则返回0,0点。
     */
    private static Point bfs(Point p1, Point p2, int[] tx, int[] ty) {
        Queue<Point> q = new LinkedList<>();
        //每一个(tx,ty)和P1加运算代表一个点的上下右左相邻的点
        int[][] deep = new int[ROW][COL];
        q.offer(p1);

        //设置一个相同规格的二位数组，除了第一个选择的点为0，其他都为-1,防止多次加入队列
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                deep[i][j] = -1;
        deep[p1.x][p1.y] = 0;

        while (q.size() > 0) {
            Arrays.stream(q.toArray()).forEach(System.out::println);
            System.out.println("******************************");
            Point p = q.poll();
            //如果从队列取出的点为目标点，则退出while循环
            if (p.x == p2.x && p.y == p2.y)
                break;
            //循环判断当前点相邻的点是否符合条件
            // System.out.println("px:" + p.x + "," + "py:" + p.y);
            for (int i = 0; i < 4; i++) {
                int x = p.x + tx[i];
                int y = p.y + ty[i];
                // System.out.println("\tx:" + x + "," + "y:" + y);
                //不超出边界
                if (x >= 0 && x < ROW && y >= 0 && y < COL && (deep[x][y] == -1 || deep[x][y] == deep[p.x][p.y] + 1)) {
                    //选定的两个点值相同，并且当前点等于第二个点，返回经过的点数
                    if (buttons[p1.x][p1.y].getText().equals(buttons[p2.x][p2.y].getText()) && x == p2.x && y == p2.y) {
                        Point point = new Point(x, y, p);
                        for (int ii = 0; ii < ROW; ii++) {
                            for (int j = 0; j < COL; j++) {
                                System.out.print(deep[ii][j] + "\t");
                            }
                            System.out.println();
                        }
                        System.out.println("----------------------------------------");
                        // return deep[p2.x][p2.y];
                        Point pp1 = point;
                        //判断拐点次数是不是超过两次
                        if (pp1.x == pp1.point.x) {
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                        } else {
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                deep[x][y] = deep[p.x][p.y] + 1;
                                return point;
                            }
                        }
                    } else if (buttons[x][y].getText().equals(" ") || deep[x][y] == deep[p.x][p.y] + 1) {
                        // System.out.println("deep[" + x + "][" + y + "]:" + deep[x][y]);
                        Point point = new Point(x, y, p);
                        Point pp1 = point;
                        //判断拐点次数是不是超过两次
                        if (pp1.x == pp1.point.x) {
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                pp1 = pp1.point;
                            }
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                q.offer(point);
                                //若deep[x][y]的值已经是上一次循环点的值+1，则表示是一个访问过的点，直接下一个，不对deep[x][y]做修改
                                if (deep[x][y] == deep[p.x][p.y] + 1) {
                                    continue;
                                }
                                deep[x][y] = deep[p.x][p.y] + 1;
                            }
                        } else {
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            while (pp1.point != null && pp1.x == pp1.point.x) {
                                pp1 = pp1.point;
                            }
                            while (pp1.point != null && pp1.y == pp1.point.y) {
                                pp1 = pp1.point;
                            }
                            if (pp1.x == p1.x && pp1.y == p1.y) {
                                q.offer(point);
                                if (deep[x][y] == deep[p.x][p.y] + 1) {
                                    continue;
                                }
                                deep[x][y] = deep[p.x][p.y] + 1;
                            }
                        }
                    }
                }
            }
        }
        return new Point(0, 0);
    }

    private static int getPaintX(int x) {
        return x * eachBtnSize + eachBtnSize / 2 - 2 * x;
    }

    private static int getPaintY(int y) {
        return y * eachBtnSize + eachBtnSize / 2 - y - y;
    }

    /**
     * 根据bfs判断两个Point能否消除，能消除画出连线
     *
     * @param bfs          bfs方法的返回值，若bfs等于（第二次点击的）currentPoint则消除
     * @param firstPoint   第一次点击的点（按钮）
     * @param currentPoint 第二次点击的点（按钮）
     * @param flag         用于判断是否是第一次执行该函数，主要用于控制按钮被点击的状态。
     */
    private static void canClean(Point bfs, Point firstPoint, Point currentPoint, boolean flag) {
        if (bfs != null && bfs.x == currentPoint.x && bfs.y == currentPoint.y) {
            while (bfs.point != null) {
                g = panel.getGraphics();
                g.setColor(Color.BLACK);
                g.drawLine(getPaintX(bfs.y), getPaintY(bfs.x),
                        getPaintX(bfs.point.y), getPaintY(bfs.point.x));
                bfs = bfs.point;
            }
            buttons[firstPoint.x][firstPoint.y].setText(" ");
            buttons[firstPoint.x][firstPoint.y].setIcon(null);
            buttons[firstPoint.x][firstPoint.y].setEnabled(false);
            buttons[firstPoint.x][firstPoint.y].setBackground(null);
            // buttons[firstPoint.x][firstPoint.y].setVisible(false);
            buttons[currentPoint.x][currentPoint.y].setText(" ");
            buttons[currentPoint.x][currentPoint.y].setIcon(null);
            buttons[currentPoint.x][currentPoint.y].setEnabled(false);
            buttons[currentPoint.x][currentPoint.y].setBackground(null);
            // buttons[currentPoint.x][currentPoint.y].set(false);
            queue.clear();
        } else if (flag) {
            buttons[firstPoint.x][firstPoint.y].setBackground(Color.lightGray);
            buttons[firstPoint.x][firstPoint.y].setEnabled(true);
        }
    }
}
