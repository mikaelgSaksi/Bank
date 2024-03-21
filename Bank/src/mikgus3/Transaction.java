/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction implements Serializable{
	
	private SimpleDateFormat sdf;
	private BigDecimal amount;
	private BigDecimal balance;
	
	public Transaction(SimpleDateFormat sdf, BigDecimal amount, BigDecimal balance) {
		this.sdf = sdf;
		this.amount = amount;
		this.balance = balance;
		
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}
	
	//String representation of transaction class
	public String toString() {
		Locale currentLocale = Locale.getDefault();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator(' '); 

		DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00", otherSymbols);

	    String formattedDate = getSdf().format(new Date());
	    String formattedAmount = df.format(getAmount().doubleValue());
	    String formattedBalance = df.format(getBalance().doubleValue());
		return formattedDate + " " + formattedAmount + " kr " + "Saldo: " + formattedBalance + " kr";
	}
	
	
}
