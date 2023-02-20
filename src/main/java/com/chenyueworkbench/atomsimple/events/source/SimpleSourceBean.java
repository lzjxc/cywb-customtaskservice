package com.chenyueworkbench.atomsimple.events.source;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.chenyueworkbench.atomsimple.events.model.AtomsimpleChangeModel;
import com.chenyueworkbench.atomsimple.utils.ActionEnum;
import com.chenyueworkbench.atomsimple.utils.UserContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleSourceBean {
    private Source source;

    public SimpleSourceBean(Source source){
        this.source = source;
    }

    public void publishAtomsimpleChange(ActionEnum action, String atomsimpleId){
       log.debug("Sending Kafka message {} for Atomsimple Id: {}", action, atomsimpleId);
        AtomsimpleChangeModel change =  new AtomsimpleChangeModel(
                AtomsimpleChangeModel.class.getTypeName(),
                action.toString(),
                atomsimpleId,
                UserContext.getCorrelationId(),
                UserContext.getStarterUserId());
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
