package com.gree.gprs.data;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.util.lzo.LzoCompressor1x_1;
import com.gree.gprs.util.lzo.lzo_uintp;
import com.gree.gprs.variable.Variable;

/**
 * 数据中心
 * 
 * @author lihaotian
 *
 */
public class DataCenter {

	public static final int TOTAL_SIZE = 8 * 1024 * 1024;
	public static final int BUFFER_SIZE = 2 * 1024;
	public static final int BUFFER_MARK_SIZE = TOTAL_SIZE / BUFFER_SIZE;

	// data save buffer mark
	static int dataBufferMark = 0;

	// lock
	private static Object lock = new Object();
	// 缓存数据长度
	private static int writeDataBufferPoi = 0;

	// lzo 压缩
	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzoUintp = new lzo_uintp();
	private static byte[] lzoBuffer = new byte[BUFFER_SIZE];

	private static Thread dataTransmitThread;
	private static DataTransmit dataTransmit = new DataTransmit();

	public static long Package_Time = 0L;
	public static long Check_Transmit_Time = 0L;

	public static boolean Transmit_Choose_Or_Power = false;
	public static boolean Transmit_Cache_Warning = false;

	// GPRS is power
	public static boolean Do_Power_Transmit = false;

	static DataInterface dataInterface;

	public static byte[] Data_Send_State = new byte[256];

	public static void setDataInterface(DataInterface dataInterface) {
		DataCenter.dataInterface = dataInterface;
	}

	/**
	 * 初始化
	 */
	public static void init() {

		int dataAddress = FileReadModel.queryDataAddress();
		dataBufferMark = dataAddress / BUFFER_SIZE;

		dataInterface.init();
	}

	/**
	 * 将机组数据写入4k缓存数据中
	 * 
	 * @param data
	 * @param length
	 */
	public static void saveDataBuffer(byte[] data, int length) {

		if (!ControlCenter.canWorking()) {

			return;
		}

		if (length > 0) {

			synchronized (lock) {

				if (writeDataBufferPoi + length >= Variable.Data_Cache_Buffer.length) {

					packageData();
				}

				// for (int i = 0; i < length; i++) {
				//
				// Variable.Data_Cache_Buffer[i + writeDataBufferPoi] = data[i];
				// }

				writeDataBufferPoi = dataInterface.saveDataBuffer(writeDataBufferPoi, data, length)
						+ writeDataBufferPoi;
			}
		}
	}

	/**
	 * 将缓存数据打包
	 */
	public static void packageData() {

		Package_Time = Variable.System_Time;

		if (writeDataBufferPoi == 0) {

			return;
		}

		synchronized (lock) {

			lzo.compress(Variable.Data_Cache_Buffer, 0, writeDataBufferPoi, lzoBuffer, 0, lzoUintp);

			if (lzoUintp.value > Variable.Data_Save_Buffer.length - 12) {

				writeDataBufferPoi = 0;
				return;
			}

			for (int i = 0; i < lzoUintp.value; i++) {

				Variable.Data_Save_Buffer[i + 12] = lzoBuffer[i];
			}

			// 游标位
			byte[] mark = Utils.intToBytes(dataBufferMark);
			Variable.Data_Save_Buffer[0] = mark[0];
			Variable.Data_Save_Buffer[1] = mark[1];

			// 长度位
			byte[] length = Utils.intToBytes(lzoUintp.value);
			Variable.Data_Save_Buffer[2] = length[0];
			Variable.Data_Save_Buffer[3] = length[1];

			// 时间
			byte[] time = Utils.longToBytes(Variable.System_Time);
			for (int i = 0; i < time.length; i++) {

				Variable.Data_Save_Buffer[i + 4] = time[i];
			}

			dataInterface.saveData(Variable.Data_Save_Buffer);

			dataBufferMark++;
			if (dataBufferMark == BUFFER_MARK_SIZE) {

				dataBufferMark = 0;
			}

			writeDataBufferPoi = 0;
		}
	}

	/**
	 * 开始上传数据
	 */
	public static void startTransmit() {

		dataTransmitThread = new Thread(dataTransmit);
		dataTransmitThread.start();
	}

	/**
	 * 通知上传数据
	 */
	public static void notifyTransmit() {

		dataTransmit.notifyTransmit();
	}

	/**
	 * 实时传输
	 */
	public static void alwaysTransmit() {

		dataTransmit.alwaysTransmit();
	}

	/**
	 * 实时传输
	 */
	public static void debugTransmit() {

		dataTransmit.debugTransmit();
	}

	/**
	 * 故障上报
	 */
	public static void errorTransmit() {

		dataTransmit.errorTransmit();
	}

	/**
	 * 开机上报
	 */
	public static void openTransmit() {

		dataTransmit.openTransmit();
	}

	/**
	 * 关机上报
	 */
	public static void closeTransmit() {

		dataTransmit.closeTransmit();
	}

	/**
	 * 厂家参数变化上报
	 */
	public static void changeTransmit() {

		dataTransmit.changeTransmit();
	}

	/**
	 * 按键上报
	 */
	public static void pushKeyTransmit() {

		dataTransmit.pushKeyTransmit();
	}

	/**
	 * 亚健康上报
	 */
	public static void warningTransmit() {

		dataTransmit.warningTransmit();
	}

	/**
	 * 选举上报
	 */
	public static void chooseTransmit() {

		DoChoose.resetRandomNum();
		dataTransmit.chooseTransmit();
	}

	/**
	 * 上电上报
	 */
	public static void powerTransmit() {

		Do_Power_Transmit = true;
		dataTransmit.powerTransmit();
	}

	/**
	 * 注册打卡上报
	 */
	public static void registerCheckTransmit() {

		if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP
				|| Variable.Transmit_Type == Constant.TRANSMIT_TYPE_ALWAYS) {

			stopTransmit(false);

		} else if (Variable.Transmit_Cache_Type != Constant.TRANSMIT_TYPE_CHECK) {

			FileWriteModel.saveCheckTransmit();
			ControlCenter.requestStartUpload();
		}

		Check_Transmit_Time = 0L;
		Variable.Stop_Time = 0L;
		FileWriteModel.saveCheckTransmit();
	}

	/**
	 * 进行打卡上报
	 */
	public static void checkTransmit() {

		Check_Transmit_Time = Variable.System_Time;
		dataTransmit.checkTransmit();
	}

	/**
	 * 暂停上报
	 */
	public static void pauseTransmit() {

		dataTransmit.pauseTransmit();
	}

	/**
	 * 停止数据上报
	 * 
	 * @param stopTcp
	 */
	public static void stopTransmit(boolean stopTcp) {

		dataTransmit.stopTransmit();

		if (stopTcp) {

			ControlCenter.stopTcpServer();
		}
	}

	/**
	 * 销毁上报
	 */
	public static void destoryTransmit() {

		DataCenter.Transmit_Cache_Warning = false;
		DataCenter.stopTransmit(true);
		FileWriteModel.saveCheckTransmit();
		Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_STOP;
	}

	/**
	 * get transmit is arrive end mark
	 * 
	 * @return
	 */
	public static boolean arriveEndMark() {

		return dataTransmit.arriveEndMark;
	}

	public static Thread getDataTransmitThread() {
		return dataTransmitThread;
	}

	public interface DataInterface {

		public void init();

		public int saveDataBuffer(int poi, byte[] data, int length);

		public void saveData(byte[] data);

		public boolean queryData(int address);

		public boolean queryDataHasSend();

		public void markDataIsSend(int address);
	}

}
