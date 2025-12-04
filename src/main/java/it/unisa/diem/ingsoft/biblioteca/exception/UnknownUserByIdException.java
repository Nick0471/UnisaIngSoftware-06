package it.unisa.diem.ingsoft.biblioteca.exception;

import it.unisa.diem.ingsoft.biblioteca.UserException;

public class UnknownUserByIdException extends UserException {
	public UnknownUserByIdException() {
		super("Non esiste alcun utente con la matricola inserita");
	}
}
