package library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class FileStorageHandler extends PersistenceHandler {
	private static final String user_file = "C:\\Users\\Sana Mir\\eclipse-workspace\\java_coding_task\\src\\library\\LibraryUsers.txt";
	private static final String book_file = "C:\\Users\\Sana Mir\\eclipse-workspace\\java_coding_task\\src\\library\\LibraryBooks.txt";
	private static final String loan_file = "C:\\Users\\Sana Mir\\eclipse-workspace\\java_coding_task\\src\\library\\UserLoans.txt";

	public void displayFileContents(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			System.out.println("Error reading file: " + filePath);
			e.printStackTrace();
		}
	}

	public List<Book> getBooks() {
	    List<Book> books = new ArrayList<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(book_file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] details = line.split(",");

	            Book book = null;

	            if (details[8].trim().equalsIgnoreCase("Novel")) {
	                book = new Novel(
	                    details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                    Integer.parseInt(details[4].trim()), details[5].trim(), 
	                    Double.parseDouble(details[6].trim())
	                );
	            } else if (details[8].trim().equalsIgnoreCase("Textbook")) {
	                book = new Textbook(
	                    details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                    Integer.parseInt(details[4].trim()), details[5].trim(), 
	                    Double.parseDouble(details[6].trim())
	                );
	            } else if (details[8].trim().equalsIgnoreCase("Reference Book")) {
	                book = new ReferenceBook(
	                    details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                    Integer.parseInt(details[4].trim()), details[5].trim(), 
	                    Double.parseDouble(details[6].trim())
	                );
	            } else {
	                System.out.println("Warning: Invalid book type '" + details[8].trim() + "' for book ID: " + details[0].trim());
	            }

	            if (book != null) {
	                books.add(book);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading file: " + book_file);
	        e.printStackTrace();
	    }
	    return books;
	}

	public List<User> getUsers() {
	    List<User> users = new ArrayList<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(user_file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] details = line.split(",");

	            User user = null;

	            if (details[6].trim().equalsIgnoreCase("Student")) {
	                user = new Student(
	                    details[0].trim(), details[1].trim(), details[2].trim(), 
	                    details[4].trim(), details[5].trim()
	                );
	            } else if (details[6].trim().equalsIgnoreCase("Faculty")) {
	                user = new Faculty(
	                    details[0].trim(), details[1].trim(), details[2].trim(), 
	                    details[4].trim(), details[5].trim()
	                );
	            } else if (details[6].trim().equalsIgnoreCase("Member")) {
	                user = new Member(
	                    details[0].trim(), details[1].trim(), details[2].trim(), 
	                    details[4].trim(), details[5].trim()
	                );
	            } else {
	                System.out.println("Warning: Invalid user type '" + details[6].trim() + "' for user ID: " + details[0].trim());
	            }

	            if (user != null) {
	                users.add(user);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading file: " + user_file);
	        e.printStackTrace();
	    }
	    return users;
	}

    
	public boolean userExists(String userId) {
		// Step 1: Read the users from the file
		try (BufferedReader br = new BufferedReader(new FileReader(user_file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] userDetails = line.split(",");
				if (userDetails[0].equalsIgnoreCase(userId)) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			System.out.println("Error reading file: " + user_file);
			e.printStackTrace();
			return false;
		}
	}

	public boolean bookExists(String bookId) {
		// Step 1: Read the books from the file
		try (BufferedReader br = new BufferedReader(new FileReader(book_file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] bookDetails = line.split(",");
				if (bookDetails[0].equalsIgnoreCase(bookId)) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			System.out.println("Error reading file: " + book_file);
			e.printStackTrace();
			return false;
		}
	}

	public boolean loanExists(int loanId) {
		try (BufferedReader br = new BufferedReader(new FileReader(loan_file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] loanDetails = line.split(","); // No space after comma for better parsing
				if (loanDetails[0].equals(Integer.toString(loanId))) {
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading file: " + loan_file);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void displayUserLoans(String userId) {
	    boolean found = false;
	    
	    List<String> loans = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(loan_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loans.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + loan_file);
            e.printStackTrace();
        }

	    System.out.println("Loans for User ID: " + userId);
	    for (String loan : loans) {
	        String[] details = loan.split(",");
	        if (details[3].trim().equalsIgnoreCase(userId)) {
	            System.out.println("Loan ID: " + details[0] +
	                               ", Start Date: " + details[1] +
	                               ", Return Date: " + details[2] +
	                               ", Book ID: " + details[4]);
	            found = true;
	        }
	    }
	    if (!found) {
	        System.out.println("No loans found for User ID: " + userId);
	    }
	}
	
	public void displayBook(String bookId) {
	    boolean found = false;
	    
	    List<String> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(book_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                books.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + book_file);
            e.printStackTrace();
        }
        
        for (String book : books) {
            String[] details = book.split(",");
            if (details[0].trim().equals(bookId)) {  // Check if book ID matches
                System.out.println("Book ID: " + details[0] +
                                   ", Title: " + details[1] +
                                   ", Author: " + details[2] +
                                   ", ISBN: " + details[3] +
                                   ", Year: " + details[4] +
                                   ", Genre: " + details[5] +
                                   ", Loan Price: $" + details[6] +
                                   ", Type: " + details[7]);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No book found with ID: " + bookId);
        }
	}
	
	public void displayUser(String userId) {
	    boolean found = false;
	    
	    List<String> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(user_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + user_file);
            e.printStackTrace();
        }
        
        for (String user : users) {
            String[] details = user.split(",");
            if (details[0].trim().equals(userId)) {  // Check if user ID matches
                System.out.println("User ID: " + details[0] +
                                   ", Name: " + details[1] +
                                   ", Email: " + details[2] +
                                   ", Phone Number: " + details[3] +
                                   ", Address: " + details[4] +
                                   ", Total Loan Fees: $" + details[5] +
                                   ", Role: " + details[6]);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No user found with ID: " + userId);
        }
	}
	
	public Book getBook(String bookId) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(book_file))) {
	        String line;

	        while ((line = reader.readLine()) != null) {
	            String[] details = line.split(",");

	            // Check if the book ID matches
	            if (details[0].trim().equalsIgnoreCase(bookId)) {
	                Book book = null;

	                // Create appropriate Book object based on type
	                switch (details[8].trim().toLowerCase()) {
	                    case "novel" -> book = new Novel(
	                        details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                        Integer.parseInt(details[4].trim()), details[5].trim(), 
	                        Double.parseDouble(details[6].trim())
	                    );
	                    case "textbook" -> book = new Textbook(
	                        details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                        Integer.parseInt(details[4].trim()), details[5].trim(), 
	                        Double.parseDouble(details[6].trim())
	                    );
	                    case "reference book" -> book = new ReferenceBook(
	                        details[0].trim(), details[1].trim(), details[2].trim(), details[3].trim(), 
	                        Integer.parseInt(details[4].trim()), details[5].trim(), 
	                        Double.parseDouble(details[6].trim())
	                    );
	                    default -> System.out.println("Warning: Invalid book type '" + details[8].trim() + "' for book ID: " + details[0].trim());
	                }

	                // Return the found book
	                if (book != null) {
	                    return book;
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading file: " + book_file);
	        e.printStackTrace();
	    }

	    System.out.println("Book with ID '" + bookId + "' not found.");
	    return null;  // Return null if no matching book was found
	}
	
	public List<Loan> getUserLoans(String userId) {
	    List<Loan> loans = new ArrayList<>();

	    try (BufferedReader reader = new BufferedReader(new FileReader(loan_file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(",");  
	            if (parts[3].equals(userId)) {  // Assuming second field is userId
	            	int loanId = Integer.parseInt(parts[0]);
	                String bookId = parts[4];
	                Date startDate = Date.valueOf(parts[1]);
	                Date returnDate = Date.valueOf(parts[2]);

	                // Fetch the book object using its ID
	                Book book = getBook(bookId);
	                if (book != null) {
	                    Loan loan = new Loan(book, startDate, returnDate);
	                    loan.setLoandID(loanId);
	                    loans.add(loan);
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading loans file: " + e.getMessage());
	    }

	    return loans;
	}

	// Add a new loan
	public void addLoan(Loan loan, String userId) {
		if (!loanExists(loan.getLoanID())) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(loan_file, true))) {
				String loanData = loan.getLoanID() + "," + loan.formatDate(loan.getStartDate()) + ","
						+ loan.formatDate(loan.getReturnDate()) + "," + userId + "," + loan.getBookId();
				writer.write(loanData);
				writer.newLine();
				System.out.println("Loan with ID: " + loan.getLoanID() + " added successfully.");
			} catch (IOException e) {
				System.out.println("Error while writing loan data to file.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Loan with ID: " + loan.getLoanID() + " already exists.");
		}
	}

	// Delete an existing loan by ID
	public void deleteLoan(int loanId) {
		if (loanExists(loanId)) {
			List<String> loans = new ArrayList<>();

			// Read and filter out the loan to be deleted
			try (BufferedReader br = new BufferedReader(new FileReader(loan_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] loanDetails = line.split(",");
					if (!loanDetails[0].equals(Integer.toString(loanId))) {
						loans.add(line); // Add all other loans to the list
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + loan_file);
				e.printStackTrace();
			}

			// Write the filtered list back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(loan_file))) {
				for (String loan : loans) {
					bw.write(loan);
					bw.newLine();
				}
				System.out.println("Loan with ID: " + loanId + " deleted successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + loan_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("Loan with ID: " + loanId + " does not exist.");
		}
	}

	// Update loan information
	public void updateLoan(Loan updatedLoan) {
		if (loanExists(updatedLoan.getLoanID())) {
			List<String> loans = new ArrayList<>();

			// Read the file and update the matching loan entry
			try (BufferedReader br = new BufferedReader(new FileReader(loan_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] loanDetails = line.split(",");
					if (loanDetails[0].equals(Integer.toString(updatedLoan.getLoanID()))) {
						// Create the updated loan entry
						String updatedLine = updatedLoan.getLoanID() + ","
								+ updatedLoan.formatDate(updatedLoan.getStartDate()) + ","
								+ updatedLoan.formatDate(updatedLoan.getReturnDate()) + "," + loanDetails[3] + "," + // Retain
																														// user
																														// ID
								updatedLoan.getBookId();
						loans.add(updatedLine);
					} else {
						loans.add(line); // Add unchanged lines
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + loan_file);
				e.printStackTrace();
			}

			// Write the updated loans back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(loan_file))) {
				for (String loan : loans) {
					bw.write(loan);
					bw.newLine();
				}
				System.out.println("Loan with ID: " + updatedLoan.getLoanID() + " updated successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + loan_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("Loan with ID: " + updatedLoan.getLoanID() + " does not exist.");
		}
	}

	public void addUser(User user) {
		if (!userExists(user.getUserId())) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(user_file, true))) {
				bw.write(userToString(user));
				bw.newLine();
				System.out.println("User added to file.");

			} catch (IOException e) {
				System.out.println("Error reading file: " + user_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("User with ID: " + user.getUserId() + " already exists.");
		}
	}

	public void deleteUser(String userId) {
		if (userExists(userId)) {
			List<String> users = new ArrayList<>();

			// Step 1: Read the users from the file
			try (BufferedReader br = new BufferedReader(new FileReader(user_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] userDetails = line.split(",");
					if (!userDetails[0].equalsIgnoreCase(userId)) {
						users.add(line);
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + user_file);
				e.printStackTrace();
			}

			// Step 2: Write the updated list of users back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(user_file))) {
				for (String user : users) {
					bw.write(user);
					bw.newLine();
				}
				System.out.println("User with ID: " + userId + " deleted successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + user_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("User with ID: " + userId + " does not exist.");
		}
	}

	public void updateUser(String userId, String attribute, String updatedValue) {
		if (userExists(userId)) {
			List<String> users = new ArrayList<>();

			// Step 1: Read the users from the file
			try (BufferedReader br = new BufferedReader(new FileReader(user_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] userDetails = line.split(",");

					// Step 2: Check if the current line corresponds to the user we're updating
					if (userDetails[0].equalsIgnoreCase(userId)) {
						// Step 3: Update the specified attribute
						switch (attribute.toLowerCase()) {
						case "name":
							userDetails[1] = updatedValue;
							break;
						case "email":
							userDetails[2] = updatedValue;
							break;
						case "phone_number":
							userDetails[3] = updatedValue;
							break;
						case "address":
							userDetails[4] = updatedValue;
							break;
						case "total_loan_fees":
							userDetails[5] = updatedValue;
							break;
						default:
							System.out.println("Invalid attribute.");
							return;
						}
						// Rebuild the line with updated user information
						line = String.join(",", userDetails);
					}
					// Add the (updated or unchanged) user back to the list
					users.add(line);
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + user_file);
				e.printStackTrace();
			}

			// Step 4: Write the updated list of users back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(user_file))) {
				for (String user : users) {
					bw.write(user);
					bw.newLine();
				}
				System.out.println("User with ID: " + userId + " updated successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + user_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("User with ID: " + userId + " does not exist.");
		}
	}

	public void addBook(Book book) {
		if (!bookExists(book.getBookId())) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(book_file, true))) {
				bw.write(bookToString(book));
				bw.newLine();
				System.out.println("Book added to file.");

			} catch (IOException e) {
				System.out.println("Error reading file: " + book_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("Book with ID: " + book.getBookId() + " already exists.");
		}
	}

	public void deleteBook(String bookId) {
		if (bookExists(bookId)) {
			List<String> books = new ArrayList<>();

			// Step 1: Read the books from the file
			try (BufferedReader br = new BufferedReader(new FileReader(book_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] bookDetails = line.split(",");
					if (!bookDetails[0].equalsIgnoreCase(bookId)) {
						books.add(line);
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + book_file);
				e.printStackTrace();
			}

			// Step 2: Write the updated list of books back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(book_file))) {
				for (String book : books) {
					bw.write(book);
					bw.newLine();
				}
				System.out.println("Book with ID: " + bookId + " deleted successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + book_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("Book with ID: " + bookId + " does not exist.");
		}
	}

	public void updateBook(String bookId, String attribute, String updatedValue) {
		if (bookExists(bookId)) {
			List<String> books = new ArrayList<>();

			// Step 1: Read the books from the file
			try (BufferedReader br = new BufferedReader(new FileReader(book_file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] bookDetails = line.split(",");

					// Step 2: Check if the current line corresponds to the book we're updating
					if (bookDetails[0].equalsIgnoreCase(bookId)) {
						// Step 3: Update the specified attribute
						switch (attribute.toLowerCase()) {
						case "title":
							bookDetails[1] = updatedValue;
							break;
						case "author":
							bookDetails[2] = updatedValue;
							break;
						case "isbn":
							bookDetails[3] = updatedValue;
							break;
						case "publication_year":
							try {
								int year = Integer.parseInt(updatedValue);
								bookDetails[4] = updatedValue; // Convert to String to store in array
							} catch (NumberFormatException e) {
								System.out.println("Invalid value for publication year.");
								return;
							}
							break;
						case "genre":
							bookDetails[5] = updatedValue;
							break;
						case "base_loan_fee":
							try {
								double baseLoanFee = Double.parseDouble(updatedValue);
								bookDetails[6] = String.valueOf(baseLoanFee); // Convert double to String
							} catch (NumberFormatException e) {
								System.out.println("Invalid value for base loan fee.");
								return;
							}
							break;
						case "loan_status":
							try {
								boolean loanStatus = Boolean.parseBoolean(updatedValue);
								bookDetails[7] = String.valueOf(loanStatus); // Convert boolean to String
							} catch (Exception e) {
								System.out.println("Invalid value for loan status. Use 'true' or 'false'.");
								return;
							}
							break;
						default:
							System.out.println("Invalid attribute.");
							return;
						}
						// Rebuild the line with updated book information
						line = String.join(",", bookDetails);
					}
					// Add the (updated or unchanged) book back to the list
					books.add(line);
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + book_file);
				e.printStackTrace();
			}

			// Step 4: Write the updated list of books back to the file
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(book_file))) {
				for (String book : books) {
					bw.write(book);
					bw.newLine();
				}
				System.out.println("Book with ID: " + bookId + " updated successfully.");
			} catch (IOException e) {
				System.out.println("Error writing to file: " + book_file);
				e.printStackTrace();
			}
		} else {
			System.out.println("Book with ID: " + bookId + " does not exist.");
		}
	}

	private String userToString(User user) {
		return user.getUserId().toUpperCase() + "," + user.getName() + "," + user.getEmail() + ","
				+ user.getPhoneNumber() + "," + user.getAddress() + "," + user.getLoanFees() + ","
				+ user.getClass().getSimpleName();
	}

	private String bookToString(Book book) {
		return book.getBookId().toUpperCase() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn()
				+ "," + book.getPublicationYear() + "," + book.getGenre() + "," + book.getLoanFee() + ","
				+ book.getLoanStatus() + "," + book.getClass().getSimpleName();
	}

	public static String getUserFilePath() {
		return user_file;
	}

	public static String getBookFilePath() {
		return book_file;
	}
}
