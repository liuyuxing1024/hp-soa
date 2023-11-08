
package io.github.hpsocket.soa.starter.kafka.config;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerMdcInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.aspect.KafkaListenerTracingInspector;
import io.github.hpsocket.soa.starter.kafka.consumer.listener.KafkaReadOnlyEventListener;

/** <b>HP-SOA Kafka 配置</b> */
@AutoConfiguration(before = {KafkaAutoConfiguration.class})
@MapperScan(basePackages = "io.github.hpsocket.soa.starter.kafka.producer.mapper")
public class SoaKafkaConfig
{
    @Bean
    KafkaReadOnlyEventListener kafkaReadOnlyEventListener()
    {
        return new KafkaReadOnlyEventListener();
    }
    
    @Bean
    @ConditionalOnClass(Trace.class)
    KafkaListenerTracingInspector kafkaListenerTracingInspector()
    {
        return new KafkaListenerTracingInspector();
    }    
    
    @Bean
    KafkaListenerMdcInspector kafkaListenerMdcInspector()
    {
        return new KafkaListenerMdcInspector();
    }
    
    /** 默认 Kafka Template，在事务环境下支持以非事方式务发送消息。 */
    @Bean
    @ConditionalOnMissingBean(KafkaTemplate.class)
    public KafkaTemplate<?, ?> allowNonTransactionalKafkaTemplate(
        KafkaProperties properties,
        ProducerFactory<Object, Object> kafkaProducerFactory,
        ProducerListener<Object, Object> kafkaProducerListener,
        ObjectProvider<RecordMessageConverter> messageConverter)
    {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        map.from(kafkaProducerListener).to(kafkaTemplate::setProducerListener);
        map.from(properties.getTemplate().getDefaultTopic()).to(kafkaTemplate::setDefaultTopic);
        map.from(properties.getTemplate().getTransactionIdPrefix()).to(kafkaTemplate::setTransactionIdPrefix);
        kafkaTemplate.setAllowNonTransactional(true);

        return kafkaTemplate;
    }
    
}
