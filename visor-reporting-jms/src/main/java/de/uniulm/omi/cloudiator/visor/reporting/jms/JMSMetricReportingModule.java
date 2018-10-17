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

import static com.google.common.base.Preconditions.checkArgument;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSink.DataSinkConfiguration;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import javax.jms.JMSException;

/**
 * Created by daniel on 01.12.16.
 */
public class JMSMetricReportingModule extends MetricReportingModule {

  private static final String IDENTIFIER = "JMS";

  @Override
  protected void configure() {
    super.configure();
    bind(JMSEncoding.class).to(MelodicJsonEncoding.class);
    bind(TopicSelector.class).to(MetricNameTopicSelector.class);
  }

  private static class JMSReportingInterfaceFactory implements ReportingInterfaceFactory<Metric> {

    private static final String JMS_BROKER = "jms.broker";
    private static final String JMS_TOPIC_SELECTOR = "jms.topic.selector";
    private static final String JMS_MESSAGE_FORMAT = "jms.message.format";
    private static final String VALIDATION_ERROR = "Expected configuration %s to contain %s.";

    @Override
    public ReportingInterface<Metric> of(DataSinkConfiguration dataSinkConfiguration) {

      validate(dataSinkConfiguration);

      final String broker = dataSinkConfiguration.values().get(JMS_BROKER);
      final TopicSelector topicSelector = loadTopicSelector(
          dataSinkConfiguration.values().get(JMS_TOPIC_SELECTOR));
      final JMSEncoding jmsEncoding = loadEncoding(
          dataSinkConfiguration.values().get(JMS_MESSAGE_FORMAT));

      try {
        return new JMSReporter(new JMSProducer(broker, topicSelector, jmsEncoding));
      } catch (JMSException e) {
        throw new IllegalStateException("Could not establish JMS communication.", e);
      }
    }

    private JMSEncoding loadEncoding(String clazz) {
      try {
        return (JMSEncoding) Class.forName(clazz).newInstance();
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
        throw new IllegalStateException("Could not load jms encoding class " + clazz, e);
      }
    }

    private TopicSelector loadTopicSelector(String clazz) {
      try {
        return (TopicSelector) Class.forName(clazz).newInstance();
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
        throw new IllegalStateException("Could not load topic selector class " + clazz, e);
      }
    }

    private void validate(DataSinkConfiguration dataSinkConfiguration) {
      checkArgument(dataSinkConfiguration.values().containsKey(JMS_BROKER),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, JMS_BROKER));
      checkArgument(dataSinkConfiguration.values().containsKey(JMS_TOPIC_SELECTOR),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, JMS_TOPIC_SELECTOR));
      checkArgument(dataSinkConfiguration.values().containsKey(JMS_MESSAGE_FORMAT),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, JMS_MESSAGE_FORMAT));
    }


  }


  private static final JMSReportingInterfaceFactory INSTANCE = new JMSReportingInterfaceFactory();

  @Override
  protected ReportingInterfaceFactory<Metric> reportingInterfaceFactory() {
    return INSTANCE;
  }

  @Override
  protected String identifier() {
    return IDENTIFIER;
  }
}
