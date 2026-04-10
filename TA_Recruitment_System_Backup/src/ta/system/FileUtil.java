package ta.system;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    private static final String DATA_DIR = "data/";

    public static JSONArray readJSONArray(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_DIR + filename)));
            return new JSONArray(content);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static JSONObject readJSONObject(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_DIR + filename)));
            return new JSONObject(content);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static void writeJSONArray(String filename, JSONArray array) {
        try (FileWriter file = new FileWriter(DATA_DIR + filename)) {
            file.write(array.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeJSONObject(String filename, JSONObject obj) {
        try (FileWriter file = new FileWriter(DATA_DIR + filename)) {
            file.write(obj.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}