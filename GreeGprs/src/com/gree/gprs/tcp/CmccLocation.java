package com.gree.gprs.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.entity.Device;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;

public class CmccLocation {

	public static void SearchLocation() {

		if (Utils.simCucc()) {

			return;
		}

		new Thread(new Runnable() {

			public void run() {

				try {

					Logger.log("CmccLocation Start", "Search Cmcc Location");

					StreamConnection streamConnection = (StreamConnection) Connector
							.open("socket://115.29.212.220:8180");
					OutputStream outputStream = streamConnection.openOutputStream();

					StringBuffer stringBuffer = new StringBuffer();

					stringBuffer.append("POST /api/Geolocate/Post?key=2_InDTU_" + Device.getInstance().getIccid()
							+ " HTTP/1.1\r\n");
					stringBuffer.append("Host: 115.29.212.220:8180\r\n");
					stringBuffer.append("Content-Type: application/json\r\n");
					stringBuffer.append("Cache-Control: no-cache\r\n");

					String body = "{\"HomeMobileCountryCode\":" + Device.getInstance().getMcc()
							+ ", \"HomeMobileNetworkCode\":" + Device.getInstance().getMnc() + ", "
							+ "\"CellTowers\":[{ \"CellId\":\"" + Device.getInstance().getCid()
							+ "\", \"LocationAreaCode\":\"" + Device.getInstance().getLac() + "\", "
							+ "\"MobileCountryCode\":\"" + Device.getInstance().getMcc()
							+ "\", \"MobileNetworkCode\":\"" + Device.getInstance().getMnc()
							+ "\", \"SignalStrength\":0, " + "\"TimingAdvance\":0}]}";

					Logger.log("CmccLocation Requst", body);

					stringBuffer.append("Content-Length: " + body.length() + "\r\n");
					stringBuffer.append("\r\n");

					stringBuffer.append(body + "\r\n");
					stringBuffer.append("\r\n");

					byte[] data = stringBuffer.toString().getBytes();

					outputStream.write(data);

					outputStream.flush();
					outputStream.close();

					InputStream inputStream = streamConnection.openInputStream();
					byte[] readByte = new byte[256];
					int length = 0;
					while ((length = inputStream.read(readByte)) != -1) {

						Logger.log("CmccLocation Response", new String(readByte, 0, length));
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
