package com.tradingbot.dotty.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsocketMessageDTO {

     private String type;
     private String topic;
     private String message;

     @Override
     public String toString() {
          return topic == null ? "{\"type\": \"" + type +  "\", \"message\": \"" + message + "\"}" :
                  message == null ? "{\"type\": \"" + type + "\", \"topic\": \"" + topic + "\"}" :
                          "{\"type\": \"" + type + "\", \"topic\": \"" + topic + "\", \"message\": \"" + message + "\"}";
     }

}
