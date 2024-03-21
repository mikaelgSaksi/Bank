/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CreditAccount extends Account implements Serializable{
	
	private BigDecimal creditLimit;
	private BigDecimal debtInterest;
	
	public CreditAccount(){
		super(new BigDecimal(0.5), "Kreditkonto");
		this.creditLimit = new BigDecimal(-5000);
		this.debtInterest = new BigDecimal(7);
	}
	
	public BigDecimal getCreditLimit() {
		return creditLimit;
	}
	
	public BigDecimal getDebtInterest() {
		return debtInterest;
	}

	
	/**
	 * Calculates interest rate at time of closure
	 * returns balance * interest rate of 0.5% if balance > 0
	 * returns balance * debt interest rate of 7% if balance < 0
	 */
	@Override
	public BigDecimal calculateInterestRate() {
		if(getBalance().compareTo(BigDecimal.ZERO) > 0)
			return getBalance().multiply(getInterestRate().divide(new BigDecimal(100)));
		return getBalance().multiply(getDebtInterest().divide(new BigDecimal(100)));

	}

	/**
	 * Withdraw money from account if it does not exceed credit limit
	 * @param withDrawal - The amount to withdraw from account 
	 */
	@Override
	public boolean withDraw(BigDecimal withDrawal) {
		if(getBalance().subtract(withDrawal).compareTo(getCreditLimit()) >= 0) {
			setBalance(getBalance().subtract(withDrawal));
			addTransaction(withDrawal.negate(), getBalance());
			return true;
		}
		return false;
	}
	
	//toString if balance is < || > 0
	public String toString() {
		Locale currentLocale = Locale.getDefault();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator(' '); 

		DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00", otherSymbols);
		DecimalFormat df2 = new DecimalFormat("###,###,###,###,##0.0", otherSymbols);
		DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0", otherSymbols);
		String interestRateFormatted;
		
		String balanceFormatted = df.format(getBalance());
		
		if(getBalance().compareTo(BigDecimal.ZERO) >= 0)
			interestRateFormatted = df2.format(getInterestRate());
		else
			interestRateFormatted = df3.format(getDebtInterest());

		return getAccountNmbr() + " " + balanceFormatted + " kr " + getAccountType() + " " + interestRateFormatted+ " %";
	}
}
