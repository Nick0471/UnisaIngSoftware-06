package it.unisa.diem.ingsoft.biblioteca.exception;

public class UnknownUserByIdException extends UserException {
	public UnknownUserByIdException() {
		super("Non esiste alcun utente con la matricola inserita");
	}
}
