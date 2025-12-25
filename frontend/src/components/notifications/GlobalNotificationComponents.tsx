import React, { useState, useEffect } from 'react';
import { Bell, Wifi, WifiOff } from 'lucide-react';
import { useWebSocket, type GlobalNotification } from '../../contexts/WebSocketContext';
import ToastFeed, { type ToastNotification } from '../common/PopNotification';

export const ConnectionStatus: React.FC = () => {
  const { connectionStatus } = useWebSocket();

  const statusConfig = {
    connected: { icon: Wifi, color: 'text-green-500', bg: 'bg-green-500/10', text: 'Connected' },
    connecting: { icon: Wifi, color: 'text-yellow-500', bg: 'bg-yellow-500/10', text: 'Connecting...' },
    reconnecting: { icon: Wifi, color: 'text-orange-500', bg: 'bg-orange-500/10', text: 'Reconnecting...' },
    error: { icon: WifiOff, color: 'text-red-500', bg: 'bg-red-500/10', text: 'Connection Error' },
    disconnected: { icon: WifiOff, color: 'text-gray-500', bg: 'bg-gray-500/10', text: 'Disconnected' }
  };

  const config = statusConfig[connectionStatus];
  const Icon = config.icon;

  if (connectionStatus === 'connected') return null;

  return (
    <div className={`fixed top-4 right-4 z-50 flex items-center gap-2 px-4 py-2 rounded-lg ${config.bg} border border-white/10`}>
      <Icon className={`w-4 h-4 ${config.color} ${connectionStatus === 'connecting' || connectionStatus === 'reconnecting' ? 'animate-pulse' : ''}`} />
      <span className={`text-sm font-anta ${config.color}`}>{config.text}</span>
    </div>
  );
};

// Convert GlobalNotification to ToastNotification format
function mapGlobalToToast(notification: GlobalNotification): ToastNotification {
  const type = notification.type === 'warning' ? 'info' : notification.type;
  
  const sender = notification.metadata?.senderUsername || 
                 notification.metadata?.accepterUsername || 
                 'System';

  const numericId = notification.id.split('-').reduce((acc, part) => {
    const num = parseInt(part) || 0;
    return acc + num;
  }, 0);

  return {
    id: numericId,
    title: notification.title,
    message: notification.message,
    sender: sender,
    type: type as 'info' | 'success' | 'error'
  };
}

export const GlobalNotificationToasts: React.FC = () => {
  const { notifications, removeNotification, markAsRead } = useWebSocket();
  const [displayedNotifications, setDisplayedNotifications] = useState<ToastNotification[]>([]);
  const [processedIds, setProcessedIds] = useState<Set<string>>(new Set());

  // Watch for new notifications and add them to display queue
  useEffect(() => {
    notifications.slice(0, 3).forEach(notification => {
      if (!processedIds.has(notification.id)) {
        // Mark as read immediately
        if (!notification.read) {
          markAsRead(notification.id);
        }

        // Add to processed set
        setProcessedIds(prev => new Set([...prev, notification.id]));

        // Add to display queue
        const toastNotification = mapGlobalToToast(notification);
        setDisplayedNotifications(prev => {
          // Check if already in display queue
          if (prev.some(n => n.id === toastNotification.id)) {
            return prev;
          }
          return [...prev, toastNotification];
        });
      }
    });
  }, [notifications, markAsRead, processedIds]);

  // Handle close (manual or auto after 5 seconds)
  const handleClose = (numericId: number) => {
    // Remove from display queue
    setDisplayedNotifications(prev => prev.filter(n => n.id !== numericId));

    // Find and remove from context
    const notification = notifications.find(n => {
      const mapped = mapGlobalToToast(n);
      return mapped.id === numericId;
    });

    if (notification) {
      removeNotification(notification.id);
      // Clean up processed IDs (remove old ones to prevent memory leak)
      setProcessedIds(prev => {
        const newSet = new Set(prev);
        newSet.delete(notification.id);
        return newSet;
      });
    }
  };

  return <ToastFeed notifications={displayedNotifications} onClose={handleClose} />;
};

export const NotificationButton: React.FC<{ onClick: () => void }> = ({ onClick }) => {
  const { unreadCount } = useWebSocket();

  return (
    <button
      onClick={onClick}
      className="relative p-2 text-text hover:text-orange transition-colors"
      aria-label={`Notifications ${unreadCount > 0 ? `(${unreadCount} unread)` : ''}`}
    >
      <Bell className="w-6 h-6" />
      {unreadCount > 0 && (
        <span className="absolute -top-1 -right-1 w-5 h-5 bg-orange rounded-full flex items-center justify-center text-white text-xs font-bold">
          {unreadCount > 9 ? '9+' : unreadCount}
        </span>
      )}
    </button>
  );
};