package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static ServiceCounter counter=ServiceCounter.getInstance();
    public static LinkedList<Thread> allThreads=new LinkedList<>();

    public static void main(String[] args) {

        String inputFilePath = args[0];

        Gson Gson = new Gson();
        HashMap<Integer, Customer> CustomerPrintMap = new HashMap<>();
        try {
            JsonReader Jreader = new JsonReader(new FileReader(inputFilePath));
            JsonObject jsonobject = Gson.fromJson(Jreader, JsonObject.class);

            Inventory inventory = Inventory.getInstance();
            BookInventoryInfo[] books = initializingInventory(jsonobject);
            inventory.load(books);

            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            DeliveryVehicle[] vehicles = initializingVehicles(jsonobject);
            resourcesHolder.load(vehicles);

            BlockingQueue<MicroService> ServiceQueue = initializingServices(jsonobject);

            //JSON: Initializing the Customers and their APIService:
            CustomerPrintMap = initializingAPIServices(jsonobject, ServiceQueue);

            int numOfServices=ServiceQueue.size();
            for(int p=0;p<numOfServices;p++){

                MicroService serviceToActivate = ServiceQueue.poll();
                if(serviceToActivate instanceof TimeService)
                    ServiceQueue.add(serviceToActivate);
                else {
                    Thread thread = new Thread(serviceToActivate);
                    allThreads.add(thread);
                    int oldvale;
                    int newval;
                    do{
                        oldvale=counter.counter.get();
                        newval=oldvale+1;

                    }
                    while(!counter.counter.compareAndSet(oldvale,newval));
                    thread.start();
                }
            }
            initializingTimeService(jsonobject);

            for(Thread t:allThreads){           //waiting for al threads to terminate
                try {
                    t.join();
                }catch (InterruptedException e){e.printStackTrace();}
            }

        } catch (FileNotFoundException e) {}


        printCustomersToFile(CustomerPrintMap, args[1]);       //Printing Customers HashMap
        Inventory.getInstance().printInventoryToFile(args[2]);    //Printing Books HashMap
        MoneyRegister.getInstance().printOrderReceipts(args[3]);    //Printing Order Receipts
        printMoneyRegisterToFile(args[4]);                          //Printing MoneyRegister

    }
                                // READ FROM JSON//


