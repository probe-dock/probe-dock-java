package io.probedock.client.common.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test for class {@link MetaDataBuilder}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class MetaDataBuilderTest {
	@Test
	public void itShouldBePossibleToAddMetaDataInTheBuilder() {
		MetaDataBuilder builder = new MetaDataBuilder();

		builder.add("k1", "v1").add("k2", "v2");

		Map<String, String> data = builder.toMetaData();

		assertEquals("v1", data.get("k1"));
		assertEquals("v2", data.get("k2"));
	}

	@Test
	public void itShouldBePossibleToMergeTwoMetaDataBuilders() {
		MetaDataBuilder builder1 = new MetaDataBuilder();
		MetaDataBuilder builder2 = new MetaDataBuilder();

		builder1.add("k1", "v1");
		builder2.add("k2", "v2");

		builder1.add(builder2);

		Map<String, String> data = builder1.toMetaData();

		assertEquals("v1", data.get("k1"));
		assertEquals("v2", data.get("k2"));
	}
}
