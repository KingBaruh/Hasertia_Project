package il.cshaifasweng.OCSFMediatorExample.client.Controllers;

import il.cshaifasweng.OCSFMediatorExample.client.Connect.SimpleClient;

import java.io.IOException;

public class MovieTicketPurchasePage {
    private static SimpleClient client;

    public static void main(String[] args) throws IOException {
        client = SimpleClient.getClient();
        client.openConnection();

   //   Purchase purchase = new Purchase();
     //   String customerID = "";
     //   PurchaseMessage isCreditCardValid = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.ADD_PURCHASE, purchase, customerID );
    }
}
