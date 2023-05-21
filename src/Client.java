import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        Socket ftpServer;
        BufferedReader from_user;
        BufferedReader from_ftpServer;
        PrintWriter to_ftpServer;

        try {
            ftpServer = new Socket("localhost", 9999);

            from_user = new BufferedReader(new InputStreamReader(System.in));
            from_ftpServer = new BufferedReader(new InputStreamReader(ftpServer.getInputStream()));
            to_ftpServer = new PrintWriter(ftpServer.getOutputStream(), true);
            System.out.println(from_ftpServer.readLine());
            String ServicType = from_user.readLine();
            String serviceWanted = ServicType;

            to_ftpServer.println(serviceWanted);

            if (!serviceWanted.equals("exit")) {

            	 if (serviceWanted.equals("FTP")) {
                     while (true) {
                         System.out.println("Please enter your username: ");
                         String username = from_user.readLine();
                         System.out.println("Please enter your password: ");
                         String password = from_user.readLine();

                         to_ftpServer.println(username);
                         to_ftpServer.println(password);

                         String response = from_ftpServer.readLine();
                         System.out.println(response);

                         if (response.equals("Correct Username and Password")) {

                             while (true) {
                            	 BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                                 BufferedReader serverReader = new BufferedReader(new InputStreamReader(ftpServer.getInputStream()));
                                 PrintWriter writer = new PrintWriter(ftpServer.getOutputStream(), true);

                                 System.out.println("Connected to server. Enter 'UPLOAD' or 'DOWNLOAD' to continue:");
                                 String option = consoleReader.readLine();
                                 writer.println(option);

                                 if (option.equals("DOWNLOAD")) {
                                     System.out.print("Enter the file name to download: ");
                                     String fileName = consoleReader.readLine();
                                     writer.println(fileName);

                                     String line;
                                     while (!(line = serverReader.readLine()).equals("EOF")) {
                                         System.out.println(line);
                                     }
                                     System.out.println("Download completed.");
                                 } else if (option.equals("UPLOAD")) {
                                     System.out.print("Enter the file path to upload: ");
                                     String filePath = consoleReader.readLine();
                                     File file = new File(filePath);

                                     if (file.exists()) {
                                         long fileSize = file.length();
                                         if (fileSize > 1024) {
                                             System.out.println("File size exceeds the maximum limit of 1KB");
                                         } else {
                                             writer.println(file.getName());
                                             writer.println(fileSize);
                                             BufferedReader fileReader = new BufferedReader(new FileReader(file));

                                             String line;
                                             while ((line = fileReader.readLine()) != null) {
                                                 writer.println(line);
                                             }

                                             fileReader.close();
                                             writer.println("EOF");  // End of file marker
                                             System.out.println("Upload completed.");
                                         }
                                     } else {
                                         System.out.println("File not found.");
                                     }
                                 }
                             }
                         } else {
                             System.out.println("Username or Password is wrong, or User not found");
                         }
                     }
                } else if (serviceWanted.equals("DNS")) {
                    DatagramSocket client;
                    Scanner kb;
                    try {
                    	while (true) {
                    		 client = new DatagramSocket();
                             kb = new Scanner(System.in);
                             System.out.print("Enter the domain name or IP address to resolve: ");
                             String message = kb.nextLine();

                             byte[] data = message.getBytes();
                             DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5000);
                             client.send(packet);

                             byte[] receivedData = new byte[1000];
                             DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);

                             // Receive response packet
                             client.receive(receivedPacket);
                             if (receivedPacket.getLength() > 0) {
                                 System.out.printf("%s\n", new String(receivedPacket.getData(), 0, receivedPacket.getLength()));
                             }

                        // Close the socket after receiving the response
                        client.close();
                    	}
                    } catch (IOException e) {
                        System.exit(1);
                    }

                }

            } else {
                System.out.println("Quitting the service");
            }

            ftpServer.close();
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        }
    }
    
    public static void askCreds() {
    	
    }
}
