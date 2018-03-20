package com.gree.gprs.spi.jedi;

public interface FlashROMDeviceDescriptor {
	public FlashROMDeviceDescriptor getDescriptor();

	public Class getFlashROMDeviceClass() throws ClassNotFoundException;
}
