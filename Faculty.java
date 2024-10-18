package library;

public class Faculty extends User {
	private static final int bookLimit = 10;
	
    public Faculty(String userID, String name, String email, String phoneNumber, String address) {
        super(userID, name, email, phoneNumber, address);
    }

    @Override
    public boolean canBorrowBook() {
        return loanedBooks.size() < bookLimit;
    }
}
