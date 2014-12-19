/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.samples.chat.jersey.web;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.*;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.atmosphere.util.SimpleBroadcaster;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import java.io.IOException;

import static java.lang.String.format;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

/**
 * @author Ilya Sadykov
 */
@Singleton
@AtmosphereHandlerService(
        interceptors = {
                AtmosphereResourceLifecycleInterceptor.class,
                TrackMessageSizeInterceptor.class,
                BroadcastOnPostAtmosphereInterceptor.class,
                SuspendTrackerInterceptor.class},
        broadcaster = SimpleBroadcaster.class)
public class ChatHandler extends AtmosphereHandlerAdapter implements AtmosphereServletProcessor {
    private ApplicationContext context;

    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {
        final AtmosphereRequest req = resource.getRequest();
        final Broadcaster broadcaster = getBroadcaster(req);
        resource.setBroadcaster(broadcaster);
        if (req.getMethod().equalsIgnoreCase("GET")) {
            resource.suspend();
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        AtmosphereResource r = event.getResource();
        AtmosphereResponse res = r.getResponse();
        if (r.isSuspended() && event.getMessage() != null) {
            String body = event.getMessage().toString();
            res.getWriter().write(body);
            switch (r.transport()) {
                case JSONP:
                case LONG_POLLING:
                    event.getResource().resume();
                    break;
                case WEBSOCKET:
                case STREAMING:
                    res.getWriter().flush();
                    break;
            }
        } else if (!event.isResuming()) {
            event.broadcaster().broadcast(event.getMessage());
        }
    }

    private Broadcaster getBroadcaster(AtmosphereRequest request) {
        final String room = request.getParameter("room");
        final String topic = request.getParameter("topic");
        final String broadcasterId = format("%s/%s", room, topic);
        final BroadcasterFactory factory = context.getBean(BroadcasterFactory.class);
        return factory.lookup(broadcasterId);
    }


    @Override
    public void init(AtmosphereConfig config) throws ServletException {
        context = getWebApplicationContext(config.getServletContext());
    }
}
