package it.unisa.diem.ingsoft.biblioteca.exception;

public class DuplicateUserByIdException extends UserException {
	public DuplicateUserByIdException() {
		super("Esiste già un utente registrato con questa matricola");
	}
}
