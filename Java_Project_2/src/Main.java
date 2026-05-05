import javax.swing.*;
import java.awt.*;

void main() {
    System.out.println("tu kod");

    //ForkJoinPool pool = ForkJoinPool.commonPool();

    //int proc = Runtime.getRuntime().availableProcessors();
    //System.out.println("Number of available core in the processor is: " + proc);

    ArrayList<Points> obstacleList = new ArrayList<>();
    obstacleList.add(new Points(200, 250, 300, 400));
    obstacleList.add(new Points(100, 150, 200, 300));

    ArrayList<Points> lineList = new ArrayList<>(); // do tej listy będą dodawane linie zwrócone przy kalkulacjach
    lineList.add(new Points(10, 100, 10, 100)); // to są przykłady do testowania
    lineList.add(new Points(200, 30, 50, 120));

    LineDrawing d = new LineDrawing(lineList);
    Screen S = new Screen(500,500, d);
    S.paintObstacles(obstacleList);
}

class Screen extends JFrame {
    int sizeX;
    int sizeY;

    public Screen(int x, int y, LineDrawing drawing) {
        this.sizeX = x;
        this.sizeY = y;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawing.setPreferredSize(new Dimension(x, y));
        this.getContentPane().add(drawing, BorderLayout.CENTER);
        this.pack();
        this.setLayout(null);
        this.setVisible(true);
    }

    public void paintObstacles(ArrayList<Points> obs){
        for (Points o : obs){
            JPanel k = new JPanel();
            k.setBackground(Color.BLACK);
            k.setBounds(o.x0, o.y0, Math.abs(o.x1-o.x0),Math.abs(o.y1-o.y0));
            this.getContentPane().add(k);
        }
    }
}

class Points {
    int x0;
    int x1;
    int y0;
    int y1;

    public Points(int x0, int x1, int y0, int y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }
}

class LineDrawing extends JComponent{
    ArrayList<Points> list;

    public LineDrawing(ArrayList<Points> l){
        this.list = l;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Points l : list) {
            g.setColor(Color.RED);
            g.drawLine(l.x0, l.y0, l.x1, l.y1);
        }
    }
}