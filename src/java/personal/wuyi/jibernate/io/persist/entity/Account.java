package personal.wuyi.jibernate.io.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import personal.wuyi.jibernate.io.persist.core.Uri;

@Entity
@Table(name="account")
public class Account extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")              private Long    id;
	@Column(name="first_name")      private String  firstName;
	@Column(name="last_name")       private String  lastName;
	@Column(name="portfolio_value") private double  portfolioValue;
	@Column(name="stock_value")     private double  stockValue;
	@Column(name="cash_value")      private double  cashValue;
	@Column(name="day_trade_limit") private int     dayTradeLimit;
	@Column(name="day_trade_count") private int     dayTradeCount;
	@Column(name="day_trade_date")  private Date    dayTradeDate;
	@Column(name="readlock")        private Boolean readLock;
	
	public Long    getId()                                  { return id;                            }
	public void    setId(Long id)                           { this.id = id;                         }
	public String  getFirstName()                           { return firstName;                     }
	public void    setFirstName(String firstName)           { this.firstName = firstName;           }
	public String  getLastName()                            { return lastName;                      }
	public void    setLastName(String lastName)             { this.lastName = lastName;             }
	public double  getPortfolioValue()                      { return portfolioValue;                }
	public void    setPortfolioValue(double portfolioValue) { this.portfolioValue = portfolioValue; }
	public double  getStockValue()                          { return stockValue;                    }
	public void    setStockValue(double stockValue)         { this.stockValue = stockValue;         }
	public double  getCashValue()                           { return cashValue;                     }
	public void    setCashValue(double cashValue)           { this.cashValue = cashValue;           }
	public int     getDayTradeLimit()                       { return dayTradeLimit;                 }
	public void    setDayTradeLimit(int dayTradeLimit)      { this.dayTradeLimit = dayTradeLimit;   }
	public int     getDayTradeCount()                       { return dayTradeCount;                 }
	public void    setDayTradeCount(int dayTradeCount)      { this.dayTradeCount = dayTradeCount;   }
	public Date    getDayTradeDate()                        { return dayTradeDate;                  }
	public void    setDayTradeDate(Date dayTradeDate)       { this.dayTradeDate = dayTradeDate;     }
	public boolean getReadLock()                             { return readLock;                      }
	public void    setReadLock(boolean readLock)            { this.readLock = readLock;             }
	
	@Override
	public Uri getUri() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isPersisted() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void refreshPortfolioValue() {
		portfolioValue = stockValue + cashValue;
	}
}
