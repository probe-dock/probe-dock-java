package io.probedock.client.core.serializer;

import io.probedock.client.common.model.ProbeTestRun;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Serializer interface
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public interface ProbeSerializer {

	/**
	 * Serialize a payload
	 *
	 * @param osw Output stream
	 * @param payload The payload to serialize
	 * @param pretty Whether to indent the output
	 * @exception IOException
	 */
	void serializePayload(OutputStreamWriter osw, ProbeTestRun payload, boolean pretty) throws IOException;

	/**
	 * Deserialize a payload
	 *
	 * @param <T> The type of payload to deserialize
	 * @param isr Input stream
	 * @param clazz The type to deserialize
	 * @return Payload The payload deserialized
	 * @throws IOException I/O Errors
	 */
	<T extends ProbeTestRun> T deserializePayload(InputStreamReader isr, Class<T> clazz) throws IOException;
}
