package subscriptionapp;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import static java.lang.System.*;


class SP_Server {

    static HashMap<Integer, SubscriptionThread> userSubs = new HashMap<>(1500);


    public static void main(String... args) throws IOException {


        ServerSocket server = new ServerSocket(7777);

        //TODO Java net try with resources?
        try {
            while (1==1) {
                log(InetAddress.getLocalHost());
                Socket client = server.accept();
                log("accepted ok. local socket address: " + client.getLocalSocketAddress());

                try {
                    ObjectOutputStream outObj = new ObjectOutputStream(client.getOutputStream());
                    outObj.flush();
                    log("openned output stream ok");
                    ObjectInputStream inObj = new ObjectInputStream(client.getInputStream());
                    log("openned intput stream ok");

                    //protocol: 1)read ID 2)read request 3) act
                    int clientID = inObj.readInt();
                    Request newRequest = (Request) inObj.readObject();

                    if (newRequest == Request.SIGNUP){
                        SubscriptionThread st = new SubscriptionThread(new Subscription(clientID));
                        userSubs.put(clientID, st);
                        st.start();
                        test(clientID);

                        outObj.writeObject("Signed up successful. Welcome!");

                    }else if (newRequest == Request.UPGRADE){
                        SubscriptionThread toUpgrade = userSubs.get(clientID) ;
                        if (toUpgrade != null){

                            toUpgrade.disable();
                            outObj.writeObject(toUpgrade.getSubscription());
                            outObj.flush();

                            Subscription freshlyUpgraded = (Subscription) inObj.readObject();
                            toUpgrade.setSubscription(freshlyUpgraded);
                            toUpgrade.enable();
                            //alternatively, delete from map & put again - progress is saved inside of subs anyhow

                            outObj.writeObject("You are a VIP now! Congrats!");
                            test(clientID);
                        }

                    } else if (newRequest ==Request.CANCEL){
                        int totalDuration = userSubs.get(clientID).getSubscription().getDuration();
                        userSubs.get(clientID).disable();
                        userSubs.remove(clientID);

                        outObj.writeObject("Thank you for staying "+ totalDuration + " days with us!");
                        test(clientID);
                    }

                    //bye bye client
                    outObj.close();
                    inObj.close();
                    client.close();
                    log("closed the streams and socket ok");
                } catch (IOException|ClassNotFoundException ex) {
                    ex.printStackTrace();
                } finally {
                    client.close();
                }
            }
        } finally {
            server.close();
        }
    }



    private static class SubscriptionThread extends Thread {

        Subscription subs;
        volatile boolean disabled;

        SubscriptionThread(Subscription newSubscription) {
            subs = newSubscription;
        }

        @Override
        public void run() {
            while (true) {
                try{
                    synchronized (subs) {
                        while (disabled) subs.wait();
                    }
                    sleep(2400);//2.4 seconds for 1 day
                    subs.duration++; //plus 1 day
                    subs.updateTotalCost();
                } catch (InterruptedException eix){
                    eix.printStackTrace();
                }
            }
        }

        Subscription getSubscription() {
            return subs;
        }
        void setSubscription(Subscription updated){
            subs = updated;
        }
        void disable(){disabled = true;}
        void enable(){
             disabled = false;
             synchronized (subs) {
                 subs.notify();
             }
        }


    }

    private static void test(int clientID){
        //test:
        //print stats, put main thread to sleep for 12 seconds - 5 "days"
        //reprint stats
        if (!userSubs.containsKey(clientID)) {
            out.println("User not found.");
            return;
        }
        out.println(userSubs.get(clientID).getSubscription());
        try   { Thread.sleep(12050);}
        catch (InterruptedException ex){ex.printStackTrace();}
        out.println(userSubs.get(clientID).getSubscription());
    }

    public static void log(Object o){
        out.println(o);
    }


}
//TODO fix modifiers, indentation
//TODO