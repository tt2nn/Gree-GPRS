package com.gree.gprs.control;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter.ControlInterface;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.gpio.GpioTool;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class ControlTimer implements Runnable {

	private final long SLEEP_TIME = 1000L;
	private long sleepTime = 1000L;
	private long workTime = 0L;
	private long pinTime = 0L;
	private long checkTime = 0L;
	public long tcpErrorTime = 0L;
	public long recoverTime = 0L;

	private int systemResetTime = 0;
	private int mathTime = 0;
	private int errorTime = 0;

	private int tcpTransmitTime = 0;

	public boolean chooseTransmit = false;

	private ControlInterface controlInterface;

	public void run() {

		pinTime = Variable.System_Time;
		checkTime = Variable.System_Time;
		systemResetTime = 0;
		mathTime = 0;

		while (Boot.Gprs_Running) {

			try {

				Thread.sleep(sleepTime);
				workTime = Variable.System_Time;

				// 计时
				if (mathTime < 150) {

					mathTime++;
				}

				// 一分钟检测重置功能
				if (ControlCenter.Push_Key_Down && mathTime <= 60) {

					if (mathTime - systemResetTime >= 15) {

						ControlCenter.Push_Key_Down = false;
						ControlCenter.resetSystem();
						return;
					}

				} else {

					systemResetTime = mathTime;
				}

				// 5s检查
				if (Variable.System_Time - checkTime >= 5 * 1000) {

					checkTime = Variable.System_Time;

					// 检测设备信息
					if (mathTime > 35) {

						DeviceConfigure.deviceInfo();
					}

					// 检测异常
					if (mathTime > 120) {

						if (!DeviceConfigure.hasSim()) {

							Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_SIM;

						} else if (!Variable.Gprs_Init_Success || !DeviceConfigure.hasNetwork()) {

							Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NETWORK;

						} else if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_SERVER) {

							Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NO;
						}
					}

					// logger
					Logger.log("Control Timer",
							"" + "Transmit:" + Variable.Transmit_Type + " = " + DataCenter.isTransmiting() + " Sign:"
									+ DeviceConfigure.getNetworkSignalLevel() + " Init:" + Variable.Gprs_Init_Success
									+ "-" + TcpServer.isServerNormal() + " Error:" + Variable.Gprs_Error_Type);
				}

				// 重连Tcp Server
				if (!Variable.Gprs_Init_Success && pinTime + 90 * 1000 <= Variable.System_Time) {

					pinTime = Variable.System_Time;
					Utils.pingServer();
				}

				// 异常状态下 异常灯亮 通讯灯灭
				if (Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_SIM
						|| Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_NETWORK
						|| (Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_SERVER
								&& Variable.System_Time - tcpErrorTime >= 15 * 1000)) {

					if (!GpioTool.getErrorValue()) {

						GpioPin.openError();
					}

					//异常灯常量超过5分钟，重启模块。
					errorTime++;
					if (errorTime > 300) {

						ControlCenter.reboot();
					}

				} else {

					errorTime = 0;

					if (GpioTool.getErrorValue()) {

						GpioPin.closeError();
					}
				}

				// 更新信号灯
				Variable.Network_Signal_Level = DeviceConfigure.getNetworkSignalLevel();
				GpioTool.setSignLevel(Variable.Network_Signal_Level);
				if (controlInterface != null) {

					controlInterface.controlPriod();
				}

				// 检查通讯灯
				if (Variable.Gprs_Choosed) {

					// 上传数据时灯闪烁
					if (Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP && !GpioTool.getErrorValue()) {

						if (GpioTool.getTransmitValue()) {

							GpioPin.closeTransmit();

						} else {

							GpioPin.openTransmit();
						}

					} else {

						if (!GpioTool.getTransmitValue()) {

							GpioPin.openTransmit();
						}
					}

				} else if (GpioTool.getTransmitValue()) {

					GpioPin.closeTransmit();
				}

				/**
				 * 90s选举上报
				 */
				if (mathTime >= 90 && Variable.Gprs_Choosed && DoChoose.isChooseResp() && !chooseTransmit
						&& !DataCenter.Transmit_Choose_Or_Power && Variable.Gprs_Init_Success) {

					chooseTransmit = true;
					DataCenter.chooseTransmit();
				}

				if (ControlCenter.canWorking()) {

					if (Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP && !DataCenter.isTransmiting()) {

						if (tcpTransmitTime == 60) {

							ControlCenter.stopTcpServerWithError();
							tcpTransmitTime = 0;

						} else {

							tcpTransmitTime++;
						}

					} else {

						tcpTransmitTime = 0;
					}

					// 每三秒打包一次数据
					if (Variable.System_Time - DataCenter.Package_Time >= 3 * 1000) {

						DataCenter.packageData();
					}

					// 周期性开机或者打卡上报
					if (Variable.System_Time - DataCenter.Check_Transmit_Time >= Configure.Transmit_Check_Period
							* 1000) {

						DataCenter.Check_Transmit_Time = Variable.System_Time;

						if (Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_NO) {

							DataCenter.checkTransmit();
						}
					}

					// 按键
					if (systemResetTime >= 60 && Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_NO) {

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
					if (Variable.System_Time - recoverTime >= 10 * 1000) {

						recoverTime = Variable.System_Time;

						if (Variable.Gprs_Error_Type == Constant.GPRS_ERROR_TYPE_SERVER
								&& Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

							Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NO;

						} else if (TcpServer.isServerNormal() && Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO
								&& Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

							ControlCenter.recoverUpload();
						}
					}
				}

				sleepTime = SLEEP_TIME - (Variable.System_Time - workTime);
				sleepTime = sleepTime < 0 ? 0 : sleepTime;

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public void setControlInterface(ControlInterface controlInterface) {
		this.controlInterface = controlInterface;
	}

}
