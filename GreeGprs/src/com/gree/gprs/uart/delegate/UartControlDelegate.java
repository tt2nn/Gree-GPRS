package com.gree.gprs.uart.delegate;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter.ControlInterface;
import com.gree.gprs.variable.UartVariable;
import com.gree.gprs.variable.Variable;

public class UartControlDelegate implements ControlInterface {

	public void controlPriod() {

		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			UartVariable.Server_Modbus_Word_Data[19] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			UartVariable.Server_Modbus_Word_Data[19] = (byte) 0x00;

		} else {

			UartVariable.Server_Modbus_Word_Data[19] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			UartVariable.Server_Modbus_Word_Data[21] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			UartVariable.Server_Modbus_Word_Data[21] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			UartVariable.Server_Modbus_Word_Data[21] = (byte) 0x03;
			break;

		default:
			UartVariable.Server_Modbus_Word_Data[21] = (byte) 0x00;
			break;
		}

		UartVariable.Server_Modbus_Word_Data[23] = (byte) Variable.Network_Signal_Level;

		UartVariable.Input_Registers_Stack.update(19, UartVariable.Server_Modbus_Word_Data, 19, 3);
	}

}
