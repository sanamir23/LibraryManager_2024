package library;

import java.sql.Date;

public class ReferenceBook extends Book {
	private boolean isExtended;

	public ReferenceBook(String bookID, String title, String author, String ISBN, int publicationYear, String genre,
			double baseLoanFee) {
		super(bookID, title, author, ISBN, publicationYear, genre, baseLoanFee);
		this.isExtended = false; // for Reference book no extension or loaning is allowed
	}

	@Override
	public void loanBook(User user, Date start_date, Date return_date) {
		System.out.println("Reference books cannot be loaned. They are to be used for in-library reading only.");
	}

	@Override
	public double calculateLoanCost() {
		return 0.0; // loaning not allowed
	}

	@Override
	public void extendLoan(Date new_return_date) {
		System.out.println("Loan extension not applicable for reference books.");
	}
}