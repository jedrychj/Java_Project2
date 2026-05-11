import javax.swing.*;
import java.awt.*;
import java.util.List;

void main() {
    int screenSizeX = 500;
    int screenSizeY = 500;

    // utworzenie losowych przeszkód
    ArrayList<Points> obstacleList = Points.randomObstacles(10, screenSizeX, screenSizeY);

    // dobranie losowo punktu startowego
    Point startPoint = Beam.generateRandomPoint(screenSizeX, screenSizeY, obstacleList);

    // utworzenie puli wątków
    ForkJoinPool pool = ForkJoinPool.commonPool();
    // deklaracja zadania
    Task t = new Task(1000, 200, startPoint, obstacleList, screenSizeX, screenSizeY);
    // przydzielenie zadania do puli wątków
    ArrayList<Points> lineList = pool.invoke(t);

    // wyrysowanie linii i wyświetlenie okna
//    LineDrawing d = new LineDrawing(lineList);
//    Screen s = new Screen(screenSizeX,screenSizeY, d);
//    s.paintObstacles(obstacleList);
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

// reprezentacja linii oraz przeszkód
static class Points {
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

    public static ArrayList<Points> randomObstacles(int amount, int maxX, int maxY) {
        ArrayList<Points> obstacles = new ArrayList<>();
        Random rand = new Random();
        int margin = 10;
        int minSize = 10;
        int maxSize = 200;

        for (int i = 0; i < amount; i++) {
            int x1;
            int y1;
            int x2;
            int y2;

            do{
                x1 = rand.nextInt(maxX - 2*margin - minSize) + margin;
                y1 = rand.nextInt(maxY - 2*margin - minSize) + margin;

                x2 = rand.nextInt(maxSize - minSize) + minSize;
                y2 = rand.nextInt(maxSize - minSize) + minSize;
            }while(x2*y2>90*90 || x1+x2>maxX-margin || y1+y2>maxY-margin);

            obstacles.add(new Points(x1, y1, x1+x2, y1+y2));
        }
        return obstacles;
    }
}

// klasa potrzebna do rysowania linii w oknie
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

// klasa służąca do wyznaczania drogi przebytej przez wiązki
class Beam {
    double startX;
    double startY;
    double velX;
    double velY;

    int maxBounces;

    public Beam (double x, double y, double angle, int n){
        startX = x;
        startY = y;

        velX = Math.sin(angle);
        velY = -Math.cos(angle);

        maxBounces = n;
    }

    // wyznaczenie drogi wiązki
    public ArrayList<Points> calcPoints(ArrayList<Points> obstacles, int screenX, int screenY) {
        double currentX = startX;
        double currentY = startY;

        double lastBounceX = startX;
        double lastBounceY = startY;

        int bounceCount = 0;

        ArrayList<Points> resultLines = new ArrayList<>();

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
        return resultLines;
    }

    public static Point generateRandomPoint(int maxX, int maxY, ArrayList<Points> obstacles) {
        Random rand = new Random();
        Point p = new Point();
        boolean isInsideObstacle;
        do {
            p.x = rand.nextInt(maxX);
            p.y = rand.nextInt(maxY);
            isInsideObstacle = false;

            for (Points obs : obstacles) {
                // Zakładamy, że x0 to lewa krawędź, x1 to prawa, y0 to górna, a y1 to dolna
                if (p.x >= obs.x1 && p.x <= obs.x2 && p.y >= obs.y1 && p.y <= obs.y2) {
                    isInsideObstacle = true;
                    break;
                }
            }
        } while (isInsideObstacle); // Powtarzaj, jeśli punkt jest w przeszkodzie

        return p;
    }
}

// pomocnicze zmienne do wyświetlania
int j=0;
int k=0;

// klasa zadania
class Task extends RecursiveTask<ArrayList<Points>> {

    int threshold = 20;
    ArrayList<Beam> B;

    ArrayList<Points> obstacles;
    int maxX;
    int maxY;

    // konstruktor użyty przy początkowym utworzeniu zadania
    public Task(int amount, int nBounce, Point p, ArrayList<Points> obstacles, int screenX, int screenY) {
        double step = 2*Math.PI / amount;
        double shift = step*(new Random().nextInt(10)+1)/10; // żeby nie było pionowych/poziomych linii bo się kiepsko odbijają

        this.B = new ArrayList<>();

        for (int i=0; i<amount; i++){
            this.B.add(new Beam(p.x, p.y, shift + step*i, nBounce));
        }


        this.obstacles = obstacles;
        maxX = screenX;
        maxY = screenY;
    }

    // konstruktor używany przy podziale zadania
    public Task(ArrayList<Beam> B, ArrayList<Points> obstacles, int screenX, int screenY) {
        this.B = B;

        this.obstacles = obstacles;
        maxX = screenX;
        maxY = screenY;
    }

    @Override
    protected ArrayList<Points> compute() {
        if (B.size() > threshold) {
            Collection<Task> subtasks = divide();
            ForkJoinTask.invokeAll(subtasks);
            ArrayList<Points> result = new ArrayList<>();

            for (Task t : subtasks) {
                result.addAll(t.join());
            }

            return result;
        } else {
            return conquer();
        }
    }

    // podział zadania na mniejsze
    private Collection<Task> divide(){
        System.out.println("dziele" + j++);
        List<Task> dividedTasks = new ArrayList<>();
        dividedTasks.add(new Task(Task.split(B, true), this.obstacles, this.maxX, this.maxY));
        dividedTasks.add(new Task(Task.split(B, false), this.obstacles, this.maxX, this.maxY));
        return dividedTasks;
    }

    // wykonanie zadania
    private ArrayList<Points> conquer(){
        System.out.println("robie" + k++);
        ArrayList<Points> result = new ArrayList<>();
        for (Beam b : this.B)
        {
            result.addAll(b.calcPoints(this.obstacles, this.maxX, this.maxY));
        }
        return result;
    }

    // funkcja pomocnicza przy podziale zadań
    static private ArrayList<Beam> split(ArrayList<Beam> A, boolean half){
        ArrayList<Beam> B = new ArrayList<>();
        if(half)
            for(int i=0; i<A.size()/2; i++)
                B.add(A.get(i));
        else
            for(int i=A.size()/2; i<A.size(); i++)
                B.add(A.get(i));
        return B;
    }
}