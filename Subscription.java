package subscriptionapp;

import java.io.Serializable;


    /* I decided to build the following architecture:
    * unlimited subscriptions running in threads on the server for daily price 5.99,
    * 1 day corresponds to 2,4 second , that can be upgraded to VIP for 9.99/day.
    * A client can connect to the server and sign up, providing his ID.
    * On the server side, a SubscriptionThread is created and put to server's
    * HashMap. SubscriptionThread essentially adds up a day to subscription's total duration
    * by sleeping for 2,4 sec in each while run.
    *
    * Furthermore the plan is to create full scale communication between server
    * and client. A client should be able to send specific requests (signup/upgrade/cancel)
    * for the server to react correspondingly. Your task will therefore be implemented
    * in update() part:$
    *
    * Now, questions/problems:
    * - HashMap for purposes of storing threads?
    * - if y, hashing by integer ID - good or bad?
    * - I want 5 days in the output here instead of 4, why? "never know w/ threads?"
    * - it's not the real world, should I really care about making eth private + 1k getters?
    *   is that still used?:$
    * - how's generally my coding style? you haven't seen it for AGES, my angel.
    * Offtop: exited about finally mastering wait() and notify();
    *
    * T.
    * */



class Subscription implements Serializable{

    private static final long serialVersionUID = -1475518831223793067L;

    private int ID;
    int duration;
    boolean VIP;
    double totalCost;

    private final double COST_SIMPLE = 5.99;
    private final double COST_VIP = 9.99;

    Subscription(int userID){
        ID = userID;
    }

    public void updateTotalCost(){
        totalCost += ((VIP)? COST_VIP: COST_SIMPLE);
    }


    public void upgradetoVIP(){VIP = true;}
    public int getDuration(){return duration;}
    public double getBill(){return totalCost;}

    public String toString(){
        return ID + "\tVIP" + ((VIP)? " + " : " - ")+"\tDays:" + duration + "\tTotal: " + totalCost;
    }


    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof Subscription)) return false;
        return ((Subscription) o).ID == this.ID;
    }

    public int hashCode(){return ID;}

}