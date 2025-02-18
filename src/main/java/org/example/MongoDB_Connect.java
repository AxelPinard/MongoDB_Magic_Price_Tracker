package org.example;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MongoDB_Connect {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDB_Connect(){
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("mydatabase");
        collection = database.getCollection("mycollection");
    }

    public void addCard(Scanner reader) throws SQLException {
        Document document = new Document();

        System.out.print("Name of card: ");
        document.append("Name", reader.next());

        System.out.print("Set of card: ");
        document.append("Set", reader.next());

        System.out.print("Num of card: ");
        document.append("Num", reader.nextInt());

        System.out.print("Price bought at: ");
        document.append("Price", reader.nextDouble());

        document.append("Current Price", 0.00);

        collection.insertOne(document);
        System.out.println();
    }

    public void updateCurrentPrice(double price, String name) throws SQLException {
        collection.updateOne(Filters.eq("Name", name), Updates.set("Current Price", price));
    }

    public void showCards() throws SQLException {
        FindIterable<Document> documents = collection.find();
        MongoCursor<Document> cursor = documents.iterator();

        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
        cursor.close();
        System.out.println();
    }

    public String[] getSetandNum(String name) throws SQLException {
        String[] joke = new String[2];
        joke[0] = collection.find(Filters.eq("Name", name)).first().getString("Set");
        joke[1] = Integer.toString(collection.find(Filters.eq("Name", name)).first().getInteger("Num"));
        return joke;
    }

    public void deleteCard(Scanner reader) throws SQLException {
        System.out.print("Name of card: ");
        collection.deleteOne(Filters.eq("Name", reader.next()));
        System.out.println();
    }

    public void getPrices(Scanner reader) throws IOException, SQLException {
        String Scryfall_info = "failed";
        String ScryURL;
        OkHttpClient client = new OkHttpClient();

        System.out.print("Name of card to getPrices for: ");
        String name = reader.next();
        String[] setandNum = getSetandNum(name);
        ScryURL = "https://api.scryfall.com/cards/" + setandNum[0] + "/" + setandNum[1];
        Request request = new Request.Builder().url(ScryURL).build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            Scryfall_info = response.body().string();
        }

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createJsonParser(Scryfall_info);

        while(parser.nextToken() != JsonToken.END_OBJECT) {
            parser.nextToken();
            try {
                if (parser.getCurrentName().equals("usd")) {
                    updateCurrentPrice(Double.parseDouble(parser.getText()), name);
                    break;
                }
            } catch (NullPointerException e) {
            }
        }
        parser.close();
    }
}