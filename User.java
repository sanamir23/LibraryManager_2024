package library;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class User {
	protected String userID;
	protected String name;
	protected String email;
	//PersistenceHandler p;
	protected List<Loan> loanedBooks; // COMPOSITION relation implemented as loaned books cannot exist without the
										// existence of respective user
	protected double totalLoanFees;
	protected String phoneNumber;
	protected String address;

	public User(String userID, String name, String email, String phoneNumber, String address) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.loanedBooks = new ArrayList<>();
		this.totalLoanFees = 0.0;
	}

	public abstract boolean canBorrowBook();

	public void addLoanedBook(Loan loanedBook) {
		loanedBooks.add(loanedBook);
	}

	public void removeLoanedBook(Loan loanedBook) {
		loanedBooks.remove(loanedBook);
	}

	public boolean checkIfLoaned(String bookId) {
		for (Loan l : loanedBooks) {
			if (l.getBookId().equals(bookId)) {
				return true;
			}
		}
		System.out.println("Book not found in your loaned list.");
		return false;
	}

	public double getLoanFees() {
		double total = 0.0;
		for (Loan l : loanedBooks) {
			total += l.getLoanFee();
		}
		return total;
	}

	public void displayLoans() {
		if (!loanedBooks.isEmpty()) {
			for (Loan l : loanedBooks) {
				l.displayLoanInfo();
			}
		} else {
			System.out.println("No books loaned as of now!");
		}
	}

	public boolean ifNotLoanedBooks() {
		return loanedBooks.isEmpty();
	}

	public void registerUser() {
		System.out.println("User registered successfully!");
	}

	public String getUserId() {
		return userID;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}
	
	public boolean hasLoanedBook(String bookId) {
	    for (Loan loan : loanedBooks) { // Assuming loanedBooks is the list of loans the user has
	        if (loan.getBookId().equalsIgnoreCase(bookId)) {
	            return true;
	        }
	    }
	    return false;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public Loan getLoan(String bookId) {
		for (Loan l: loanedBooks) {
			if (l.getBookId().equalsIgnoreCase(bookId)) {
				return l;
			}
		}
		return null;
	}
	
	public void loadLoansForUser(PersistenceHandler p) {
	    List<Loan> loansFromFile = p.getUserLoans(this.userID);

	    if (loansFromFile != null) {
	        this.loanedBooks.clear();  // Clear any existing data to avoid duplication
	        this.loanedBooks.addAll(loansFromFile);  // Populate from file
	        System.out.println("Loaded " + loansFromFile.size() + " loans for user: " + this.userID);
	    } else {
	        System.out.println("No loans found for user: " + this.userID);
	    }
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setTotalLoanFees(double totalLoanFees) {
		this.totalLoanFees = totalLoanFees;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getLoanIdforBook(String bookId) {
		for (Loan l: loanedBooks) {
			if (l.getBookId().equalsIgnoreCase(bookId)) {
				return l.getLoanID();
			}
		}
		return -1;
	}

	public void displayUserDetails() {
		System.out.println("Name: " + name);
		System.out.println("Email: " + email);
		System.out.println("Phone: " + phoneNumber);
		System.out.println("Address: " + address);
		System.out.println("Total Loan Fees: $" + getLoanFees());
	}

	public void returnBook(String bookId, Date actual_return_date) {
		Optional<Loan> loanToReturn = loanedBooks.stream().filter(loan -> loan.getBookId().equalsIgnoreCase(bookId)).findFirst();

		// Check if loan exists
		if (loanToReturn.isPresent()) {
			Loan l = loanToReturn.get();
			Date return_date = l.getReturnDate();
			double loanBill = 0.0;

			// Check if the actual return date is before or on the return date
			if (!return_date.before(actual_return_date)) {
				loanBill = l.getLoanFee();
			} else {
				// Calculate days after the return date
				long diffInMillies = actual_return_date.getTime() - return_date.getTime();
				long daysAfter = diffInMillies / (1000 * 60 * 60 * 24); // Convert milliseconds to days
				int fineRate = l.getFineRate();
				loanBill = l.getLoanFee();
				loanBill += (daysAfter * fineRate);
			}

			this.removeLoanedBook(l);
			l.setLoanStatus(false);
			//this.updateLoanFees();
			System.out.println("Loan returned successfully.");
			System.out.println("Bill to be paid is: $" + loanBill);
		} else {
			System.out.println("Loan not found.");
		}
	}

	public void extendUserLoan(String bookId, Date new_return_date) {
		for (Loan l : loanedBooks) {
			if (l.getBookId().equals(bookId)) {
				l.extendLoan(new_return_date);
				return;
			}
		}
		System.out.println("Book not found in your loaned list.");
	}

}
