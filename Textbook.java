package library;

import java.sql.Date;

public class Textbook extends Book {
	private boolean isExtended;
	private static final int extensionFee = 5; // standard 5 dollar extension fee

	public Textbook(String bookID, String title, String author, String ISBN, int publicationYear, String genre,
			double baseLoanFee) {
		super(bookID, title, author, ISBN, publicationYear, genre, baseLoanFee);
		this.isExtended = false; // for textbook only one extension is allowed
	}

	@Override
	public void loanBook(User user, Date start_date, Date return_date) {
		if (!loanStatus && user.canBorrowBook()) {
			loanStatus = true;
			Loan l = new Loan(this, start_date, return_date);
			user.addLoanedBook(l);
		}

		else {
			System.out.println("Book cannot be loaned as of now.");
		}
	}

	@Override
	public double calculateLoanCost() {

		if (isExtended != true) {
			return baseLoanFee;
		}

		return (baseLoanFee + extensionFee);

	}

	@Override
	public void extendLoan(Date new_return_date) {
		if (!isExtended) {
			isExtended = true;
			System.out.println("Loan extended successfully!");
			System.out.println("The extended return date is: " + new_return_date);
		} else {
			System.out.println("Loan cannot be extended further.");
		}
	}
}