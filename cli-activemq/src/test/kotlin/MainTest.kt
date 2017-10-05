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

import com.redhat.mqe.aoc.Main
import org.junit.jupiter.api.Test

class AocMainTest : AbstractMainTest() {

    override val brokerUrl = "tcp://10.37.145.71:61616"

    override val senderAdditionalOptions =
// TODO(jdanek): uncomment when we can handle mixing regular and reconnect options
//--conn-reconnect true
//--conn-reconnect-initial-delay 1
//--conn-reconnect-interval 1000
//--conn-reconnect-limit 1000
//--conn-reconnect-backoff-multiplier 1
//--conn-reconnect-backoff false
//--conn-reconnect-start-limit 1000
//--conn-reconnect-warn-attempts 1
//--conn-reconnect-timeout 1000
        """
--conn-tcp-traffic-class 2
--conn-prefix-packet-size-ena false
--capacity 1
--conn-async-send true
--conn-cache-ena false
--conn-cache-size 1
--conn-clientid aClientId
--conn-close-timeout 1000
--conn-heartbeat 1000
--conn-max-frame-size 4096
--conn-prefetch 1
--conn-prefetch-browser 1
--conn-prefetch-queue 1
--conn-prefetch-topic 1
--conn-prefetch-topic-dur 1
--conn-redeliveries-max 1
--conn-server-stack-trace-ena false
--conn-sync-send true
--conn-tcp-buf-size-recv 1
--conn-tcp-buf-size-send 1
--conn-tcp-conn-timeout 1000
--conn-tcp-keep-alive true
--conn-tcp-no-delay false
--conn-tcp-sock-linger 1000
--conn-tcp-sock-timeout 1000
--conn-tight-encoding-ena false
--msg-content-type aMsgContentType
--msg-correlation-id aCorrelationId
--msg-durable false
--msg-group-id aMsgGroupId
--msg-group-seq -1
--msg-no-timestamp true
--msg-priority 1
--timeout 2
--tx-size 1
--msg-reply-to aReplyToQueue
--msg-reply-to-group-id aReplyToGroupId
--msg-subject aMsgSubject
--msg-ttl 10000
--msg-user-id aMsgUserId
--property-type String
""".split(" ", "\n").toTypedArray()

    override fun main(args: Array<String>) = Main.main(args)


    @Test
    fun sendAndReceiveSingleMessage_ow() {
        val numberOfMessages = 100;

        val receiver1 = Thread {
            main("receiver --log-msgs dict --broker $brokerUrl --address Consumer.A.VirtualTopic.Orders --count 1".split(" ").toTypedArray())
        }

        val receiver2 = Thread {
            main("receiver --log-msgs dict --broker $brokerUrl --address Consumer.B.VirtualTopic.Orders --count 1000 -t 10".split(" ").toTypedArray())
        }

        val threads = ArrayList<Thread>()
        for (i in 1..numberOfMessages) {
            threads.add(Thread {
                main("sender --log-msgs dict --broker ${brokerUrl} --address topic://VirtualTopic.Orders --count 1".split(" ").toTypedArray())
            })
        }

        receiver1.start()
        receiver2.start()

        Thread.sleep(1000)

        for (t in threads) {
            t.start()
        }
        for (t in threads) {
            t.join()
        }
    }
}
