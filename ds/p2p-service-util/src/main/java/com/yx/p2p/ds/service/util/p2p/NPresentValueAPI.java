package com.yx.p2p.ds.service.util.p2p;
import static java.lang.Math.*;
/**
 *
 * The class NPresentValueAPI contains methods to calculate present values of a series
 *of cashflows. There are three basic methods in the class.
 * @author Phil Barker
 * @see IRRapi
 * 目前使用方法如下，
 * 先通过IRRapi.getDayIRR(final double estimatedResult,final double[] cashFlows )
 * 得到IRR，做为r的传入参数，同时传入现金流。
 * 	double result1=pV(double r,double[] cashflows)
 */
public final class NPresentValueAPI {
   /** creates a new instance of PresentValue */
   /*
	public NPresentValueAPI() {
	}
   */
	static public double pV(double[] discounts,double[]cashflows){
		int n=cashflows.length;
		double presval=0;
		// Interest Rate Calculations
		for(int i=0;i<n;i++){
			presval+=discounts[i]*cashflows[i];
			// returns sum of
			// discounted values..
	        //for each period cashflow
	     }
	         return presval;
	}
	/**
	 * @param r ： IRR，IRRapi.getDayIRR(final double estimatedResult,final double[] cashFlows )
	 * @param cashflows：现金流。	 *
	 * @return
	 */
	static public  double pV(double r,double[] cashflows){
		int indx=0;
		double sum=0;
		for(int i=0;i<cashflows.length;i++){
			sum+=(cashflows[i]/(pow((1+r),(indx))));//Implements//PV =
			indx++;
		}
		return sum;
	}
	static public double pV(double r,double cash,int period){
		double sum=0;
		int indx=1;
		for(int i=0;i<period;i++){
			sum+=(cash/(pow((1+r),(indx))));// Implements PV =
			indx++;
		}
		return sum;
	}
}