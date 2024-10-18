package library;

import java.util.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Date;

public class LMS {

	private static List<Book> books = new ArrayList<>(); // AGGREGATION relation as books can exist without library
	private static List<User> users = new ArrayList<>(); // AGGREGATION relations as users exist independently of library
	private static PersistenceHandler p;
	private static Scanner scanner = new Scanner(System.in);
	private static boolean useDatabase = false;

	public static void main(String[] args) {
		displayMenu();
	}

	private static void chooseStorageOption() {
		while (true) {
			System.out.println("Choose storage method:");
			System.out.println("1. File Storage");
			System.out.println("2. Database Storage");

			int storageChoice;
			try {
				storageChoice = scanner.nextInt();
				scanner.nextLine(); // Clear the input buffer

				if (storageChoice == 1) {
					useDatabase = false;
					p = new FileStorageHandler();
					books = p.getBooks();
					users = p.getUsers();
					loadAllUsersLoans();
					System.out.println("You selected File Storage.");
					System.out.println("Book, User and Loan data has been successfully loaded.");
					break;
				} else if (storageChoice == 2) {
					useDatabase = true;
					p = new DbStorageHandler();
					books = p.getBooks();
					users = p.getUsers();
					loadAllUsersLoans();
					System.out.println("You selected Database Storage.");
					System.out.println("Book, User and Loan data has been successfully loaded.");
					break;
				} else {
					System.out.println("Invalid choice. Please enter 1 or 2.");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number (1 or 2).");
				scanner.nextLine(); // Clear the invalid input
			}
		}
	}
	
	private static void loadAllUsersLoans() {
	    for (User user : users) {
	        user.loadLoansForUser(p);
	    }
	}

	private static void displayMenu() {
		chooseStorageOption();

		while (true) {
			System.out.println("\nWelcome to the FAST Library Management System");
			System.out.println("Please choose your role:");
			System.out.println("1. Student");
			System.out.println("2. Faculty");
			System.out.println("3. Member");
			System.out.println("4. Exit");

			int choice;
			try {
				choice = scanner.nextInt();
				scanner.nextLine();

				switch (choice) {
				case 1 -> handleUser("Student");
				case 2 -> handleUser("Faculty");
				case 3 -> handleUser("Member");
				case 4 -> {
					System.out.println("Exiting the system. Goodbye!");
					return;
				}
				default -> System.out.println("Invalid choice. Please try again.");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number (1-4).");
				scanner.nextLine();
			}
		}
	}

	private static void handleUser(String userType) {
		User user = authenticateUser(userType);
		if (user == null) {
			System.out.println("User not found or incorrect role. Returning to main menu.");
			return;
		}

		while (true) {
			System.out.println("\nLibrary Menu for " + userType + ": " + user.getUserId());
			System.out.println("1. View available books");
			System.out.println("2. View registered users");
			System.out.println("3. Borrow a book");
			System.out.println("4. Return a book");
			System.out.println("5. Extend a loan");
			System.out.println("6. View current loans");
			System.out.println("7. Search a book");
			System.out.println("8. Pay loan fees");
			
			if (userType.equals("Faculty")) {
				System.out.println("9. Add a new book");
				System.out.println("10. Remove a book");
				System.out.println("11. Update a book");
				System.out.println("12. Add a user");
				System.out.println("13. Remove a user");
				System.out.println("14. Update a user");
			}
			System.out.println("15. Back to main menu");

			int choice;
			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // Clear the buffer

				switch (choice) {
				case 1 -> displayBooks();
				case 2 -> displayUsers();
				case 3 -> borrowBook(user);
				case 4 -> returnBook(user);
				case 5 -> extendLoan(user);
				case 6 -> displayUserLoans(user);
				case 7 -> searchBook();
				case 8 -> searchBook();
				case 9 -> {
					if (userType.equals("Faculty"))
						addBook();
					else
						System.out.println("Invalid choice.");
				}
				case 10 -> {
					if (userType.equals("Faculty"))
						removeBook();
					else
						System.out.println("Invalid choice.");
				}
				case 11 -> {
					if (userType.equals("Faculty"))
						updateBook();
					else
						System.out.println("Invalid choice.");
				}
				case 12 -> {
					if (userType.equals("Faculty"))
						addUser();
					else
						System.out.println("Invalid choice.");
				}
				case 13 -> {
					if (userType.equals("Faculty"))
						removeUser();
					else
						System.out.println("Invalid choice.");
				}
				case 14 -> {
					if (userType.equals("Faculty"))
						updateUser();
					else
						System.out.println("Invalid choice.");
				}
				case 15 -> {
					System.out.println("Returning to main menu.");
					return;
				}
				default -> System.out.println("Invalid choice. Please try again.");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				scanner.nextLine(); // Clear the invalid input
			}
		}
	}

	// checks if user exists by searching UserID
	private static User authenticateUser(String userType) {
		System.out.println("Enter your User ID:");
		String userId = scanner.nextLine().trim();
		for (User user : users) {
			if (user.getUserId().equalsIgnoreCase(userId)
					&& user.getClass().getSimpleName().equalsIgnoreCase(userType)) {
				return user;
			}
		}
		return null;
	}

	// displays all available books
	private static void displayBooks() {
		System.out.println("\nAvailable Books:");
		for (Book book : books) {
			if (!book.isLoaned()) {
				System.out.println(book.getBookId() + ": " + book.getTitle() + " by " + book.getAuthor());
			}
		}
	}
	
	private static void searchBook() {
		System.out.println("\nEnter Book Title:");
		String bookTitle = scanner.nextLine();
		for (Book book : books) {
			if (book.getTitle().equalsIgnoreCase(bookTitle)) {
				System.out.println("Book found!");
				book.displayBookDetails();
				return;
			}
		}
		System.out.println("No book matches the title you entered.");
	}

	// displays all current users' info
	private static void displayUsers() {
		System.out.println("\nCurrent Users:");
		for (User u : users) {
			System.out.println(u.getUserId());
			u.displayUserDetails();
			u.displayLoans();
		}
	}
	
	private static void updateBook() {
	    System.out.println("\nEnter Book ID to edit:");
	    String bookId = scanner.nextLine();
	    Book bookToUpdate = null;

	    // Find the book with the specified ID
	    for (Book book : books) {
	        if (book.getBookId().equalsIgnoreCase(bookId)) {
	            bookToUpdate = book;
	            break;
	        }
	    }

	    if (bookToUpdate == null) {
	        System.out.println("Book with ID: " + bookId + " does not exist.");
	        return;
	    }

	    int choice;
	    System.out.println("\nChoose book attribute to edit: ");
	    System.out.println("1. Title");
	    System.out.println("2. Author");
	    System.out.println("3. ISBN");
	    System.out.println("4. Publication Year");
	    System.out.println("5. Genre");
	    System.out.println("6. Loan Status");
	    System.out.println("7. Base Loan Fee");

	    try {
	        choice = scanner.nextInt();
	        scanner.nextLine(); // Clear buffer

	        String attributeChosen = "";
	        String updatedValue;

	        // Get updated value with appropriate validation
	        switch (choice) {
	            case 1 -> {
	                attributeChosen = "title";
	                System.out.println("Enter new title:");
	                updatedValue = scanner.nextLine();
	                bookToUpdate.setTitle(updatedValue);
	                p.updateBook(bookId, attributeChosen, updatedValue);
	            }
	            case 2 -> {
	                attributeChosen = "author";
	                System.out.println("Enter new author:");
	                updatedValue = scanner.nextLine();
	                bookToUpdate.setAuthor(updatedValue);
	                p.updateBook(bookId, attributeChosen, updatedValue);
	            }
	            case 3 -> {
	                attributeChosen = "isbn";
	                System.out.println("Enter new ISBN:");
	                updatedValue = scanner.nextLine();
	                bookToUpdate.setISBN(updatedValue);
	                p.updateBook(bookId, attributeChosen, updatedValue);
	            }
	            case 4 -> {
	                attributeChosen = "publication_year";
	                System.out.println("Enter new publication year:");
	                int year = scanner.nextInt();
	                if (year < 1000 || year > 9999) {
	                    System.out.println("Invalid year. Please enter a valid year.");
	                    return;
	                }
	                bookToUpdate.setPublicationYear(year);
	                p.updateBook(bookId, attributeChosen, Integer.toString(year));
	            }
	            case 5 -> {
	                attributeChosen = "genre";
	                System.out.println("Enter new genre:");
	                updatedValue = scanner.nextLine();
	                bookToUpdate.setGenre(updatedValue);
	                p.updateBook(bookId, attributeChosen, updatedValue);
	            }
	            case 6 -> {
	                attributeChosen = "loan_status";
	                System.out.println("Enter new loan status (true/false):");
	                boolean loanStatus = scanner.nextBoolean();
	                bookToUpdate.setLoanStatus(loanStatus);
	                p.updateBook(bookId, attributeChosen, Boolean.toString(loanStatus));
	            }
	            case 7 -> {
	                attributeChosen = "base_loan_fee";
	                System.out.println("Enter new base loan fee:");
	                double loanFee = scanner.nextDouble();
	                if (loanFee < 0) {
	                    System.out.println("Loan fee cannot be negative.");
	                    return;
	                }
	                bookToUpdate.setBaseLoanFee(loanFee);
	                p.updateBook(bookId, attributeChosen, Double.toString(loanFee));
	            }
	            default -> {
	                System.out.println("Invalid choice. Please try again.");
	                return;
	            }
	        }
	        
	        System.out.println("Book attribute '" + attributeChosen + "' updated successfully.");

	    } catch (InputMismatchException e) {
	        System.out.println("Invalid input. Please enter the correct data type.");
	        scanner.nextLine(); // Clear the invalid input
	    }
	}
	
	private static void updateUser() {
	    System.out.println("\nEnter User ID to edit:");
	    String userId = scanner.nextLine();
	    User userToUpdate = null;

	    // Find the book with the specified ID
	    for (User u : users) {
	        if (u.getUserId().equalsIgnoreCase(userId)) {
	            userToUpdate = u;
	            break;
	        }
	    }

	    if (userToUpdate == null) {
	        System.out.println("User with ID: " + userId + " does not exist.");
	        return;
	    }

	    int choice;
	    System.out.println("\nChoose user attribute to edit: ");
	    System.out.println("1. Name");
	    System.out.println("2. Email");
	    System.out.println("3. Phone Number");
	    System.out.println("4. Address");
	    System.out.println("5. Total Loan Fees");
	    try {
	        choice = scanner.nextInt();
	        scanner.nextLine(); // Clear buffer

	        String attributeChosen = "";
	        String updatedValue;

	        // Get updated value with appropriate validation
	        switch (choice) {
	            case 1 -> {
	                attributeChosen = "name";
	                System.out.println("Enter new name:");
	                updatedValue = scanner.nextLine();
	                userToUpdate.setName(updatedValue);
	                p.updateUser(userId, attributeChosen, updatedValue);
	            }
	            case 2 -> {
	                attributeChosen = "email";
	                System.out.println("Enter new email:");
	                updatedValue = scanner.nextLine();
	                userToUpdate.setEmail(updatedValue);
	                p.updateUser(userId, attributeChosen, updatedValue);
	            }
	            case 3 -> {
	                attributeChosen = "phone_number";
	                System.out.println("Enter new phone number:");
	                updatedValue = scanner.nextLine();
	                userToUpdate.setPhoneNumber(updatedValue);
	                p.updateUser(userId, attributeChosen, updatedValue);
	            }
	            case 4 -> {
	                attributeChosen = "address";
	                System.out.println("Enter new address:");
	                updatedValue = scanner.nextLine();
	                userToUpdate.setAddress(updatedValue);
	                p.updateUser(userId, attributeChosen, updatedValue);
	            }
	            case 5 -> {
	            	attributeChosen = "total_loan_fees";
	                System.out.println("Enter new total loan fees:");
	                double loanFee = scanner.nextDouble();
	                if (loanFee < 0) {
	                    System.out.println("Loan fee cannot be negative.");
	                    return;
	                }
	                userToUpdate.setTotalLoanFees(loanFee);
	                p.updateUser(userId, attributeChosen, Double.toString(loanFee));
	            }
	            default -> {
	                System.out.println("Invalid choice. Please try again.");
	                return;
	            }
	        }
	        
	        System.out.println("User attribute '" + attributeChosen + "' updated successfully.");

	    } catch (InputMismatchException e) {
	        System.out.println("Invalid input. Please enter the correct data type.");
	        scanner.nextLine(); // Clear the invalid input
	    }
	}
	
	private static void displayUserLoans(User user) {
		System.out.println("\nCurrent Loans for User: " + user.getUserId());
		for (User u : users) {
			if (u.getUserId().equalsIgnoreCase(user.getUserId())) {
				u.displayLoans();
			}
		}
	}
	

	// adds book to user's list of loaned books and sets isLoaned to true
	private static void borrowBook(User user) {
	    if (!user.canBorrowBook()) {
	        System.out.println("Loaning no longer allowed for User: " + user.getUserId());
	        return;
	    }

	    System.out.println("Enter Book ID to borrow:");
	    String bookId = scanner.nextLine();

	    // Check if the book exists and is available for loan
	    Book bookToBorrow = null;
	    for (Book book : books) {
	        if (book.getBookId().equalsIgnoreCase(bookId) && !book.isLoaned()) {
	            bookToBorrow = book;
	            break;
	        }
	    }

	    if (bookToBorrow == null) {
	        System.out.println("Book not found or already loaned.");
	        return;
	    }

	    Date s_date = null; // Start Date
	    Date r_date = null; // Return Date
	    LocalDate t = LocalDate.now();
	    Date today = Date.valueOf(t); // Current date

	    // Input for Start Date
	    System.out.println("Enter start date of loan:");
	    s_date = getDateFromInput();
	    if (s_date == null || s_date.before(today)) {
	        System.out.println("Invalid start date. It must be today or a future date.");
	        return;
	    }

	    // Input for Return Date
	    System.out.println("Enter return date of loan:");
	    r_date = getDateFromInput();
	    if (r_date == null || r_date.before(s_date)) {
	        System.out.println("Invalid return date. It must be after the start date.");
	        return;
	    }

	    // Ensure loan period is at least 30 days
	    long daysBetween = (r_date.getTime() - s_date.getTime()) / (1000 * 60 * 60 * 24);
	    if (daysBetween < 30) {
	        System.out.println("Loan period must be at least 30 days.");
	        return;
	    }

	    // Proceed with loaning the book
	    bookToBorrow.loanBook(user, s_date, r_date);
	    p.updateBook(bookId, "loan_status", "true");
	    Loan loan = user.getLoan(bookId);
	    p.addLoan(loan, user.getUserId());
	    String loanFees = Double.toString(user.getLoanFees());
	    p.updateUser(user.getUserId(), "total_loan_fees", loanFees);
	    System.out.println(loanFees);
	    System.out.println("Book successfully loaned.");
	}

	// Helper method to get a date from input
	private static Date getDateFromInput() {
	    try {
	        System.out.println("Enter day (1-31):");
	        int day = Integer.parseInt(scanner.nextLine());

	        System.out.println("Enter month (1-12):");
	        int month = Integer.parseInt(scanner.nextLine());

	        System.out.println("Enter year:");
	        int year = Integer.parseInt(scanner.nextLine());

	        // Create Date object using java.sql.Date
	        return Date.valueOf(year + "-" + month + "-" + day);
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid input. Please enter valid numbers.");
	    } catch (IllegalArgumentException e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	    return null; // Return null if input is invalid
	}


	// checks if user has loaned this book
	// for novel: returns base loan fee
	// for textbook: returns base loan fee + duration based charges
	// for reference book: returns 0
	private static void returnBook(User user) {
	    if (user.ifNotLoanedBooks()) {
	        System.out.println("Error returning loan! User " + user.getUserId() + " has not loaned any books yet.");
	        return;
	    }

	    System.out.println("Enter Book ID to return:");
	    String bookId = scanner.nextLine();

	    // Check if the book ID is consistent with the user's loans
	    if (!user.hasLoanedBook(bookId)) {
	        System.out.println("This book ID does not correspond to any book loaned by you.");
	        return;
	    }

	    // Retrieve the existing loan details to check the previous return date
	    Loan existingLoan = user.getLoan(bookId);
	    Date startDate = existingLoan.getStartDate(); // Assuming the Loan class has this method
	    
	    System.out.println("Enter return date of loan:");
	    Date ar_date = getDateFromInput(); // Use the helper method for date input
	    if (ar_date == null) {
	        System.out.println("Invalid return date. Operation canceled.");
	        return;
	    }

	    // Check if the new return date is after the previous return date
	    if (!ar_date.after(startDate)) {
	        System.out.println("Invalid return date. It must be after the start date");
	        return;
	    }

	    // Call returnBook method on user with the entered Book ID and return date
	    int loanId = user.getLoanIdforBook(bookId);
	    user.returnBook(bookId, ar_date);
	    p.updateBook(bookId, "loan_status", "false");
	    p.deleteLoan(loanId);
	    String loanFees = Double.toString(user.getLoanFees());
	    p.updateUser(user.getUserId(), "total_loan_fees", loanFees);
	    System.out.println(loanFees);
	    System.out.println("Book returned successfully.");
	}

	// exhibits polymorphism
	// for Novel extension not allowed
	// for ReferenceBook extension not allowed
	// for Textbook extension applicable
	private static void extendLoan(User user) {	
		if (user.ifNotLoanedBooks()) {
	        System.out.println("Error returning loan! User " + user.getUserId() + " has not loaned any books yet.");
	        return;
	    }

	    System.out.println("Enter Book ID to extend:");
	    String bookId = scanner.nextLine();

	    // Check if the book ID is consistent with the user's loans
	    if (!user.hasLoanedBook(bookId)) {
	        System.out.println("This book ID does not correspond to any book loaned by you.");
	        return;
	    }
		
		System.out.println("Enter new return date of extended loan:");
		Date ext_date = getDateFromInput();
		
		if (ext_date == null) {
	        System.out.println("Invalid return date. Operation canceled.");
	        return;
	    }
		
		Loan existingLoan = user.getLoan(bookId);
		Date actualReturnDate = existingLoan.getStartDate();
		
		if (!ext_date.after(actualReturnDate)) {
	        System.out.println("Invalid return date. It must be after the previously entered return date");
	        return;
	    }

		// Call extendUserLoan method on user with the entered Book ID and extended date
		user.extendUserLoan(bookId, ext_date);
		Loan l = user.getLoan(bookId);
		p.updateLoan(l);
	    String loanFees = Double.toString(user.getLoanFees());
	    p.updateUser(user.getUserId(), "total_loan_fees", loanFees);
	}

	private static boolean isIdDuplicate(String Id, String type) {
		// equalIgnoreCase for case insensitive search
		if (type.equalsIgnoreCase("book")) {
			for (Book book : books) {
				if (book.getBookId().equalsIgnoreCase(Id)) {
					return true;
				}
			}
		}

		if (type.equalsIgnoreCase("user")) {
			for (User user : users) {
				if (user.getUserId().equalsIgnoreCase(Id)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isIsbnDuplicate(String isbn) {
		return books.stream().anyMatch(book -> book.getIsbn().equalsIgnoreCase(isbn));
	}

	// only allowed for faculty
	private static void addBook() {
		System.out.println("Enter Book Type (1 for Textbook, 2 for Novel, 3 for Reference Book):");
		int type;
		while (true) {
			if (scanner.hasNextInt()) {
				type = scanner.nextInt();
				if (type >= 1 && type <= 3)
					break;
			}
			System.out.println("Invalid input. Please enter 1, 2, or 3 for the Book Type:");
			scanner.nextLine(); // clearing the invalid input
		}
		scanner.nextLine();

		System.out.println("Enter Book ID:");
		String bookId = scanner.nextLine().trim();

		if (isIdDuplicate(bookId, "book")) {
			System.out.println("Book with the same ID already exists. Book not added.");
			return;
		}

		System.out.println("Enter Title:");
		String title = scanner.nextLine();

		System.out.println("Enter Author:");
		String author = scanner.nextLine();

		System.out.println("Enter ISBN:");
		String isbn = scanner.nextLine();

		if (isIsbnDuplicate(isbn)) {
			System.out.println("Book with the same ISBN already exists. Book not added.");
			return;
		}

		System.out.println("Enter Publication Year:");
		int year = scanner.nextInt();
		scanner.nextLine();

		System.out.println("Enter Genre:");
		String genre = scanner.nextLine();

		System.out.println("Enter Base Loan Fee:");
		double baseFee = scanner.nextDouble();
		scanner.nextLine();

		switch (type) {
		case 1 -> {
			books.add(new Textbook(bookId, title, author, isbn, year, genre, baseFee));
			p.addBook(new Textbook(bookId, title, author, isbn, year, genre, baseFee));
		}
		case 2 -> {
			books.add(new Novel(bookId, title, author, isbn, year, genre, baseFee));
			p.addBook(new Novel(bookId, title, author, isbn, year, genre, baseFee));
		}
		case 3 -> {
			books.add(new ReferenceBook(bookId, title, author, isbn, year, genre, baseFee));
			p.addBook(new ReferenceBook(bookId, title, author, isbn, year, genre, baseFee));
		}
		default -> System.out.println("Invalid book type.");
		}

		System.out.println("Book added successfully.");
	}

	// only allowed for faculty
	private static void addUser() {
		System.out.println("Enter User Type (1 for Student, 2 for Faculty, 3 for Public Member):");
		int type;
		while (true) {
			if (scanner.hasNextInt()) {
				type = scanner.nextInt();
				if (type >= 1 && type <= 3)
					break;
			}
			System.out.println("Invalid input. Please enter 1, 2, or 3 for the User Type:");
			scanner.nextLine(); // clearing the invalid input
		}

		scanner.nextLine();

		System.out.println("Enter User ID:");
		String userId = scanner.nextLine();

		if (isIdDuplicate(userId, "user")) {
			System.out.println("User with the same ID already exists. User not added.");
			return;
		}

		System.out.println("Enter Name:");
		String name = scanner.nextLine();

		System.out.println("Enter Email:");
		String email = scanner.nextLine();

		System.out.println("Enter Phone Number:");
		String pno = scanner.nextLine();

		System.out.println("Enter Address:");
		String address = scanner.nextLine();

		switch (type) {
		case 1 -> {
			users.add(new Student(userId, name, email, pno, address));
			p.addUser(new Student(userId, name, email, pno, address));
		}
		case 2 -> {
			users.add(new Faculty(userId, name, email, pno, address));
			p.addUser(new Faculty(userId, name, email, pno, address));
		}
		case 3 -> {
			users.add(new Member(userId, name, email, pno, address));
			p.addUser(new Member(userId, name, email, pno, address));
		}
		default -> System.out.println("Invalid user type.");
		}
		System.out.println("User added successfully.");
	}

	// only allowed for faculty
	private static void removeBook() {
		System.out.println("Enter Book ID to remove:");
		String bookId = scanner.nextLine();

		// search book by ID
		Optional<Book> bookToRemove = books.stream().filter(book -> book.getBookId().equalsIgnoreCase(bookId)).findFirst();

		// check if book exists
		if (bookToRemove.isPresent()) {
			Book book = bookToRemove.get();

			// check if the book is borrowed by an user
			if (!book.isLoaned()) {
				books.remove(book); // remove the book if not loaned
				p.deleteBook(bookId);
				System.out.println("Book removed successfully.");
			} else {
				System.out.println("Cannot remove book. It is currently loaned.");
			}
		} else {
			System.out.println("Book not found.");
		}
	}

	// only allowed for faculty
	private static void removeUser() {
		System.out.println("Enter User ID to remove:");
		String userId = scanner.nextLine();

		// search user by ID
		Optional<User> userToRemove = users.stream().filter(user -> user.getUserId().equalsIgnoreCase(userId)).findFirst();

		// check if user exists
		if (userToRemove.isPresent()) {
			User user = userToRemove.get();

			// check if the user has any loaned books
			if (user.ifNotLoanedBooks()) {
				users.remove(user); // remove the user if no loaned books
				p.deleteUser(userId);
				System.out.println("User removed successfully.");
			} else {
				System.out.println("Cannot remove user. They have loaned books.");
			}
		} else {
			System.out.println("User not found.");
		}
	}
}
