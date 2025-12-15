/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

public class ServiceRepository {
    private final AuthService passwordService;
    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public ServiceRepository(AuthService passwordService, UserService userService,
                             BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.passwordService = passwordService;
    }

    public AuthService getPasswordService() {
        return this.passwordService;
    }

    public BookService getBookService() {
        return this.bookService;
    }

    public LoanService getLoanService() {
        return this.loanService;
    }

    public UserService getUserService() {
        return this.userService;
    }
}
