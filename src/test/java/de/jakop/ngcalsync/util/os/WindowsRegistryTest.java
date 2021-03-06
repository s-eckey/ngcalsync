package de.jakop.ngcalsync.util.os;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * 
 * @author fjakop
 *
 */
public class WindowsRegistryTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_valueDoesNotContainTab_returnsNull() throws Exception {
		final Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(IOUtils.toInputStream("\r\n! REG.EXE  Path  REG_SZ  C:\\foo"));

		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("foo", "bar")).thenReturn(process);

		final String result = new WindowsRegistry(factory).readRegistry("foo", "bar");
		assertNull(result);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_createProcessThrowsIOException_returnsNull() throws Exception {
		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("foo", "bar")).thenThrow(new IOException());

		final WindowsRegistry windowsRegistry = new WindowsRegistry(factory);
		final Log logMock = mock(Log.class);
		windowsRegistry.log = logMock;
		final String result = windowsRegistry.readRegistry("foo", "bar");

		verify(logMock).error(Matchers.eq("LocalizedUserStrings.MSG_FAILED_TO_READ_REGISTRY(\"bar\")"), Matchers.any(IOException.class));
		assertNull(result);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_realWindowsXPOutput_returnsCorrectValue() throws Exception {
		final Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(
				IOUtils.toInputStream("\r\n! REG.EXE VERSION 3.0\r\n\r\nHKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes\r\n    Path\tREG_SZ\tC:\\Programme\\IBM\\Lotus\\Notes\\\r\n\r\n"));

		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path")).thenReturn(process);

		final String result = new WindowsRegistry(factory).readRegistry("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path");
		assertEquals("C:\\Programme\\IBM\\Lotus\\Notes\\\r\n\r\n", result);

	}
}
