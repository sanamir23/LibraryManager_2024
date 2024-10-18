package library;

import java.util.List;

public abstract class PersistenceHandler {
	// Abstract methods for user operations
    public abstract void addUser(User user);
    public abstract void deleteUser(String userId);
    public abstract void updateUser(String userId, String attribute, String updatedValue);
    public abstract void displayUser(String userId);

    // Abstract methods for book operations
    public abstract void addBook(Book book);
    public abstract void deleteBook(String bookId);
    public abstract void updateBook(String bookId, String attribute, String updatedValue);
    public abstract void displayBook(String bookId);
    
    public abstract void addLoan(Loan loan, String userId);
    public abstract void deleteLoan(int loanId);
    public abstract void updateLoan(Loan updatedLoan);
    public abstract void displayUserLoans(String userId);
    
    public abstract List<Book> getBooks();
    public abstract List<User> getUsers();
    public abstract List<Loan> getUserLoans(String userId);
}
