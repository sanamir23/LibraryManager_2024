package library;

import java.sql.Date;

public abstract class Book implements LoanableItem {
	protected String bookID;
	protected String title;
	protected String author;
	protected String ISBN;
	protected int publicationYear;
	protected String genre;
	protected boolean loanStatus; // indicates whether book available for loan or not
	protected double baseLoanFee;

	public Book(String bookID, String title, String author, String ISBN, int publicationYear, String genre,
			double baseLoanFee) {
		this.bookID = bookID;
		this.title = title;
		this.author = author;
		this.ISBN = ISBN;
		this.publicationYear = publicationYear;
		this.genre = genre;
		this.loanStatus = false; // book available for loan at the beginning
		this.baseLoanFee = baseLoanFee;
	}

	public void setBookID(String bookID) {
		this.bookID = bookID;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public void setBaseLoanFee(double baseLoanFee) {
		this.baseLoanFee = baseLoanFee;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void setLoanStatus(boolean loanStatus) {
		this.loanStatus = loanStatus;
	}

	public void displayBookDetails() {
		System.out.println("Title: " + title);
		System.out.println("Author: " + author);
		System.out.println("ISBN: " + ISBN);
		System.out.println("Publication Year: " + publicationYear);
		System.out.println("Genre: " + genre);
		System.out.println("Loan Status: " + (loanStatus ? "Loaned" : "Available"));
		System.out.println("Base Loan Fee: $" + baseLoanFee);
	}

	public String getBookId() {
		return this.bookID;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getIsbn() {
		return this.ISBN;
	}

	public String getGenre() {
		return this.genre;
	}

	public int getPublicationYear() {
		return this.publicationYear;
	}

	public void setLoaned(boolean status) {
		loanStatus = status;
	}

	public double getLoanFee() {
		return baseLoanFee;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isLoaned() {
		return loanStatus;
	}

	public String getLoanStatus() {
		if (loanStatus) {
			return "true";
		} else {
			return "false";
		}
	}

	public abstract void loanBook(User user, Date start_date, Date return_date);

	public abstract double calculateLoanCost();

	public abstract void extendLoan(Date new_return_date);
}
