package com.gree.gprs.control;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.gpio.GpioTool;
import com.gree.gprs.tcp.TcpPin;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.variable.Variable;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class ControlTimer implements Runnable {

	private final long Sleep_Time = 1000L;
	private long sleepTime = 1000L;
	private long workTime = 0L;
	private long pinTime = 0L;
	private long signTime = 0L;

	private int systemResetTime = 0;
	private int mathTime = 0;

	public boolean chooseTransmit = false;

	public void run() {

		pinTime = Variable.System_Time;
		signTime = Variable.System_Time;
		systemResetTime = 0;
		mathTime = 0;

		while (Boot.Gprs_Running) {

			try {

				Thread.sleep(sleepTime);
				workTime = Variable.System_Time;

				// 上电后前一分钟，响应重置操作
				if (mathTime < 100) {

					mathTime++;

					if (ControlCenter.Push_Key_Down && mathTime <= 60) {

						if (mathTime - systemResetTime >= 15) {

							ControlCenter.Push_Key_Down = false;
							ControlCenter.resetSystem();
							return;
						}

					} else {

						systemResetTime = mathTime;
					}
				}

				if (mathTime > 35) {

					if (!DeviceConfigure.hasSim()) {

						Variable.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_SIM;

					} else if ((systemResetTime >= 60 && !Variable.Gprs_Init_Success)
							|| !DeviceConfigure.hasNetwork()) {

						Variable.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NETWORK;

					} else if (Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_SERVER) {

						Variable.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NO;
					}
				}

				if (!Variable.Gprs_Init_Success && pinTime + 90 * 1000 <= Variable.System_Time) {

					pinTime = Variable.System_Time;

					new TcpPin().startPin(true);
					new Thread(new Runnable() {

						public void run() {

							try {
								Thread.sleep(5 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							new TcpPin().startPin(false);
						}
					}).start();
				}

				// 异常状态下 异常灯亮 通讯灯灭
				if (Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

					if (!GpioTool.getErrorValue()) {

						GpioPin.errorLight();
					}

					if (GpioTool.getCommunicationValue()) {

						GpioPin.communicationDark();
					}

				} else {

					if (GpioTool.getErrorValue()) {

						GpioPin.errorDark();
					}
				}

				// 1秒更新信号灯
				if (Variable.System_Time - signTime >= 1 * 1000) {

					signTime = Variable.System_Time;
					GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());
				}

				/**
				 * 90s选举上报
				 */
				if (mathTime >= 90 && Variable.Gprs_Choosed && !chooseTransmit
						&& !DataCenter.Transmit_Choose_Or_Power) {

					chooseTransmit = true;
					DataCenter.chooseTransmit();
				}

				if (ControlCenter.canWorking()) {

					if (Variable.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

						// 上传数据时灯闪烁
						if (Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

							if (GpioTool.getCommunicationValue()) {

								GpioPin.communicationDark();

							} else {

								GpioPin.communicationLight();
							}

						} else {

							if (!GpioTool.getCommunicationValue()) {

								GpioPin.communicationLight();
							}
						}
					}

					// 每三秒打包一次数据
					if (Variable.System_Time - DataCenter.Package_Time >= 3 * 1000) {

						DataCenter.packageData();
					}

					// 周期性心跳
					if (Variable.System_Time - Variable.Heart_Beat_Time >= Configure.Tcp_Heart_Beat_Period * 1000) {

						if (Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

							Variable.Heart_Beat_Time += 10 * 1000;

						} else {

							ControlCenter.heartBeat();
						}
					}

					// 周期性开机或者打卡上报
					if (Variable.System_Time - DataCenter.Check_Transmit_Time >= Configure.Transmit_Check_Period
							* 1000) {

						if (Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

							DataCenter.Check_Transmit_Time = Variable.System_Time;

						} else {

							DataCenter.checkTransmit();
						}
					}

					if (systemResetTime >= 60 && Variable.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

						// 判断进行按键上报
						if (Variable.System_Time - ControlCenter.Push_Key_Time >= 3 * 1000
								&& ControlCenter.Push_Key_Down
								&& Variable.Transmit_Type != Constant.TRANSMIT_TYPE_PUSHKEY) {

							ControlCenter.Push_Key_Down = false;
							DataCenter.pushKeyTransmit();
						}

						// 判断停止按键上报
						if (Variable.System_Time - ControlCenter.Push_Key_Time >= 5 * 1000
								&& ControlCenter.Push_Key_Down
								&& Variable.Transmit_Type == Constant.TRANSMIT_TYPE_PUSHKEY) {

							ControlCenter.Push_Key_Down = false;
							DataCenter.stopTransmit(true);
						}
					}

					// 恢复数据上报
					if (TcpServer.isServerNormal() && Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO
							&& Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

						ControlCenter.recoverUpload();
					}
				}

				sleepTime = Sleep_Time - (Variable.System_Time - workTime);
				sleepTime = sleepTime < 0 ? 0 : sleepTime;

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

}
