import { Client, type IMessage } from "@stomp/stompjs";

let client: Client | null = null;

const WS_URL = "ws://localhost:8080/ws";
// when using ngrok later:
// const WS_URL = "wss://xxxx.ngrok-free.app/ws";

export const wsService = {
  connect(onConnect?: () => void) {
    if (client?.connected) return;

    const token = localStorage.getItem("token");

    client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
      debug: (str) => console.log("STOMP:", str),

      connectHeaders: token
        ? { Authorization: `Bearer ${token}` }
        : {},
    });

    client.onConnect = () => {
      console.log("WS connected");
      onConnect?.();
    };

    client.onStompError = (frame) => {
      console.error("Broker error:", frame.headers["message"]);
    };

    client.activate();
  },

  subscribe(destination: string, callback: (payload: any) => void) {
    if (!client || !client.connected) {
      console.warn("WS not connected");
      return;
    }

    return client.subscribe(destination, (message: IMessage) => {
      try {
        callback(JSON.parse(message.body));
      } catch (e) {
        console.error("WS parse error", e);
      }
    });
  },

  send(destination: string, body: any) {
    if (!client || !client.connected) return;

    client.publish({
      destination,
      body: JSON.stringify(body),
    });
  },

  disconnect() {
    client?.deactivate();
    client = null;
  },
};
