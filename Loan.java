package library;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Loan {
	private Book book; // can be Novel, Reference Book, or Textbook
	private int loanID;
	private Date startDate;
	private Date returnDate;
	private static final int finePerDay = 5;

	private static int loanCounter = 1;

	public Loan(Book b, Date sDate, Date rDate) {
		loanID = loanCounter++;
		book = b;
		startDate = sDate;
		returnDate = rDate;
	}

	public double getLoanFee() {
		return book.calculateLoanCost();
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setReturnDate(Date r) {
		returnDate = r;
	}

	public int getFineRate() {
		return finePerDay;
	}

	public String getBookId() {
		return book.getBookId();
	}

	public int getLoanID() {
		return loanID;
	}
	
	public void setLoandID(int id) {
		loanID = id;
	}
	
	public void setLoanStatus(boolean s) {
		book.setLoaned(s);
	}

	public void extendLoan(Date new_return_date) {
		if (book instanceof Textbook) {
			setReturnDate(new_return_date);
		}
		book.extendLoan(new_return_date);
	}

	public void displayLoanInfo() {
		System.out.println("Loan ID: " + loanID);
		System.out.println("Book ID: " + book.getBookId());
		System.out.println("Book Title: " + book.getTitle());
		System.out.println("Start Date: " + formatDate(startDate));
		System.out.println("Return Date: " + formatDate(returnDate));
		System.out.println("Loan Fee: $" + getLoanFee());
		System.out.println("Fine Rate (per day): $" + finePerDay);
	}

	public String formatDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		} else {
			return "Not set";
		}
	}
}