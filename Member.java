package library;

public class Member extends User {
	private static final int bookLimit = 3; 
	
    public Member(String userID, String name, String email, String phoneNumber, String address) {
        super(userID, name, email, phoneNumber, address);
    }

    @Override
    public boolean canBorrowBook() {
        return loanedBooks.size() < bookLimit;
    }
}
