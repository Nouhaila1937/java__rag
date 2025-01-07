package org.example.projet_java_rag_llm.DBase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import com.mongodb.MongoException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class MongoDBConnection {

    // Connexion avec MongoDB Atlas
    private static final String URI = "mongodb+srv://nouhaila:nouha123@cluster0.qqusd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    public static MongoClient mongoClient;
    public static MongoDatabase database;

    static {
        try {
            // Initialiser la connexion à MongoDB Atlas
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase("java_rag"); // Remplacez "java_rag" par le nom de votre base de données
        } catch (MongoException e) {
            System.out.println("Erreur lors de la connexion à la base de données MongoDB : " + e.getMessage());
        }
    }
    public static boolean userExists(String username) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document query = new Document("username", username);

        try {
            Document document = collection.find(query).first();
            return document != null;
        } catch (MongoException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
            return false;
        }
    }


    // Méthode pour ajouter un utilisateur à la collection "users"
    /*public static void addUser(String username, String email, String password) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Créer un document représentant l'utilisateur
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", hashedPassword); // Vous devriez sécuriser le mot de passe avant de l'insérer

        // Insérer le document dans la collection
        try {
            collection.insertOne(newUser);
            System.out.println("Utilisateur ajouté avec succès : " + username);
        } catch (MongoException e) {
            System.out.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }

    }*/

    public static String createNewSession(String username, String sessionTitle) {
        MongoCollection<Document> collection = database.getCollection("users");
        String sessionId = UUID.randomUUID().toString();

        // Créer un document pour la nouvelle session avec un titre
        Document newSession = new Document("sessionId", sessionId)
                .append("title", sessionTitle) // Ajout du titre de la session
                .append("messages", new ArrayList<Document>());

        Document query = new Document("username", username);
        Document updateOperation = new Document("$push", new Document("sessions", newSession));

        try {
            collection.updateOne(query, updateOperation);
            System.out.println("Nouvelle session créée pour l'utilisateur : " + username + " avec titre : " + sessionTitle);
            return sessionId;
        } catch (MongoException e) {
            System.out.println("Erreur lors de la création de la session : " + e.getMessage());
            return null;
        }
    }

    // Méthode pour ajouter un utilisateur à la collection "users"
    /*public static void addUser(String username, String email, String password) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Créer un document représentant l'utilisateur
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", hashedPassword); // Vous devriez sécuriser le mot de passe avant de l'insérer

        // Insérer le document dans la collection
        try {
            collection.insertOne(newUser);
            System.out.println("Utilisateur ajouté avec succès : " + username);

            // Créer une session vide après l'ajout de l'utilisateur
            String sessionId = createNewSession(username); // Crée une session pour l'utilisateur

            // Débogage
            if (sessionId != null) {
                System.out.println("Session créée pour l'utilisateur " + username + " avec sessionId : " + sessionId);
            } else {
                System.out.println("Erreur lors de la création de la session pour l'utilisateur " + username);
            }

        } catch (MongoException e) {
            System.out.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }
    }*/

    // Exemple d'ajout d'un utilisateur et création de session avec titre
    public static void addUser(String username, String email, String password) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Créer un document représentant l'utilisateur
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", hashedPassword); // Vous devriez sécuriser le mot de passe avant de l'insérer

        // Insérer le document dans la collection
        try {
            collection.insertOne(newUser);
            System.out.println("Utilisateur ajouté avec succès : " + username);

            // Créer une session vide après l'ajout de l'utilisateur
            String sessionTitle = "Session initiale";  // Exemple de titre de session
            String sessionId = createNewSession(username, sessionTitle); // Crée une session pour l'utilisateur

            // Débogage
            if (sessionId != null) {
                System.out.println("Session créée pour l'utilisateur " + username + " avec sessionId : " + sessionId);
            } else {
                System.out.println("Erreur lors de la création de la session pour l'utilisateur " + username);
            }

        } catch (MongoException e) {
            System.out.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }
    }

    // Vérifier si l'email est déjà utilisé
    public static boolean isEmailInUse(String email) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Créer une requête pour vérifier si l'email existe déjà
        Document query = new Document("email", email);
        Document result = collection.find(query).first();

        return result != null;  // Si un utilisateur avec cet email est trouvé, retourne true
    }

    // vérifier l'existance des données
    public static boolean datafound(String email, String password) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("email", email);
        Document result = collection.find(query).first();

        if (result != null) {
            String hashedPassword = result.getString("password");
            return BCrypt.checkpw(password, hashedPassword);
        }
        return false;
    }

    public static void addMessage(String username, String message) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document query = new Document("username", username);

        // Debug: Affiche la requête avant de la lancer
        System.out.println("Requête MongoDB: " + query.toJson());

        Document updateOperation = new Document("$push",
                new Document("sessions.$[elem].messages",
                        new Document("sender", username)
                                .append("content", message)));

        try {
            // Debug: Affiche la mise à jour
            System.out.println("Mise à jour MongoDB: " + updateOperation.toJson());

            // Exécuter la mise à jour
            UpdateResult result = collection.updateOne(query, updateOperation);

            // Vérifier si le message a été ajouté
            if (result.getMatchedCount() > 0) {
                System.out.println("Message ajouté à la session de l'utilisateur : " + username);
            } else {
                System.out.println("Aucune session trouvée pour l'utilisateur : " + username);
            }
        } catch (MongoException e) {
            System.out.println("Erreur lors de l'ajout du message : " + e.getMessage());
        }
    }

    public static List<Document> getMessages(String username) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document query = new Document("username", username);
        Document filter = new Document("$exists", "messages");
        return collection.find(query).filter(filter).into(new ArrayList<>());
    }

    public static String createNewSession(String username) {
        MongoCollection<Document> collection = database.getCollection("users");
        String sessionId = UUID.randomUUID().toString();

        Document newSession = new Document("sessionId", sessionId)
                .append("messages", new ArrayList<Document>());

        Document query = new Document("username", username);
        Document updateOperation = new Document("$push", new Document("sessions", newSession));

        try {
            collection.updateOne(query, updateOperation);
            System.out.println("Nouvelle session créée pour l'utilisateur : " + username);
            return sessionId;
        } catch (MongoException e) {
            System.out.println("Erreur lors de la création de la session : " + e.getMessage());
            return null;
        }

    }

    public static List<Document> getUserSessions(String username) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("username", username);

        try {
            return collection.find(query).into(new ArrayList<>());
        } catch (MongoException e) {
            System.err.println("Erreur lors de la recherche des sessions : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void addMessageToSession(String username, String sessionId, String sender, String content) {
        MongoCollection<Document> collection = database.getCollection("users");

        Document query = new Document("username", username)
                .append("sessions.sessionId", sessionId);

        // Debug: Affiche la requête avant de la lancer
        System.out.println("Requête MongoDB: " + query.toJson());

        Document newMessage = new Document("sender", sender)
                .append("content", content);

        Document updateOperation = new Document("$push", new Document("sessions.$[elem].messages", newMessage));

        try {
            // Debug: Affiche la mise à jour
            System.out.println("Mise à jour MongoDB: " + updateOperation.toJson());

            // Exécuter la mise à jour
            UpdateResult result = collection.updateOne(query, updateOperation);

            // Vérifier si le message a été ajouté
            if (result.getMatchedCount() > 0) {
                System.out.println("Message ajouté à la session : " + sessionId);
            } else {
                System.out.println("Aucune session trouvée pour l'utilisateur : " + username + " avec sessionId : " + sessionId);
            }
        } catch (MongoException e) {
            System.out.println("Erreur lors de l'ajout du message : " + e.getMessage());
        }
    }

    public static void logoutUser(String username) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Rechercher l'utilisateur par son nom d'utilisateur
        Document query = new Document("username", username);
        Document user = collection.find(query).first();

        if (user != null) {
            // Récupérer la première session de l'utilisateur (si existante)
            List<Document> sessions = (List<Document>) user.get("sessions");
            if (sessions != null && !sessions.isEmpty()) {
                // On suppose que la première session est la session active
                String sessionId = sessions.get(0).getString("sessionId");

                // Supprimer la session active
                Document updateOperation = new Document("$pull", new Document("sessions", new Document("sessionId", sessionId)));
                try {
                    // Exécuter la mise à jour
                    collection.updateOne(query, updateOperation);
                    System.out.println("Session supprimée avec succès pour l'utilisateur : " + username);
                } catch (MongoException e) {
                    System.out.println("Erreur lors de la suppression de la session : " + e.getMessage());
                }
            } else {
                System.out.println("Aucune session active trouvée pour l'utilisateur : " + username);
            }
        } else {
            System.out.println("Utilisateur non trouvé : " + username);
        }
    }

    public static List<String> getSessionTitles(String username) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("username", username);

        try {
            Document user = collection.find(query).first();

            if (user != null) {
                Object sessionsObject = user.get("sessions");
                if (sessionsObject instanceof List) {
                    List<Document> sessions = (List<Document>) sessionsObject;
                    List<String> sessionTitles = new ArrayList<>();

                    for (Document session : sessions) {
                        String title = session.getString("title");
                        if (title != null && !title.isEmpty()) {
                            sessionTitles.add(title);
                        }
                    }

                    return sessionTitles;
                } else {
                    System.out.println("Aucune session trouvée pour cet utilisateur");
                }
            } else {
                System.out.println("Utilisateur non trouvé : " + username);
            }
        } catch (MongoException e) {
            System.err.println("Erreur lors de la recherche des sessions : " + e.getMessage());
        }

        return new ArrayList<>();
    }

    public static List<Document> getSessionMessages(String sessionId) {
        try {
            MongoCollection<Document> collection = database.getCollection("users");
            Document query = new Document("sessionId", sessionId);
            // Recherche du document correspondant à la session
            Document session = collection.find(query).first();
            if (session != null) {
                return (List<Document>) session.get("messages", List.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }



    // Fermer la connexion à la base de données
    public static void close() {
        mongoClient.close();
    }

}