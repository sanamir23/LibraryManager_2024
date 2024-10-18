package library;

import java.sql.Date;

//LoanableItem interface created to increase coupling, and maintainability
//interfaces allow multi-inheritance
public interface LoanableItem {
	void loanBook(User user, Date start_date, Date return_date);

	double calculateLoanCost();

	void extendLoan(Date new_return_date);
}
