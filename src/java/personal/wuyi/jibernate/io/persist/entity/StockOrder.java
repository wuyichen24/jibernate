package personal.wuyi.autostock.io.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import personal.wuyi.autostock.io.persist.core.Uri;

@Entity
@Table(name="stock_order")
public class StockOrder extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")                 private Long         id;
	@Column(name="symbol")             private String       symbol;
	@Column(name="quantity")           private int          quantity;
	@Column(name="market_price")       private double       marketPrice;
	@Column(name="market_total_value") private double       marketTotalValue;
	@Column(name="market_date_time")   private Date         marketDateTime;
	@Column(name="buy_price")          private double       buyPrice;
	@Column(name="buy_total_cost")     private double       buyTotalCost;
	@Column(name="buy_date_time")      private Date         buyDateTime;
	@Column(name="sell_price")         private double       sellPrice;
	@Column(name="sell_total_cost")    private double       sellTotalCost;
	@Column(name="sell_date_time")     private Date         sellDateTime;
	@Column(name="earning")            private double       earning;
	@Enumerated(EnumType.STRING)
	@Column(name="status")             private BoughtOrSold status;
	
	public Long         getId()                                      { return id;                                }
	public void         setId(Long id)                               { this.id = id;                             }
	public String       getSymbol()                                  { return symbol;                            }
	public void         setSymbol(String symbol)                     { this.symbol = symbol;                     }
	public int          getQuantity()                                { return quantity;                          }
	public void         setQuantity(int quantity)                    { this.quantity = quantity;                 }
	public double       getMarketPrice()                             { return marketPrice;                       }
	public void         setMarketPrice(double marketPrice)           { this.marketPrice = marketPrice;           }
	public double       getMarketTotalValue()                        { return marketTotalValue;                  }
	public void         setMarketTotalValue(double marketTotalValue) { this.marketTotalValue = marketTotalValue; }
	public Date         getMarketDateTime()                          { return marketDateTime;                    }
	public void         setMarketDateTime(Date marketDateTime)       { this.marketDateTime = marketDateTime;     }
	public double       getBuyPrice()                                { return buyPrice;                          }
	public void         setBuyPrice(double buyPrice)                 { this.buyPrice = buyPrice;                 }
	public double       getBuyTotalCost()                            { return buyTotalCost;                      }
	public void         setBuyTotalCost(double buyTotalCost)         { this.buyTotalCost = buyTotalCost;         }
	public Date         getBuyDateTime()                             { return buyDateTime;                       }
	public void         setBuyDateTime(Date buyDateTime)             { this.buyDateTime = buyDateTime;           }
	public double       getSellPrice()                               { return sellPrice;                         }
	public void         setSellPrice(double sellPrice)               { this.sellPrice = sellPrice;               }
	public Date         getSellDateTime()                            { return sellDateTime;                      }
	public void         setSellDateTime(Date sellDateTime)           { this.sellDateTime = sellDateTime;         }
	public double       getSellTotalCost()                           { return sellTotalCost;                     }
	public void         setSellTotalCost(double sellTotalCost)       { this.sellTotalCost = sellTotalCost;       }
	public double       getEarning()                                 { return earning;                           }
	public void         setEarning(double earning)                   { this.earning = earning;                   }
	public BoughtOrSold getStatus()                                  { return status;                            }
	public void         setStatus(BoughtOrSold status)               { this.status = status;                     }
	
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
