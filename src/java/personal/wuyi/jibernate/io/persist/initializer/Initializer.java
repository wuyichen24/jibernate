package personal.wuyi.autostock.io.persist.initializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import personal.wuyi.autostock.io.persist.config.MysqlDbConfig;
import personal.wuyi.autostock.io.persist.core.AutoStockEntityManagerDao;
import personal.wuyi.autostock.io.persist.entity.Account;
import personal.wuyi.autostock.io.persist.entity.BoughtOrSold;
import personal.wuyi.autostock.io.persist.entity.StockOrder;
import personal.wuyi.autostock.io.persist.entity.StockStaticMetrics;

public class Initializer {
	private static MysqlDbConfig              dbConfig;
	private static AutoStockEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException {
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new AutoStockEntityManagerDao(dbConfig);
	}
	
	@Test
	public void initializeStockOrder() throws ParseException {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		StockOrder order = new StockOrder();
		order.setSymbol("IQ");
		order.setQuantity(2);
		order.setBuyPrice(36.3);
		order.setBuyTotalCost(72.6);
		order.setBuyDateTime(df.parse("07/16/2018 12:29:00"));
		order.setStatus(BoughtOrSold.BOUGHT);
		
		dao.updateStockOrder(order);
	}
	
	@Test
	public void initializeStockStaticMetrics() {
		StockStaticMetrics ssm = new StockStaticMetrics();
		ssm.setSymbol("IQ");
		ssm.setToleratedDownPercentage(-0.005);
		ssm.setToleratedUpPercentage(0.01);
		ssm.setToleratedMovingAverageDownPercentage(-0.0014);
		ssm.setToleratedMovingAverageUpPercentage(0.001);
		dao.updateStockStaticMetrics(ssm);
	}
	
	@Test
	public void initializeAccount() {
		Account account = new Account();
		account.setFirstName("Wuyi");
		account.setLastName("Chen");
		account.setPortfolioValue(9479.05);
		account.setStockValue(388.24);
		account.setCashValue(9090.81);
		account.setDayTradeLimit(4);
		account.setDayTradeCount(0);
		account.setDayTradeDate(new Date());
		account.setReadLock(false);
		dao.updateLockedAccount(account);
	}
}
