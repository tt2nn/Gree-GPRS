package com.gree.gprs.uart;

import java.io.IOException;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.model.FrockCheckModel;
import com.gree.gprs.uart.model.MbReadBitModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.MbWriteModel;
import com.gree.gprs.uart.model.SeveneModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.UartUtils;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.UartVariable;
import com.joshvm.greenland.io.modbus.HoldingRegistersStack;
import com.joshvm.greenland.io.modbus.ModbusController;

/**
 * Uart功能
 * 
 * @author lihaotian
 *
 */
public class UartModel {

	private static byte[] frockBytes = { (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x15, (byte) 0x00,
			(byte) 0x00, (byte) 0x5D, (byte) 0x36 };

	public static final int UART_TYPE_7E = 1;
	public static final int UART_TYPE_MODBUS = 2;

	public static void init() {

		try {

			HoldingRegistersStack holdingRegistersStack = ModbusController.getModbusController()
					.allocateHoldingRegisters(0, 0, false);
			holdingRegistersStack.setVotingFrameFilter0(10, Constant.FUNCTION_CHOOSE);

			UartVariable.Input_Registers_Stack = ModbusController.getModbusController().allocateInputRegisters(0, 360,
					true);

			// word0
			UartVariable.Server_Modbus_Word_Data[0] = (byte) 0x00;
			UartVariable.Server_Modbus_Word_Data[1] = Constant.GPRS_MODEL;

			// word1~8
			byte[] imeiBytes = Device.getInstance().getImei().getBytes();
			for (int i = 0; i < imeiBytes.length; i++) {

				UartVariable.Server_Modbus_Word_Data[i + 2] = imeiBytes[i];
			}
			UartVariable.Server_Modbus_Word_Data[17] = (byte) 0x00;

			// word9
			UartVariable.Server_Modbus_Word_Data[18] = (byte) 0x00;
			UartVariable.Server_Modbus_Word_Data[19] = (byte) 0x00;

			// word10
			UartVariable.Server_Modbus_Word_Data[20] = (byte) 0x00;
			UartVariable.Server_Modbus_Word_Data[21] = (byte) 0x00;

			// word11
			UartVariable.Server_Modbus_Word_Data[22] = (byte) 0x00;
			UartVariable.Server_Modbus_Word_Data[23] = (byte) DeviceConfigure.getNetworkSignalLevel();

			// word12
			UartVariable.Server_Modbus_Word_Data[24] = (byte) 0x00;
			UartVariable.Server_Modbus_Word_Data[25] = (byte) 0x00;

			UartUtils.resetModbusData(UartVariable.Server_Modbus_Word_Data, 26);

			UartVariable.Input_Registers_Stack.setDefaultValues(UartVariable.Server_Modbus_Word_Data);
			UartVariable.Input_Registers_Stack.setVolatile(0, 24, false);
			UartVariable.Input_Registers_Stack.update(0, UartVariable.Server_Modbus_Word_Data, 0,
					UartVariable.Server_Modbus_Word_Data.length);

			UartVariable.Discrete_Inputs_Buffer = ModbusController.getModbusController().allocateDiscreteInputsBuffer(0,
					48, true);
			UartVariable.Discrete_Inputs_Buffer.setDefaultValues(UartVariable.Server_Modbus_Bit_Data);
			UartVariable.Discrete_Inputs_Buffer.update(0, UartVariable.Server_Modbus_Bit_Data, 0,
					UartVariable.Server_Modbus_Bit_Data.length);

			UartUtils.enableNativeResponse(false);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断串口通信协议类型（7E7E / modbus）
	 */
	public static void analyze() {

		// time = System.currentTimeMillis();

		if (UartVariable.Uart_In_Buffer_Length == 4 && UartVariable.Uart_In_Buffer[0] == (byte) 0xA5
				&& UartVariable.Uart_In_Buffer[1] == (byte) 0xA7 && UartVariable.Uart_In_Buffer[2] == (byte) 0xB6
				&& UartVariable.Uart_In_Buffer[3] == (byte) 0xB4) {

			// A5 A7 B6 B4 是一个机组帧
			DataCenter.saveDataBuffer(UartVariable.Uart_In_Buffer, UartVariable.Uart_In_Buffer_Length);

			return;
		}

		// 判断是否是工装测试
		boolean isFrock = true;
		for (int i = 0; i < frockBytes.length; i++) {

			if (UartVariable.Uart_In_Buffer[i] != frockBytes[i]) {

				isFrock = false;
				break;
			}
		}
		if (isFrock) {

			FrockCheckModel.frockCheck();

			return;
		}

		// 如果是 A5A7 开头 则是 7E7E协议
		if (UartVariable.Uart_In_Buffer[0] == (byte) 0xA5 && UartVariable.Uart_In_Buffer[1] == (byte) 0xA7) {

			// 7E7E 下标5表示 有效数据长度
			int dataLength = UartVariable.Uart_In_Buffer[6] & 0xFF;

			// 有效数据长度是否符合条件，验证CRC8校验是否正确
			if (dataLength <= 85 && UartVariable.Uart_In_Buffer[7 + dataLength] == CRC.crc8(UartVariable.Uart_In_Buffer,
					7 + dataLength)) {

				SeveneModel.analyze();
				logBuffer();
				DataCenter.saveDataBuffer(UartVariable.Uart_In_Buffer, UartVariable.Uart_In_Buffer_Length);
			}

			return;
		}

		// 判断是modbus协议
		if (UartVariable.Uart_In_Buffer[0] == (byte) 0xFF || UartVariable.Uart_In_Buffer[0] == (byte) 0XF7) {

			// System.out.println("message is modbus");

			switch (UartVariable.Uart_In_Buffer[1]) {

			case (byte) 0x10: // 10写功能

				if (UartVariable.Uart_In_Buffer_Length == 8) {

					return;
				}

				// modbus 下标6表示 有效数据长度
				int dataLength = UartVariable.Uart_In_Buffer[6] & 0xFF;

				byte[] crc10 = CRC.crc16(UartVariable.Uart_In_Buffer, 7 + dataLength);

				// 判断CRC16校验是否正确
				if (dataLength <= 246 && UartVariable.Uart_In_Buffer[7 + dataLength] == crc10[1]
						&& UartVariable.Uart_In_Buffer[8 + dataLength] == crc10[0]) {

					MbWriteModel.analyze();
					logBuffer();
					DataCenter.saveDataBuffer(UartVariable.Uart_In_Buffer, UartVariable.Uart_In_Buffer_Length);
				}

				return;

			case (byte) 0x04: // 读word CRC16的校验位在6和7

				if (UartVariable.Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc04 = CRC.crc16(UartVariable.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(UartVariable.Uart_In_Buffer, 4, 5) <= 123
						&& UartVariable.Uart_In_Buffer[6] == crc04[1] && UartVariable.Uart_In_Buffer[7] == crc04[0]) {

					// logBuffer();

					MbReadWordModel.analyze();
				}

				return;

			case (byte) 0x02:// 读bit

				if (UartVariable.Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc02 = CRC.crc16(UartVariable.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(UartVariable.Uart_In_Buffer, 4, 5) <= 48
						&& UartVariable.Uart_In_Buffer[6] == crc02[1] && UartVariable.Uart_In_Buffer[7] == crc02[0]) {

					// logBuffer();

					MbReadBitModel.analyze();
				}

				return;
			}
		}

		// 如果GPRS被选中则缓存机组数据
		DataCenter.saveDataBuffer(UartVariable.Uart_In_Buffer, UartVariable.Uart_In_Buffer_Length);
	}

	/**
	 * 建立 发出数据 并 调用 server 发送函数
	 */
	public static void build(int length) {

		UartVariable.Uart_Out_Buffer[0] = (byte) 0xFA;
		UartVariable.Uart_Out_Buffer[1] = (byte) 0xFB;

		// Logger.log(Constant.Uart_Out_Buffer, length);

		UartServer.sendData(length);
	}

	/**
	 * 打log用于测试
	 */
	private static void logBuffer() {

		Logger.log(UartVariable.Uart_In_Buffer, UartVariable.Uart_In_Buffer_Length);
	}

}
