// src/services/websocket.service.ts
import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type ConnectionStatus = 'disconnected' | 'connecting' | 'connected' | 'error' | 'reconnecting';

export class EnhancedWebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, StompSubscription> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;
  private reconnectDelay = 5000;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private statusCallback?: (status: ConnectionStatus) => void;
  private messageCallback?: (message: any) => void;
  private token: string = '';
  private manualDisconnect = false; // prevent retry loop on intentional disconnect

  connect(token: string, onStatusChange: (status: ConnectionStatus) => void, onMessage: (msg: any) => void) {
    this.token = token;
    this.statusCallback = onStatusChange;
    this.messageCallback = onMessage;
    this.manualDisconnect = false;

    this.attemptConnect();
  }

  private attemptConnect() {
    if (this.manualDisconnect) return;
    if (this.client?.connected) {
      console.log('WS already connected');
      return;
    }

    // Clean up previous client before creating a new one
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }

    this.statusCallback?.(this.reconnectAttempts > 0 ? 'reconnecting' : 'connecting');
    console.log(`🔄 WS connection attempt ${this.reconnectAttempts + 1}/${this.maxReconnectAttempts}`);

    const WS_URL = "https://clash-code-production-3790.up.railway.app/ws";

    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 0, // we handle reconnect manually
      heartbeatIncoming: 20000,
      heartbeatOutgoing: 20000,
      connectHeaders: this.token ? { Authorization: `Bearer ${this.token}` } : {},

      debug: (str) => {
        if (str.includes('ERROR')) console.error('STOMP:', str);
      },

      onConnect: () => {
        console.log('✅ WebSocket connected successfully');
        this.reconnectAttempts = 0;
        this.clearReconnectTimer();
        this.statusCallback?.('connected');
      },

      onDisconnect: () => {
        console.log('❌ WebSocket disconnected');
        this.statusCallback?.('disconnected');
        this.subscriptions.clear();
        this.scheduleReconnect(); // retry on drop
      },

      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame.headers['message']);
        this.statusCallback?.('error');
        this.scheduleReconnect();
      },

      onWebSocketError: (event) => {
        console.error('❌ WebSocket error:', event);
        this.statusCallback?.('error');
        this.scheduleReconnect(); // KEY: retry on initial connection failure too
      }
    });

    this.client.activate();
  }

  private scheduleReconnect() {
    if (this.manualDisconnect) return;
    if (this.reconnectTimer) return; // already scheduled

    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('❌ Max reconnect attempts reached. Giving up.');
      this.statusCallback?.('error');
      return;
    }

    this.reconnectAttempts++;
    console.log(`⏳ Reconnecting in ${this.reconnectDelay / 1000}s... (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null;
      this.attemptConnect();
    }, this.reconnectDelay);
  }

  private clearReconnectTimer() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
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
    this.manualDisconnect = true; // stops retry loop
    this.clearReconnectTimer();
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.subscriptions.clear();
    this.client?.deactivate();
    this.client = null;
    this.reconnectAttempts = 0;
    this.statusCallback?.('disconnected');
  }

  isConnected(): boolean {
    return this.client?.connected ?? false;
  }
}

export const wsService = new EnhancedWebSocketService();