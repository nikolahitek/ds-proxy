package com.nikolahitek;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer {

    private static final Map<String, Integer> servers = new HashMap<>();

    synchronized static void addServer(String url, int port) {
        servers.put(url, port);
        System.out.println("New server: " + url + " - " + port);
    }

    static int getServersPort(String url) {
        System.out.println("Client asked for: " + url);
        return servers.get(url);
    }

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket(1010);

        while (true) {
            byte[] buff = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            socket.receive(packet);

            Thread thread = new Thread(new ProxyServerWorker(packet));
            thread.start();
        }

    }
}

class ProxyServerWorker implements Runnable {

    private DatagramPacket packet;
    private DatagramSocket socket;

    ProxyServerWorker(DatagramPacket packet) throws SocketException {
        this.packet = packet;
        socket = new DatagramSocket();
    }

    @Override
    public void run() {
        String data = new String(packet.getData());
        String[] parts = data.split(" ");

        if (parts.length == 2) {
            ProxyServer.addServer(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }

        if (parts.length == 1) {
            Integer port = ProxyServer.getServersPort(parts[0].trim());
            DatagramPacket portPacket =
                    new DatagramPacket(port.toString().getBytes(), port.toString().getBytes().length, packet.getAddress(), packet.getPort());
            try {
                socket.send(portPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
