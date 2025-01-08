package org.example.projet_java_rag_llm.service;
import org.example.projet_java_rag_llm.Dao.UserDao;
public class UserService {
    private static int currentUserId;
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public int login(String email, String password) {
        this.currentUserId = this.userDao.login(email,password);
        return this.currentUserId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}
