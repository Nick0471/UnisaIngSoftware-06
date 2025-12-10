/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.model.User;

/**
 * @brief Interfaccia per la gestione degli utenti
 */
public interface UserService {
    /**
     * @brief Recupera una lista di tutti gli utenti registrati.
     * @return Una lista contenente tutti gli utenti.
     */
    List<User> getAll();

    /**
     * @brief Recupera una lista di tutti gli utenti registrati la cui matricola contiene la stringa
     *  specificata in qualsiasi posizione.
     * @param id La matricola da cercare.
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
    List<User> getAllByIdContaining(String id);

    /**
     * @brief Recupera una lista di tutti gli utenti registrati la cui email contiene la stringa
     *  specificata in qualsiasi posizione.
     * @param email La mail da cercare.
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
    List<User> getAllByEmailContaining(String email);

    /**
     * @brief Recupera una lista di tutti gli utenti registrati il cui nome e cognome contengono
     *  la stringa specificata in qualsiasi posizione.
     * @param name La stringa da cercare nel nome
     * @param surname La stringa da cercare nel cognome
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
    List<User> getAllByFullNameContaining(String name, String surname);

    /**
     * @brief Cerca un utente usando la sua matricola.
     * @param id La matricola dell'utente.
     * @return Un Optional contenente l'utente registrato, Optional.empty() altrimenti.
     */
    Optional<User> getById(String id);

    /**
     * @brief Registra un nuovo utente.
     * @param user L'utente da registrare.
     * @throws DuplicateUserByEmailException Esiste già un utente con la mail specificata.
     * @throws DuplicateUserByIdException Esiste già un utente con la matricola specificata.
     */
	void register(User user) throws DuplicateUserByIdException, DuplicateUserByEmailException;

    /**
     * @brief Rimuove un utente in base alla sua matricola.
     * @param id La matricola dell'utente da rimuovere.
     * @return true se l'utente è stato rimosso, false altrimenti.
     */
    boolean removeById(String id);

    /**
     * @brief Aggiorna le informazioni di un utente già registrato.
     * @param user L'oggetto User contenente la matricola dell'utente da modificare e
     *  le nuove informazioni da salvare.
     * @invariant La matricola dell'utente è un invariante. Se è necessario modificarla
     *  bisogna eliminare e reinserire l'utente.
     * @throws UnknownUserByIdException Non esiste alcun utente con la matricola specificata.
     */
    void updateById(User user) throws UnknownUserByIdException;

    /**
     * @brief Controlla se un utente con una matricola è già stato registrato.
     * @param id La matricola dell'utente da controllare.
     * @return true se l'utente esiste, false altrimenti.
     */
    boolean existsById(String id);

    /**
     * @brief Controlla se un utente con una email è già stato registrato.
     * @param email L'email dell'utente da controllare.
     * @return true se l'utente esiste, false altrimenti.
     */
    boolean existsByEmail(String email);

    /**
     * @brief Controlla che una mail sia valida per la registrazione di un utente.
     * @param email La mail da controllare.
     * @return true se la mail è valida, false altrimenti.
     */
    boolean isEmailValid(String email);
}
