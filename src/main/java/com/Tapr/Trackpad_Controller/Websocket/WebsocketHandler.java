package com.Tapr.Trackpad_Controller.Websocket;


import com.Tapr.Trackpad_Controller.Services.GestureExecutionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private final GestureExecutionService gestureExecutionService;
    private final ObjectMapper objectMapper;

    public WebsocketHandler(GestureExecutionService gestureExecutionService, ObjectMapper objectMapper) {
        this.gestureExecutionService = gestureExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        GestureEventDTO event = objectMapper.readValue(json, GestureEventDTO.class);
        gestureExecutionService.executeGesture(event.getFingerCount(), event.getGestureType(), event.getValue());
    }
}


