package personal.wuyi.autostock.io.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import personal.wuyi.autostock.io.persist.core.Uri;

@Entity
@Table(name="historical_stock_price")
public class HistoricalStockPrice extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")             private long   id;
	@Column(name="symbol")         private String symbol;
	@Column(name="price")          private double price;
	@Column(name="moving_average") private double movingAverage;
	@Column(name="date_time")      private Date   datetime;
	@Column(name="year")           private int    year;
	@Column(name="month")          private int    month;
	@Column(name="day")            private int    day;
	@Column(name="hour")           private int    hour;
	@Column(name="minute")         private int    minute;
	@Column(name="second")         private int    second;
	@Column(name="day_of_week")    private int    dayOfWeek;
	
	public Long   getId()                                { return id;                          }
	public void   setId(long id)                         { this.id = id;                       }
	public String getSymbol()                            { return symbol;                      }
	public void   setSymbol(String symbol)               { this.symbol = symbol;               }
	public double getPrice()                             { return price;                       }
	public void   setPrice(double price)                 { this.price = price;                 }
	public double getMovingAverage()                     { return movingAverage;               }
	public void   setMovingAverage(double movingAverage) { this.movingAverage = movingAverage; }
	public Date   getDatetime()                          { return datetime;                    }
	public void   setDatetime(Date datetime)             { this.datetime = datetime;           }
	public int    getYear()                              { return year;                        }
	public void   setYear(int year)                      { this.year = year;                   }
	public int    getMonth()                             { return month;                       }
	public void   setMonth(int month)                    { this.month = month;                 }
	public int    getDay()                               { return day;                         }
	public void   setDay(int day)                        { this.day = day;                     }
	public int    getHour()                              { return hour;                        }
	public void   setHour(int hour)                      { this.hour = hour;                   }
	public int    getMinute()                            { return minute;                      }
	public void   setMinute(int minute)                  { this.minute = minute;               }
	public int    getSecond()                            { return second;                      }
	public void   setSecond(int second)                  { this.second = second;               }
	public int    getDayOfWeek()                         { return dayOfWeek;                   }
	public void   setDayOfWeek(int dayOfWeek)            { this.dayOfWeek = dayOfWeek;         }
	
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
	
	
}