// Inventory Initialization:
    private static BookInventoryInfo[] initializingInventory(JsonObject object) {
        JsonArray inventoryFromJson = object.getAsJsonArray("initialInventory");
        BookInventoryInfo[] books = new BookInventoryInfo[inventoryFromJson.size()];
        for (int i = 0; i < inventoryFromJson.size(); i++) {
            JsonElement element = inventoryFromJson.get(i);
            String bookTitle = element.getAsJsonObject().get("bookTitle").getAsString();
            int amountInInventory = element.getAsJsonObject().get("amount").getAsInt();
            int price = element.getAsJsonObject().get("price").getAsInt();
            BookInventoryInfo bookToAdd = new BookInventoryInfo(bookTitle, amountInInventory, price);
            books[i] = bookToAdd;
        }
        return books;
    }

    // resources Initialization:
    private static DeliveryVehicle[] initializingVehicles(JsonObject object) {
        JsonArray ResourceFromJson = object.getAsJsonArray("initialResources");
        JsonObject Object = (JsonObject) ResourceFromJson.get(0);
        JsonArray VehiclesFromJson = Object.get("vehicles").getAsJsonArray();
        DeliveryVehicle[] vehicles = new DeliveryVehicle[VehiclesFromJson.size()];
        for (int i = 0; i < VehiclesFromJson.size(); i++) {
            JsonElement element = VehiclesFromJson.get(i);
            int vehicleLicense = element.getAsJsonObject().get("license").getAsInt();
            int speed = element.getAsJsonObject().get("speed").getAsInt();
            DeliveryVehicle vehicle = new DeliveryVehicle(vehicleLicense, speed);
            vehicles[i] = vehicle;
        }
        return vehicles;
    }
    private static BlockingQueue<MicroService> initializingServices(JsonObject object) {
        BlockingQueue<MicroService> ServiceQueue = new LinkedBlockingQueue<>();
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();


// sellingService Initialization:
        int SellingsToInitialize = ServicesFromJson.get("selling").getAsInt();
        for (int i = 0; i < SellingsToInitialize; i++) {
            String tmpName="sellingService "+i;
            SellingService sellingService = new SellingService(tmpName);
            ServiceQueue.add(sellingService);
        }

        int InventoryServiceToInitialize = ServicesFromJson.get("inventoryService").getAsInt();
        for (int i = 0; i < InventoryServiceToInitialize; i++) {
            String tmpName="inventoryService "+i;
            InventoryService inventoryService = new InventoryService(tmpName);
            ServiceQueue.add(inventoryService);
        }

        int LogisticsServiceToInitialize = ServicesFromJson.get("logistics").getAsInt();
        for (int i = 0; i < LogisticsServiceToInitialize; i++) {
            LogisticsService logisticsService = new LogisticsService("logisticService"+i);
            ServiceQueue.add(logisticsService);
        }

        int ResourcesServiceToInitialize = ServicesFromJson.get("resourcesService").getAsInt();
        for (int i = 0; i < ResourcesServiceToInitialize; i++) {
            ResourceService resourceService = new ResourceService("resourcesService"+i);
            ServiceQueue.add(resourceService);
        }
        return ServiceQueue;
    }

    private static HashMap<Integer, Customer> initializingAPIServices(JsonObject object, BlockingQueue<MicroService> servicesThreads) {
        HashMap<Integer, Customer> CustomerPrintMap = new HashMap<>();
        BlockingQueue<MicroService> servicesThreadsWithAPI = servicesThreads;
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();

        JsonArray CustomersFromJson = ServicesFromJson.get("customers").getAsJsonArray();
        for (int i = 0; i < CustomersFromJson.size(); i++) {
            JsonElement element = CustomersFromJson.get(i);
            int customerID = element.getAsJsonObject().get("id").getAsInt();
            String customerName = element.getAsJsonObject().get("name").getAsString();
            String customerAddress = element.getAsJsonObject().get("address").getAsString();
            int customerDistance = element.getAsJsonObject().get("distance").getAsInt();
            JsonObject creditCard = element.getAsJsonObject().get("creditCard").getAsJsonObject();
            int customerCreditCard = creditCard.get("number").getAsInt();
            AtomicInteger customerAmountInCard = new AtomicInteger();
            customerAmountInCard.set(creditCard.get("amount").getAsInt());
            JsonArray booksOfCustomer = element.getAsJsonObject().get("orderSchedule").getAsJsonArray();
            LinkedList<OrderPair> ordersList=new LinkedList<>();
            for(int k=0;k<booksOfCustomer.size();k++){
                OrderPair tmp=new OrderPair(booksOfCustomer.get(k).getAsJsonObject().get("bookTitle").getAsString(),booksOfCustomer.get(k).getAsJsonObject().get("tick").getAsInt());
                ordersList.add(tmp);
            }

            Customer customer = new Customer(customerID, customerName,ordersList, customerAddress,  customerDistance,new LinkedList<>(), customerCreditCard, customerAmountInCard.get());
            CustomerPrintMap.put(customerID, customer); //initialize the customer map that should be printed.
            String nameOfAPI="APIService"+i;
            APIService apiService = new APIService(customer,nameOfAPI);
            servicesThreadsWithAPI.add(apiService);
        }

        return CustomerPrintMap;
    }

    private static void initializingTimeService(JsonObject object) {
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();
        JsonObject timeServiceJson = ServicesFromJson.get("time").getAsJsonObject();
        int speed = timeServiceJson.get("speed").getAsInt();
        int duration = timeServiceJson.get("duration").getAsInt();
        while(counter.counter.intValue()!=0){}
        Thread time=new Thread(new TimeService(duration,speed));
        allThreads.add(time);
        time.start();
    }

    private static void printMoneyRegisterToFile(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(MoneyRegister.getInstance());
            out.close();
            fileOut.close();
        } catch (IOException i) {}

    }

    private static void printCustomersToFile(HashMap customersMap, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(customersMap);
            out.close();
            fileOut.close();
        } catch (IOException i) {}
    }
}