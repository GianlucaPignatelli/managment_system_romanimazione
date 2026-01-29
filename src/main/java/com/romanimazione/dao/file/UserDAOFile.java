package com.romanimazione.dao.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Amministratore;
import com.romanimazione.entity.Animatore;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class UserDAOFile extends GenericFileDAO<User> implements UserDAO {

    public UserDAOFile() {
        super("users.json");
        // Seed if empty
        try {
            List<User> users = load(new TypeReference<List<User>>(){});
            if (users.isEmpty()) {
                users.add(new Animatore("demo", "pass", "Demo", "User", "demo@romanimazione.com"));
                users.add(new Amministratore("admin", "admin", "Super", "Admin", "admin@romanimazione.com"));
                save(users);
                System.out.println("File: Seeded default users (demo/pass, admin/admin).");
            }
        } catch (DAOException e) {
            System.err.println("Error seeding users: " + e.getMessage());
        }
    }

    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        List<User> users = load(new TypeReference<List<User>>(){});
        for (User u : users) {
             // Strict check or lax check? Doing strict match on username or email
            if (u.getUsername().equals(identifier) || u.getEmail().equals(identifier)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        List<User> users = load(new TypeReference<List<User>>(){});
        for (User u : users) {
             if (u.getUsername().equals(user.getUsername())) throw new DAOException("User already exists");
        }
        // Generate ID? For now relying on user-provided or auto-inc imitation not needed for username login
        int maxId = users.stream().mapToInt(User::getId).max().orElse(0);
        user.setId(maxId + 1);
        
        users.add(user);
        save(users);
    }
}
