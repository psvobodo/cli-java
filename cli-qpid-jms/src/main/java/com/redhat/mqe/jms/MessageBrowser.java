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

package com.redhat.mqe.jms;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import static com.redhat.mqe.jms.ClientOptions.*;

/**
 * Class for browsing Queue(s) of messages.
 */
public class MessageBrowser extends CoreClient {

  private boolean transacted;
  private String msgSelector;
  ClientOptions clientOptions;

  MessageBrowser(ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
  }

  @Override
  ClientOptions getClientOptions() {
    return this.clientOptions;
  }

  void setMessageBrowser(ClientOptions options) {
    if (options != null) {
      transacted = Boolean.parseBoolean(options.getOption(TRANSACTED).getValue());
      if (options.getOption(ClientOptions.MSG_SELECTOR).hasParsedValue()) {
        msgSelector = options.getOption(ClientOptions.MSG_SELECTOR).getValue();
      }
    }
  }

  @Override
  void startClient() {
    this.setMessageBrowser(clientOptions);
    this.browseMessages();
  }

  /**
   * Browse messages using Queue Browser.
   * By default, you browse all actual messages in the queue.
   * Messages may be arriving and expiring while the scan is done.
   */
  void browseMessages() {
    Connection conn = createConnection(clientOptions);
    Session ssn = createSession(clientOptions, conn, transacted);
    try {
      QueueBrowser qBrowser = ssn.createBrowser((Queue) getDestination(), msgSelector);

      conn.start();

      Enumeration<?> enumMsgs = qBrowser.getEnumeration();
      while (enumMsgs.hasMoreElements()) {
        Message msg = (Message) enumMsgs.nextElement();
        CoreClient.printMessage(clientOptions, msg);
      }
    } catch (JMSException jmse) {
      LOG.trace("Exception while browsing messages", jmse);
      jmse.printStackTrace();
      System.exit(1);
    } finally {
      close(conn);
    }
  }
}
