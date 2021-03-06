package com.gree.gprs.data;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.file.FileWriteModel;
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
	private int transmitLevel = TRANSMIT_LEVEL_STOP;

	// tcp f4 cache time
	private long tcpSigTime = 0L;

	private int dataTransmitMark = 0;
	private long transmitEndTime = 0;
	private int transmittingTime = 0;

	private boolean canTransmitData = false;
	public boolean arriveEndMark = false;

	/**
	 * 通知上传数据
	 */
	public void notifyTransmit() {

		canTransmitData = true;

		if (transmitEndTime > 0 && transmitEndTime < ControlCenter.Gprs_Valid_Time) {

			mathOutEndMark(transmittingTime);
		}

		synchronized (this) {

			this.notify();
		}
	}

	/**
	 * 实时上报
	 */
	public void alwaysTransmit() {

		if (transmitLevel < TRANSMIT_LEVEL_ALWAYS) {

			// 重置静默时间
			Variable.Stop_Time = 0;
			stopTransmit();

			setTransmitType(Constant.TRANSMIT_TYPE_ALWAYS, TRANSMIT_LEVEL_ALWAYS);
			FileWriteModel.saveAlwaysTransmit();
			resetTransmitTime();

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 调试上报
	 */
	public void debugTransmit() {

		if (Variable.System_Time > Variable.Stop_Time && transmitLevel < TRANSMIT_LEVEL_DEBUG) {

			stopTransmit();

			setTransmitType(Constant.TRANSMIT_TYPE_DEBUG, TRANSMIT_LEVEL_DEBUG);
			resetTransmitTime();

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

			if (transmitLevel < TRANSMIT_LEVEL_ERROR) {

				stopTransmit();

				mathOutStartMark(Configure.Transmit_Error_Start_Time);
				mathOutEndMark(Configure.Transmit_Error_End_Time);
				setTransmitType(Constant.TRANSMIT_TYPE_ERROR, TRANSMIT_LEVEL_ERROR);

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

			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE || transmitLevel < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopTransmit();

				mathOutStartMark(Configure.Transmit_Open_Start_Time);
				mathOutEndMark(Configure.Transmit_Open_End_Time);
				setTransmitType(Constant.TRANSMIT_TYPE_OPEN, TRANSMIT_LEVEL_OPEN_CLOSE);

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

			if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN || transmitLevel < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopTransmit();

				mathOutStartMark(Configure.Transmit_Close_Start_Time);
				mathOutEndMark(Configure.Transmit_Close_End_Time);
				setTransmitType(Constant.TRANSMIT_TYPE_CLOSE, TRANSMIT_LEVEL_OPEN_CLOSE);

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 厂家参数变化上报
	 */
	public void changeTransmit() {

		if (transmitLevel < TRANSMIT_LEVEL_CHANGE && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			mathOutStartMark(5 * 60);
			mathOutEndMark(Configure.Transmit_Change_End_Time);
			setTransmitType(Constant.TRANSMIT_TYPE_CHANGE, TRANSMIT_LEVEL_CHANGE);

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 按键上报
	 */
	public void pushKeyTransmit() {

		if (transmitLevel < TRANSMIT_LEVEL_PUSHKEY && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			dataTransmitMark = DataCenter.dataBufferMark;
			mathOutEndMark(Configure.Transmit_Pushkey_End_Time);
			setTransmitType(Constant.TRANSMIT_TYPE_PUSHKEY, TRANSMIT_LEVEL_PUSHKEY);

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 亚健康上报
	 */
	public void warningTransmit() {

		if (transmitLevel < TRANSMIT_LEVEL_WARNING && Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			DataCenter.Transmit_Cache_Warning = true;
			resetTransmitTime();
			setTransmitType(Constant.TRANSMIT_TYPE_WARNING, TRANSMIT_LEVEL_WARNING);

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 选举上报
	 */
	public void chooseTransmit() {

		if (Variable.System_Time > Variable.Stop_Time) {

			stopTransmit();

			dataTransmitMark = DataCenter.dataBufferMark;
			mathOutEndMark(5 * 60);
			setTransmitType(Constant.TRANSMIT_TYPE_CHOOSE, TRANSMIT_LEVEL_CHOOSE);

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 上电上报
	 */
	public void powerTransmit() {

		dataTransmitMark = DataCenter.dataBufferMark;
		mathOutEndMark(5 * 60);
		setTransmitType(Constant.TRANSMIT_TYPE_POWER, TRANSMIT_LEVEL_POWER);

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
		if (transmitLevel > TRANSMIT_LEVEL_CHECK) {

			return;
		}

		// 判断缓存上报状态
		if (DataCenter.Transmit_Cache_Warning || Variable.Transmit_Cache_Type != Constant.TRANSMIT_TYPE_CHECK) {

			return;
		}

		dataTransmitMark = DataCenter.dataBufferMark;
		mathOutEndMark(Configure.Transmit_Check_End_Time);
		setTransmitType(Constant.TRANSMIT_TYPE_CHECK, TRANSMIT_LEVEL_CHECK);

		ControlCenter.requestStartUpload();
	}

	/**
	 * 暂停上报
	 */
	public void pauseTransmit() {

		canTransmitData = false;
	}

	/**
	 * 停止数据上报
	 */
	public void stopTransmit() {

		pauseTransmit();
		arriveEndMark = false;
		resetTransmitTime();
		setTransmitType(Constant.TRANSMIT_TYPE_STOP, TRANSMIT_LEVEL_STOP);
	}

	public void run() {

		while (Boot.Gprs_Running) {

			try {

				// 如果停止上传，阻塞
				if (!canTransmitData) {

					synchronized (this) {

						this.wait();
					}
				}

				// 判断服务器是否正常
				if (!TcpServer.isServerNormal()) {

					Thread.sleep(1000);
					continue;
				}

				// 每10分钟需要上传GPRS信号
				if (Variable.System_Time - tcpSigTime >= Configure.Tcp_Sig_Period * 1000) {

					tcpSigTime = Variable.System_Time;
					ControlCenter.sendGprsSignal();
				}

				// 周期性心跳
				if (Variable.System_Time - Variable.Heart_Beat_Time >= Configure.Tcp_Heart_Beat_Period * 1000) {

					if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

						Variable.Heart_Beat_Time += 10 * 1000;

					} else {

						ControlCenter.heartBeat();
					}
				}

				// 正在进行上电上报，如果缓存了实时上报，切换为实时上报
				if (!DataCenter.Transmit_Choose_Or_Power && (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CHOOSE
						|| Variable.Transmit_Type == Constant.TRANSMIT_TYPE_POWER)) {

					switch (Variable.Transmit_Cache_Type) {
					case Constant.TRANSMIT_TYPE_ALWAYS:

						alwaysTransmit();
						break;
					}

					DataCenter.Transmit_Choose_Or_Power = true;
					continue;
				}

				int length = 0;
				long time = 0L;

				if (DataCenter.dataInterface.queryData(dataTransmitMark * DataCenter.BUFFER_SIZE)) {

					// 达到上报标志位
					long spiTimeStamp = Utils.bytesToLong(Variable.Data_Query_Buffer, 4);
					if (transmitEndTime > 0 && spiTimeStamp > transmitEndTime) {

						if ((Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CHANGE
								&& ControlCenter.getTransmitMarkChange() == 1)
								|| (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN
										&& ControlCenter.getTransmitMarkOpen() == 1)
								|| (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE
										&& ControlCenter.getTransmitMarkClose() == 1)) {

							arriveEndMark = true;
							transmitEndTime = -1;

						} else {

							DataCenter.stopTransmit(true);
						}

						continue;
					}

					// 验证数据没有发过
					if (!DataCenter.dataInterface.queryDataHasSend()) {

						length = Utils.bytesToInt(Variable.Data_Query_Buffer, 2, 3);

						if (length > 0 && length < 1792) { // 验证数据是否正确

							time = Utils.bytesToLong(Variable.Data_Query_Buffer, 4);

							for (int i = 12; i < 12 + length; i++) {

								Variable.Tcp_Out_Data_Buffer[i - 12 + 25] = Variable.Data_Query_Buffer[i];
							}

							ControlCenter.transmitData(length, time);
							DataCenter.dataInterface.markDataIsSend(dataTransmitMark * DataCenter.BUFFER_SIZE);
						}
					}

					dataTransmitMark = markAdd(dataTransmitMark);
				}

				Thread.sleep(1000);

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

		long startTime = Variable.System_Time - beforeTime * 1000;

		int reduceNum = 0;
		boolean markCheckOk = false;

		int startMark = DataCenter.dataBufferMark;
		startMark = markReduce(startMark, beforeTime / 3);

		// check data save time in spi is after transmit start time
		while (true) {

			DataCenter.dataInterface.queryData(startMark * DataCenter.BUFFER_SIZE);
			long spiTimeStamp = Utils.bytesToLong(Variable.Data_Query_Buffer, 4);

			if (spiTimeStamp - startTime < -5 * 1000) {

				startMark = markAdd(startMark);
				markCheckOk = true;
				continue;
			}

			if (reduceNum > 10) {

				markCheckOk = true;
				reduceNum = 0;
				startMark = 0;
				continue;
			}

			if (spiTimeStamp - startTime > 5 * 1000) {

				if (markCheckOk) {

					dataTransmitMark = startMark;
					break;
				}

				startMark = markReduce(startMark, 5);
				reduceNum++;
				continue;
			}

			dataTransmitMark = startMark;
			break;
		}
	}

	/**
	 * 验证结束Mark
	 * 
	 * @param endTime
	 */
	private void mathOutEndMark(int endTime) {

		transmittingTime = endTime;
		transmitEndTime = Variable.System_Time + endTime * 1000;
	}

	/**
	 * Mark Add
	 */
	private int markAdd(int mark) {

		mark++;

		if (mark == DataCenter.BUFFER_MARK_SIZE) {

			mark = 0;
		}

		return mark;
	}

	/**
	 * Mark reduce
	 * 
	 * @param mark
	 * @param value
	 */
	private int markReduce(int mark, int value) {

		mark = mark - value;

		if (mark < 0) {

			mark += DataCenter.BUFFER_MARK_SIZE;
		}

		return mark;
	}

	/**
	 * 重置传输时间
	 */
	private void resetTransmitTime() {

		dataTransmitMark = DataCenter.dataBufferMark;
		transmitEndTime = -1;
	}

	/**
	 * 设置传输类型
	 * 
	 * @param type
	 * @param level
	 */
	private void setTransmitType(byte type, int level) {

		Variable.Transmit_Type = type;
		transmitLevel = level;
	}

}
