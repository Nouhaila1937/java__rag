package org.example.projet_java_rag_llm;

import org.example.projet_java_rag_llm.DBase.MysqlConnection;

public class Main {

        public static void main(String[] args) {
            MysqlConnection mysqlConnection = new MysqlConnection();
            mysqlConnection.fetchData(); // Appel d'une méthode d'exemple pour récupérer des données
        }

}
