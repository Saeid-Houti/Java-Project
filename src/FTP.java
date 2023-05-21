import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.FileInputStream;

public class FTP extends Thread {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String UPLOAD_DIR = USER_HOME + "/Desktop/upload/directory";
    private static final String DOWNLOAD_DIR = USER_HOME + "/Desktop/download/directory";
    private static final int MAX_FILE_SIZE = 1024 * 1024; // 1MB

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
                	to_client.println("What service do you want? (DOWNLOAD, UPLOAD, CANCEL)");
                	
                	
                	String whatClientWanted = from_client.readLine();
                	if (whatClientWanted.startsWith("DOWNLOAD")) {
                		handleDownload(whatClientWanted, from_client, to_client);
                		
                	} else if (whatClientWanted.startsWith("UPLOAD")) {
                		System.out.println("what client wanted" + whatClientWanted);
                		handleUpload(whatClientWanted, from_client, to_client);
                		
                	} else if (whatClientWanted.startsWith("CANCEL")) {
                		to_client.println("Canceled.");break;
                	}
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
        }}

    private void handleUpload(String request, BufferedReader reader, PrintWriter writer) throws IOException {
    	String readerLine = reader.readLine();
    	String filename = readerLine.substring(readerLine.indexOf(" ") + 1);
        if (filename.isEmpty() || filename.contains("/") || filename.contains("\\")) {
            writer.println("ERROR: Invalid filename");
            return;
        }

        String uploadFilePath = UPLOAD_DIR + File.separator + filename;
        FileOutputStream fileOutputStream = new FileOutputStream(uploadFilePath);

        long fileSize = Long.parseLong(reader.readLine());
        if (fileSize > MAX_FILE_SIZE) {
            writer.println("ERROR: File size exceeds the maximum allowed limit (" + MAX_FILE_SIZE + " bytes)");
            fileOutputStream.close();
            return;
        }

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = nextClient.getInputStream().read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        writer.println("SUCCESS: File uploaded successfully");
        return;
    }

    private void handleDownload(String request, BufferedReader reader, PrintWriter writer) throws IOException {
        String readerLine = reader.readLine();
        String filename = readerLine.substring(readerLine.indexOf(" ") + 1);
        if (filename.isEmpty()) {
            writer.println("ERROR: Invalid filename");
            return;
        }

        String downloadFilePath = DOWNLOAD_DIR + File.separator + filename;
        File file = new File(downloadFilePath);

        if (!file.exists() || file.isDirectory()) {
            writer.println("ERROR: File not found");
            return;
        }

        long fileSize = file.length();
        writer.println(fileSize);

        byte[] buffer = new byte[4096];
        int bytesRead;

        
        String destinationPath = reader.readLine();// read destination path from client

//        System.out.println("destination path: " + destinationPath);
        File destinationFile = new File(destinationPath); // create destination file
//       System.out.println("destination file: " + destinationFile);
        FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

        try (FileInputStream fileInputStream = new FileInputStream(file)) { // write the downloaded file to destination path
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            	System.out.println("before write");
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }

        fileOutputStream.close();

        writer.println("SUCCESS: File downloaded and saved successfully");
        return;
    }

}
