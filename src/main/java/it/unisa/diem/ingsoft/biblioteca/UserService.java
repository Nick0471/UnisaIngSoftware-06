package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

/**
 * @brief Interfaccia per la gestione degli utenti
 */
public interface UserService {
    /**
     * @brief 
     */
    List<User> getAll();
    Optional<User> getById(String id);
	void register(User user);
    boolean removeById(String id);
    void updateById(String id, User user);
}
