package org.atmosphere.samples.chat.jersey;

import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.cpr.Deliver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ilya Sadykov
 */
public class CustomBroadcaster extends DefaultBroadcaster {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void dispatchMessages(Deliver e) {
        logger.info("Dispatching message {}...", e.getMessage());
        super.dispatchMessages(e);
    }
}

