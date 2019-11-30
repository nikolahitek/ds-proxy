package com.nikolahitek;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter URL:");
        String URL = scanner.nextLine();

        DatagramSocket socket = new DatagramSocket();

        // Ask for PORT from Proxy
        DatagramPacket packet = new DatagramPacket(URL.getBytes(), URL.getBytes().length, InetAddress.getLocalHost(), 1010);
        socket.send(packet);

        // Receive PORT from Proxy
        byte[] buff = new byte[1024];
        packet = new DatagramPacket(buff, buff.length);
        socket.receive(packet);
        int port = Integer.parseInt(new String(packet.getData()).trim());
        System.out.println("Server on PORT: " + port);

        // Sey HI to Server
        String initial = "HI";
        packet = new DatagramPacket(initial.getBytes(), initial.getBytes().length, InetAddress.getLocalHost(), port);
        socket.send(packet);

        // Receive KEY from Server
        buff = new byte[1024];
        packet = new DatagramPacket(buff, buff.length);
        socket.receive(packet);
        String key = new String(packet.getData()).trim();
        System.out.println("Received server's KEY: " + key);

        //Send encrypted URL
        String encURL = URL + key;
        packet = new DatagramPacket(encURL.getBytes(), encURL.getBytes().length, InetAddress.getLocalHost(), port);
        socket.send(packet);

        //Receive encrypted DATA
        buff = new byte[1024];
        packet = new DatagramPacket(buff, buff.length);
        socket.receive(packet);
        String data = new String(packet.getData()).trim();
        System.out.println("Received server's DATA: " + data);

    }
}
