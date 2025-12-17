export interface ToastNotification {
    id: number;
    title: string;
    message: string;
    sender: string;
    type: 'info' | 'success' | 'error';
}

interface ToastFeedProps {
    notifications: ToastNotification[];
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

export default function ToastFeed({ notifications }: ToastFeedProps) {
    if (notifications.length === 0) return null;

    return (
        <div className="fixed bottom-6 right-6 flex flex-col gap-4 z-[9999] pointer-events-none">
            {notifications.map((n) => {
                const style = TOAST_STYLES[n.type] || TOAST_STYLES.info;

                return (
                    <div
                        key={n.id}
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
                        `}
                    >
                        {/* Left Accent Line */}
                        <div className={`absolute top-0 left-0 w-1.5 h-full ${style.accent}`} />

                        {/* Content */}
                        <div className="pl-3 flex flex-col gap-1">
                            {/* Header: Title + Time */}
                            <div className="flex justify-between items-center mb-1">
                                <h4 className={`text-sm font-bold tracking-wider uppercase ${style.title}`}>
                                    {n.title}
                                </h4>
                                <span className="text-xs text-text/40 font-mono">
                                    {new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </span>
                            </div>

                            <p className="text-sm text-text leading-relaxed">
                                
                                {n.message}
                            </p>
                        </div>

                        {/* Background Glow */}
                        <div className={`absolute -top-10 -right-10 w-20 h-20 blur-2xl opacity-10 rounded-full ${style.glow}`}></div>
                    </div>
                );
            })}
        </div>
    );
}