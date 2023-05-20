import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DNS extends Thread {
	DatagramSocket server;
	String DNSlist[][] = { { "cdns2.qatar.net.qa", "212.77.192.60" },
			{ "www.yahoo.com", "87.248.122.122" },
			{ "java.sun.com", "192.9.162.55" },
			{ "www.google.com", "173.194.36.52" },
			{ "www.google.com", "173.194.36.48" },
			{ "www.google.com", "173.194.36.50" },
			{ "www.google.com", "173.194.36.51" },
			{ "www.google.com", "173.194.36.49" },
			{ "www.qu.edu.qa", "86.36.68.18" },
			{ "upm.edu.my", "119.40.119.1" },
			{ "uum.edu.my", "103.5.180.122" },
			{ "yu.edu.jo", "87.236.233.10" },
			{ "www.sun.com", "137.254.16.113" },
			{ "www.oracle.com", "2.23.241.55" },
			{ "www.gm.com", "2.22.9.175" },
			{ "www.motorola.com", "23.14.215.224" },
			{ "www.nokia.com", "2.22.75.80" },
			{ "www.intel.com", "212.77.199.203" },
			{ "www.intel.com", "212.77.199.210" },
			{ "www.apple.com", "2.22.77.15" },
			{ "www.honda.com", "164.109.25.248" },
			{ "www.gmail.com", "173.194.36.54" },
			{ "www.gmail.com", "173.194.36.53" },
			{ "www.hotmail.com", "94.245.116.13" },
			{ "www.hotmail.com", "94.245.116.11" },
			{ "www.toyota.com", "212.77.199.224" },
			{ "www.toyota.com", "212.77.199.203" },
			{ "www.gmc.com", "2.22.247.241" },
			{ "87.248.122.122", "www.yahoo.com" },
			{ "www.mit.edu", "18.9.22.169" }, { "www.cmu.edu", "128.2.10.162" } };

	public void run() {
        try {
            server = new DatagramSocket(5000);
        } catch (SocketException socketException) {
            System.exit(1);
        }
        while (true) {
            try {
                byte[] data = new byte[100];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                server.receive(packet);

                String query = new String(data, 0, packet.getLength()).trim();
                String response = "";

                if (isValidIPAddress(query)) {
                    response = findDomainNameByIPAddress(query);
                } else {
                    response = findIPAddressByDomainName(query);
                }

                byte[] r = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(r, r.length, packet.getAddress(), packet.getPort());
                server.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidIPAddress(String query) {
        String[] octets = query.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            int num = Integer.parseInt(octet);
            if (num < 0 || num > 255) {
                return false;
            }
        }
        return true;
    }

    private String findIPAddressByDomainName(String query) {
        boolean found = false;
        String response = "";

        for (int i = 0; i < DNSlist.length; i++) {
            if (DNSlist[i][0].equals(query)) {
                response = "Domain: " + DNSlist[i][0] + "\tIP: " + DNSlist[i][1];
                found = true;
                break;
            }
        }

        if (!found) {
            response = "Cannot resolve Domain to IP.";
        }

        return response;
    }

    private String findDomainNameByIPAddress(String query) {
        boolean found = false;
        String response = "";

        for (int i = 0; i < DNSlist.length; i++) {
            if (DNSlist[i][1].equals(query)) {
                response = "IP: " + DNSlist[i][1] + "\tDomain: " + DNSlist[i][0];
                found = true;
                break;
            }
        }

        if (!found) {
            response = "Cannot resolve IP to Domain.";
        }

        return response;
    }
	
}
