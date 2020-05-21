package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;

/**
 * 目前通过getDayIRR这个method计算IRR。
 * @see NewtonRaphson
 */
public class IRRapi extends NewtonRaphson {

	 /**
	 * NewtonRaphson中默认精度：  private double precisionvalue=1e-6;
	 * NewtonRaphson中默认迭代：   private int iterate=50;
	 * 如需修改精度和迭代可通过setter方式传入。
	 * @param estimatedResult：传入Double.NaN则默认0.0005,否则传入自定义对比值。
	 * @param cashFlows:现金流
	 * @return IRR
	 */
	static public double getDayIRR(final double estimatedResult,final double[] cashFlows ) {
		 double result = 0;
		 double defaultEstimated = 0.0005;//设置参数 ， 精度和迭代
		 if (!String.valueOf(estimatedResult).equals(String.valueOf(Double.NaN))){
			 defaultEstimated=estimatedResult;
         }
		 IRRapi me=new IRRapi();
		 result=me.accuracy(defaultEstimated,cashFlows);
		 return result;
	 }
	 static public double getMonthIRR(final double[] cashFlows, final double estimatedResult) {
		 double result = 0;
		 return result;
	 }
	 /**
	  * 
	  *
	  * Description:根据月IRR获取年IRR 
	  *
	  * @param BigDecimal monthIrr 日IRR
	  * @return BigDecimal yearIrr 年IRR
	  * @throws
	  * @Author wangxinguang 
	  * Create Date: 2013-10-29
	  */
	public static BigDecimal getYearIRRByMonthIRR(BigDecimal monthIrr){
		BigDecimal add = monthIrr.add(new BigDecimal("1"));
		BigDecimal pow = add.pow(12);
		BigDecimal yearIrr = pow.subtract(new BigDecimal("1"));
		return yearIrr;
	}
	
	/**
	 * 
	 *
	 * Description:根据日IRR获取年IRR 
	 *
	 * @param BigDecimal dayIrr 日IRR
	 * @return BigDecimal yearIrr 年IRR
	 * @throws
	 * @Author wangxinguang 
	 * Create Date: 2013-10-29
	 */
	public static BigDecimal getYearIRRByDayIRR(BigDecimal dayIrr){
		BigDecimal add = dayIrr.add(new BigDecimal("1"));
		BigDecimal pow = add.pow(365);
		BigDecimal yearIrr = pow.subtract(new BigDecimal("1"));
		return yearIrr;		
	}
}
