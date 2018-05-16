package com.gree.gprs.variable;

public class UartVariable {
	
	// 串口通讯Buffer
	public static byte[] Uart_In_Buffer = new byte[512];
	public static byte[] Uart_Out_Buffer = new byte[512];
	public static int Uart_In_Buffer_Length = 0;
	
	public static byte[] Server_7E_Data = new byte[65];
	public static byte[] Server_Modbus_Word_Data = new byte[720];	
	public static byte[] Server_Modbus_Bit_Data = new byte[6];	
}
