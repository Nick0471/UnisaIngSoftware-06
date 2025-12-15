/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

public class ServiceRepository {
    private final AuthService authService;
    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public ServiceRepository(AuthService authService, UserService userService,
                             BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.authService = authService;
    }

    public AuthService getAuthService() {
        return this.authService;
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
