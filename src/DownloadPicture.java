import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DownloadPicture implements Runnable{

    final static String HOST_NAME = "me.utm.md";
    final static int PORT = 80;

    @Override
    public void run() {

    }
    private static void getImg(String imgName) throws Exception {
        Socket socket = new Socket(HOST_NAME, PORT);
        DataOutputStream bw = new DataOutputStream(socket.getOutputStream());
        bw.writeBytes("GET /" + imgName + " HTTP/1.1\r\n");
        bw.writeBytes("Host: " + HOST_NAME + ":80\r\n\r\n");
        bw.flush();

        String[] tokens = imgName.split("/");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        OutputStream dos = new FileOutputStream("images/" + tokens[tokens.length - 1]);

        int count, offset;
        byte[] buffer = new byte[2048];
        boolean eohFound = false;
        while ((count = in.read(buffer)) != -1) {
            offset = 0;
            if (!eohFound) {
                String string = new String(buffer, 0, count);
                int indexOfEOH = string.indexOf("\r\n\r\n");
                if (indexOfEOH != -1) {
                    count = count - indexOfEOH - 4;
                    offset = indexOfEOH + 4;
                    eohFound = true;
                } else {
                    count = 0;
                }
            }
            dos.write(buffer, offset, count);
            dos.flush();
        }
        in.close();
        dos.close();
        System.out.println("image transfer done");

        socket.close();
    }
}
