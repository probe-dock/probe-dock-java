package io.probedock.client.common.config;

import io.probedock.client.common.utils.FingerprintGenerator;
import io.probedock.client.utils.ConfigurationUtils;
import io.probedock.client.utils.EnvironmentUtils;
import org.apache.commons.configuration.ConfigurationException;

import java.util.Map;

/**
 * SCM info
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class ScmInfo {
	public static final String ENV_SCM_NAME = "SCM_NAME";
	public static final String ENV_SCM_VERSION = "SCM_VERSION";
	public static final String ENV_SCM_DIRTY = "SCM_DIRTY";

	private String name;
	private String version;

	private Boolean dirty;

	private String branch;
	private String commit;

	private ScmRemoteInfo remote;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ScmRemoteInfo getRemote() {
		return remote;
	}

	public void setRemote(ScmRemoteInfo remote) {
		this.remote = remote;
	}

	public Boolean isDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	@SuppressWarnings("unchecked")
	public void configureWith(Map<String, Object> data) throws ConfigurationException {
		this.name = ConfigurationUtils.configureString(name, data, "name");
		this.version = ConfigurationUtils.configureString(version, data, "version");
		this.branch = ConfigurationUtils.configureString(branch, data, "branch");
		this.commit = ConfigurationUtils.configureString(commit, data, "commit");
		this.dirty = ConfigurationUtils.configureBoolean(dirty, data, "dirty");

		Object value = data.get("remote");

		if (value != null) {
			if (!(value instanceof Map)) {
				throw new ConfigurationException("The \"remote\" must be a map.");
			}
			else {
				this.remote.configureWith((Map<String, Object>) value);
			}
		}
	}

	public void overrideByEnvVars() {
		name = EnvironmentUtils.getEnvironmentString(ENV_SCM_NAME, name);
		version = EnvironmentUtils.getEnvironmentString(ENV_SCM_VERSION, version);
		dirty = EnvironmentUtils.getEnvironmentBoolean(ENV_SCM_DIRTY, dirty);
		remote.overrideByEnvVars();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("name: \"").append(name).append("\"");
		builder.append(", version: \"").append(version).append("\"");
		builder.append(", dirty: \"").append(dirty).append("\"");
		builder.append(", remote: \"").append(remote).append("\"");

		return builder.toString();
	}
}
