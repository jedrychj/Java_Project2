import javax.swing.*;
import java.awt.*;

void main() {
    System.out.println("tu kod");
    //int proc = Runtime.getRuntime().availableProcessors();
    //System.out.println("Number of available core in the processor is: " + proc);

    int ScreenSizeX = 500;
    int ScreenSizeY = 500;

    ArrayList<Points> obstacleList = new ArrayList<>(); // spawnowanie losowe?
    obstacleList.add(new Points(200, 250, 300, 400));
    obstacleList.add(new Points(100, 150, 200, 300));

    //ForkJoinPool pool = ForkJoinPool.commonPool(); -> lineList

    ArrayList<Points> lineList = new ArrayList<>(); // do tej listy będą dodawane linie zwrócone przy kalkulacjach
    lineList.add(new Points(10, 100, 10, 100)); // to są przykłady do testowania
    lineList.add(new Points(200, 30, 50, 120));

    Beam beam = new Beam();
    Beam Top = new Beam();
    beam.calc_points(30, 10, 3, 4, obstacleList, lineList, ScreenSizeX, ScreenSizeY, 3);
    Top.calc_points(30, 430, 2, -2.5, obstacleList, lineList, ScreenSizeX, ScreenSizeY, 2);


    LineDrawing d = new LineDrawing(lineList);
    Screen S = new Screen(ScreenSizeX,ScreenSizeY, d);
    S.paintObstacles(obstacleList);
}

class Screen extends JFrame {
    public Screen(int x, int y, LineDrawing drawing) {
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
            k.setBounds(o.x1, o.y1, Math.abs(o.x2-o.x1),Math.abs(o.y2-o.y1));
            this.getContentPane().add(k);
        }
    }
}

class Points {
    int x1;
    int y1;
    int x2;
    int y2;

    public Points(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
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
            g.drawLine(l.x1, l.y1, l.x2, l.y2);
        }
    }
}

class Beam {
    public void calc_points(double startX, double startY, double velX, double velY,
                                  ArrayList<Points> obstacles, ArrayList<Points> resultLines,
                                  int screenX, int screenY, int maxBounces) {
// dałem double a nie int bo mogły być błędy przy małych prędkościach


        double currentX = startX;
        double currentY = startY;

        // Punkt z którego wiązka wyleciała po ostatnim odbiciu
        double lastBounceX = startX;
        double lastBounceY = startY;

        int bounceCount = 0;


        while (bounceCount <= maxBounces) {
            double prevX = currentX;
            double prevY = currentY;

            currentX += velX;
            currentY += velY;

            boolean bounced = false;

            // Sprawdzanie granic ekranu
            if (currentX <= 0 || currentX >= screenX) {
                velX = -velX;
                currentX = (currentX <= 0) ? 0 : screenX;
                bounced = true;
            }
            if (currentY <= 0 || currentY >= screenY) {
                velY = -velY;
                currentY = (currentY <= 0) ? 0 : screenY;
                bounced = true;
            }

            // Sprawdzanie odbic od przeszkod
            if (!bounced) {
                for (Points obs : obstacles) {
                    int left = Math.min(obs.x1, obs.x2);
                    int right = Math.max(obs.x1, obs.x2);
                    int top = Math.min(obs.y1, obs.y2);
                    int bottom = Math.max(obs.y1, obs.y2);

                    if (currentX >= left && currentX <= right && currentY >= top && currentY <= bottom) {

                        // Sprawdzenie strony, z której wlecial
                        if (prevX < left || prevX > right) {
                            velX = -velX;
                            currentX = (prevX < left) ? left : right;
                        }
                        else {
                            velY = -velY;
                            currentY = (prevY < top) ? top : bottom;
                        }

                        bounced = true;
                        break;
                    }
                }
            }

            // Zapisywanie odbicia do listy
            if (bounced) {
                resultLines.add(new Points((int)lastBounceX, (int)lastBounceY, (int)currentX, (int)currentY));
                lastBounceX = currentX;
                lastBounceY = currentY;
                bounceCount++;
            }
        }
    }















}