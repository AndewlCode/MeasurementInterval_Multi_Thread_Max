package src;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] texts = new String[25];

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        // Заведите пул потоков.
        // Вместо списка из потоков сделайте список из Future.
        List<Future> threads = new ArrayList<>();

        long startTsMulti = System.currentTimeMillis(); // start time

        // В цикле отправьте в пул потоков задачи на исполнение,
        // получив в ответ на каждую отправку Future, которые войдут в список.
        for (String text : texts) {
            Callable task = () -> {
                {
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
                }
            };
            FutureTask<Integer> future = new FutureTask<>(task);
            threads.add(future);
            new Thread(future).start();
        }


        // После цикла с отправкой задач на исполнение пройдитесь циклом по Future
        // и у каждого вызовите get для ожидания и получения результата,
        // который вы обработаете для получения ответа на задачу.
        int maxValue = 0;
        for (Future thread : threads) {
            int threadValue = (int) thread.get();
            if (threadValue > maxValue) {
                maxValue = threadValue;
            }
            System.out.println("max Value: " + maxValue + " threadValue: " + threadValue);
        }

        long endTsMulti = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTsMulti - startTsMulti) + "ms");
        System.out.println("Max Value = " + maxValue);

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