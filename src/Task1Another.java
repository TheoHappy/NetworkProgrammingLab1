import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task1Another {
    final static String HOST_NAME = "me.utm.md";
    final static int PORT = 80;

    public static void main(String[] args) throws Exception {

        String serverResponse = getResponseFromServer(HOST_NAME, PORT, "/");
        List<String> listOfImg = getPics(serverResponse);
        listOfImg.remove(listOfImg.size() - 1);
        listOfImg.remove(listOfImg.size() - 1);
        System.out.println(listOfImg);

        String responseFromSecurisedServer = getResponseFromSecurisedServer("www.facebook.com", 443, "/");
//        System.out.println(responseFromSecurisedServer);

//        System.out.println(getResponseFromSecurisedServer("utm.md", 443, "/"));
//        Semaphore semaphore = new Semaphore(2);
//        ExecutorService exec = Executors.newFixedThreadPool(4);
//        boolean status = true;
//        while (status) {
//            for (String element : listOfImg) {
//                semaphore.acquire();
//                exec.execute(() -> {
//                    try {
//                        getImg(getRealNameOfPicture(element));
//                        semaphore.release();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(Thread.currentThread().getName());
//                });
//                if (element.equals(listOfImg.get(listOfImg.size()-1)))
//                {
//                    status = false;
//                    break;
//                }
//            }
//        }
//        exec.shutdown();
//        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

//        test();
    }

//    public static void test() throws IOException {
//        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//
//
//        SSLSocket result = (SSLSocket) sslsocketfactory.createSocket(
//                "www.facebook.com", 443);
//        result.startHandshake();
//        result.getOutputStream().write("GET / HTTP/1.1\r\nHost: www.facebook.com\r\n\r\n".getBytes());
//        String  res=my.getData(result.getInputStream());
//        print(res);
//    }
    public static List<String> getPics(String text) {
        String img;
        String regex = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        List<String> pics = new ArrayList<>();

        Pattern pImage = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher mImage = pImage.matcher(text);

        while (mImage.find()) {
            img = mImage.group();
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }


    public static String getResponseFromServer(String hostName, int port, String getArgument) throws IOException {
        String serverResponse = null;
        int c;

        Socket socket = new Socket(hostName, port);

        InputStream response = socket.getInputStream();
        OutputStream request = socket.getOutputStream();

        byte[] data = ("GET " + getArgument + " HTTP/1.1\n" + "Host: " + hostName + "\n\n").getBytes();
        request.write(data);

        while ((c = response.read()) != -1) {
            serverResponse += (char) c;
        }
        socket.close();

        return serverResponse;
    }

    public static String getResponseFromSecurisedServer(String hostName, int port, String getArgument) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        String serverResponse = null;
        int c;

        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory
                .createSocket(hostName, port);


        InputStream response = sslsocket.getInputStream();
        OutputStream request = sslsocket.getOutputStream();

        byte[] data = ("GET / HTTP/1.1\r\nHost: www.facebook.com\r\n\r\n").getBytes();
        request.write(data);
        request.flush();

        while (response.available() > 0) {
            System.out.println(response.read());
            serverResponse += (char)response.read();
            System.out.println("asd");
        }


        sslsocket.close();
        System.out.println("Secured connection performed successfully");
        return serverResponse;
    }



    public static String getRealNameOfPicture(String text) {
        String result = null;

        if (text.contains("http://mib.utm.md")) {
            result = text.replace("http://mib.utm.md", "");
            result = result.replace("'", "");
        } else result = text;
        return result;
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

