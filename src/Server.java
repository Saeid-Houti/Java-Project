import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    private static final int FTP_PORT = 8888;
    private static final int DNS_PORT = 9999;

    public static void main(String[] args) {
        try {
            ServerSocket ftpServerSocket = new ServerSocket(FTP_PORT);
            DatagramSocket dnsSocket = null;

            System.out.println("The server is waiting for the client to connect....");

            //create DatagramSocket instance on dns port
            try {
                dnsSocket = new DatagramSocket(DNS_PORT);
            } catch (SocketException e) {
                System.out.println("Failed to create DatagramSocket on port " + DNS_PORT);
                e.printStackTrace();
                return;
            }
            
            Thread ftpListenerThread = new Thread(() -> {//thread for FTP
                while (true) {
                    try {
                        Socket ftpClientSocket = ftpServerSocket.accept();
                        FTPService ftpServiceThread = new FTPService(ftpClientSocket);
                        ftpServiceThread.start();
                    } catch (IOException e) {
                        System.out.println("Error accepting FTP client connection: " + e.getMessage());
                    }
                }
            });
            ftpListenerThread.start();

            final DatagramSocket finalDnsSocket = dnsSocket; // Declare final reference because it was giving: Local variable dnsSocket defined in an enclosing scope must be final or effectively final
            Thread dnsListenerThread = new Thread(() -> {//thread for DNS
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    try {
                        finalDnsSocket.receive(packet);// receive a datagram packet from the network
                        //The received data will be stored in the packet object.. which includes the buffer where the data will be written and the length of the data as seen in the above instance of DatagramPacket
                        DNSService dnsServiceThread = new DNSService(finalDnsSocket); //the service should accept packet like this: DNSService dnsServiceThread = new DNSService(finalDnsSocket,packet);
                        dnsServiceThread.start();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            dnsListenerThread.start();

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
