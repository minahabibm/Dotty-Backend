package com.tradingbot.dotty.utils.constants;

public enum WebSockets {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    BROADCAST("broadcast");

    public final String value;
    public final String topic ;
    public final String queue;

    WebSockets(String value) {
        this.value = value;
        this.topic = "/topic/";
        this.queue = "/queue/";
    }

    public static WebSockets fromString(String text) {
        for (WebSockets ws : WebSockets.values()) {
            if (ws.value.equalsIgnoreCase(text)) {
                return ws;
            }
        }
        throw new IllegalArgumentException("Unknown command: " + text);
    }

}
