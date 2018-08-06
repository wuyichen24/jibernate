package personal.wuyi.autostock.io.persist.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Joiner;

@Entity
@Table(name="operation_decision")
public class OperationDecision extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")                    private Long          id;
	@Column(name="symbol")                private String        symbol;
	@Enumerated(EnumType.STRING)
	@Column(name="type")                  private DecisionType  type;
	@Column(name="quantity")              private int           quantity;
	@Column(name="create_date_time")      private Date          createDateTime;
	@Column(name="valid_until_date_time") private Date          validUntilDateTime;
	@Column(name="executed_date_time")    private Date          executedDateTime;
	@Column(name="execution_price")       private double        executionPrice;
	@Column(name="is_executed")           private boolean       isExecuted = false;
	@Column(name="made_by")               private String        madeBy;
	
	public Long          getId()                                        { return id;                                    }
	public void          setId(Long id)                                 { this.id = id;                                 }
	public String        getSymbol()                                    { return symbol;                                }
	public void          setSymbol(String symbol)                       { this.symbol = symbol;                         }
	public DecisionType  getType()                                      { return type;                                  }
	public void          setType(DecisionType type)                     { this.type = type;                             }
	public int           getQuantity()                                  { return quantity;                              }
	public void          setQuantity(int quantity)                      { this.quantity = quantity;                     }
	public Date          getCreateDateTime()                            { return createDateTime;                        }
	public void          setCreateDateTime(Date createDateTime)         { this.createDateTime = createDateTime;         }
	public Date          getValidUntilDateTime()                        { return validUntilDateTime;                    }
	public void          setValidUntilDateTime(Date validUntilDateTime) { this.validUntilDateTime = validUntilDateTime; }
	public Date          getExecutedDateTime()                          { return executedDateTime;                      }
	public void          setExecutedDateTime(Date executedDateTime)     { this.executedDateTime = executedDateTime;     }
	public double        getExecutionPrice()                            { return executionPrice;                        }
	public void          setExecutionPrice(double executionPrice)       { this.executionPrice = executionPrice;         }
	public boolean       isExecuted()                                   { return isExecuted;                            }
	public void          setExecuted(boolean isExecuted)                { this.isExecuted = isExecuted;                 }
	public String        getMadeBy()                                    { return madeBy;                                }
	public void          setMadeBy(String madeBy)                       { this.madeBy = madeBy;                         }
	
	public OperationDecision (String symbol, String madeBy) {
		this.symbol = symbol;
		this.madeBy = madeBy;
	}
	
	public void makeDecision(DecisionType type, int quantity, long timeout, TimeUnit unit) {
		long timeoutInMillis = unit.toMillis(timeout);
		Date createDateTime = new Date();
		Date validUntilDateTime = new Date(createDateTime.getTime() + timeoutInMillis);
		makeDecision(type, quantity, createDateTime, validUntilDateTime);
	}
	
	public void makeDecision(DecisionType type, int quantity, Date validUntilDateTime) {
		makeDecision(type, quantity, new Date(), validUntilDateTime);
	}
	
	public void makeDecision(DecisionType type, int quantity, Date createDateTime, Date validUntilDateTime) {
		this.type               = type;
		this.quantity           = quantity;
		this.createDateTime     = createDateTime;
		this.validUntilDateTime = validUntilDateTime;
		this.isExecuted         = false;
	}
	
	@Override
	public String toString() {
		return Joiner.on(" ").join(Arrays.asList(symbol, type, quantity));
	}
}
