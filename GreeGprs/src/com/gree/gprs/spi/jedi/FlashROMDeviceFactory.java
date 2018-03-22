package com.gree.gprs.spi.jedi;

public final class FlashROMDeviceFactory {
	public static FlashROM getDevice(FlashROMDeviceDescriptor desc) throws Exception {
		Class cls = desc.getFlashROMDeviceClass();
		FlashROM device = (FlashROM) cls.newInstance();
		device.mount(desc);
		return device;
	}
}
