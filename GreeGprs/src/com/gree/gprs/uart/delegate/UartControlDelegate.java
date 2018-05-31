package com.gree.gprs.uart.delegate;

import com.gree.gprs.control.ControlCenter.ControlInterface;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.SeveneModel;

public class UartControlDelegate implements ControlInterface {

	public void controlPriod() {

		switch (UartModel.Uart_Type) {

		case UartModel.UART_TYPE_MODBUS:

			MbReadWordModel.updateData();

			break;

		case UartModel.UART_TYPE_7E:

			SeveneModel.updateData();

			break;
		}
	}
}
