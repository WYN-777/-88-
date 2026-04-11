import org.json.JSONObject;

public class TestJSON {
    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.put("test", "success");
        System.out.println("JSON库测试成功：" + obj.toString());
    }
}