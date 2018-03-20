package com.gree.gprs.spi.jedi;

import java.io.IOException;

public interface FlashROM {
	public byte[] readManufacturInfo() throws IOException, DriverException;

	public byte[] readDeviceInfo() throws IOException, DriverException;

	public byte readStatusByte(int offset) throws IOException, DriverException;

	public byte[] readStatusBytes() throws IOException, DriverException;

	public void writeEnable() throws IOException, DriverException;

	public void chipErase() throws IOException, DriverException;

	public void pageProgram(int address, byte[] data) throws IOException, DriverException, UnsupportedPageSizeException;

	public void pageProgram(int address, byte[] data, int offset, int size)
			throws IOException, DriverException, UnsupportedPageSizeException;

	public byte[] read(int address, int size) throws IOException, DriverException;

	public void erase(int address, int size) throws IOException, DriverException, UnalignedAddressException;

	public void mount(FlashROMDeviceDescriptor desc)
			throws InvalidDeviceDescriptorException, IOException, DriverException;

	public int getPageSize();

	public long getTotalSize();
}
