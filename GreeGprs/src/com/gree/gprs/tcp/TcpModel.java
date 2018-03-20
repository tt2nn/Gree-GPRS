package com.gree.gprs.tcp;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.tcp.model.LoginModel;
import com.gree.gprs.tcp.model.ParamModel;
import com.gree.gprs.tcp.model.TimeModel;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * TCP 业务类
 * 
 * @author lihaotian
 *
 */
public class TcpModel {

	/**
	 * 解析 TCP协议 判断 功能码
	 */
	public static void analyze() {

		// 检验引导码
		if (Variable.Tcp_In_Buffer[0] != (byte) 0x7E || Variable.Tcp_In_Buffer[1] != (byte) 0x7E) {

			return;
		}

		// 检验校验码
		int dataLength = Utils.bytesToInt(Variable.Tcp_In_Buffer, 16, 17);

		if (Variable.Tcp_In_Buffer[18 + dataLength] != CRC.crc8(Variable.Tcp_In_Buffer, 2, 18 + dataLength)) {

			return;
		}

		// 判断功能码
		switch (Variable.Tcp_In_Buffer[18]) {

		case (byte) 0x72:

			ControlCenter.resetSystem();

			break;

		case (byte) 0x90: // 登录 响应

			LoginModel.loginResponse();

			break;

		case (byte) 0x91: // 开始传输 响应

			TransmitModel.startResponse();

			break;

		case (byte) 0x92: // 停止发送数据 请求

			TransmitModel.stopResponse();

			break;

		case (byte) 0x93: // 进行实时监控 请求

			TransmitModel.monitorResponse();

			break;

		case (byte) 0x94: // 修改GPRS模块 请求

			ParamModel.updateResponse();

			break;

		case (byte) 0x95: // 查询GPRS模块 请求

			ParamModel.query();

			break;

		case (byte) 0x96: // 服务器发送机组数据至GPRS模块 请求

			TransmitModel.dataTransmResponse();

			break;

		case (byte) 0x98: // 设置静默时间 请求

			TimeModel.stopTimeResponse();

			break;

		case (byte) 0xF3: // 心跳命令 响应

			TimeModel.heartResponse();

			break;
		}

	}

	/**
	 * TCP send data
	 * 
	 * @param dataLength
	 *            有效数据长度
	 * @param crcPosition
	 *            crc校验码位置
	 */
	public static void build(int dataLength, int crcPosition) {

		buildBufferData(Variable.Tcp_Out_Buffer, dataLength, crcPosition);

		Logger.log("Tcp Send Message", Variable.Tcp_Out_Buffer, 0, crcPosition + 1);

		TcpServer.sendData(Variable.Tcp_Out_Buffer, crcPosition + 1);
	}

	/**
	 * TCP send 机组 data
	 * 
	 * @param dataLength
	 *            有效数据长度
	 * @param crcPosition
	 *            crc校验码位置
	 */
	public static void buildForTransm(int dataLength, int crcPosition) {

		buildBufferData(Variable.Tcp_Out_Data_Buffer, dataLength, crcPosition);

		Logger.log("Tcp Send Message", Variable.Tcp_Out_Data_Buffer, 0, crcPosition + 1);

		TcpServer.sendData(Variable.Tcp_Out_Data_Buffer, crcPosition + 1);
	}

	/**
	 * 构建通用数据
	 * 
	 * @param buffer
	 * @param dataLength
	 * @param crcPosition
	 */
	private static void buildBufferData(byte[] buffer, int dataLength, int crcPosition) {

		// 引导码
		buffer[0] = (byte) 0x7E;
		buffer[1] = (byte) 0x7E;

		// 目标地址
		for (int i = 0; i < Variable.Server_Mac.length; i++) {

			buffer[i + 2] = Variable.Server_Mac[i];
		}

		// 源地址
		for (int i = 0; i < Variable.Gprs_Mac.length; i++) {

			buffer[i + 9] = Variable.Gprs_Mac[i];
		}

		// 数据长度
		byte[] lengthBytes = Utils.intToBytes(dataLength);
		buffer[16] = lengthBytes[0];
		buffer[17] = lengthBytes[1];

		// crc8校验码
		buffer[crcPosition] = CRC.crc8(buffer, 2, crcPosition);
	}

}
