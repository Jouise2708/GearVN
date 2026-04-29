package gearvn.ui;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    // ===== POST =====
    public static String post(String endpoint, String json) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // gửi dữ liệu
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // đọc response (kể cả lỗi)
            int status = conn.getResponseCode();

            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String response = readStream(is);

            // 🔥 debug cực quan trọng
            System.out.println("POST " + endpoint);
            System.out.println("STATUS: " + status);
            System.out.println("RESPONSE: " + response);

            return response;

        } catch (Exception e) {
            System.out.println("ERROR CONNECT BACKEND");
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ===== GET =====
    public static String get(String endpoint) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();

            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String response = readStream(is);

            System.out.println("GET " + endpoint);
            System.out.println("STATUS: " + status);
            System.out.println("RESPONSE: " + response);

            return response;

        } catch (Exception e) {
            System.out.println("ERROR CONNECT BACKEND");
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ===== HELPER =====
    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder res = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            res.append(line);
        }

        return res.toString();
    }
}