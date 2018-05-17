package com.gree.gprs;

import com.gree.gprs.variable.Variable;

public class UartBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.6";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x06;
		
		init();
	}

}
