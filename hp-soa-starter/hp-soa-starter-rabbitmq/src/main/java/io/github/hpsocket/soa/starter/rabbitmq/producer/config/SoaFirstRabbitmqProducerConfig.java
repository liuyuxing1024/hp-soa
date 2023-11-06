package io.github.hpsocket.soa.starter.rabbitmq.producer.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitStreamTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import org.springframework.retry.RecoveryCallback;

import com.rabbitmq.stream.Environment;

import io.github.hpsocket.soa.starter.rabbitmq.common.properties.SoaFirstRabbitmqProperties;

@AutoConfiguration
@ConditionalOnBean({SoaRabbitmqProducerConfig.class, SoaFirstRabbitmqProperties.class})
public class SoaFirstRabbitmqProducerConfig extends SoaAbstractRabbitmqProducerConfig
{
    public static final String rabbitTemplateConfigurerBeanName = "firstRabbitTemplateConfigurer";
    public static final String rabbitTemplateBeanName = "firstRabbitTemplate";
    public static final String rabbitCachingConnectionFactoryBeanName = "firstRabbitCachingConnectionFactory";
    public static final String rabbitTemplateCustomizerBeanName = "firstRabbitTemplateCustomizer";
    public static final String rabbitReturnsCallbackBeanName = "firstRabbitReturnsCallback";
    public static final String rabbitConfirmCallbackBeanName = "firstRabbitConfirmCallback";
    public static final String rabbitRecoveryCallbackBeanName = "firstRabbitRecoveryCallback";
    public static final String rabbitMessagingTemplateBeanName = "firstRabbitMessagingTemplate";
    public static final String rabbitStreamTemplateConfigurerBeanName = "firstRabbitStreamTemplateConfigurer";
    public static final String rabbitStreamProducerCustomizerBeanName = "firstRabbitStreamProducerCustomizer";
    public static final String rabbitStreamTemplateBeanName = "firstRabbitStreamTemplate";
    public static final String rabbitStreamEnvironmentBeanName = "firstRabbitStreamEnvironment";
    
    public SoaFirstRabbitmqProducerConfig(SoaFirstRabbitmqProperties properties)
    {
        super(properties);
    }

    @Override
    @Bean(rabbitTemplateConfigurerBeanName)
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers)
    {
        return super.rabbitTemplateConfigurer(messageConverter, retryTemplateCustomizers);
    }
    
    @Override
    @Bean(rabbitTemplateBeanName)
    public RabbitTemplate rabbitTemplate(
        @Qualifier(rabbitTemplateConfigurerBeanName) RabbitTemplateConfigurer configurer,
        @Qualifier(rabbitCachingConnectionFactoryBeanName) ConnectionFactory connectionFactory,
        @Qualifier(rabbitTemplateCustomizerBeanName) ObjectProvider<RabbitTemplateCustomizer> customizers,
        @Qualifier(rabbitReturnsCallbackBeanName) ObjectProvider<ReturnsCallback> returnsCallback,
        @Qualifier(rabbitConfirmCallbackBeanName) ObjectProvider<ConfirmCallback> confirmCallback,
        @Qualifier(rabbitRecoveryCallbackBeanName) ObjectProvider<RecoveryCallback<?>> recoveryCallback)
    {
        return super.rabbitTemplate(configurer, connectionFactory, customizers, returnsCallback, confirmCallback, recoveryCallback);
    }
    
    @Override
    @Bean(rabbitReturnsCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitReturnsCallbackBeanName)
    public ReturnsCallback returnsCallback()
    {
        return super.returnsCallback();
    }
    
    @Override
    @Bean(rabbitConfirmCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitConfirmCallbackBeanName)
    public ConfirmCallback confirmCallback()
    {
        return super.confirmCallback();
    }
    
    @Override
    @Bean(rabbitRecoveryCallbackBeanName)
    @ConditionalOnMissingBean(name = rabbitRecoveryCallbackBeanName)
    public <T> RecoveryCallback<T> recoveryCallback()
    {
        return super.recoveryCallback();
    }
    
    @Override
    @Bean(rabbitMessagingTemplateBeanName)
    public RabbitMessagingTemplate rabbitMessagingTemplate(@Qualifier(rabbitTemplateBeanName) RabbitTemplate rabbitTemplate)
    {
        return super.rabbitMessagingTemplate(rabbitTemplate);
    }
    
    @Override
    @Bean(rabbitStreamTemplateConfigurerBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnMissingBean(name = rabbitStreamTemplateConfigurerBeanName)
    public RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(
        ObjectProvider<MessageConverter> messageConverter,
        ObjectProvider<StreamMessageConverter> streamMessageConverter,
        @Qualifier(rabbitStreamProducerCustomizerBeanName)ObjectProvider<ProducerCustomizer> producerCustomizer)
    {
        return super.rabbitStreamTemplateConfigurer(messageConverter, streamMessageConverter, producerCustomizer);
    }

    @Override
    @Bean(rabbitStreamTemplateBeanName)
    @ConditionalOnClass(StreamRabbitListenerContainerFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq-first.stream", name = "name")
    public RabbitStreamTemplate rabbitStreamTemplate(
        @Qualifier(rabbitStreamEnvironmentBeanName) Environment rabbitStreamEnvironment,
        @Qualifier(rabbitStreamTemplateConfigurerBeanName) RabbitStreamTemplateConfigurer configurer)
    {
        return super.rabbitStreamTemplate(rabbitStreamEnvironment, configurer);
    }

}
