import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;

public class Main {
    private static int limit;
    private static int num_threads;

    public static void main(String[] args) {
        
        Shared_Resources sr = new Shared_Resources();

        // User Input
        // Scanner sc = new Scanner(System.in);
        // System.out.print("Enter Upper Bound: ");
        // limit = Integer.parseInt(sc.nextLine());

        // System.out.print("Enter Number of Threads: ");
        // num_threads = Integer.parseInt(sc.nextLine());
        // sc.close();

        limit = (int) 1e7;

        ArrayList<Thread> threads = new ArrayList<>();

        // Thread Counts
        for (int num_threads = 1; num_threads <= 1024; num_threads *= 2){
            float ave = 0;

            // Run 3 times for cache
            for (int i = 0; i < 3; i++){
                run(num_threads, sr, threads);
                threads.clear();
                sr.clear_primes();
            }

            // Run 5 times for average
            for (int i = 0; i < 5; i++){
                ave += run(num_threads, sr, threads);
                System.err.println(ave);

                threads.clear();
                sr.clear_primes();
            }

            ave = ave / 5;
            System.err.printf("\nAverage time for %d Threads: %f \n", num_threads, ave);

            System.gc();
        }

        // System.out.printf("\n%d primes were found.\n", rs.primes.size());    
        // System.out.printf("Execution Time: %d ms\n", duration);  
    }    

    public static long run(int num_threads, Shared_Resources rs, ArrayList<Thread> threads){
        long start_time = System.nanoTime();
        int x = (int) Math.ceilDiv(limit, num_threads);

        // Create threads
        for(int current_num = 2; current_num <= limit;) {
            int temp = ((current_num + x) > limit) ? limit : current_num + x;
            int copy = current_num;

            // Divide limit by the number of threads using ranges
            threads.add(new Thread(() -> rs.check_prime_range(copy, temp)));

            current_num = current_num + x;
        }

        // Run threads
        for (Thread th : threads){
            th.start();
        }

        // Wait for each thread to finish
        for (Thread th: threads){
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // End Time
        long duration = (System.nanoTime() - start_time)/1000000;

        return duration;
    }

    /*
    This function checks if an integer n is prime.

    Parameters:
    n : int - integer to check

    Returns true if n is prime, and false otherwise.
    */
    public static boolean check_prime(int n) {
        for(int i = 2; i * i <= n; i++) {
            if(n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Shared Resources between threads
    static class Shared_Resources {
        private List<Integer> primes = new ArrayList<Integer>();

        public void check_prime_range(int x, int y){
            for (int i = x; i <= y; i++){
                if (check_prime(i)){
                    append_prime(i);
                };
            }
        }

        // Mutual Exclusion of list of Primes
        public synchronized void append_prime(int num){
            primes.add(Integer.valueOf(num));
        }

        // Clear list of primes
        public void clear_primes(){
            primes.clear();
        }
    }
}