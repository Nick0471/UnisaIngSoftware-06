package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

/**
 * @brief Interfaccia per la gestione degli utenti
 */
public interface UserService {
    /**
     * @brief Ritorna una lista di tutti gli utenti registrati
     * @return Una lista contenente tutti gli utenti
     */
    List<User> getAll();

    /**
     * @brief Cerca un utente usando la sua matricola
     * @param id La matricola dell'utente
     * @return Un Optional contenente l'utente registrato, empty altrimenti
     */
    Optional<User> getById(String id);

    /**
     * @brief Registra un nuovo utente
     * @param user L'utente da registrare
     */
	void register(User user);

    /**
     * @brief Rimuove un utente in base alla sua matricola
     * @param id La matricola dell'utente da rimuovere
     * @return true se l'utente è stato rimosso, false altrimenti
     */
    boolean removeById(String id);

    /**
     * @brief Aggiorna le informazioni di un utente già registrato
     * @param id La matricola dell'utente registrato
     * @param user Le nuove informazioni da salvare (la matricola non può essere cambiata)
     */
    void updateById(String id, User user);
}
