import java.util.concurrent.*;

void main() {
    System.out.println("tu kod");

    //ForkJoinPool pool = ForkJoinPool.commonPool();

    int proc = Runtime.getRuntime().availableProcessors();
    System.out.println("Number of available core in the processor is: " + proc);
}
