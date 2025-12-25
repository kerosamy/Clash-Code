import React, { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import { EnhancedWebSocketService, type ConnectionStatus } from '../services/ws.ts';
import { getUsername } from '../utils/jwtDecoder';

export interface GlobalNotification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  metadata?: any;
}

interface WebSocketContextType {
  connectionStatus: ConnectionStatus;
  notifications: GlobalNotification[];
  unreadCount: number;
  subscribe: (destination: string, callback: (payload: any) => void) => (() => void) | undefined;
  send: (destination: string, body: any) => void;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  removeNotification: (id: string) => void;
  clearAll: () => void;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const WebSocketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [connectionStatus, setConnectionStatus] = useState<ConnectionStatus>('disconnected');
  const [notifications, setNotifications] = useState<GlobalNotification[]>([]);
  const wsService = useRef(new EnhancedWebSocketService());
  const processedMessageIds = useRef(new Set<string>());

  const handleWebSocketMessage = useCallback((message: any) => {
    // Create unique ID for deduplication based on message content
    const messageId = `${message.notificationType}-${message.matchId || ''}-${message.senderUsername || ''}-${message.submissionStatus || ''}-${Math.floor(Date.now() / 1000)}`;
    
    // Skip if already processed (within same second)
    if (processedMessageIds.current.has(messageId)) {
      console.log('🚫 Skipping duplicate message:', messageId);
      return;
    }
    
    processedMessageIds.current.add(messageId);
    
    // Clean up old IDs (keep only last 100)
    if (processedMessageIds.current.size > 100) {
      const entries = Array.from(processedMessageIds.current);
      entries.slice(0, 50).forEach(id => processedMessageIds.current.delete(id));
    }

    const notification = mapWebSocketMessageToNotification(message);
    if (notification) {
      setNotifications(prev => {
        // Additional check: don't add if same notification already exists recently
        const isDuplicate = prev.some(n => 
          n.title === notification.title && 
          n.message === notification.message &&
          Date.now() - n.timestamp.getTime() < 2000 // within 2 seconds
        );
        
        if (isDuplicate) {
          console.log('🚫 Duplicate notification detected in state, skipping');
          return prev;
        }
        
        console.log('✅ Adding new notification:', notification.title);
        return [notification, ...prev].slice(0, 50);
      });
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const user = getUsername();
    
    if (!token || token === 'undefined' || !user) {
      return;
    }

    wsService.current.connect(token, setConnectionStatus, handleWebSocketMessage);

    // ONLY subscribe once here in the context - this is the single source of truth
    setTimeout(() => {
      if (wsService.current.isConnected() && user) {
        console.log('🔌 Subscribing to notifications for:', user);
        wsService.current.subscribe(`/topic/match-pop/${user}`, (payload) => {
          console.log('📬 WebSocket message received:', payload.notificationType);
          // Messages are already handled by handleWebSocketMessage in connect
        });
      }
    }, 1000);

    return () => {
      wsService.current.disconnect();
      processedMessageIds.current.clear();
    };
  }, [handleWebSocketMessage]);

  const subscribe = useCallback((destination: string, callback: (payload: any) => void) => {
    return wsService.current.subscribe(destination, callback);
  }, []);

  const send = useCallback((destination: string, body: any) => {
    wsService.current.send(destination, body);
  }, []);

  const markAsRead = useCallback((id: string) => {
    setNotifications(prev => 
      prev.map(n => n.id === id ? { ...n, read: true } : n)
    );
  }, []);

  const markAllAsRead = useCallback(() => {
    setNotifications(prev => prev.map(n => ({ ...n, read: true })));
  }, []);

  const removeNotification = useCallback((id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  }, []);

  const clearAll = useCallback(() => {
    setNotifications([]);
  }, []);

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <WebSocketContext.Provider value={{
      connectionStatus,
      notifications,
      unreadCount,
      subscribe,
      send,
      markAsRead,
      markAllAsRead,
      removeNotification,
      clearAll
    }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error('useWebSocket must be used within WebSocketProvider');
  }
  return context;
};

function mapWebSocketMessageToNotification(message: any): GlobalNotification | null {
  const baseNotification: Partial<GlobalNotification> = {
    id: `${Date.now()}-${Math.random()}`,
    timestamp: new Date(),
    read: false,
    metadata: message
  };

  switch (message.notificationType) {
    case 'MATCH_STARTED':
      return {
        ...baseNotification,
        type: 'success',
        title: 'Match Started!',
        message: 'Your match has begun. Good luck!',
      } as GlobalNotification;

    case 'MATCH_COMPLETED':
      return {
        ...baseNotification,
        type: 'info',
        title: 'Match Completed',
        message: 'The match has ended. Check your results!',
      } as GlobalNotification;

    case 'USER_RESIGNED':
      return {
        ...baseNotification,
        type: 'success',
        title: 'Opponent Resigned',
        message: `${message.senderUsername} has resigned from the match.`,
      } as GlobalNotification;

    case 'SUBMISSION_RECEIVED':
      return {
        ...baseNotification,
        type: 'info',
        title: 'Code Submitted',
        message: `${message.senderUsername} submitted a solution`,
      } as GlobalNotification;

    case 'SUBMISSION_RESULT':
      { const isSuccess = message.submissionStatus === 'ACCEPTED';
      return {
        ...baseNotification,
        type: isSuccess ? 'success' : 'error',
        title: isSuccess ? 'Solution Accepted' : 'Submission Failed',
        message: `${message.senderUsername} ${isSuccess ? 'passed' : 'failed'} ${message.passedCases}/${message.totalCases} test cases`,
      } as GlobalNotification; }

    case 'FRIEND_REQUEST_RECEIVED':
      return {
        ...baseNotification,
        type: 'info',
        title: 'New Friend Request',
        message: `${message.senderUsername} sent you a friend request`,
      } as GlobalNotification;

    case 'FRIEND_REQUEST_ACCEPTED':
      return {
        ...baseNotification,
        type: 'success',
        title: 'Friend Request Accepted',
        message: `${message.accepterUsername} accepted your friend request`,
      } as GlobalNotification;

    default:
      return {
        ...baseNotification,
        type: 'info',
        title: message.title || 'Notification',
        message: message.message || 'You have a new notification',
      } as GlobalNotification;
  }
}