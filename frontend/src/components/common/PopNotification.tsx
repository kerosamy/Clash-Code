import { useEffect } from 'react';
import { X } from 'lucide-react';

export interface ToastNotification {
    id: number;
    title: string;
    message: string;
    sender: string;
    type: 'info' | 'success' | 'error';
}

interface ToastFeedProps {
    notifications: ToastNotification[];
    onClose?: (id: number) => void;
    onClick?: (id: number) => void;
}

const TOAST_STYLES = {
    success: {
        accent: 'bg-winGreen shadow-[0_0_10px_#81B64C]',
        title: 'text-winGreen',
        glow: 'bg-winGreen',
    },
    error: {
        accent: 'bg-loseRed shadow-[0_0_10px_#FF2424]',
        title: 'text-loseRed',
        glow: 'bg-loseRed',
    },
    info: {
        accent: 'bg-orange shadow-[0_0_10px_#EC7438]',
        title: 'text-orange',
        glow: 'bg-orange',
    },
};

function Toast({ 
    notification, 
    onClose,
    onClick
}: { 
    notification: ToastNotification; 
    onClose: (id: number) => void;
    onClick?: (id: number) => void;
}) {
    const style = TOAST_STYLES[notification.type] || TOAST_STYLES.info;

    // Auto-dismiss after 5 seconds
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose(notification.id);
        }, 5000);

        return () => clearTimeout(timer);
    }, [notification.id, onClose]);

    const handleClick = () => {
        if (onClick) {
            onClick(notification.id);
        }
    };

    return (
        <div
            key={notification.id}
            onClick={handleClick}
            className={`
                pointer-events-auto 
                relative overflow-hidden
                w-[350px] p-4
                bg-container 
                rounded-button 
                shadow-[0_8px_30px_rgb(0,0,0,0.5)] 
                border border-white/5
                animate-fade-in-out
                font-anta
                ${onClick ? 'cursor-pointer hover:bg-container/80 transition-colors' : ''}
            `}
        >
            {/* Left Accent Line */}
            <div className={`absolute top-0 left-0 w-1.5 h-full ${style.accent}`} />

            {/* Content */}
            <div className="pl-3 pr-8 flex flex-col gap-1">
                {/* Header: Title + Time */}
                <div className="flex justify-between items-center mb-1">
                    <h4 className={`text-sm font-bold tracking-wider uppercase ${style.title}`}>
                        {notification.title}
                    </h4>
                    <span className="text-xs text-text/40 font-mono">
                        {new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                </div>

                <p className="text-sm text-text leading-relaxed whitespace-pre-line">
                    {notification.message}
                </p>
            </div>

            {/* Close Button */}
            <button
                onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    onClose(notification.id);
                }}
                className="absolute top-2 right-2 p-1.5 text-text/60 hover:text-text transition-colors rounded hover:bg-white/10 pointer-events-auto cursor-pointer z-10"
                aria-label="Close notification"
                type="button"
            >
                <X className="w-4 h-4" />
            </button>

            {/* Background Glow */}
            <div className={`absolute -top-10 -right-10 w-20 h-20 blur-2xl opacity-10 rounded-full ${style.glow}`}></div>
        </div>
    );
}

export default function ToastFeed({ notifications, onClose, onClick }: ToastFeedProps) {
    if (notifications.length === 0) return null;

    const handleClose = (id: number) => {
        if (onClose) {
            onClose(id);
        }
    };

    const handleClick = (id: number) => {
        if (onClick) {
            onClick(id);
        }
    };

    return (
        <div className="fixed bottom-6 right-6 flex flex-col gap-4 z-[9999] pointer-events-none">
            {notifications.map((notification) => (
                <Toast 
                    key={notification.id} 
                    notification={notification} 
                    onClose={handleClose}
                    onClick={onClick ? handleClick : undefined}
                />
            ))}
        </div>
    );
}