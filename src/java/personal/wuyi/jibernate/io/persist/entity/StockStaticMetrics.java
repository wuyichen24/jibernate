package personal.wuyi.autostock.io.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="stock_static_metrics")
public class StockStaticMetrics extends AbstractAutoStockEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")                                       private Long   id;
	@Column(name="symbol")                                   private String symbol;
	@Column(name="tolerated_down_percentage")                private double toleratedDownPercentage;
	@Column(name="tolerated_up_percentage")                  private double toleratedUpPercentage;
	@Column(name="tolerated_moving_average_down_percentage") private double toleratedMovingAverageDownPercentage;
	@Column(name="tolerated_moving_average_up_percentage")   private double toleratedMovingAverageUpPercentage	;
	
	public Long   getId()                                                                              { return id;                                                                        }
	public void   setId(Long id)                                                                       { this.id = id;                                                                     }
	public String getSymbol()                                                                          { return symbol;                                                                    }
	public void   setSymbol(String symbol)                                                             { this.symbol = symbol;                                                             }
	public double getToleratedDownPercentage()                                                         { return toleratedDownPercentage;                                                   }
	public void   setToleratedDownPercentage(double toleratedDownPercentage)                           { this.toleratedDownPercentage = toleratedDownPercentage;                           }
	public double getToleratedUpPercentage()                                                           { return toleratedUpPercentage;                                                     }
	public void   setToleratedUpPercentage(double toleratedUpPercentage)                               { this.toleratedUpPercentage = toleratedUpPercentage;                               }
	public double getToleratedMovingAverageDownPercentage()                                            { return toleratedMovingAverageDownPercentage;                                      }
	public void   setToleratedMovingAverageDownPercentage(double toleratedMovingAverageDownPercentage) { this.toleratedMovingAverageDownPercentage = toleratedMovingAverageDownPercentage; }
	public double getToleratedMovingAverageUpPercentage()                                              { return toleratedMovingAverageUpPercentage;                                        }
	public void   setToleratedMovingAverageUpPercentage(double toleratedMovingAverageUpPercentage)     { this.toleratedMovingAverageUpPercentage = toleratedMovingAverageUpPercentage;     }
}
