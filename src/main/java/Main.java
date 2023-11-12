import service.httpClient.KVTaskClient;
import service.server.HttpTaskServer;
import service.server.KVServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {
        checkTaskServer();
    }

    private void checkKv() throws IOException {
        new KVServer().start();
        KVTaskClient client = new KVTaskClient("http://localhost:8078");
        String key = "1";
        client.put(key, "str");
        String res = client.load("2");
        System.out.println(res);
    }

    private static void checkTaskServer() throws IOException {
       Path path = Paths.get("src", "main", "resources", "tasks.csv");
        new HttpTaskServer().start(path.toString());
    }
}