package it.unisa.diem.ingsoft.biblioteca.exception;

public class UnknownLoanException extends LoanException {
	public UnknownLoanException() {
		super("Non esiste alcun prestito tra utente e libro specificati");
	}
}
