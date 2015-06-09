package io.probedock.client.core.serializer.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.probedock.client.common.model.ProbeTestRun;
import io.probedock.client.core.serializer.ProbeSerializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Json Serializer implementation of {@link ProbeSerializer}
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class JsonSerializer implements ProbeSerializer {

	@Override
	public void serializePayload(OutputStreamWriter osw, ProbeTestRun probeTestRun, boolean pretty) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

		if (pretty) {
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		}

		mapper.writeValue(osw, probeTestRun);
	}

	@Override
	public <T extends ProbeTestRun> T deserializePayload(InputStreamReader isr, Class<T> clazz) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(isr, clazz);
	}
}
