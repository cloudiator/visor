/*
 * Copyright (c) 2014-2018 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.reporting.jms;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.jms.JMSEncoding.EncodingException;
import java.util.concurrent.ExecutionException;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

@Singleton
public class JMSProducer {

  private final Session session;
  private final TopicSelector topicSelector;
  private final Cache<String, MessageProducer> producerCache;
  private final JMSEncoding jmsEncoding;

  @Inject
  JMSProducer(@Named("jmsBroker") String broker, TopicSelector topicSelector,
      JMSEncoding jmsEncoding) throws JMSException {
    checkNotNull(jmsEncoding, "jmsEncoding is null");
    this.jmsEncoding = jmsEncoding;
    checkNotNull(broker, "broker is null");
    checkNotNull(topicSelector, "topicSelector is null");
    this.topicSelector = topicSelector;

    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(broker);
    Connection connection = activeMQConnectionFactory.createConnection();

    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    producerCache = CacheBuilder.newBuilder().build();


  }

  public void sendMetric(Metric metric) throws JMSException, EncodingException {
    String topic = topicSelector.apply(metric);

    try {
      final MessageProducer messageProducer = producerCache
          .get(topic, () -> {
            checkNotNull(topic, "topic is null");
            Destination destination = session.createTopic(topic);
            return session.createProducer(destination);
          });
      messageProducer.send(session.createTextMessage(jmsEncoding.encode(metric)));
    } catch (ExecutionException e) {
      if (e.getCause() instanceof JMSException) {
        throw (JMSException) e.getCause();
      }
      throw new IllegalStateException("Error while creating message producer", e);
    }


  }

}
