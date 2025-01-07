package org.example.projet_java_rag_llm;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class Usersearch {
    private static final String DATABASE_NAME = "java_rag";
    private static final String COLLECTION_NAME = "users";

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb+srv://nouhaila:nouha123@cluster0.qqusd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            System.out.println("Sessions de l'utilisateur ajachi :");

            // Filtrer les documents de l'utilisateur ajachi
            Document query = new Document("username", "ajachi");

            // Parcourir les documents filtrés
            collection.find(query).forEach(document -> {
                System.out.println("Titre de session : " + document.get("sessions", List.class));

                // Si vous voulez afficher chaque titre individuellement
                List<Document> sessions = document.getList("sessions", Document.class);
                for (Document session : sessions) {
                    System.out.println("Titre : " + session.get("title"));
                }

                System.out.println("---");
            });
        }
    }
}
