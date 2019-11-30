package com.nikolahitek;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Server {

    static String URL;
    static int PORT;
    static DatagramSocket socket;
    static String KEY;

    public static void main(String[] args) throws IOException {

        KEY = "dbajf37shjdj";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter URL:");
        URL = scanner.nextLine();
        System.out.println("Enter PORT:");
        PORT = scanner.nextInt();

        socket = new DatagramSocket(PORT);

        // Register with Proxy
        String data = URL + " " + PORT;
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getLocalHost(), 1010);
        socket.send(packet);

        // Receiving packets from Client
        while (true) {
            byte[] buff = new byte[1024];
            DatagramPacket received = new DatagramPacket(buff, buff.length);
            socket.receive(received);

            Thread thread = new Thread(new ServerWorker(received));
            thread.start();
        }
    }
}

class ServerWorker implements Runnable {

    private DatagramPacket packet;
    private DatagramSocket socket;

    ServerWorker(DatagramPacket packet) throws SocketException {
        this.packet = packet;
        socket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            String data = new String(packet.getData()).trim();

            if (data.equals("HI")) {
                System.out.println("Client requested my KEY.");
                DatagramPacket keyPacket =
                        new DatagramPacket(Server.KEY.getBytes(), Server.KEY.getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(keyPacket);

            } else if (data.endsWith(Server.KEY) && data.startsWith(Server.URL)) {
                System.out.println("Client sent my encrypted URL.");
                String encData = Server.URL + Server.KEY;
                DatagramPacket dataPacket =
                        new DatagramPacket(encData.getBytes(), encData.getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(dataPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
