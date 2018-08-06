package personal.wuyi.autostock.io.persist.entity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.wuyi.autostock.io.persist.exception.EntityNotFoundException;

public class EntityHelper {
	private static Logger logger = LoggerFactory.getLogger(EntityHelper.class);
	
	public static StockOrder getStockOrder(String symbol, List<StockOrder> orderList) throws EntityNotFoundException {
		for (StockOrder order : orderList) {
			if (symbol.equalsIgnoreCase(order.getSymbol())) {
				return order;
			}
		}
		throw new EntityNotFoundException("Can not find StockOrder for symbol: " + symbol);
	}
	
	public static StockStaticMetrics getStockStaticMetrics(String symbol, List<StockStaticMetrics> staticMetricsList) throws EntityNotFoundException {
		for (StockStaticMetrics staticMetrics : staticMetricsList) {
			if (symbol.equals(staticMetrics.getSymbol())) {
				return staticMetrics;
			}
		}
		throw new EntityNotFoundException("Can not find StockStaticMetrics for symbol: " + symbol);
	}
	
	public static StockRealTimeMetrics getStockRealTimeMetrics(String symbol, List<StockRealTimeMetrics> realTimeMetricsList) throws EntityNotFoundException {
		for (StockRealTimeMetrics realTimeMetrics : realTimeMetricsList) {
			if (symbol.equals(realTimeMetrics.getSymbol())) {
				return realTimeMetrics;
			}
		}
		throw new EntityNotFoundException("Can not find StockRealTimeMetrics for symbol: " + symbol);
	}
	
	public static List<String> getStockSymbolList(List<StockRealTimeMetrics> srtmList) {
		List<String> symbolList = new ArrayList<>();
		for (StockRealTimeMetrics srtm : srtmList) {
			symbolList.add(srtm.getSymbol());
		}
		return symbolList;
	}
	
	public static boolean isExistInStockRealTimeMetricsList(String symbol, List<StockRealTimeMetrics> srtmList) {
		for (StockRealTimeMetrics srtm : srtmList) {
			if (symbol.equals(srtm.getSymbol())) {
				return true;
			}
		}
		return false;
	}
	
	public static List<OperationDecision> filterOperationDecisionList(List<OperationDecision> decisionList, DecisionType type) {
		List<OperationDecision> newDecisionList = new ArrayList<>();
		
		for (OperationDecision decision : decisionList) {
			if (decision.getType() == type) {
				newDecisionList.add(decision);
			}
		}
		
		return newDecisionList;
	}
	
	public static void displayStockRealTimeMetricsList(List<StockRealTimeMetrics> srtmList) {
		for (StockRealTimeMetrics srtm : srtmList) {
			logger.info(srtm.toString());
		}
	}
	
	public static void displayDecisionList(List<OperationDecision> decisionList) {
		for (OperationDecision decision : decisionList) {
			logger.info(decision.toString());
		}
	}
}	
