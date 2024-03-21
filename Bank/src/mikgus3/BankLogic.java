/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BankLogic {

	private ArrayList<Customer> customers;

	public BankLogic() {
		this.customers = new ArrayList<Customer>();
	}

	// returns a list of Strings of customers in format pNO, First name and Last
	// name
	public List<String> getAllCustomers() {
		ArrayList<String> customersCopy = new ArrayList<String>();
		String pNoName;
		for (Customer customer : customers) {
			pNoName = customer.getpNo() + " " + customer.getName() + " " + customer.getSurname();
			customersCopy.add(pNoName);
		}
		return customersCopy;
	}

	// creates new customer if pNo is unique and adds to customer list
	public boolean createCustomer(String name, String surname, String pNo) {
		Customer customer = findCustomer(pNo);
		if (customer != null)
			return false;

		Customer newCustomer = new Customer(name, surname, pNo);
		customers.add(newCustomer);
		return true;
	}

	/**
	 * deletes customer and associated accounts adds deleted customer to list
	 * 
	 * @param pNo - pNo of customer to be deleted returns list with deleted customer
	 *            and associated accounts or null if no pNo match
	 */
	public List<String> deleteCustomer(String pNo) {
		ArrayList<String> deletedCustomer = new ArrayList<String>();

		Iterator<Customer> itr = customers.iterator();
		while (itr.hasNext()) {
			Customer customer = itr.next();
			if (customer.getpNo().equals(pNo)) {
				itr.remove();
				deletedCustomer.add(customer.getpNo() + " " + customer.getName() + " " + customer.getSurname());
				if (!customer.getAccounts().isEmpty())
					for (Account account : customer.getAccounts())
						deletedCustomer.add(account.toStringCalcInterestRate());
				customer.removeAllAccounts();
				return deletedCustomer;
			}
		}
		return null;
	}

	/**
	 * changes customer name if params not empty
	 * 
	 * @param pNo           - pNo of customer to have name changed
	 * @param name/surname- name to change to returns true if name was changed
	 */
	public boolean changeCustomerName(String name, String surname, String pNo) {

		if (name == null || surname == null || name.isEmpty() || surname.isEmpty())
			return false;

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			if (!name.isEmpty())
				customer.setName(name);
			if (!surname.isEmpty()) {
				customer.setSurname(surname);
			}
			return true;
		}
		return false;
	}

	/**
	 * removes account from customer
	 * 
	 * @param pNo       - pNo of customer to have name changed
	 * @param accountId - the accountNumber of the account to be closed returns
	 *                  String representation of account with calculated interest
	 *                  rate at time of closure
	 */
	public String closeAccount(String pNo, int accountId) {

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account account = customer.getAccount(accountId);
			if (account != null) {
				customer.removeAccount(account);
				return account.toStringCalcInterestRate();
			}
		}
		return null;
	}

	/**
	 * creates new account for specified customer and adds to customer list of
	 * accounts
	 * 
	 * @param pNo - pNo of customer that should have the new account returns -1 if
	 *            no account was created
	 */
	public int createSavingsAccount(String pNo) {

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account account = new SavingsAccount();
			customer.addAccount(account);
			return account.getAccountNmbr();
		}
		return -1;
	}

	/**
	 * deposit money to specified customer account
	 * 
	 * @param pNo       - pNo of customer
	 * @param accountId - the account to deposit to
	 * @param amount    - the amount to be deposited returns true if deposit was
	 *                  successful
	 */
	public boolean deposit(String pNo, int accountId, int amount) {
		if (amount <= 0)
			return false;

		BigDecimal bigDecimalAmount = new BigDecimal(amount);

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> accounts = customer.getAccounts();
			for (Account account : accounts) {
				if (account.getAccountNmbr() == accountId) {
					account.deposit(bigDecimalAmount);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * finds and returns a string representation of specified account
	 * 
	 * @param pNo       - pNo of customer
	 * @param accountId - the account number
	 */
	public String getAccount(String pNo, int accountId) {

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account account = customer.getAccount(accountId);
			if (account != null)
				return account.toString();
		}
		return null;
	}

	public List<Account> getAccountList(String pNo) {
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> accounts = customer.getAccounts();
			return new ArrayList<>(accounts);
		}
		return Collections.emptyList();
	}

	/**
	 * finds specified customer and adds to list
	 * 
	 * @param pNo - pNo of customer returns list of specified customer and
	 *            associated accounts / null if not found
	 */
	public List<String> getCustomer(String pNo) {
		ArrayList<String> customerDetails = new ArrayList<String>();

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			customerDetails.add(pNo + " " + customer.getName() + " " + customer.getSurname());
			List<Account> accounts = customer.getAccounts();
			for (Account account : accounts) {
				customerDetails.add(account.toString());
			}
			return customerDetails;
		}
		return null;
	}

	/**
	 * withdraws specified amount from account
	 * 
	 * @param pNo       - pNo of customer
	 * @param accountId - account number
	 * @param amount    - amount to withdraw returns true if successful
	 */
	public boolean withdraw(String pNo, int accountId, int amount) {
		if (amount <= 0)
			return false;

		BigDecimal bigDecimalAmount = new BigDecimal(amount);

		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account account = customer.getAccount(accountId);
			if (account != null && account.withDraw(bigDecimalAmount))
				return true;
		}
		return false;
	}

	/**
	 * creates CreditAccount for customer
	 * 
	 * @param pNo - pNo of customer returns accountNmbr or -1 if unsuccessful
	 */
	public int createCreditAccount(String pNo) {
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			CreditAccount creditAccount = new CreditAccount();
			customer.addAccount(creditAccount);
			return creditAccount.getAccountNmbr();
		}
		return -1;
	}

	/**
	 * returns list of transactions for specified account
	 * 
	 * @param pNo       - pNo of customer
	 * @param accountId - account number return null if pNo or accountId does not
	 *                  exist
	 */
	public List<String> getTransactions(String pNo, int accountId) {
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account account = customer.getAccount(accountId);
			if (account != null) {
				List<String> transactions = account.getTransactions();
				return transactions;
			}
		}
		return null;
	}

	/**
	 * returns string with customerDetail for better readability in GUI
	 * 
	 * @param pNo - pNo of customer return null if customer does not exist
	 */
	public String customerDetails(String pNo) {
		Customer c = findCustomer(pNo);
		if (c != null) {
			String customerDetails = c.getName() + " " + c.getSurname() + ", " + c.getpNo();
			return customerDetails;
		}
		return null;
	}

	// function to find and return customer
	public Customer findCustomer(String pNo) {
		for (Customer customer : customers)
			if (customer.getpNo().equals(pNo))
				return customer;
		return null;
	}

	// Write customerobjects and lastassigned number to file
	public void saveCustomerAndAccounts(String fileName) throws IOException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(fileName));

			out.writeInt(Account.getLastAssignedNmbr());

			for (Customer customer : customers) {
				out.writeObject(customer);
			}
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Reads customerobjects from file and repopulates customerlist
	public void loadCustomerAndAccounts(String fileName) throws IOException, ClassNotFoundException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(fileName));
			customers.clear();

			// Read and set lastAssigned number
			Account.setLastAssignedNmbr(in.readInt());

			// Read and add customers to list
			while (true) {
				try {
					Customer customer = (Customer) in.readObject();
					customers.add(customer);
				} catch (EOFException e) {
					break;
				}
			}

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Save transactions for specified account to readable text file
	public void saveTransactionsToFile(String fileName, String pNo, Integer accountId) throws IOException {

		List<String> transactions = getTransactions(pNo, accountId);

		try {
			FileWriter fileWriter = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fileWriter);

			out.write("Date saved: ");
			SimpleDateFormat date = new SimpleDateFormat();
			String formatedDate = date.format(new Date());
			out.write(formatedDate + "\n");
			out.write("Saved transactions for account: " + accountId + "\n");

			for (String transaction : transactions) {
				out.write(transaction.toString() + "\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Load transactions from file into list for dialog window in GUI
	public List<String> loadSavedTransactions(String fileName) throws IOException {
		List<String> transactions = new ArrayList<>();
		String line;

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader in = new BufferedReader(fileReader);

			while ((line = in.readLine()) != null) {
				transactions.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return transactions;
	}
}
