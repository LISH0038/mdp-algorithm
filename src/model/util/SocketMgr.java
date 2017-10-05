package model.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A singleton class for using the socket
 */
public class SocketMgr {

    private static SocketMgr mInstance;
    private Socket mSocket;
    private PrintWriter mSocketWriter;
    private BufferedReader mSocketReader;
    private static final int PORT = 5560;
    private static final String ADDRESS = "192.168.16.1";

    private SocketMgr() { }

    public static SocketMgr getInstance() {
        if (mInstance == null)
            mInstance = new SocketMgr();
        return mInstance;
    }

    public void openConnection() {
        try {
            mSocket = new Socket(ADDRESS, PORT);
            mSocketWriter = new PrintWriter(mSocket.getOutputStream(), true);
            mSocketReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            System.out.println("Socket connection successful");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket connection failed");

        }
    }

    public void closeConnection() {
        mSocketWriter.close();
        try {
            mSocketReader.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Socket connection closed");
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    public void sendMessage(String dest, String msg) {
        //if (!isConnected())
        //    openConnection();
        mSocketWriter.println(dest + msg);
        System.out.println("Sent message: " + dest + msg);
    }

    public String receiveMessage() {
        //if (!isConnected())
        //    openConnection();
        try {
            String msg = mSocketReader.readLine();
            System.out.println("Received message: " + msg);

            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void clearInputBuffer() {
        String input;
        try {
            while ((input = mSocketReader.readLine()) != null) {
                System.out.println("Discarded message: " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
