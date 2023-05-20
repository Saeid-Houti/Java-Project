import java.io.BufferedReader;
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
                        System.out.println("Please Enter your Username : ");
                        String username = from_user.readLine();
                        System.out.println("Please Enter Your Password : ");
                        String password = from_user.readLine();

                        to_ftpServer.println(username);
                        to_ftpServer.println(password);

                        String response = from_ftpServer.readLine();
                        System.out.println(response);

                        if (response.equals("Correct Username and Password")) {
                            System.out.println(from_ftpServer.readLine());
                            String serviceNeeded = from_user.readLine();

                            to_ftpServer.println(serviceNeeded);

                            if (serviceNeeded.equals("UPLOAD")) {
                                System.out.println("Enter the filename to upload: ");
                                String filename = from_user.readLine();
                                to_ftpServer.println("UPLOAD " + filename);

                                response = from_ftpServer.readLine();
                                System.out.println(response);

                                if (response.startsWith("SUCCESS")) {
                                    System.out.println("Enter the file content: ");
                                    String content = from_user.readLine();
                                    to_ftpServer.println(content);

                                    response = from_ftpServer.readLine();
                                    System.out.println(response);
                                }
                                if (response.startsWith("CANCEL")) {
                                    break;
                                }
                            } else if (serviceNeeded.equals("DOWNLOAD")) {
                                System.out.println("Enter the filename to download: ");
                                String filename = from_user.readLine();
                                to_ftpServer.println("DOWNLOAD " + filename);

                                String fileSize = from_ftpServer.readLine();
                                System.out.println("File size: " + fileSize);

                                if (!fileSize.startsWith("ERROR")) {
                                    System.out.println("Enter the destination path: ");
                                    String destinationPath = from_user.readLine();
                                    to_ftpServer.println(destinationPath);

                                    response = from_ftpServer.readLine();
                                    System.out.println(response);
                                }
                            } else {
                                System.out.println("Invalid service");
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
                        System.out.print("domain name: ");
                        String message = kb.nextLine();

                        byte[] data = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(data, data.length,
                                InetAddress.getLocalHost(), 5000);
                        client.send(packet);

                        byte[] receivedData = new byte[1000];
                        DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);

                        // Receive packets until a response is received
                        boolean receivedResponse = false;
                        while (!receivedResponse) {
                            client.receive(receivedPacket);
                            if (receivedPacket.getLength() > 0) {
                                System.out.printf("%s\n", new String(receivedPacket.getData(), 0, receivedPacket.getLength()));
                                receivedResponse = true;
                            }
                        }

                        // Close the socket after receiving the response
                        client.close();
                    	}
                    } catch (IOException e) {
                        System.exit(1);
                    }

                }

            } else {
                System.out.println("Thanks for being with us, Bye!!");
            }

            ftpServer.close();
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        }
    }
}
