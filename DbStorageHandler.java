package library;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Date;

public class DbStorageHandler extends PersistenceHandler {
	private static final String url = "jdbc:postgresql://localhost:5432/library";
	private static final String username = "postgres";
	private static final String password = "killercode23";
	private static final String jdbc_driver = "org.postgresql.Driver";
	private Connection connection;
	private boolean isConnected;

	public DbStorageHandler() {
		isConnected = false;
		connection = null;
		this.connectDB();
	}

	private void connectDB() {
		try {
			// Register PostgreSQL JDBC driver
			Class.forName(jdbc_driver);

			// Open a connection
			System.out.println("Connecting to database...");
			this.connection = DriverManager.getConnection(url, username, password);

			// Check if connection was successful
			if (connection != null) {
				isConnected = true;
				System.out.println("Connection successful!");

			} else {
				System.out.println("Failed to make connection!");
			}

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	
	public List<Book> getBooks() {
	    if (!isConnected) {
	        System.out.println("Error fetching books. Database not connected.");
	        return null;
	    }

	    List<Book> books = new ArrayList<>();
	    String query = "SELECT * FROM library_book"; // Replace with your actual table name

	    try (PreparedStatement statement = connection.prepareStatement(query);
	         ResultSet resultSet = statement.executeQuery()) {  // No query string needed here

	        while (resultSet.next()) {
	            String id = resultSet.getString("book_id");
	            String title = resultSet.getString("title");
	            String author = resultSet.getString("author");
	            String isbn = resultSet.getString("isbn");
	            int year = resultSet.getInt("publication_year");
	            String genre = resultSet.getString("genre");
	            double fee = resultSet.getDouble("base_loan_fee");
	            String type = resultSet.getString("book_type");

	            Book book = switch (type.toLowerCase()) {
	                case "novel" -> new Novel(id, title, author, isbn, year, genre, fee);
	                case "textbook" -> new Textbook(id, title, author, isbn, year, genre, fee);
	                case "reference book" -> new ReferenceBook(id, title, author, isbn, year, genre, fee);
	                default -> {
	                    System.out.println("Warning: Invalid book type '" + type + "' for book ID: " + id);
	                    yield null;
	                }
	            };

	            if (book != null) {
	                books.add(book);
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving books from the database: " + e.getMessage());
	    }
	    return books;
	}

	public List<User> getUsers() {
	    if (!isConnected) {
	        System.out.println("Error fetching users. Database not connected.");
	        return null;
	    }

	    List<User> users = new ArrayList<>();
	    String query = "SELECT * FROM library_user"; // Replace with your actual table name

	    try (PreparedStatement statement = connection.prepareStatement(query);
	         ResultSet resultSet = statement.executeQuery()) {  // No query string needed here

	        while (resultSet.next()) {
	            String id = resultSet.getString("user_id");
	            String name = resultSet.getString("name");
	            String email = resultSet.getString("email");
	            String phoneNumber = resultSet.getString("phone_number");
	            String address = resultSet.getString("address");
	            String userType = resultSet.getString("user_type");

	            User user = switch (userType.toLowerCase()) {
	                case "student" -> new Student(id, name, email, phoneNumber, address);
	                case "faculty" -> new Faculty(id, name, email, phoneNumber, address);
	                case "member" -> new Member(id, name, email, phoneNumber, address);
	                default -> {
	                    System.out.println("Warning: Invalid user type '" + userType + "' for user ID: " + id);
	                    yield null;
	                }
	            };

	            if (user != null) {
	                users.add(user);
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving users from the database: " + e.getMessage());
	    }
	    return users;
	}

    
    public Book getBook(String bookId) {
    	if (!isConnected) {
    		System.out.println("Error fetching book. Database not connected.");
    		return null;
    	}
        String query = "SELECT * FROM library_book WHERE book_id = ?";
        Book book = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Extract common fields for all book types
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String publicationYear = rs.getString("publication_year");
                String genre = rs.getString("genre");
                double baseLoanFee = rs.getDouble("base_loan_fee");
                String bookType = rs.getString("book_type");

                // Create the appropriate Book subclass based on book type
                switch (bookType.toLowerCase()) {
                    case "novel":
                        book = new Novel(bookId, title, author, isbn, Integer.parseInt(publicationYear), genre, baseLoanFee);
                        break;
                    case "textbook":
                        book = new Textbook(bookId, title, author, isbn, Integer.parseInt(publicationYear), genre, baseLoanFee);
                        break;
                    case "reference book":
                        book = new ReferenceBook(bookId, title, author, isbn, Integer.parseInt(publicationYear), genre, baseLoanFee);
                        break;
                    default:
                        System.out.println("Warning: Invalid book type '" + bookType + "' for book ID: " + bookId);
                }
            } else {
                System.out.println("Book with ID " + bookId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching book from the database.");
            e.printStackTrace();
        }

        return book;
    }
    
    public List<Loan> getUserLoans(String userId) {
    	if (!isConnected) {
			System.out.println("Error fetching books. Database not connected.");
			return null;
		}
		
        List<Loan> userLoans = new ArrayList<>();
        
        String query = "SELECT * FROM user_loan WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Retrieve the book object using the book ID from the loan record
                String bookId = rs.getString("book_id");
                Book book = getBook(bookId);
                
                // Extract other loan details
                int loanId = rs.getInt("loan_id");
                Date startDate = rs.getDate("start_date");
                Date returnDate = rs.getDate("return_date");

                // Create a Loan object and add it to the list
                Loan loan = new Loan(book, startDate, returnDate);
                loan.setLoandID(loanId); 
                userLoans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loans for user: " + userId);
            e.printStackTrace();
        }

        return userLoans;
    }

	public void displayTableContent(String tableName) {
		if (isConnected) {
			String sql = "SELECT * FROM " + tableName;
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {

				// Get the metadata of the table to know the number of columns
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();

				// Display each row
				while (resultSet.next()) {
					for (int i = 1; i <= columnCount; i++) {
						if (i == columnCount) {
							System.out.print(resultSet.getString(i));
						} else {
							System.out.print(resultSet.getString(i) + ", ");
						}
					}
					System.out.println();
				}
			} catch (SQLException e) {
				System.out.println("Error displaying " + tableName + " content.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void addUser(User user) {
		if (isConnected) {
			if (!userExists(user.getUserId())) {
				String sql = "INSERT INTO LIBRARY_USER (user_id, name, email, phone_number, address, user_type) VALUES (?, ?, ?, ?, ?, ?)";

				try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
					// Set parameters for the prepared statement
					pstmt.setString(1, user.getUserId());
					pstmt.setString(2, user.getName());
					pstmt.setString(3, user.getEmail());
					pstmt.setString(4, user.getPhoneNumber());
					pstmt.setString(5, user.getAddress());

					// Determine user type based on the actual instance type
					String userType;
					if (user instanceof Student) {
						userType = "Student";
					} else if (user instanceof Faculty) {
						userType = "Faculty";
					} else if (user instanceof Member) {
						userType = "Member";
					} else {
						throw new IllegalArgumentException("Unknown user type");
					}

					pstmt.setString(6, userType); // Set the user type

					// Execute the insert
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("User with ID: " + user.getUserId()
								+ " added successfully to LIBRARY_USER table in Library Database.");
					} else {
						System.out.println("Failed to add user.");
					}
				} catch (SQLException se) {
					se.printStackTrace(); // Print stack trace for debugging
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage()); // Handle unknown user type
				}
			} else {
				System.out.println("User with ID: " + user.getUserId() + " already exists in the database.");
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void deleteUser(String userId) {
		if (isConnected) {
			if (userExists(userId)) {
				String sql = "DELETE FROM library_user WHERE user_id = ?";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setString(1, userId); // Set userID parameter in the query
					int rowsAffected = statement.executeUpdate();

					if (rowsAffected > 0) {
						System.out.println("User with ID: " + userId + " deleted successfully.");
					} else {
						System.out.println("User with ID: " + userId + " not found.");
					}
				} catch (SQLException e) {
					System.out.println("Error deleting user from the database.");
					e.printStackTrace();
				}
			} else {
				System.out.println("User with ID: " + userId + " does not exist in the database.");
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void updateUser(String userId, String attribute, String updatedValue) {
		if (isConnected) {
			if (userExists(userId)) {
				String sql = "UPDATE library_user SET " + attribute + " = ? WHERE user_id = ?";

				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					// Check if user is updating a numeric field like 'total_loan_fees'
					if (attribute.equalsIgnoreCase("total_loan_fees")) {
						// Convert updatedValue to double
						double loanFees = Double.parseDouble(updatedValue);
						statement.setDouble(1, loanFees);
					} else {
						// For other attributes like 'Name', 'Email', 'Address', etc., use setString
						statement.setString(1, updatedValue);
					}

					statement.setString(2, userId); // Set userId parameter in the query

					int rowsAffected = statement.executeUpdate();

					if (rowsAffected > 0) {
						System.out.println("User with ID: " + userId + " updated successfully.");
					} else {
						System.out.println("User with ID: " + userId + " not found.");
					}
				} catch (SQLException e) {
					System.out.println("Error updating user in the database.");
					e.printStackTrace();
				} catch (NumberFormatException e) {
					System.out.println("Invalid value provided for 'total_loan_fees'. Please enter a numeric value.");
				}
			} else {
				System.out.println("User with ID: " + userId + " does not exist in the database.");
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void displayUser(String userId) {
		if (isConnected) {
			if (userExists(userId)) {
				String sql = "SELECT * FROM library_user WHERE user_id = ?";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setString(1, userId); // Set the userId parameter

					try (ResultSet resultSet = statement.executeQuery()) {
						// Check if there are results
						if (!resultSet.next()) {
							System.out.println("No user found with ID: " + userId);
							return;
						}

						// Get the metadata of the table to know the number of columns
						ResultSetMetaData metaData = resultSet.getMetaData();
						int columnCount = metaData.getColumnCount();

						// Display the user data
						do {
							for (int i = 1; i <= columnCount; i++) {
								System.out.print(resultSet.getString(i) + (i < columnCount ? ", " : ""));
							}
							System.out.println();
						} while (resultSet.next());
					}
				} catch (SQLException e) {
					System.out.println("Error displaying user with ID: " + userId);
					e.printStackTrace();
				}
			} else {
				System.out.println("User with ID: " + userId + " does not exist in the database.");
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void addBook(Book book) {
		if (isConnected) {
			if (!bookExists(book.getBookId())) {
				String sql = "INSERT INTO library_book (book_id, title, author, isbn, publication_year, genre, book_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

				try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
					// Set parameters for the prepared statement
					pstmt.setString(1, book.getBookId());
					pstmt.setString(2, book.getTitle());
					pstmt.setString(3, book.getAuthor());
					pstmt.setString(4, book.getIsbn());
					pstmt.setInt(5, book.getPublicationYear());
					pstmt.setString(6, book.getGenre());

					// Determine book type based on the actual instance type
					String bookType;
					if (book instanceof Novel) {
						bookType = "Novel";
					} else if (book instanceof Textbook) {
						bookType = "Textbook";
					} else if (book instanceof ReferenceBook) {
						bookType = "Reference Book";
					} else {
						throw new IllegalArgumentException("Unknown book type");
					}

					pstmt.setString(7, bookType); // Set the book type

					// Execute the insert
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Book with ID: " + book.getBookId()
								+ " added successfully to LIBRARY_BOOK table in Library Database.");
					} else {
						System.out.println("Failed to add book.");
					}
				} catch (SQLException se) {
					se.printStackTrace(); // Print stack trace for debugging
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage()); // Handle unknown book type
				}
			} else {
				System.out.println("Book with ID: " + book.getBookId() + " already exists in the database.");
				return;
			}
		} else {
			System.out.println("Not connected to the database.");
			return;
		}
	}

	public void deleteBook(String bookId) {
		if (isConnected) {
			if (bookExists(bookId)) {
				String sql = "DELETE FROM library_book WHERE book_id = ?";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setString(1, bookId); // Set bookID parameter in the query
					int rowsAffected = statement.executeUpdate();

					if (rowsAffected > 0) {
						System.out.println("Book with ID: " + bookId + " deleted successfully.");
					} else {
						System.out.println("Book with ID: " + bookId + " not found.");
					}
				} catch (SQLException e) {
					System.out.println("Error deleting book from the database.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Book with ID: " + bookId + " does not exist in the database.");
				return;
			}
		} else {
			System.out.println("Not connected to the database.");
			return;
		}
	}

	public void updateBook(String bookId, String attribute, String updatedValue) {
		if (isConnected) {
			if (bookExists(bookId)) {
				String sql = "UPDATE library_book SET " + attribute + " = ? WHERE book_id = ?";

				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					// Handle different data types for attributes
					if (attribute.equalsIgnoreCase("base_loan_fee")) {
						// Convert updatedValue to double for base loan fee
						double baseLoanFee = Double.parseDouble(updatedValue);
						statement.setDouble(1, baseLoanFee);
					} else if (attribute.equalsIgnoreCase("publication_year")) {
						// Convert updatedValue to int for publication year
						int year = Integer.parseInt(updatedValue);
						statement.setInt(1, year);
					} else if (attribute.equalsIgnoreCase("loan_status")) {
						// Convert updatedValue to boolean for loan status
						boolean ifLoaned = Boolean.parseBoolean(updatedValue);
						statement.setBoolean(1, ifLoaned);
					} else {
						// For other attributes like 'title', 'isbn', 'genre', use setString
						statement.setString(1, updatedValue);
					}

					// Set the bookId parameter in the query
					statement.setString(2, bookId);

					// Execute the update
					int rowsAffected = statement.executeUpdate();

					if (rowsAffected > 0) {
						System.out.println("Book with ID: " + bookId + " updated successfully.");
						return;
					} else {
						System.out.println("Book with ID: " + bookId + " not found.");
						return;
					}
				} catch (SQLException e) {
					System.out.println("Error updating book in the database.");
					e.printStackTrace();
				} catch (NumberFormatException e) {
					System.out.println("Invalid value provided for the numeric field. Please enter a valid number.");
				}
			} else {
				System.out.println("Book with ID: " + bookId + " does not exist in the database.");
				return;
			}
		} else {
			System.out.println("Not connected to the database.");
			return;
		}
	}

	public void displayBook(String bookId) {
		if (isConnected) {
			if (bookExists(bookId)) {
				String sql = "SELECT * FROM library_book WHERE book_id = ?";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setString(1, bookId); // Set the bookId parameter

					try (ResultSet resultSet = statement.executeQuery()) {
						if (!resultSet.next()) {
							System.out.println("No book found with ID: " + bookId);
							return;
						}

						// Get the metadata of the table to know the number of columns
						ResultSetMetaData metaData = resultSet.getMetaData();
						int columnCount = metaData.getColumnCount();

						// Display the book data
						do {
							for (int i = 1; i <= columnCount; i++) {
								System.out.print(resultSet.getString(i) + (i < columnCount ? ", " : ""));
							}
							System.out.println();
						} while (resultSet.next());
					}
				} catch (SQLException e) {
					System.out.println("Error displaying book with ID: " + bookId);
					e.printStackTrace();
				}
			} else {
				System.out.println("Book with ID: " + bookId + " does not exist in the database.");
				return;
			}
		} else {
			System.out.println("Not connected to the database.");
			return;
		}
	}

	public void addLoan(Loan loan, String userId) {	
	    if (!bookExists(loan.getBookId())) {
	        System.out.println("Book with ID " + loan.getBookId() + " does not exist.");
	        return;
	    }

	    if (isConnected) {
	        String sql = "INSERT INTO USER_LOAN (loan_id, start_date, return_date, user_id, book_id) VALUES (?, ?, ?, ?, ?)";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            // Set parameters for the prepared statement
	            pstmt.setInt(1, loan.getLoanID());
	            pstmt.setDate(2, loan.getStartDate());
	            pstmt.setDate(3, loan.getReturnDate());
	            pstmt.setString(4, userId);
	            pstmt.setString(5, loan.getBookId());

	            // Execute the insert
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("Loan with ID: " + loan.getLoanID()
	                        + " added successfully to USER_LOAN table in the Library Database.");
	            } else {
	                System.out.println("Failed to add loan.");
	            }
	        } catch (SQLException se) {
	            se.printStackTrace(); // Print stack trace for debugging
	        }
	    } else {
	        System.out.println("Not connected to the database.");
	    }
	}

	public void deleteLoan(int loanId) {
		if (isConnected) {
			String sql = "DELETE FROM user_loan WHERE loan_id = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				// String l = Integer.toString(loanId);
				statement.setInt(1, loanId); // Set loanID parameter in the query
				int rowsAffected = statement.executeUpdate();

				if (rowsAffected > 0) {
					System.out.println("Loan with ID: " + loanId + " deleted successfully.");
				} else {
					System.out.println("Loan with ID: " + loanId + " not found.");
				}
			} catch (SQLException e) {
				System.out.println("Error deleting loan from the database.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void updateLoan(Loan loan) {
		if (isConnected) {
			// SQL query to update the loan's attributes (start_date, return_date, book_id)
			String updateSql = "UPDATE USER_LOAN SET loan_id = ?, start_date = ?, return_date = ?, book_id = ? WHERE loan_id = ?";

			try {
				// Proceed with updating the loan if no conflicts
				try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
					pstmt.setInt(1, loan.getLoanID());
					pstmt.setDate(2, loan.getStartDate());
					pstmt.setDate(3, loan.getReturnDate());
					pstmt.setString(4, loan.getBookId());
					pstmt.setInt(5, loan.getLoanID()); // Use loan ID to identify the loan to update

					// Execute the update query
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Loan with ID: " + loan.getLoanID() + " updated successfully.");
					} else {
						System.out.println("No loan found with ID: " + loan.getLoanID());
					}
				}
			} catch (SQLException se) {
				se.printStackTrace(); // Print stack trace for debugging
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public void displayUserLoans(String userId) {
		if (isConnected) {
			String sql = "SELECT loan_id, start_date, return_date, book_id FROM USER_LOAN WHERE user_id = ?";

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				// Set the userId parameter
				pstmt.setString(1, userId);

				// Execute the query
				ResultSet rs = pstmt.executeQuery();

				// Check if the result set has any loans
				if (!rs.isBeforeFirst()) { // If no loans found
					System.out.println("No loans found for user ID: " + userId);
				} else {
					System.out.println("Loans for User ID: " + userId);
					// Loop through the result set and display loan information
					while (rs.next()) {
						int loanId = rs.getInt("loan_id");
						String startDate = rs.getString("start_date");
						String returnDate = rs.getString("return_date");
						String bookId = rs.getString("book_id");

						// Display the loan information
						System.out.println("Loan ID: " + loanId + ", Start Date: " + startDate + ", Return Date: "
								+ returnDate + ", Book ID: " + bookId);
					}
				}
			} catch (SQLException se) {
				se.printStackTrace(); // Print stack trace for debugging
			}
		} else {
			System.out.println("Not connected to the database.");
		}
	}

	public boolean userExists(String userId) {
		if (isConnected) {
			String sql = "SELECT COUNT(*) FROM library_user WHERE user_id = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, userId.toUpperCase());
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						int count = resultSet.getInt(1);
						return count > 0; // Return true if user exists
					}
				}
			} catch (SQLException e) {
				System.out.println("Error checking if user exists in the database.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Not connected to the database.");
		}
		return false; // Return false if user does not exist or there's an error
	}

	public boolean bookExists(String bookId) {
		if (isConnected) {
			String sql = "SELECT COUNT(*) FROM library_book WHERE book_id = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, bookId.toUpperCase());
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						int count = resultSet.getInt(1);
						return count > 0; // Return true if book exists
					}
				}
			} catch (SQLException e) {
				System.out.println("Error checking if book exists in the database.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Not connected to the database.");
		}
		return false; // Return false if book does not exist or there's an error
	}
}
