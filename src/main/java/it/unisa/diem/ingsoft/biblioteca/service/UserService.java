package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.UserException;
import it.unisa.diem.ingsoft.biblioteca.model.User;

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
     * @return Un Optional contenente l'utente registrato, Optional.empty() altrimenti
     */
    Optional<User> getById(String id) throws UserException;

    /**
     * @brief Registra un nuovo utente
     * @param user L'utente da registrare
     */
	void register(User user) throws UserException;

    /**
     * @brief Rimuove un utente in base alla sua matricola
     * @param id La matricola dell'utente da rimuovere
     * @return true se l'utente è stato rimosso, false altrimenti
     */
    boolean removeById(String id);

    /**
     * @brief Aggiorna le informazioni di un utente già registrato
     * @param user L'oggetto User contenente la matricola dell'utente da modificare e
     *  le nuove informazioni da salvare
     * @invariant La matricola dell'utente è un invariante. Se è necessario modificarla
     *  bisogna eliminare e reinserire l'utente
     */
    void updateById(User user) throws UserException;

    /**
     * @brief Controlla se un utente con una matricola è già stato registrato
     * @param id La matricola dell'utente da controllare
     * @return true se l'utente esiste, false altrimenti
     */
    boolean existsById(String id);

    /**
     * @brief Controlla se un utente con una email è già stato registrato
     * @param email L'email dell'utente da controllare
     * @return true se l'utente esiste, false altrimenti
     */
    boolean existsByEmail(String email);
}
