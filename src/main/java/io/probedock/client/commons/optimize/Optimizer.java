package io.probedock.client.commons.optimize;

import io.probedock.client.common.model.ProbeTestRun;

/**
 * Define how an optimizer should work
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public interface Optimizer {
	/**
	 * Execute the optimization 
	 * 
	 * @param store The optimizer store to check if a test has changed or not
	 * @param optimizable The optimizable to optimize
	 * @return The optimizable optimized
	 */
	ProbeTestRun optimize(OptimizerStore store, ProbeTestRun optimizable);
}
