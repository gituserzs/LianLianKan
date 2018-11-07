import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Map4 {
    static final int WIDTH = 600;
    static final int HEIGHT =600;
    static final int ROW = 12;
    static final int COL = 12;
    //用于存放被点击按钮下标的队列
    static Queue<Point> queue = new ArrayDeque<>();
    //创建按钮
    static JButton[][] buttons = new JButton[ROW][COL];
    static int eachBtnSizeY = HEIGHT / ROW;
    static int eachBtnSizeX = WIDTH / COL;
    static Point tmpPoint = new Point(0, 0);
    static JFrame frame = new JFrame("连连看");
    static JPanel leftPanel = new JPanel();
    static JPanel rightPanel = new JPanel();
    //创建网格布局
    static GridLayout gridLayout = new GridLayout(ROW, COL);
    //按钮下标对应的图片编号列表
    static List<Integer> list = new ArrayList<>();
    // 创建分隔面板
    static JSplitPane splitPane = new JSplitPane();
    static Graphics g;
    static JButton startBtn;
    static JButton restartBtn;
    static JButton nextBtn;

    public static void main(String[] args) {
        init();
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
            Point p = q.poll();
            //循环判断当前点相邻的点是否符合条件
            for (int i = 0; i < 4; i++) {
                int x = p.x + tx[i];
                int y = p.y + ty[i];
                //不超出边界
                //宽度优先不允许在队列中添加已经添加过的点，这里可以。
                if (x >= 0 && x < ROW && y >= 0 && y < COL && (deep[x][y] == -1 || deep[x][y] == deep[p.x][p.y] + 1)) {
                    //选定的两个点值相同，并且当前点等于第二个点，返回经过的点数
                    if (buttons[x][y].getText().equals(" ") || deep[x][y] == deep[p.x][p.y] + 1
                            || (x == p2.x && y == p2.y)) {
                        Point point = new Point(x, y, p);
                        //判断拐点次数是不是超过两次
                        Point pp1 = point;
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
                            } else {
                                continue;
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
                            } else {
                                continue;
                            }
                        }
                        if (buttons[p1.x][p1.y].getText().equals(buttons[p2.x][p2.y].getText()) && x == p2.x && y == p2.y) {
                            return point;
                        }
                    }
                }
            }
        }
        return new Point(0, 0);
    }

    private static int getPaintX(int x) {
        return x * eachBtnSizeX + eachBtnSizeX / 2;
    }

    private static int getPaintY(int y) {
        return y * eachBtnSizeY + eachBtnSizeY / 2;
    }

    /**
     * 根据bfs判断两个Point能否消除，能消除画出连线
     * @param bfs          bfs方法的返回值，若bfs等于（第二次点击的）currentPoint则消除
     * @param firstPoint   第一次点击的点（按钮）
     * @param currentPoint 第二次点击的点（按钮）
     * @param flag         用于判断是否是第一次执行该函数，主要用于控制按钮被点击的状态。
     */
    private static void canClean(Point bfs, Point firstPoint, Point currentPoint, JPanel leftPanelBottom, boolean flag) {
        if (bfs != null && bfs.x == currentPoint.x && bfs.y == currentPoint.y) {
            while (bfs.point != null) {
                g = leftPanelBottom.getGraphics();
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

    //初始化存储图片编号的列表
    public static void init() {
        frame.setSize(WIDTH + 200, HEIGHT + 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //清空所有内容
        leftPanel.removeAll();
        leftPanel.setLayout(new FlowLayout());
        leftPanel.setSize(WIDTH, HEIGHT + 100);
        //设置左上部分的时间条
        JPanel leftPanelTop = new JProcessBar();
        leftPanelTop.setBackground(Color.white);
        leftPanelTop.setPreferredSize(new Dimension(WIDTH, 25));
        //设置左下部分的主体
        JPanel leftPanelBottom = new JPanel();
        leftPanelBottom.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        leftPanelBottom.setBackground(Color.white);
        leftPanelBottom.setLayout(gridLayout);
        leftPanel.add(leftPanelTop);
        leftPanel.add(leftPanelBottom);
        splitPane.setDividerLocation(WIDTH);
        //设置左半边
        splitPane.setLeftComponent(leftPanel);
        //设置右半边
        splitPane.setRightComponent(rightPanel);
        frame.setContentPane(splitPane);
        list.clear();
        //暂时设定每(ROW-2)个为一组图片
        for (int i = 0; i < ROW - 2; i++) {
            for (int j = 0; j < COL - 2; j++) {
                list.add(i + 1);
            }
        }
        //打乱顺序
        Collections.shuffle(list);
        initRightPanel();
        frame.setVisible(true);
    }

    //初始化左半边
    private static void initLeftJPanel() {
        startBtn.setEnabled(false);
        restartBtn.setEnabled(true);
        nextBtn.setEnabled(true);
        frame.setSize(WIDTH + 200, HEIGHT + 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //清空所有内容
        leftPanel.removeAll();
        leftPanel.setLayout(new FlowLayout());
        leftPanel.setSize(WIDTH, HEIGHT + 100);
        //设置左上部分的时间条
        JPanel leftPanelTop = new JProcessBar();
        leftPanelTop.setBackground(Color.white);
        leftPanelTop.setPreferredSize(new Dimension(WIDTH, 25));
        //设置左下部分的主体
        JPanel leftPanelBottom = new JPanel();
        leftPanelBottom.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        leftPanelBottom.setBackground(Color.white);
        leftPanelBottom.setLayout(gridLayout);
        leftPanel.add(leftPanelTop);
        leftPanel.add(leftPanelBottom);
        splitPane.setDividerLocation(WIDTH);
        //设置左半边
        splitPane.setLeftComponent(leftPanel);
        //设置右半边
        splitPane.setRightComponent(rightPanel);
        frame.setContentPane(splitPane);
        int index = 0;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                int x = row;
                int y = col;
                //外围设空
                if (row != 0 && row != ROW - 1 && col != 0 && col != COL - 1) {
                    ImageIcon imageIcon = new ImageIcon("pic/" + list.get(index) + ".jpg");
                    imageIcon.setImage(imageIcon.getImage().getScaledInstance(eachBtnSizeX+2*COL-1 , eachBtnSizeY , Image.SCALE_DEFAULT));
                    buttons[x][y] = new JButton(list.get(index) + "", imageIcon);
                    buttons[x][y].setActionCommand(x + "," + y);
                    buttons[x][y].setBackground(Color.lightGray);
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
                                leftPanelBottom.repaint();
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
                                tmpPoint = bfs;
                                //第一次判断能否消除，若不能消除，则此时已经将第一次点击的按钮状态还原
                                canClean(bfs, firstPoint, currentPoint, leftPanelBottom, true);
                                //若从第一个点到第二个点的宽度优先算法拐点超过2个，则将第一个点作为第二个点，第二个点作为第一个点再次判断。
                                if (bfs.x == 0 && bfs.y == 0) {
                                    bfs = bfs(currentPoint, firstPoint, tx1, ty1);
                                    tmpPoint = bfs;
                                    //第二次判断，此时不对第一次点击的按钮状态进行操作。
                                    canClean(bfs, currentPoint, firstPoint, leftPanelBottom, false);
                                }
                            }
                            boolean flag = true;
                            for (int row = 0; row < ROW; row++) {
                                for (int col = 0; col < COL; col++) {
                                    if (!buttons[row][col].getText().equals(" ")) {
                                        flag = false;
                                    }
                                }
                            }
                            if (flag) {
                                init();
                                initLeftJPanel();
                                // JProcessBar.ii = 20;
                                JProcessBar.thread.start();
                            }
                        }
                    });
                    index++;
                } else {
                    buttons[x][y] = new JButton(" ");
                    buttons[x][y].setEnabled(false);
                    buttons[x][y].setVisible(false);
                }
                leftPanelBottom.add(buttons[x][y]);
            }
        }
    }

    //初始化右半边
    private static JPanel initRightPanel() {
        rightPanel.removeAll();
        BoxLayout boxLayout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(boxLayout);
        Box box = Box.createHorizontalBox();
        startBtn = new JButton("现在开始");
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initLeftJPanel();
                JProcessBar.thread.start();
            }
        });
        rightPanel.add(Box.createVerticalStrut(HEIGHT / 8));
        box.add(startBtn);
        rightPanel.add(box);

        box = Box.createHorizontalBox();
        restartBtn = new JButton("重新开始");
        restartBtn.setEnabled(false);
        restartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initLeftJPanel();
                JProcessBar.thread.start();
            }
        });
        rightPanel.add(Box.createVerticalStrut(HEIGHT / 8));
        box.add(restartBtn);
        rightPanel.add(box);

        box = Box.createHorizontalBox();
        nextBtn = new JButton("下一局");
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init();
                initLeftJPanel();
                JProcessBar.thread.start();
            }
        });
        rightPanel.add(Box.createVerticalStrut(HEIGHT / 8));
        box.add(nextBtn);
        rightPanel.add(box);

        box = Box.createHorizontalBox();
        JButton button = new JButton("退出");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        rightPanel.add(Box.createVerticalStrut(HEIGHT / 8));
        box.add(button);
        rightPanel.add(box);

        return rightPanel;
    }
}
