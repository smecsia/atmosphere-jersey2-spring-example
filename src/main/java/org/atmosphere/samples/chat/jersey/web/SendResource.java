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

import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.samples.chat.jersey.ChatMessage;
import org.atmosphere.samples.chat.jersey.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import java.io.IOException;

import static java.lang.String.format;

/**
 * @author Ilya Sadykov
 */
@Path("/send")
@Component
public class SendResource {

    @Autowired
    BroadcasterFactory factory;

    @Autowired
    JsonSerializer jsonSerializer;

    @GET
    public String renderIndex(@QueryParam("room") String room,
                              @QueryParam("topic") String topic,
                              @QueryParam("message") String message) throws IOException {
        factory.lookup(format("%s/%s", room, topic)).broadcast(
                jsonSerializer.toJson(new ChatMessage("somebody", message))
        );
        return "OK";
    }

}
