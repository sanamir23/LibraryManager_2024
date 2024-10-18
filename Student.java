package library;

public class Student extends User {
	private static final int bookLimit = 5;
	
    public Student(String userID, String name, String email, String phoneNumber, String address) {
        super(userID, name, email, phoneNumber, address);
    }

    @Override
    public boolean canBorrowBook() {
        return loanedBooks.size() < bookLimit;
    }
}
