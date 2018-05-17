package com.gree.gprs.uart.delegate;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter.ControlInterface;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.variable.Variable;

public class UartControlDelegate implements ControlInterface {

	public void controlPriod() {

		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			UartModel.Server_Modbus_Word_Data[19] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			UartModel.Server_Modbus_Word_Data[19] = (byte) 0x00;

		} else {

			UartModel.Server_Modbus_Word_Data[19] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			UartModel.Server_Modbus_Word_Data[21] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			UartModel.Server_Modbus_Word_Data[21] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			UartModel.Server_Modbus_Word_Data[21] = (byte) 0x03;
			break;

		default:
			UartModel.Server_Modbus_Word_Data[21] = (byte) 0x00;
			break;
		}

		UartModel.Server_Modbus_Word_Data[23] = (byte) Variable.Network_Signal_Level;

		UartModel.Input_Registers_Stack.update(19, UartModel.Server_Modbus_Word_Data, 19, 3);
	}

}
