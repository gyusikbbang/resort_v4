package dev.lombok;

//@ @RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
public class DevVO {
    private String name="";
    private double year = 1.5;
    private int payTot = 2500000;
    private double pay_tax = 0.0;
    
    
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public double getYear() {
			return year;
		}
		
		public void setYear(double year) {
			this.year = year;
		}
		
		public int getPayTot() {
			return payTot;
		}
		
		public void setPayTot(int payTot) {
			this.payTot = payTot;
		}
    
    public double getPay_tax() {
    	return pay_tax;
    }
    
    public void setPay_tax(double pay_tax) {
    	 this.pay_tax = pay_tax;
    }
}
 