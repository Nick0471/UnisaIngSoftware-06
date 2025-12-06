/**
 * @brief Package dei model
 * @package it.unisa.diem.ingsoft.biblioteca.model
 */
package it.unisa.diem.ingsoft.biblioteca.model;

import java.io.Serializable;

/**
 * @brief Rappresenta un'entità Utente nel sistema della biblioteca.
 * Questa classe contiene tutte le informazioni relative a un singolo utente.
 */
public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String surname;

    /**
     * @brief Costruttore di default.
     * Necessario per alcune operazioni.
     */
    public User() {}

    /**
     * @brief Costruttore completo per inizializzare tutti gli attributi dell'utente.
     *
     * @param id Matricola dell'utente.
     * @param email Indirizzo email istituzionale dell'utente.
     * @param name Nome dell'utente.
     * @param surname Cognome dell'utente.
     */
    public User(String id, String email, String name, String surname) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    /**
     * @brief Restituisce la matricola dell'utente.
     * @return La matricola (Stringa) dell'utente.
     */
    public String getId() {
        return this.id;
    }

    /**
     * @brief Imposta la matricola dell'utente.
     * @param id La nuova matricola dell'utente.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @brief Restituisce l'indirizzo email istituzionale dell'utente.
     * @return L'indirizzo email istituzionale (Stringa).
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @brief Imposta l'indirizzo email istituzionale dell'utente.
     * @param email Il nuovo indirizzo email istituzionale.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Restituisce il nome dell'utente.
     * @return Il nome (Stringa).
     */
    public String getName() {
        return this.name;
    }

    /**
     * @brief Imposta il nome dell'utente.
     * @param name Il nuovo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Restituisce il cognome dell'utente.
     * @return Il cognome (Stringa).
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     * @brief Imposta il cognome dell'utente.
     * @param surname Il nuovo cognome.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }
}
