package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.*;
import it.unisa.diem.ingsoft.biblioteca.model.User;

/**
 * @brief Implementazione dello UserService usando un Database per la persistenza
 */
public class DatabaseUserService implements UserService {
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa lo UserService usando un database
     */
    public DatabaseUserService(Database database) {
        this.database = database;
    }

    /**
     * @brief Registra un nuovo utente.
     *  Esegue una insert SQL per inserire l'utente nel database.
     * @param user L'utente da registrare.
     * @throws DuplicateUserByEmailException Esiste già un utente con la mail specificata.
     * @throws DuplicateUserByIdException Esiste già un utente con la matricola specificata.
     */
	@Override
	public void register(User user) throws DuplicateUserByEmailException,
           DuplicateUserByIdException {
        String email = user.getEmail();
        if (this.existsByEmail(email))
            throw new DuplicateUserByEmailException();

        String id = user.getId();
        if (this.existsById(id))
            throw new DuplicateUserByIdException();

        if (!this.isEmailValid(email))
            throw new InvalidEmailException();

        if (!this.isIdValid(id))
            throw new InvalidIDException();

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO users(id, email, name, surname) "
                        + "VALUES (:id, :email, :name, :surname)")
                    .bind("id", user.getId())
                    .bind("email", user.getEmail())
                    .bind("name", user.getName())
                    .bind("surname", user.getSurname())
                    .execute());
	}

    /**
     * @brief Recupera una lista di tutti gli utenti registrati.
     *  Esegue una select SQL per ottenere la lista di utenti del database.
     * @return Una lista contenente tutti gli utenti.
     */
	@Override
	public List<User> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users")
                    .mapTo(User.class)
                    .list());
	}

    /**
     * @brief Cerca un utente usando la sua matricola.
     *  Esegue una select SQL per ottenere l'utente dal database.
     * @param id La matricola dell'utente.
     * @return Un Optional contenente l'utente registrato, Optional.empty() altrimenti.
     */
	@Override
	public Optional<User> getById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users "
                        + "WHERE id = :id")
                    .bind("id", id)
                    .mapTo(User.class)
                    .findFirst());
	}

    /**
     * @brief Rimuove un utente in base alla sua matricola.
     *  Esegue una delete SQL per eliminare l'utente dal database.
     * @param id La matricola dell'utente da rimuovere.
     * @return true se l'utente è stato rimosso, false altrimenti.
     */
	@Override
	public boolean removeById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("DELETE FROM users WHERE id = :id")
                    .bind("id", id)
                    .execute()) > 0;
	}

    /**
     * @brief Aggiorna le informazioni di un utente già registrato.
     *  Esegue un update SQL per modificare le informazioni dell'utente nel database.
     * @param user L'oggetto User contenente la matricola dell'utente da modificare e
     *  le nuove informazioni da salvare.
     * @invariant La matricola dell'utente è un invariante. Se è necessario modificarla
     *  bisogna eliminare e reinserire l'utente.
     * @throws UnknownUserByIdException Non esiste alcun utente con la matricola specificata.
     */
	@Override
	public void updateById(User user) throws UnknownUserByIdException {
        String id = user.getId();
        if (!this.existsById(id))
            throw new UnknownUserByIdException();

        String email = user.getEmail();
        String name = user.getName();
        String surname = user.getSurname();

        this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("UPDATE users "
                        + "SET email = :email, name = :name, surname = :surname "
                        + "WHERE id = :id")
                    .bind("id", id)
                    .bind("email", email)
                    .bind("name", name)
                    .bind("surname", surname)
                    .execute());
	}

    /**
     * @brief Controlla se un utente con una matricola è già stato registrato.
     *  Esegue una select count SQL per verificare se è presente l'utente nel database.
     * @param id La matricola dell'utente da controllare.
     * @return true se l'utente esiste, false altrimenti.
     */
	@Override
	public boolean existsById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(id) FROM users "
                        + "WHERE id = :id")
                    .bind("id", id)
                    .mapTo(Integer.class)
                    .one()) > 0;
	}

    /**
     * @brief Controlla se un utente con una email è già stato registrato.
     *  Esegue una select count SQL per contare gli utenti nel database.
     * @param email L'email dell'utente da controllare.
     * @return true se l'utente esiste, false altrimenti.
     */
	@Override
	public boolean existsByEmail(String email) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(email) FROM users "
                        + "WHERE email = :email")
                    .bind("email", email)
                    .mapTo(Integer.class)
                    .one()) > 0;
	}

    /**
     * @brief Recupera una lista di tutti gli utenti registrati la cui matricola contiene la stringa
     *  specificata in qualsiasi posizione.
     *  Esegue una select SQL per ottenere la lista degli utenti dal database.
     * @param id La matricola da cercare.
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
	@Override
	public List<User> getAllByIdContaining(String id) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM users "
                                + "WHERE id LIKE :id")
                        .bind("id", "%" + id + "%")
                        .mapTo(User.class)
                        .list());
	}

    /**
     * @brief Recupera una lista di tutti gli utenti registrati la cui email contiene la stringa
     *  specificata in qualsiasi posizione.
     *  Esegue una select SQL per ottenere la lista degli utenti dal database.
     * @param email La mail da cercare.
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
	@Override
	public List<User> getAllByEmailContaining(String email) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM users "
                                + "WHERE email LIKE :email")
                        .bind("email", "%" + email + "%")
                        .mapTo(User.class)
                        .list());
	}

    /**
     * @brief Recupera una lista di tutti gli utenti registrati il cui nome e cognome contengono
     *  la stringa specificata in qualsiasi posizione.
     *  Esegue una select SQL per ottenere la lista degli utenti dal database.
     * @param name La stringa da cercare nel nome
     * @param surname La stringa da cercare nel cognome
     * @return Una lista di {@link User} contenente tutti gli utenti che rispettano questo criterio.
     */
	@Override
	public List<User> getAllByFullNameContaining(String name, String surname) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users "
                        + "WHERE name LIKE :name AND surname LIKE :surname")
                    .bind("name", "%" + name + "%")       
                    .bind("surname", "%" + surname + "%") 
                    .mapTo(User.class)
                    .list());
    }

	@Override
	public boolean isEmailValid(String email) {
        if (!email.contains("@")) { return false; }
        
        int atIndex = email.indexOf("@");
        String domain = email.substring(atIndex);
        return "@studenti.unisa.it".equals(domain);
	}

    @Override
    public boolean isIdValid(String id) {
        return id.length() == 10;
    }
}
