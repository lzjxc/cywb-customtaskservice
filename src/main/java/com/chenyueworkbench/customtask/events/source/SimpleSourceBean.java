package com.chenyueworkbench.customtask.events.source;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.chenyueworkbench.customtask.events.model.CustomtaskChangeModel;
import com.chenyueworkbench.customtask.utils.ActionEnum;
import com.chenyueworkbench.customtask.utils.UserContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleSourceBean {
    private Source source;

    public SimpleSourceBean(Source source){
        this.source = source;
    }

    public void publishCustomtaskChange(ActionEnum action, String customtaskId){
       log.debug("Sending Kafka message {} for Customtask Id: {}", action, customtaskId);
        CustomtaskChangeModel change =  new CustomtaskChangeModel(
                CustomtaskChangeModel.class.getTypeName(),
                action.toString(),
                customtaskId,
                UserContext.getCorrelationId(),
                UserContext.getStarterUserId());
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
