package com.gree.gprs.data;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.spi.Spi;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class DataTransmit implements Runnable {

	// 上报优先级
	private final int TRANSMIT_LEVEL_STOP = 0;
	private final int TRANSMIT_LEVEL_CHECK = 1;
	private final int TRANSMIT_LEVEL_POWER = 2;
	private final int TRANSMIT_LEVEL_CHOOSE = 3;
	private final int TRANSMIT_LEVEL_WARNING = 4;
	private final int TRANSMIT_LEVEL_PUSHKEY = 5;
	private final int TRANSMIT_LEVEL_CHANGE = 6;
	private final int TRANSMIT_LEVEL_OPEN_CLOSE = 7;
	private final int TRANSMIT_LEVEL_ERROR = 8;
	private final int TRANSMIT_LEVEL_DEBUG = 9;
	private final int TRANSMIT_LEVEL_ALWAYS = 10;
	private int Transmit_Level = TRANSMIT_LEVEL_STOP;
	private int Transmit_Cache_Level = TRANSMIT_LEVEL_STOP;

	private static byte[] Data_Out_Success_Array = new byte[256];

	// tcp f4 cache time
	private long Tcp_F4_Time = 0L;

	private int Data_Buffer_Out_Mark = 0;
	private int Data_Buffer_Out_End_Mark = -1;

	private boolean Can_Transmit_Data = false;

	public boolean Arrive_End_Mark = false;

	public DataTransmit() {

		Data_Out_Success_Array[0] = (byte) 0x01;
	}

	/**
	 * 通知上传数据
	 */
	public void notifyTransmit() {

		Can_Transmit_Data = true;

		synchronized (this) {

			this.notify();
		}
	}

	/**
	 * 实时传输
	 */
	public void alwaysTransmit() {

		if (!DataCenter.Transmit_Choose_Or_Power && Transmit_Cache_Level < TRANSMIT_LEVEL_ALWAYS) {

			Transmit_Cache_Level = TRANSMIT_LEVEL_ALWAYS;
			Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_ALWAYS;

			return;
		}

		if (Transmit_Level < TRANSMIT_LEVEL_ALWAYS) {

			// 重置静默时间
			Variable.Stop_Time = 0;
			stopTransmit();

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_ALWAYS;
			Transmit_Level = TRANSMIT_LEVEL_ALWAYS;

			FileWriteModel.saveAlwaysTransmit();

			Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 实时上报
	 */
	public void debugTransmit() {

		if (Variable.System_Time > Variable.Stop_Time && Transmit_Level < TRANSMIT_LEVEL_DEBUG) {

			stopTransmit();

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_DEBUG;
			Transmit_Level = TRANSMIT_LEVEL_DEBUG;

			Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 故障上报
	 */
	public void errorTransmit() {

		if (Variable.System_Time > Variable.Stop_Time) {

			// 如果正在故障上报，向后延时
			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_ERROR) {

				mathOutEndMark(Configure.Transmit_Error_End_Time);
				return;
			}

			if (Transmit_Level < TRANSMIT_LEVEL_ERROR) {

				stopTransmit();

				mathOutStartMark(Configure.Transmit_Error_Start_Time);
				mathOutEndMark(Configure.Transmit_Error_End_Time);

				Variable.Transmit_Type = Constant.TRANSMIT_TYPE_ERROR;
				Transmit_Level = TRANSMIT_LEVEL_ERROR;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 开机上报
	 */
	public void openTransmit() {

		if (Variable.System_Time > Variable.Stop_Time) {

			// 正在开机上报，向后延时
			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN) {

				mathOutEndMark(Configure.Transmit_Open_End_Time);
				return;
			}

			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE || Transmit_Level < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopTransmit();

				// 重置发送游标
				mathOutStartMark(Configure.Transmit_Open_Start_Time);
				mathOutEndMark(Configure.Transmit_Open_End_Time);

				Variable.Transmit_Type = Constant.TRANSMIT_TYPE_OPEN;
				Transmit_Level = TRANSMIT_LEVEL_OPEN_CLOSE;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 关机上报
	 */
	public void closeTransmit() {

		if (Variable.System_Time > Variable.Stop_Time) {

			// 正在开机上报，向后延时
			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE) {

				mathOutEndMark(Configure.Transmit_Close_End_Time);
				return;
			}

			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN || Transmit_Level < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopTransmit();

				// 重置发送游标
				mathOutStartMark(Configure.Transmit_Close_Start_Time);
				mathOutEndMark(Configure.Transmit_Close_End_Time);

				Variable.Transmit_Type = Constant.TRANSMIT_TYPE_CLOSE;
				Transmit_Level = TRANSMIT_LEVEL_OPEN_CLOSE;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 厂家参数变化上报
	 */
	public void changeTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_CHANGE && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			mathOutStartMark(5 * 60);
			mathOutEndMark(Configure.Transmit_Change_End_Time);

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_CHANGE;
			Transmit_Level = TRANSMIT_LEVEL_CHANGE;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 按键上报
	 */
	public void pushKeyTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_PUSHKEY && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
			mathOutEndMark(Configure.Transmit_Pushkey_End_Time);

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_PUSHKEY;
			Transmit_Level = TRANSMIT_LEVEL_PUSHKEY;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 亚健康上报
	 */
	public void warningTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_WARNING && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			DataCenter.Transmit_Warning = true;
			Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_WARNING;
			Transmit_Level = TRANSMIT_LEVEL_WARNING;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 选举上报
	 */
	public void chooseTransmit() {

		if (Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
			mathOutEndMark(5 * 60);

			Variable.Transmit_Type = Constant.TRANSMIT_TYPE_CHOOSE;
			Transmit_Level = TRANSMIT_LEVEL_CHOOSE;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 上电上报
	 */
	public void powerTransmit() {

		Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
		mathOutEndMark(5 * 60);

		Variable.Transmit_Type = Constant.TRANSMIT_TYPE_POWER;
		Transmit_Level = TRANSMIT_LEVEL_POWER;

		ControlCenter.requestStartUpload();
	}

	/**
	 * 进行打卡上报
	 */
	public void checkTransmit() {

		// 判断静默时间
		if (Variable.System_Time < Variable.Stop_Time) {

			return;
		}

		// 判断上报优先级
		if (Transmit_Level > TRANSMIT_LEVEL_CHECK) {

			return;
		}

		// 判断缓存上报状态
		if (DataCenter.Transmit_Warning || Variable.Transmit_Cache_Type != Constant.TRANSMIT_TYPE_CHECK) {

			return;
		}

		Variable.Transmit_Type = Constant.TRANSMIT_TYPE_CHECK;
		Transmit_Level = TRANSMIT_LEVEL_CHECK;

		Data_Buffer_Out_Mark = DataCenter.Data_Buffer_Mark;
		mathOutEndMark(Configure.Transmit_Check_End_Time);

		ControlCenter.requestStartUpload();
	}

	/**
	 * 暂停上报
	 */
	public void pauseTransmit() {

		Can_Transmit_Data = false;
	}

	/**
	 * 停止数据上报
	 */
	public void stopTransmit() {

		pauseTransmit();
		Arrive_End_Mark = false;
		Data_Buffer_Out_End_Mark = -1;

		Variable.Transmit_Type = Constant.TRANSMIT_TYPE_STOP;
		Transmit_Level = TRANSMIT_LEVEL_STOP;
	}

	public void run() {

		while (Boot.Gprs_Running) {

			try {

				// 如果停止上传，阻塞
				if (!Can_Transmit_Data) {

					synchronized (this) {

						this.wait();
					}
				}

				// 达到上报标志位
				if (Data_Buffer_Out_Mark == Data_Buffer_Out_End_Mark) {

					if ((Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CHANGE
							&& ControlCenter.Transmit_Mark_Change == 1)
							|| (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN
									&& ControlCenter.Transmit_Mark_Open == 1)
							|| (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE
									&& ControlCenter.Transmit_Mark_Close == 1)) {

						Arrive_End_Mark = true;
						Data_Buffer_Out_End_Mark = -1;

					} else {

						DataCenter.stopTransmit(true);
					}

					continue;
				}

				// 判断服务器是否正常
				if (!TcpServer.isServerNormal()) {

					Thread.sleep(2000);
					continue;
				}

				// 每10分钟需要上传GPRS信号
				if (Variable.System_Time - Tcp_F4_Time >= Configure.Tcp_Sig_Period * 1000) {

					Tcp_F4_Time = Variable.System_Time;
					ControlCenter.sendGprsSignal();
				}

				// 正在进行上电上报，如果缓存了实时上报，切换为实时上报
				if (!DataCenter.Transmit_Choose_Or_Power && (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CHOOSE
						|| Variable.Transmit_Type == Constant.TRANSMIT_TYPE_POWER)) {

					if (Transmit_Cache_Level > Transmit_Level) {

						switch (Variable.Transmit_Cache_Type) {
						case Constant.TRANSMIT_TYPE_ALWAYS:

							alwaysTransmit();
							break;
						}
					}

					DataCenter.Transmit_Choose_Or_Power = true;

					continue;
				}

				int length = 0;
				long time = 0L;

				if (Spi.readData(Data_Buffer_Out_Mark * 2 * 1024)) {

					// 验证数据没有发过
					if (Variable.Data_SPI_Buffer[1792] != (byte) 0x01) {

						length = Utils.bytesToInt(Variable.Data_SPI_Buffer, 2, 3);

						if (length > 0 && length < 1792) { // 验证数据是否正确

							time = Utils.bytesToLong(Variable.Data_SPI_Buffer, 4);

							for (int i = 12; i < 12 + length; i++) {

								Variable.Tcp_Out_Data_Buffer[i - 12 + 25] = Variable.Data_SPI_Buffer[i];
							}

						} else {

							outMarkAdd();
							continue;
						}

					} else {

						outMarkAdd();
						continue;
					}
				}

				// 如果数据长度大于0，进行上传
				if (length > 0) {

					ControlCenter.transmitData(length, time);

					Spi.writeData((Data_Buffer_Out_Mark * 2 * 1024 + 1792), Data_Out_Success_Array);

					outMarkAdd();
					Thread.sleep(500);

				} else {

					Thread.sleep(2000);
				}

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * math transmit start mark
	 * 
	 * @param beforeTime
	 */
	private void mathOutStartMark(int beforeTime) {

		long localTime = Variable.System_Time;

		int startMark = DataCenter.Data_Buffer_Mark - (beforeTime / 3);

		if (startMark < 0) {

			startMark += DataCenter.BUFFER_MARK_SIZE;
		}

		// check data save time in spi is after transmit start time
		while (true) {

			Spi.readData(startMark * 2 * 1024);
			long spiTimeStamp = Utils.bytesToLong(Variable.Data_SPI_Buffer, 4);

			if (localTime - spiTimeStamp > beforeTime * 1000) {

				startMark++;

				if (startMark == DataCenter.Data_Buffer_Mark) {

					Data_Buffer_Out_Mark = startMark;
					break;
				}

				if (startMark > DataCenter.BUFFER_MARK_SIZE) {

					startMark = 0;
				}

			} else {

				Data_Buffer_Out_Mark = startMark;
				break;
			}
		}
	}

	/**
	 * 验证结束Mark
	 * 
	 * @param endTime
	 */
	private void mathOutEndMark(int endTime) {

		Data_Buffer_Out_End_Mark = DataCenter.Data_Buffer_Mark + (endTime / 3);

		if (Data_Buffer_Out_End_Mark > DataCenter.BUFFER_MARK_SIZE) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - DataCenter.BUFFER_MARK_SIZE;
		}
	}

	/**
	 * Out Mark Add
	 */
	private void outMarkAdd() {

		Data_Buffer_Out_Mark++;

		if (Data_Buffer_Out_Mark == DataCenter.BUFFER_MARK_SIZE) {

			Data_Buffer_Out_Mark = 0;
		}
	}

}
