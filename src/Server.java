
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(9999);
			System.out.println("The server is running and waiting for the client");

			for (;;) {
				Socket nextClient = server.accept();
				PrintWriter to_client = new PrintWriter(nextClient.getOutputStream(), true);
				BufferedReader from_client = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));

				to_client.println("which service do you want (FTP or DNS)");
				String type = from_client.readLine();
				if (type != null && type.equals("FTP")) {
					System.out.println(
							"Receiving Request From " + nextClient.getInetAddress() + ":" + nextClient.getPort());
					FTP ftpService = new FTP(nextClient);
					ftpService.start();
					break;
				} else if (type != null && type.equals("DNS")) {
					try {
						System.out.println(
								"Receiving Request From " + nextClient.getInetAddress() + ":" + nextClient.getPort());

						DatagramSocket udp = new DatagramSocket(8888);
						DNS service = new DNS();
						service.start();
						break;
//                        while (true) {
//                        	service.receiveAndSend();
//                        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (IOException ioe) {
			System.out.println("Error" + ioe);
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
}
