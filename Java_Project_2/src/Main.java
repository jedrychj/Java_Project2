import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


void main() {
    System.out.println("tu kod");
    //int proc = Runtime.getRuntime().availableProcessors();
    //System.out.println("Number of available core in the processor is: " + proc);

    int ScreenSizeX = 500;
    int ScreenSizeY = 500;

    // Najpierw generujemy przeszkody
    ArrayList<Points> obstacleList = generateRandomObstacles(10, ScreenSizeX, ScreenSizeY);

    // Następnie generujemy punkt, przekazując listę przeszkód do sprawdzenia kolizji
    Point randomPoint = generateRandomPoint(ScreenSizeX, ScreenSizeY, obstacleList);

    System.out.println("Zapisany punkt: X=" + randomPoint.x + ", Y=" + randomPoint.y);

    //ForkJoinPool pool = ForkJoinPool.commonPool(); -> lineList

    ArrayList<Points> lineList = new ArrayList<>(); // do tej listy będą dodawane linie zwrócone przy kalkulacjach
    lineList.add(new Points(10, 100, 10, 100)); // to są przykłady do testowania
    lineList.add(new Points(200, 30, 50, 120));

    LineDrawing d = new LineDrawing(lineList);
    Screen S = new Screen(ScreenSizeX,ScreenSizeY, d);
    S.paintObstacles(obstacleList);
}



// --- METODY DO GENEROWANIA DANYCH ---

ArrayList<Points> generateRandomObstacles(int amount, int maxX, int maxY) {
    ArrayList<Points> obstacles = new ArrayList<>();
    Random rand = new Random();

    for (int i = 0; i < amount; i++) {
        // Losujemy jeden rozmiar dla szerokości i wysokości (od 10 do 50), aby uzyskać kwadrat
        int size = rand.nextInt(41) + 10;

        // Zabezpieczamy przed wyjściem kwadratu poza ekran
        int x0 = rand.nextInt(maxX - size);
        int y0 = rand.nextInt(maxY - size);

        // Zarówno X jak i Y powiększamy o ten sam 'size'
        obstacles.add(new Points(x0, x0 + size, y0, y0 + size));
    }
    return obstacles;
}

Point generateRandomPoint(int maxX, int maxY, ArrayList<Points> obstacles) {
    Random rand = new Random();
    Point p = new Point();
    boolean isInsideObstacle;

    // Pętla wykonuje się tak długo, aż wylosowany punkt znajdzie się na pustym polu
    do {
        p.x = rand.nextInt(maxX);
        p.y = rand.nextInt(maxY);
        isInsideObstacle = false;

        // Sprawdzamy, czy wylosowane x i y znajdują się wewnątrz którejkolwiek przeszkody
        for (Points obs : obstacles) {
            // Zakładamy, że x0 to lewa krawędź, x1 to prawa, y0 to górna, a y1 to dolna
            if (p.x >= obs.x0 && p.x <= obs.x1 && p.y >= obs.y0 && p.y <= obs.y1) {
                isInsideObstacle = true;
                break; // Punkt uderzył w przeszkodę, przerywamy pętlę for i losujemy ponownie
            }
        }
    } while (isInsideObstacle); // Powtarzaj, jeśli punkt jest w przeszkodzie

    return p;
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