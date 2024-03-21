/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public abstract class Account implements Serializable{

	private BigDecimal balance;
	private BigDecimal interestRate;
	private int accountNmbr;
	private static int lastAssignedNmbr = 1000;
	private String accountType;
	private ArrayList<String > transactions;

	public Account(BigDecimal interestRate, String accountType) {
		this.balance = BigDecimal.ZERO;
		this.interestRate = interestRate; 
		Account.lastAssignedNmbr++;
		this.accountNmbr = lastAssignedNmbr;
		this.accountType = accountType;
		this.transactions = new ArrayList<String>();
	}

	public String getAccountType() {
		return accountType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal newBalance) {
		this.balance = newBalance;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public int getAccountNmbr() {
		return accountNmbr;
	}
	
	public static int getLastAssignedNmbr() {
		return lastAssignedNmbr;
	}
	
	public static void setLastAssignedNmbr(int lastAssigned) {
		lastAssignedNmbr = lastAssigned;
	}

	/**
	 * Deposit money to account
	 * @param deposit - The amount to deposit in account
	 */
	public void deposit(BigDecimal deposit) {
		this.balance = getBalance().add(deposit);
		addTransaction(deposit, getBalance());
	}


	/**
	 * Creates and adds a transaction (as String) to list of transactions
	 * @param ammount - The amount used in the transaction
	 * @param balance - balance after transaction
	 */
	public void addTransaction(BigDecimal amount, BigDecimal balance){
		SimpleDateFormat sdf = new SimpleDateFormat();
		Transaction transaction = new Transaction(sdf, amount, balance);
		transactions.add(transaction.toString());
	}

	//returns copy of transactionslist
	public List<String> getTransactions(){
		return List.copyOf(transactions);
	}

	/**
	 * String representation of account with the interest rate for the account in %
	 * Setting decimal to local char
	 */
	public String toString() {
		Locale currentLocale = Locale.getDefault();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator(' '); 

		DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00", otherSymbols);
		DecimalFormat df2 = new DecimalFormat("###,###,###,###,##0.0", otherSymbols);

		String balanceFormatted = df.format(getBalance());
		String interestRateFormatted = df2.format(getInterestRate());

		return getAccountNmbr() + " " + balanceFormatted + " kr " + getAccountType() + " " + interestRateFormatted+ " %";
	}

	/**
	 * String representation of account with the interest rate at close for the account in kr
	 * Setting decimal to local char
	 */
	public String toStringCalcInterestRate() {
		Locale currentLocale = Locale.getDefault();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator(' '); 

		DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00", otherSymbols);

		String balanceFormatted = df.format(getBalance());
		String interestRateFormatted = df.format(calculateInterestRate());

		return getAccountNmbr() + " " + balanceFormatted + " kr " + getAccountType() + " " + interestRateFormatted+ " kr";
	}

	public abstract BigDecimal calculateInterestRate();

	public abstract boolean withDraw(BigDecimal withDrawal);
}
