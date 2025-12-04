package it.unisa.diem.ingsoft.biblioteca.exception;

public class DuplicateUserByEmailException extends UserException {
	public DuplicateUserByEmailException() {
		super("Esiste già un utente registrato con questa email");
	}
}
