import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.FileInputStream;

public class FTP extends Thread {
//    private static final int PORT = 12345;
    private static final String DOWNLOAD_PATH = "C:/Users/saood/Desktop";
    private static final String UPLOAD_PATH = "C:/Users/saood/Desktop/upload/";
    private static final int MAX_FILE_SIZE = 1024; // Maximum file size in bytes (1KB)

    private final Socket nextClient;

    public FTP(Socket nextClient) {
        this.nextClient = nextClient;
    }

    @Override
    public void run() {
        BufferedReader from_client = null;
        PrintWriter to_client = null;

        try {
        	String username = null;
        	String password = null;
        	while (true) {
	            from_client = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
	            to_client = new PrintWriter(nextClient.getOutputStream(), true);
	
	            username = from_client.readLine();
	            password = from_client.readLine();
	            System.out.println(username);
	            System.out.println(password);
	            
	            if (username.equals("123") && password.equals("123")) break;
	            to_client.println("Username or Password is wrong, or User not found");
        	}
            boolean isValid = false;
            
                to_client.println("Correct Username and Password");
                System.out.println("Working fine");
                
                while (true) {
                	  BufferedReader reader = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
                      PrintWriter writer = new PrintWriter(nextClient.getOutputStream(), true);

                      String request = reader.readLine();
                      if (request.equals("DOWNLOAD")) {
                          String fileName = reader.readLine();
                          sendFileToClient(fileName, writer);
                      } else if (request.equals("UPLOAD")) {
                          String fileName = reader.readLine();
                          long fileSize = Long.parseLong(reader.readLine());

                          if (fileSize > MAX_FILE_SIZE) {
                              writer.println("ERROR: File size exceeds the maximum limit of 1KB");
                          } else {
                              receiveFileFromClient(fileName, fileSize, reader);
                          }
                      }

                      // Close the connection
                      nextClient.close();
                      System.out.println("Client disconnected: " + nextClient.getInetAddress().getHostAddress());
                }
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        } finally {
            try {
                nextClient.close();
                if (from_client != null) {
                    from_client.close();
                }
                if (to_client != null) to_client.close();
            } catch (IOException e) {e.printStackTrace();}
        }
        }

    private static void sendFileToClient(String fileName, PrintWriter writer) throws IOException {
        File file = new File(DOWNLOAD_PATH + File.separator + fileName);

        if (file.exists()) {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }

            fileReader.close();
            writer.println("EOF");  // End of file marker
            System.out.println("File sent: " + file.getName());
        } else {
            writer.println("ERROR: File not found");
        }
    }

    private static void receiveFileFromClient(String fileName, long fileSize, BufferedReader reader) throws IOException {
        File file = new File(UPLOAD_PATH + fileName);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

        long bytesReceived = 0;
        String line;
        while (bytesReceived < fileSize && (line = reader.readLine()) != null) {
            fileWriter.write(line);
            fileWriter.newLine();
            bytesReceived += line.getBytes().length + 1; // Account for newline character
        }

        fileWriter.close();
        System.out.println("File received: " + file.getName());
    }

}
