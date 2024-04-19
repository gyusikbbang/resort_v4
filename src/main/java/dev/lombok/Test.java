package dev.lombok;




public class Test {
	 public static void main(String[] args) {
     DevVO devVO = new DevVO();
     
     devVO.setName("ㅇㅇ");
     devVO.setYear(3.5);
     devVO.setPayTot(3000000);
     devVO.setPay_tax(5.3);
     
     System.out.println("성명: " + devVO.getName());
     System.out.println("경력: " + devVO.getYear());
     System.out.println("급여: " + devVO.getPayTot());
     System.out.println("세금(%): " + devVO.getPay_tax() + " %");
	 }
}
