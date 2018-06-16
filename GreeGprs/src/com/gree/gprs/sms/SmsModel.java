package com.gree.gprs.sms;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.model.AdmModel;
import com.gree.gprs.sms.model.ApnModel;
import com.gree.gprs.sms.model.ButtModel;
import com.gree.gprs.sms.model.CheckPeriodModel;
import com.gree.gprs.sms.model.CheckStartModel;
import com.gree.gprs.sms.model.CheckTimeModel;
import com.gree.gprs.sms.model.CheckingModel;
import com.gree.gprs.sms.model.DebtModel;
import com.gree.gprs.sms.model.ErrtModel;
import com.gree.gprs.sms.model.HbModel;
import com.gree.gprs.sms.model.HealtModel;
import com.gree.gprs.sms.model.OfftOneModel;
import com.gree.gprs.sms.model.OfftTwoModel;
import com.gree.gprs.sms.model.OntOneModel;
import com.gree.gprs.sms.model.OntTwoModel;
import com.gree.gprs.sms.model.PwdModel;
import com.gree.gprs.sms.model.RstModel;
import com.gree.gprs.sms.model.ServModel;
import com.gree.gprs.sms.model.SigModel;
import com.gree.gprs.sms.model.StartModel;
import com.gree.gprs.sms.model.StopModel;
import com.gree.gprs.sms.model.UsronModel;
import com.gree.gprs.sms.model.VerModel;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class SmsModel {

	private static boolean isAdmin = false;

	public static String Sms_Address;
	public static String Sms_Message;

	/**
	 * analyze sms message
	 */
	public static void analyze() {

		Logger.log("SMS Get From", Sms_Address);
		Logger.log("SMS Get Message", Sms_Message);

		if (!Variable.Gprs_Choosed || !Variable.Gprs_Init_Success || !DataCenter.Transmit_Choose_Or_Power) {

			return;
		}

		// 验证白名单
		if (!Utils.isNotEmpty(Sms_Address)) {

			return;
		}

		int start = Sms_Address.indexOf("sms://") + 6;
		int end = Sms_Address.indexOf(":", start);

		if (start == -1 || end == -1 || end <= start) {

			return;
		}

		String phone = Sms_Address.substring(start, end);
		if (phone.startsWith("+86")) {

			phone = phone.substring(3, phone.length());
		}

		if (!Utils.isNotEmpty(phone)) {

			return;
		}

		boolean phoneValid = false;
		isAdmin = false;

		// 验证管理员
		for (int i = 0; i < Configure.Sms_Admin_List.length; i++) {

			if (Configure.Sms_Admin_List[i].equals(phone)) {

				phoneValid = true;
				isAdmin = true;
				break;
			}
		}

		if (!phoneValid) {

			// 验证普通用户
			for (int i = 0; i < Configure.Sms_User_List.length; i++) {

				if (Configure.Sms_User_List[i].equals(phone)) {

					phoneValid = true;
					break;
				}
			}
		}

		if (!phoneValid) {

			return;
		}

		// 验空
		if (!Utils.isNotEmpty(Sms_Message)) {

			return;
		}

		// 验证 短信密码
		if (Utils.stringToInt(smsGetPwd(Sms_Message)) == -1) {

			buildMessageUnknow();
			return;
		}
		if (!smsGetPwd(Sms_Message).equals(Configure.Sms_Pwd)) {

			return;
		}

		// 判断短信类型
		if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_APN) != -1) {// 接入点

			SmsBaseModel smsBaseModel = new ApnModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_SERV) != -1) { // 域名、IP，端口号

			SmsBaseModel smsBaseModel = new ServModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_HB) != -1) { // 心跳间隔

			SmsBaseModel smsBaseModel = new HbModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_PWD) != -1) { // 短信密码

			SmsBaseModel smsBaseModel = new PwdModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_START) != -1) { // 开始连接服务器

			StartModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_STOP) != -1) { // 断开连接服务器

			StopModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_VER) != -1) { // DTU软件版本号

			VerModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_ADM) != -1) { // 管理员号码

			SmsBaseModel smsBaseModel = new AdmModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_USRON) != -1) { // 普通手机账号

			SmsBaseModel smsBaseModel = new UsronModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_RST) != -1) { // 复位DTU

			RstModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_ERRT) != -1) { // 故障点前传输时间

			SmsBaseModel smsBaseModel = new ErrtModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_DEBT) != -1) { // 故障点后传输时间

			SmsBaseModel smsBaseModel = new DebtModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_HEALT) != -1) { // 厂家参数改变前传输结束时间

			SmsBaseModel smsBaseModel = new HealtModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_BUTT) != -1) { // 按键调试周期

			SmsBaseModel smsBaseModel = new ButtModel();
			smsBaseModel.smsAnalyze();

		} else if (Sms_Message.indexOf(SmsConstant.SMS_TYPE_SIG) != -1) { // 信号上报周期

			SmsBaseModel smsBaseModel = new SigModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CHECK_START)) { // 打卡上报

			CheckStartModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CHECK_PERIOD)) { // 打卡间隔

			SmsBaseModel smsBaseModel = new CheckPeriodModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CHECK_TIME)) { // 打卡时长

			SmsBaseModel smsBaseModel = new CheckTimeModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_OPEN_START)) {// 开机前置时间

			SmsBaseModel smsBaseModel = new OntOneModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_OPEN_END)) {// 开机后置时间

			SmsBaseModel smsBaseModel = new OntTwoModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CLOSE_START)) {// 关机前置时间

			SmsBaseModel smsBaseModel = new OfftOneModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CLOSE_END)) {// 关机后置时间

			SmsBaseModel smsBaseModel = new OfftTwoModel();
			smsBaseModel.smsAnalyze();

		} else if (checkSmsType(SmsConstant.SMS_TYPE_CHECKING)) {

			SmsBaseModel smsBaseModel = new CheckingModel();
			smsBaseModel.smsAnalyze();

		} else {

			buildMessageUnknow();
		}
	}

	/**
	 * 判断短信是否符合类型
	 * 
	 * @param type
	 * @return
	 */
	private static boolean checkSmsType(String type) {

		if (Sms_Message.indexOf(type) != -1) {

			return true;
		}

		return false;
	}

	/**
	 * 获取Sms 短信密码
	 * 
	 * @param sms
	 * @return
	 */
	public static String smsGetPwd(String sms) {

		try {

			int start = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, 0);
			int end = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, start + 1);
			String smsPwd = sms.substring(start + 1, end);

			return smsPwd;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取短信的有效数据
	 * 
	 * @param sms
	 * @return
	 */
	public static String smsGetValue(String sms) {

		try {

			int start = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, 0);
			start = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, start + 1);
			start = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, start + 1);

			int end = 0;
			int poi = start;
			while ((poi = sms.indexOf(SmsConstant.SMS_SPLIT_KEY_SYMBOL, poi + 1)) != -1) {

				end = poi;
			}

			String smsValue = sms.substring(start + 1, end);

			return smsValue;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * GPRS 发送短信 至 服务器 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 * @param smsValue
	 *            消息内容
	 */
	public static void buildMessage(String smsType, String smsValue) {

		String message = smsType + smsValue + SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * GPRF 发送set ok短信 至 服务器 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageSetOk(String smsType) {

		String message = smsType + SmsConstant.SMS_SET_OK + SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * GPRF 发送ok短信 至 服务器 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageOk(String smsType) {

		String message = smsType + SmsConstant.SMS_OK + SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * 短信异常 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageError(String smsType) {

		String message = smsType + SmsConstant.SMS_MESSAGE_ERROR + SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * 短信有效数据为空 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageEmpty(String smsType) {

		String message = smsType + SmsConstant.SMS_MESSAGE_EMPTY + SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * unknow command
	 */
	public static void buildMessageUnknow() {

		String message = SmsConstant.SMS_MESSAGE_UNKNOW + SmsConstant.SMS_MESSAGE_ERROR
				+ SmsConstant.SMS_SPLIT_KEY_SYMBOL;

		SmsServer.sendMessage(message);
	}

	/**
	 * build message
	 * 
	 * @param smsType
	 * @param smsValue
	 * @return
	 */
	public static String buildMessWithType(String smsType, String smsValue) {

		return smsType + smsValue + SmsConstant.SMS_SPLIT_KEY_SYMBOL;
	}

	/**
	 * send message array
	 * 
	 * @param smsType
	 * @param smsArray
	 */
	public static void sendMessageArray(String smsType, String[] smsArray) {

		SmsServer.sendMessage(smsType, smsArray);
	}

	public static boolean isAdmin() {
		return isAdmin;
	}

}
