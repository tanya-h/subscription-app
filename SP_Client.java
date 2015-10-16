package subscriptionapp;

import java.io.*;
import java.net.*;
import static java.lang.System.*;

class SP_Client{


    int clientID;
    static int IDcounter = 12345;
    Socket socket = null;
    ObjectOutputStream outObj = null;
    ObjectInputStream inObj = null;


    SP_Client(){
        clientID = IDcounter++;
    }

    private void connect(){
        try{
            disconnect();
            socket = new Socket("localhost",7777);
            outObj = new ObjectOutputStream(socket.getOutputStream());
            outObj.flush();
            inObj = new ObjectInputStream(socket.getInputStream());

            outObj.writeInt(clientID);
            outObj.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


    private void disconnect(){
        try {
            if (outObj!= null) outObj.close();
            if (inObj != null) inObj.close();
            if (socket!= null) socket.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public  void request_signup(){
        try{
            connect();
            outObj.writeObject(Request.SIGNUP);
            String msg = (String) inObj.readObject();
            log(msg);
            disconnect();
        } catch (IOException|ClassNotFoundException ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void request_upgrade(){
        try{
            connect();
            outObj.writeObject(Request.UPGRADE);
            Subscription mine = (Subscription) inObj.readObject();
            mine.upgradetoVIP();
            outObj.writeObject(mine);
            String msg = (String) inObj.readObject();
            log(msg);
            disconnect();
        } catch (IOException|ClassNotFoundException  ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void request_cancel(){
        try{
            connect();
            outObj.writeObject(Request.CANCEL);
            String msg = (String) inObj.readObject();
            log(msg);
            disconnect();
        } catch (IOException|ClassNotFoundException ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void log(Object o){
        out.println(o);
    }

    public static void main(String ... args){
        SP_Client firstClient = new SP_Client();
        firstClient.request_signup();
        firstClient.request_upgrade();
        firstClient.request_cancel();
    }
}