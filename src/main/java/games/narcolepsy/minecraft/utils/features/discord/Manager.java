package games.narcolepsy.minecraft.utils.features.discord;

import com.google.gson.Gson;
import games.narcolepsy.minecraft.utils.features.discord.messages.Embedder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager implements Runnable {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final BlockingQueue<Embedder> eventQueue = new LinkedBlockingQueue<>();
    private final String webhookURL;
    private final OkHttpClient client = new OkHttpClient();
    private final Logger logger;
    private final Gson gson = new Gson();

    public Manager(Logger logger, String webhookURL) {
        this.logger = logger;
        this.webhookURL = webhookURL;
    }

    public void queue(Embedder e) {
        if (!eventQueue.offer(e)) {
            this.logger.warning("Failed to queue event to Discord.");
        }
    }

    @Override
    public void run() {
        var run = true;

        while (run) {
            try {
                var e = eventQueue.take();
                var data = gson.toJson(e.embed()).getBytes();

                submitToDiscord(data);
            } catch (InterruptedException e) {
                this.logger.info("Interrupted waiting for event for Discord.");
                run = false;
            }
        }
    }

    private void submitToDiscord(byte[] data) {
        RequestBody body = RequestBody.create(data, JSON);
        Request request = new Request.Builder()
                .url(this.webhookURL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                this.logger.log(Level.WARNING, "Failed to post to Discord - Code: {0}", response.code());
            }
        } catch (IOException e) {
            this.logger.log(Level.WARNING, "Failed to post to Discord", e);
        }
    }
}