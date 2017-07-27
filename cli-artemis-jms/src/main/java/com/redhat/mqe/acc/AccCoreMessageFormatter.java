/*
 * Copyright (c) 2017 Red Hat, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.mqe.acc;

import com.redhat.mqe.lib.CoreMessageFormatter;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;

import javax.jms.Destination;
import java.security.InvalidParameterException;

public class AccCoreMessageFormatter extends CoreMessageFormatter {
    @Override
    protected StringBuilder formatAddress(Destination destination) {
        if (destination == null) {
            return new StringBuilder("None");
        }
        if (!(destination instanceof ActiveMQDestination)) {
            throw new InvalidParameterException("Destination must be a Core destination, was " + destination.getClass());
        }

        String name = ((ActiveMQDestination) destination).getName();
        String address = dropDestinationPrefix(name);
        if (address == null) {
            return new StringBuilder("None");
        }
        return formatString(address);
    }
}
