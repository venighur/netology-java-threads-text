import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        ExecutorService threadsPool = Executors.newFixedThreadPool(25);
        List<Future<Integer>> tasks = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            Callable<Integer> findSize = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return maxSize;
            };

            Future<Integer> task = threadsPool.submit(findSize);
            tasks.add(task);
        }

        int max = 0;
        for (Future<Integer> task : tasks) {
            int result = task.get();
            System.out.println(result);
            if (result > max) {
                max = result;
            }
        }

        System.out.println("Максимальный интервал значений - " + max);

        threadsPool.shutdown();

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
