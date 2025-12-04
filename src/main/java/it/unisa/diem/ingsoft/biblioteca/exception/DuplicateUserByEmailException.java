package it.unisa.diem.ingsoft.biblioteca.exception;

import it.unisa.diem.ingsoft.biblioteca.UserException;

public class DuplicateUserByEmailException extends UserException {
	public DuplicateUserByEmailException() {
		super("Esiste già un utente registrato con questa email");
	}
}
