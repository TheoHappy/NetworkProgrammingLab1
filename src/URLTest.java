import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLTest {

    private static void sendGet() throws Exception {

        String url = "http://me.utm.md/img/logo.png";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        InputStream in = con.getInputStream();
        OutputStream out = new FileOutputStream("abc.jpg");
        try {
            byte[] bytes = new byte[2048];
            int length;

            while ((length = in.read(bytes)) != -1) {
                out.write(bytes, 0, length);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void main(String[] args) throws Exception {
        sendGet();
    }
}
