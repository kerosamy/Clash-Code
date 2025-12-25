// src/services/websocket.service.ts
import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';

export type ConnectionStatus = 'disconnected' | 'connecting' | 'connected' | 'error' | 'reconnecting';

export class EnhancedWebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, StompSubscription> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 5000;
  private statusCallback?: (status: ConnectionStatus) => void;
  private messageCallback?: (message: any) => void;

  connect(token: string, onStatusChange: (status: ConnectionStatus) => void, onMessage: (msg: any) => void) {
    if (this.client?.connected) {
      console.log('WS already connected');
      return;
    }

    this.statusCallback = onStatusChange;
    this.messageCallback = onMessage;
    this.statusCallback('connecting');

    const WS_URL = 'wss://fugally-nonrepatriable-belle.ngrok-free.dev/ws';
    // wss://fugally-nonrepatriable-belle.ngrok-free.dev/ws
    //ws://localhost:8080/ws
    this.client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 20000,
      heartbeatOutgoing: 20000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      
      debug: (str) => {
        if (str.includes('ERROR')) console.error('STOMP:', str);
      },

      onConnect: () => {
        console.log('✅ WebSocket connected successfully');
        this.reconnectAttempts = 0;
        this.statusCallback?.('connected');
      },

      onDisconnect: () => {
        console.log('❌ WebSocket disconnected');
        this.statusCallback?.('disconnected');
        this.subscriptions.clear();
      },

      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame.headers['message']);
        this.statusCallback?.('error');
        
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnectAttempts++;
          this.statusCallback?.('reconnecting');
        }
      },

      onWebSocketError: (event) => {
        console.error('❌ WebSocket error:', event);
        this.statusCallback?.('error');
      }
    });

    this.client.activate();
  }

  subscribe(destination: string, callback: (payload: any) => void): (() => void) | undefined {
    if (!this.client || !this.client.connected) {
      console.warn('⚠️ Cannot subscribe: WS not connected');
      return undefined;
    }

    const subscription = this.client.subscribe(destination, (message: IMessage) => {
      try {
        const payload = JSON.parse(message.body);
        this.messageCallback?.(payload);
        callback(payload);
      } catch (e) {
        console.error('❌ Failed to parse WS message:', e);
      }
    });

    this.subscriptions.set(destination, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
    };
  }

  send(destination: string, body: any) {
    if (!this.client || !this.client.connected) {
      console.warn('⚠️ Cannot send: WS not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body)
    });
  }

  disconnect() {
    console.log('Disconnecting WebSocket...');
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.subscriptions.clear();
    this.client?.deactivate();
    this.client = null;
    this.statusCallback?.('disconnected');
  }

  isConnected(): boolean {
    return this.client?.connected ?? false;
  }
}

export const wsService = new EnhancedWebSocketService();