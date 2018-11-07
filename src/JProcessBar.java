import javax.swing.*;
import java.awt.*;


public class JProcessBar extends JPanel {

    private static final long serialVersionUID = 1L;
    static private JProgressBar processBar;
    static Thread thread;
    static int num = 0;

    public JProcessBar() {
        // System.out.println("1:" + Thread.activeCount());
        processBar = new JProgressBar(0, 2000);// 创建进度条
        processBar.setPreferredSize(new Dimension(500, 20));
        processBar.setStringPainted(false);// 设置进度条上的字符串显示，false则不能显示
        processBar.setBackground(Color.white);
        this.add(processBar);// 向面板添加进度控件

        // 创建线程显示进度
        thread = new Thread() {
            public void run() {
                //判断是否是最新线程;
                int a = ++num;
                for (int i = processBar.getMaximum(); i >= 0; i--) {
                    if (a == num) {
                        processBar.setValue(i);
                    }
                    try {
                        Thread.sleep(1000);  //   让当前线程休眠1s
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    // 设置进度条数值
                }
                if (a == num) {
                    JOptionPane.showConfirmDialog(Map4.frame, "游戏结束", "提示", JOptionPane.DEFAULT_OPTION);
                    Map4.init();
                }
            }
        };
    }

    public static void main(String[] args) {
        JProcessBar JPBD = new JProcessBar();
        JPBD.setVisible(true);
    }
}