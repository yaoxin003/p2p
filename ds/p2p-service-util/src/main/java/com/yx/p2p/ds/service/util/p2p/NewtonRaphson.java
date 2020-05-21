package com.yx.p2p.ds.service.util.p2p;
import static java.lang.Math.abs;
/**
 * 本类原来是用来证券计算IRR，现通过将代替NPresentValueAPI原来的newtonroot
 * @author Phil Barker
 * @see Derivative
 * @see NPresentValueAPI
 */
public abstract class NewtonRaphson extends Derivative{
    int counter=0;
	//public abstract double newtonroot(double rootvalue);  //the requesting function implements the calculation fx//
	//public double precisionvalue=0.0;
    //修改于2009-05-17,将public修改为private,并增加get,set方法
    private double precisionvalue=1e-6;
    //修改于2009-05-17,增加传入现金流数组
    private double[] cashflows;
    //public int iterate=0;
    //修改于2009-05-17,将public修改为private,并增加get,set方法
    private int iterate=50;


/**
 * Description:初始化IRR参数并调用newtraph来计算IRR
 * @param lowerbound：对比值,默认0.05。
 * @param cashflows:现金流数组
 * @return IRR
 */
public  double accuracy(double lowerbound,double[] cashflows){//method gets the desired accuracy//

	//super.h=precision;//sets the superclass derivative to the desired precision//
	if(super.h!=this.precisionvalue) super.h=this.precisionvalue;
	this.precisionvalue=super.h;
	//this.iterate=iterations;
	this.setCashflows(cashflows);
    return this.newtraph(lowerbound, cashflows);
}
/**
 * 计算IRR
 * @param lowerbound：对比值,默认0.05。
 * @param cashflows:现金流数组
 * @return IRR
 */
	public double newtraph(double lowerbound,double[] cashflows){

		//System.out.println("Accuravcy levels=="+precisionvalue+"ITERATIONS=="+iterate);
         double fx=0.0;
         double Fx=0.0;
         double x=0.0;
         
         //fx=floorvalue(newtonroot(lowerbound));
         fx=floorvalue(NPresentValueAPI.pV(lowerbound,cashflows));
		 Fx=floorvalue(derivation(lowerbound));

		 x=floorvalue((lowerbound-(fx/Fx)));

		while((Math.abs(x-lowerbound)>precisionvalue&counter<iterate))
		{

			lowerbound=x;
			 //fx=newtonroot(lowerbound);
			fx=NPresentValueAPI.pV(lowerbound,this.getCashflows()) ;
			Fx=derivation(lowerbound);
			x=floorvalue((lowerbound-(fx/Fx)));
			counter++;
		}
	//System.out.println("The Solution is:....................."+x);
	return x;
	}
	/* (non-Javadoc)
	 * @see com.creditease.util.Derivative#deriveFunction(double)
	 * @param inputa：对比值,默认0.05。
	 */
	public double deriveFunction(double inputa){

            double x1=0.0;
		// x1=newtonroot(inputa);
        x1=NPresentValueAPI.pV(inputa,this.getCashflows()) ;
				return x1;

	}
    public double floorvalue(double x){

		return abs(x)<Csmallnumber.getSmallnumber()?Csmallnumber.getSmallnumber():x;
	}

		public double[] getCashflows() {
			return cashflows;
		}

		public void setCashflows(double[] cashflows) {
			this.cashflows = cashflows;
		}
		public int getIterate() {
			return iterate;
		}
		public void setIterate(int iterate) {
			this.iterate = iterate;
		}
		public double getPrecisionvalue() {
			return precisionvalue;
		}
		public void setPrecisionvalue(double precisionvalue) {
			this.precisionvalue = precisionvalue;
		}

		/**
		 * Descripsion:原证券计算IRR用来初始化精度和迭代，本应用不采用
		 * @param precision
		 * @param iterations
		 */
		public void accuracy(double precision,int iterations){//method gets the desired accuracy//

			super.h=precision;//sets the superclass derivative to the desired precision//
			this.precisionvalue=precision;
			this.iterate=iterations;


		}
}


