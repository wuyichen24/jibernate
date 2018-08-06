package personal.wuyi.jibernate.io.persist.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Joiner;

import personal.wuyi.jibernate.io.persist.core.Uri;
import personal.wuyi.numeric.NumUtil;

@Entity
@Table(name="stock_real_time_metrics")
public class StockRealTimeMetrics extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")                               private Long   id;
	@Column(name="symbol")                           private String symbol;
	
	@Column(name="current_price")                    private double currentPrice;
	@Column(name="current_date_time")                private Date   currentDateTime;
	
	@Column(name="last_price")                       private double lastPrice;
	@Column(name="last_change_delta")                private double lastChangeDelta;
	@Column(name="last_change_percent")              private double lastChangePercent;
	@Column(name="last_change_display_percent")      private double lastChangeDisplayPercent;
	@Column(name="last_date_time")                   private Date   lastDateTime;
	
	@Column(name="initial_price")                    private double initialPrice;
	@Column(name="initial_change_delta")             private double initialChangeDelta;
	@Column(name="initial_change_percent")           private double initialChangePercent;
	@Column(name="initial_change_display_percent")   private double initialChangeDisplayPercent;
	@Column(name="initial_date_time")                private Date   initialDateTime;
	
	@Column(name="high_price")                       private double highPrice = - Double.MAX_VALUE;
	@Column(name="high_gap_delta")                   private double highGapDelta;
	@Column(name="high_down_percent")                private double highDownPercent;
	@Column(name="high_down_display_percent")        private double highDownDisplayPercent;
	@Column(name="high_date_time")                   private Date   highDateTime;
	
	@Column(name="low_price")                        private double lowPrice = Double.MAX_VALUE;
	@Column(name="low_gap_delta")                    private double lowGapDelta;
	@Column(name="low_up_percent")                   private double lowUpPercent;
	@Column(name="low_up_display_percent")           private double lowUpDisplayPercent;
	@Column(name="low_date_time")                    private Date   lowDateTime;
	
	@Column(name="current_moving_average")           private double currentMovingAverage;
	@Column(name="last_moving_average")              private double lastMovingAverage;
	@Column(name="high_moving_average")              private double highMovingAverage = - Double.MAX_VALUE;
	@Column(name="high_moving_average_gap_delta")    private double highMovingAverageGapDelta;
	@Column(name="high_moving_average_down_percent") private double highMovingAverageDownPercent;
	@Column(name="high_moving_average_date_time")    private Date   highMovingAverageDateTime;
	@Column(name="low_moving_average")               private double lowMovingAverage = Double.MAX_VALUE;
	@Column(name="low_moving_average_gap_delta")     private double lowMovingAverageGapDelta;
	@Column(name="low_moving_average_up_percent")    private double lowMovingAverageUpPercent;
	@Column(name="low_moving_average_date_time")     private Date   lowMovingAverageDateTime;
	@Column(name="status")                           private String status;
	
	public Long          getId()                                                              { return id;                                                        }
	public void          setId(Long id)                                                       { this.id = id;                                                     }
	public String        getSymbol()                                                          { return symbol;                                                    }
	public void          setSymbol(String symbol)                                             { this.symbol = symbol;                                             }
	public double        getCurrentPrice()                                                    { return currentPrice;                                              }
	public void          setCurrentPrice(double currentPrice)                                 { this.currentPrice = currentPrice;                                 }
	public Date          getCurrentDateTime()                                                 { return currentDateTime;                                           }
	public void          setCurrentDateTime(Date currentDateTime)                             { this.currentDateTime = currentDateTime;                           }
	public double        getLastPrice()                                                       { return lastPrice;                                                 }
	public void          setLastPrice(double lastPrice)                                       { this.lastPrice = lastPrice;                                       }
	public double        getLastChangeDelta()                                                 { return lastChangeDelta;                                           }
	public void          setLastChangeDelta(double lastChangeDelta)                           { this.lastChangeDelta = lastChangeDelta;                           }
	public double        getLastChangePercent()                                               { return lastChangePercent;                                         }
	public void          setLastChangePercent(double lastChangePercent)                       { this.lastChangePercent = lastChangePercent;                       }
	public double        getLastChangeDisplayPercent()                                        { return lastChangeDisplayPercent;                                  }
	public void          setLastChangeDisplayPercent(double lastChangeDisplayPercent)         { this.lastChangeDisplayPercent = lastChangeDisplayPercent;         }
	public Date          getLastDateTime()                                                    { return lastDateTime;                                              }
	public void          setLastDateTime(Date lastDateTime)                                   { this.lastDateTime = lastDateTime;                                 }
	public double        getInitialPrice()                                                    { return initialPrice;                                              }
	public void          setInitialPrice(double initialPrice)                                 { this.initialPrice = initialPrice;                                 }
	public double        getInitialChangeDelta()                                              { return initialChangeDelta;                                        }
	public void          setInitialChangeDelta(double initialChangeDelta)                     { this.initialChangeDelta = initialChangeDelta;                     }
	public double        getInitialChangePercent()                                            { return initialChangePercent;                                      }
	public void          setInitialChangePercent(double initialChangePercent)                 { this.initialChangePercent = initialChangePercent;                 }
	public double        getInitialChangeDisplayPercent()                                     { return initialChangeDisplayPercent;                               }
	public void          setInitialChangeDisplayPercent(double initialChangeDisplayPercent)   { this.initialChangeDisplayPercent = initialChangeDisplayPercent;   }
	public Date          getInitialDateTime()                                                 { return initialDateTime;                                           }
	public void          setInitialDateTime(Date initialDateTime)                             { this.initialDateTime = initialDateTime;                           }
	public double        getHighPrice()                                                       { return highPrice;                                                 }
	public void          setHighPrice(double highPrice)                                       { this.highPrice = highPrice;                                       }
	public double        getHighGapDelta()                                                    { return highGapDelta;                                              }
	public void          setHighGapDelta(double highGapDelta)                                 { this.highGapDelta = highGapDelta;                                 }
	public double        getHighDownPercent()                                                 { return highDownPercent;                                           }
	public void          setHighDownPercent(double highDownPercent)                           { this.highDownPercent = highDownPercent;                           }
	public double        getHighDownDisplayPercent()                                          { return highDownDisplayPercent;                                    }
	public void          setHighDownDisplayPercent(double highDownDisplayPercent)             { this.highDownDisplayPercent = highDownDisplayPercent;             }
	public Date          getHighDateTime()                                                    { return highDateTime;                                              }
	public void          setHighDateTime(Date highDateTime)                                   { this.highDateTime = highDateTime;                                 }
	public double        getLowPrice()                                                        { return lowPrice;                                                  }
	public void          setLowPrice(double lowPrice)                                         { this.lowPrice = lowPrice;                                         }
	public double        getLowGapDelta()                                                     { return lowGapDelta;                                               }
	public void          setLowGapDelta(double lowGapDelta)                                   { this.lowGapDelta = lowGapDelta;                                   }
	public double        getLowUpPercent()                                                    { return lowUpPercent;                                              }
	public void          setLowUpPercent(double lowUpPercent)                                 { this.lowUpPercent = lowUpPercent;                                 }
	public double        getLowUpDisplayPercent()                                             { return lowUpDisplayPercent;                                       }
	public void          setLowUpDisplayPercent(double lowUpDisplayPercent)                   { this.lowUpDisplayPercent = lowUpDisplayPercent;                   }
	public Date          getLowDateTime()                                                     { return lowDateTime;                                               }
	public void          setLowDateTime(Date lowDateTime)                                     { this.lowDateTime = lowDateTime;                                   }
	public double        getCurrentMovingAverage()                                            { return currentMovingAverage;                                      }
	public void          setCurrentMovingAverage(double currentMovingAverage)                 { this.currentMovingAverage = currentMovingAverage;                 }
	public double        getLastMovingAverage()                                               { return lastMovingAverage;                                         }
	public void          setLastMovingAverage(double lastMovingAverage)                       { this.lastMovingAverage = lastMovingAverage;                       }
	public double        getHighMovingAverage()                                               { return highMovingAverage;                                         }
	public void          setHighMovingAverage(double highMovingAverage)                       { this.highMovingAverage = highMovingAverage;                       }
	public double        getHighMovingAverageGapDelta()                                       { return highMovingAverageGapDelta;                                 }
	public void          setHighMovingAverageGapDelta(double highMovingAverageGapDelta)       { this.highMovingAverageGapDelta = highMovingAverageGapDelta;       }
	public double        getHighMovingAverageDownPercent()                                    { return highMovingAverageDownPercent;                              }
	public void          setHighMovingAverageDownPercent(double highMovingAverageDownPercent) { this.highMovingAverageDownPercent = highMovingAverageDownPercent; }
	public Date          getHighMovingAverageDateTime()                                       { return highMovingAverageDateTime;                                 }
	public void          setHighMovingAverageDateTime(Date highMovingAverageDateTime)         { this.highMovingAverageDateTime = highMovingAverageDateTime;       }
	public double        getLowMovingAverage()                                                { return lowMovingAverage;                                          }
	public void          setLowMovingAverage(double lowMovingAverage)                         { this.lowMovingAverage = lowMovingAverage;                         }
	public double        getLowMovingAverageGapDelta()                                        { return lowMovingAverageGapDelta;                                  }
	public void          setLowMovingAverageGapDelta(double lowMovingAverageGapDelta)         { this.lowMovingAverageGapDelta = lowMovingAverageGapDelta;         }
	public double        getLowMovingAverageUpPercent()                                       { return lowMovingAverageUpPercent;                                 }
	public void          setLowMovingAverageUpPercent(double lowMovingAverageUpPercent)       { this.lowMovingAverageUpPercent = lowMovingAverageUpPercent;       }
	public Date          getLowMovingAverageDateTime()                                        { return lowMovingAverageDateTime;                                  }
	public void          setLowMovingAverageDateTime(Date lowMovingAverageDateTime)           { this.lowMovingAverageDateTime = lowMovingAverageDateTime;         }
	public String        getStatus()                                                          { return status;                                                    }
	public void          setStatus(String status)                                             { this.status = status;                                             }
	
	public StockRealTimeMetrics() {}
	
	public StockRealTimeMetrics (String symbol, double currentPrice, Date currentDateTime, String status) {
		this.symbol = symbol;
		this.status = status;
		initialize(currentPrice, currentDateTime, true);
	}
	
	protected void initialize(double currentPrice, Date currentDateTime, boolean needUpdateMovingAverage) {
		updateCurrentPrice(currentPrice, currentDateTime);
		setInitialPrice(currentPrice, currentDateTime);
		updateHighPrice(currentPrice, currentDateTime);
		updateLowPrice(currentPrice, currentDateTime);
		
		if (needUpdateMovingAverage) {
			updateCurrentMovingAverage(currentPrice, currentDateTime);
			updateLastMovingAverage();
			updateHighMovingAverage(currentPrice, currentDateTime);
			updateLowMovingAverage(currentPrice, currentDateTime);
			this.lastDateTime = currentDateTime;                       // moving average will be based on the last date time too
		}
		
		this.lastPrice = 0d;
	}
	
	public void refresh(double newCurrentPrice, double newCurrentMovingAverage, Date newCurrentDateTime) {
		updateLastPrice(newCurrentPrice);
		updateCurrentPrice(newCurrentPrice, newCurrentDateTime);
		updateInitialPrice(newCurrentPrice);
		updateHighPrice(newCurrentPrice, newCurrentDateTime);
		updateLowPrice(newCurrentPrice, newCurrentDateTime);
		
		updateLastMovingAverage();
		updateCurrentMovingAverage(newCurrentMovingAverage, currentDateTime);
		updateHighMovingAverage(newCurrentMovingAverage, currentDateTime);
		updateLowMovingAverage(newCurrentMovingAverage, currentDateTime);
	}
	
	public void reset (String status) {
		this.status    = status;
		this.highPrice = - Double.MAX_VALUE;
		this.lowPrice  = Double.MAX_VALUE;
		this.highMovingAverage = - Double.MAX_VALUE;
		this.lowMovingAverage = Double.MAX_VALUE;
		initialize(this.currentPrice, this.currentDateTime, false);
	}
	
	protected void setInitialPrice(double currentPrice, Date currentDateTime) {
		this.initialPrice = currentPrice;
		this.initialDateTime = currentDateTime;
	}
	
	protected void updateLastPrice(double newCurrentPrice) {
		this.lastPrice = this.currentPrice;
		this.lastDateTime = this.currentDateTime;
		this.lastChangeDelta = newCurrentPrice - this.lastPrice;
		this.lastChangePercent = this.lastChangeDelta / this.lastPrice;
		this.lastChangeDisplayPercent = lastChangePercent * 100.0d;
	}
	
	protected void updateCurrentPrice(double newCurrentPrice, Date newCurrentDateTime) {
		this.currentPrice = newCurrentPrice;
		this.currentDateTime = newCurrentDateTime;
	}
	
	protected void updateInitialPrice(double newCurrentPrice) {
		this.initialChangeDelta = newCurrentPrice - this.initialPrice;
		this.initialChangePercent = initialChangeDelta / this.initialPrice;
		this.initialChangeDisplayPercent = initialChangePercent * 100d;
	}
	
	protected void updateHighPrice(double newCurrentPrice, Date newCurrentDateTime) {
		if (newCurrentPrice >= this.highPrice) {
			this.highPrice    = newCurrentPrice;
			this.highDateTime = newCurrentDateTime;
		}
		this.highGapDelta           = newCurrentPrice - this.highPrice;
		this.highDownPercent        = highGapDelta / this.highPrice;
		this.highDownDisplayPercent = highDownPercent * 100d;
	}
	
	protected void updateLowPrice(double newCurrentPrice, Date newCurrentDateTime) {
		if (newCurrentPrice <= this.lowPrice) {
			this.lowPrice    = newCurrentPrice;
			this.lowDateTime = newCurrentDateTime;
		}
		this.lowGapDelta         = newCurrentPrice - this.lowPrice;
		this.lowUpPercent        = lowGapDelta / this.lowPrice;
		this.lowUpDisplayPercent = lowUpPercent * 100d;
	}
	
	protected void updateCurrentMovingAverage(double currentMovingAverage, Date currentDateTime) {
		this.currentMovingAverage = currentMovingAverage;
	}
	
	protected void updateLastMovingAverage() {
		this.lastMovingAverage = this.currentMovingAverage;
	}
	
	protected void updateHighMovingAverage(double newCurrentMovingAverage, Date currentDateTime) {
		if (newCurrentMovingAverage >= this.highMovingAverage) {
			this.highMovingAverage         = newCurrentMovingAverage;
			this.highMovingAverageDateTime = currentDateTime;
		}
		this.highMovingAverageGapDelta    = newCurrentMovingAverage - this.highMovingAverage;
		this.highMovingAverageDownPercent = highMovingAverageGapDelta / this.highMovingAverage;
	}
	
	protected void updateLowMovingAverage(double newCurrentMovingAverage, Date currentDateTime) {
		if (newCurrentMovingAverage <= this.lowMovingAverage) {
			this.lowMovingAverage         = newCurrentMovingAverage;
			this.lowMovingAverageDateTime = currentDateTime;
		}
		this.lowMovingAverageGapDelta  = newCurrentMovingAverage - this.lowMovingAverage;
		this.lowMovingAverageUpPercent = lowMovingAverageGapDelta / this.lowMovingAverage;
	}
	
	@Override
	public Uri getUri() {
		return null;
	}
	@Override
	public boolean isPersisted() {
		return false;
	}
