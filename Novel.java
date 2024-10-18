package library;

import java.sql.Date;

public class Novel extends Book {
	private boolean isExtended;

	public Novel(String bookID, String title, String author, String ISBN, int publicationYear, String genre,
			double baseLoanFee) {
		super(bookID, title, author, ISBN, publicationYear, genre, baseLoanFee);
		this.isExtended = false; // for Novel no extension is allowed
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
		return baseLoanFee; // flat rate, extension fee not added as EXTENSION OF LOAN NOT ALLOWED
	}

	@Override
	public void extendLoan(Date new_return_date) {
		System.out.println("Loan extension not applicable on novels.");
	}
}