package personal.wuyi.jibernate.io.persist.core;

import java.util.ArrayList;
import java.util.List;

import personal.wuyi.autostock.io.persist.config.MysqlDbConfig;
import personal.wuyi.autostock.io.persist.entity.Account;
import personal.wuyi.autostock.io.persist.entity.BoughtOrSold;
import personal.wuyi.autostock.io.persist.entity.BoughtOrWatch;
import personal.wuyi.autostock.io.persist.entity.HistoricalStockPrice;
import personal.wuyi.autostock.io.persist.entity.StockOrder;
import personal.wuyi.autostock.io.persist.entity.StockRealTimeMetrics;
import personal.wuyi.autostock.io.persist.entity.StockStaticMetrics;
import personal.wuyi.autostock.io.persist.exception.ConcurrentTimeOutException;
import personal.wuyi.autostock.io.persist.expression.Expression;
import personal.wuyi.autostock.io.persist.query.EntityQuery;

public class AutoStockEntityManagerDao extends MysqlEntityManagerDao {
	public AutoStockEntityManagerDao (MysqlDbConfig config) {
		super(config);
	}
	
	public void updateStockRealTimeMetricsList(List<StockRealTimeMetrics> srtmList) {
		write(srtmList);
	}
	
	public void updateStockRealTimeMetrics(StockRealTimeMetrics srtm) {
		write(srtm);
	}
	
	public void updateStockOrderList(List<StockOrder> orderList) {
		write(orderList);
	}
	
	public void updateStockOrder(StockOrder order) {
		write(order);
	}
	
	public void updateStockStaticMetrics(StockStaticMetrics ssm) {
		write(ssm);
	}
	
	public List<StockRealTimeMetrics> getBuyList() {
        return getStockRealTimeMetrics(BoughtOrWatch.BOUGHT);
	}
	
	public List<StockRealTimeMetrics> getWatchList() {
        return getStockRealTimeMetrics(BoughtOrWatch.WATCH);
	}
	
	public List<StockRealTimeMetrics> getStockRealTimeMetrics(BoughtOrWatch status) {
		EntityQuery<StockRealTimeMetrics> query = new EntityQuery<StockRealTimeMetrics>(StockRealTimeMetrics.class);
		query.setCriteria(new Expression("status", Expression.EQUAL, status));
        return read(query);
	}
	
	public List<StockOrder> getStockOrderList(List<String> symbolList) {
		List<StockOrder> orderList = new ArrayList<>();
		
		for (String symbol : symbolList) {
			EntityQuery<StockOrder> query = new EntityQuery<StockOrder>(StockOrder.class);
			query.setCriteria(new Expression("symbol", Expression.EQUAL, symbol).and("status", Expression.EQUAL, BoughtOrSold.BOUGHT));
			orderList.addAll(read(query));
		}
		
		return orderList;
	}
	
	public List<StockStaticMetrics> getStockStaticMetricsList(List<String> symbolList) {
		EntityQuery<StockStaticMetrics> query = new EntityQuery<StockStaticMetrics>(StockStaticMetrics.class);
		
		Expression e = new Expression("symbol", Expression.EQUAL, symbolList.get(0));
		for (int i = 1; i < symbolList.size(); i++) {
			e.or("symbol", Expression.EQUAL, symbolList.get(i));
		}
		
		query.setCriteria(e);
		return read(query);
	}
	
	public Account getUnlockedAccountById (long id) throws InterruptedException, ConcurrentTimeOutException {
		int currentTime = 0;
		int maxTryTime = 30;
		
		while (true) {
			EntityQuery<Account> query = new EntityQuery<Account>(Account.class);
			query.setCriteria(new Expression("id", Expression.EQUAL, id).and("readLock", Expression.EQUAL, false));  // readlock is not ok, readLock is ok
			List<Account> accountList = read(query);
			if (accountList.isEmpty()) {
				currentTime++;
				if (currentTime > maxTryTime) {
					throw new ConcurrentTimeOutException("Can not get the unlocked Account record: " + id);
				} else {
					Thread.sleep(500);
					continue;
				}
			} else {
				Account account = accountList.get(0);
				lockAccount(account);
				return account;
			}
		}
	}
	
	public void lockAccount(Account account) {
		account.setReadLock(true);
		write(account);
	}
	
	public void updateLockedAccount (Account account) {
		account.setReadLock(false);
		write(account);
	}
	
	public void storeHistoricalStockPriceList(List<HistoricalStockPrice> hspList) {
		write(hspList);
	}
	
	public List<HistoricalStockPrice> getHistoricalStockPriceList(String symbol, int year, int month, int day) {
		EntityQuery<HistoricalStockPrice> query = new EntityQuery<HistoricalStockPrice>(HistoricalStockPrice.class);
		
		Expression e = new Expression("symbol", Expression.EQUAL, symbol)
				.and("year", Expression.EQUAL, year)
				.and("month", Expression.EQUAL, month)
				.and("day", Expression.EQUAL, day);
		
		query.setCriteria(e);
		return read(query);
	}
}