//	@Override
//	public String toString() {
//		DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
//		String headerLine = String.format("%s %8s %24s %37s %36s", "Symbol", "Current", "Last", "High", "Low");
//		String dataLine   = String.format("%6s %7s %20s %7s %7s%% %20s %7s %7s%% %20s %7s %7s%% %20s", symbol, 
//				NumUtil.round(currentPrice, 2), df.format(currentDateTime), 
//				NumUtil.round(lastPrice, 2), NumUtil.round(lastChangeDisplayPercent, 2), df.format(lastDateTime), 
//				NumUtil.round(highPrice, 2), NumUtil.round(highDownDisplayPercent, 2),   df.format(highDateTime), 
//				NumUtil.round(lowPrice, 2),  NumUtil.round(lowUpDisplayPercent, 2),      df.format(lowDateTime));
//		return Joiner.on("\n").join(Arrays.asList(headerLine, dataLine));
//	}
	
	@Override
	public String toString() {		
		List<String> param1List = Arrays.asList(
				symbol,
				"currP="  + NumUtil.round(currentPrice,             2), 
				"lastP="  + NumUtil.round(lastPrice,                2),
				"last%P=" + NumUtil.round(lastChangeDisplayPercent, 2) + "%");
		
		List<String> param2List = Arrays.asList( 
				symbol, 
				"highP="   + NumUtil.round(highPrice,              2), 
				"highD%P=" + NumUtil.round(highDownDisplayPercent, 2) + "%",
				"lowP="    + NumUtil.round(lowPrice,               2), 
		        "lowU%P="  + NumUtil.round(lowUpDisplayPercent,    2) + "%");
		
		List<String> param3List = Arrays.asList( 
				symbol, 
				"currMA="   + NumUtil.round(currentMovingAverage,         2), 
				"lastMA="   + NumUtil.round(lastMovingAverage,            2), 
				"highMA="   + NumUtil.round(highMovingAverage,                   2),
		        "highD%MA=" + NumUtil.round(highMovingAverageDownPercent * 100d, 2) + "%",
		        "lowMA="    + NumUtil.round(lowMovingAverage,                    2),
                "lowU%MA="  + NumUtil.round(lowMovingAverageUpPercent * 100d,    2) + "%");
		
		String param1String = Joiner.on(" ").join(param1List);
		String param2String = Joiner.on(" ").join(param2List);
		String param3String = Joiner.on(" ").join(param3List);
		
		return Joiner.on("\n").join(Arrays.asList(param1String, param2String, param3String));
	}
}
