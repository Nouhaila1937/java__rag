module org.example.projet_java_rag_llm {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires jbcrypt;
    requires java.sql;
    requires com.google.protobuf;

    opens org.example.projet_java_rag_llm to javafx.fxml;
    exports org.example.projet_java_rag_llm;
    exports org.example.projet_java_rag_llm.Controller;
    opens org.example.projet_java_rag_llm.Controller to javafx.fxml;
}