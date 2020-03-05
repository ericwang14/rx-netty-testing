import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ThreadPoolConcurrentRequest {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        List<CompletableFuture<String>> futures = new ArrayList<>();
        IntStream.range(0, 10)
                .forEach(i -> futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        long a = System.currentTimeMillis();
                        String result = Request.Get("http://services.shop.com:8085/Site/260")
                                .execute()
                                .returnContent()
                                .asString();

                        System.out.println("current running thread: " + Thread.currentThread().getName() + " spend : " + (System.currentTimeMillis() - a));
                        System.out.println(result);
                        return result;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }, THREAD_POOL)));


        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("done " + (System.currentTimeMillis() - start) + "ms");
    }
}
