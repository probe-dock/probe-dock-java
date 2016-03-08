package io.probedock.client.common.config;

import io.probedock.client.utils.ConfigurationUtils;
import io.probedock.client.utils.EnvironmentUtils;
import org.apache.commons.configuration.ConfigurationException;

import java.util.Map;

/**
 * Store the SCM remote information
 *
 * @author Laurent Prevost laurent.prevost@probedock.io
 */
public class ScmRemoteInfo {
    public static final String ENV_SCM_REMOTE_NAME = "SCM_REMOTE_NAME";
    public static final String ENV_SCM_REMOTE_URL_FETCH = "SCM_REMOTE_URL_FETCH";
    public static final String ENV_SCM_REMOTE_URL_PUSH = "SCM_REMOTE_URL_PUSH";
    public static final String ENV_SCM_REMOTE_AHEAD = "SCM_REMOTE_AHEAD";
    public static final String ENV_SCM_REMOTE_BEHIND = "SCM_REMOTE_BEHIND";

    private String name;

    private String fetchUrl;
    private String pushUrl;

    private Integer ahead;
    private Integer behind;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public Integer getAhead() {
        return ahead;
    }

    public void setAhead(Integer ahead) {
        this.ahead = ahead;
    }

    public Integer getBehind() {
        return behind;
    }

    public void setBehind(Integer behind) {
        this.behind = behind;
    }

    @SuppressWarnings("unchecked")
    public void configureWith(Map<String, Object> data) throws ConfigurationException {
        name = ConfigurationUtils.configureString(name, data, "name");

        Object value = data.get("url");

        if (value != null) {
            if (!(value instanceof Map)) {
                throw new ConfigurationException("The \"url\" must be a map.");
            }

            Map<String, Object> url = (Map<String, Object>) value;

            this.fetchUrl = ConfigurationUtils.configureString(fetchUrl, url, "fetch");
            this.pushUrl = ConfigurationUtils.configureString(pushUrl, url, "push");
        }

        this.ahead = ConfigurationUtils.configureInteger(ahead, data, "ahead");
        this.behind = ConfigurationUtils.configureInteger(behind, data, "behind");
    }

    public void overrideByEnvVars() {
        name = EnvironmentUtils.getEnvironmentString(ENV_SCM_REMOTE_NAME, name);
        fetchUrl = EnvironmentUtils.getEnvironmentString(ENV_SCM_REMOTE_URL_FETCH, fetchUrl);
        pushUrl = EnvironmentUtils.getEnvironmentString(ENV_SCM_REMOTE_URL_PUSH, pushUrl);
        ahead = EnvironmentUtils.getEnvironmentInteger(ENV_SCM_REMOTE_AHEAD, ahead);
        behind = EnvironmentUtils.getEnvironmentInteger(ENV_SCM_REMOTE_BEHIND, behind);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("name: \"").append(name).append("\"");
        builder.append(", fetchUrl: \"").append(fetchUrl).append("\"");
        builder.append(", pushUrl: \"").append(pushUrl).append("\"");
        builder.append(", ahead: \"").append(ahead).append("\"");
        builder.append(", behind: \"").append(behind).append("\"");

        return builder.toString();
    }
}
