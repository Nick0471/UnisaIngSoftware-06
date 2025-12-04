package it.unisa.diem.ingsoft.biblioteca.exception;

public class LoanAlreadyRegisteredException extends LoanException {
	public LoanAlreadyRegisteredException() {
		super("Esiste già un prestito per l'utente ed il libro specificati");
	}
}
