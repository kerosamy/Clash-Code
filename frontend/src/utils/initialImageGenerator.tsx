export const generateInitialImage = (username: string, color: string): Promise<File> => {
    return new Promise((resolve) => {
        const size = 512;
        const canvas = document.createElement('canvas');
        canvas.width = size;
        canvas.height = size;
        const ctx = canvas.getContext('2d');

        if (!ctx) throw new Error("Could not get canvas context");

        const hexToRgbaWithAlpha20 = (hex: string) => {
            const cleanHex = hex.replace('#', '');
            const r = parseInt(cleanHex.slice(0, 2), 16);
            const g = parseInt(cleanHex.slice(2, 4), 16);
            const b = parseInt(cleanHex.slice(4, 6), 16);
            return `rgba(${r}, ${g}, ${b}, ${32 / 255})`;
        };

        ctx.fillStyle = hexToRgbaWithAlpha20(color);
        ctx.fillRect(0, 0, size, size);


        // Configure Anta Font & Center Text
        const initial = username.charAt(0).toUpperCase();
        ctx.font = 'bold 260px "Anta", sans-serif';
        ctx.fillStyle = color; 
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';

        // Draw the Initial
        ctx.fillText(initial, size / 2, size / 2);

        canvas.toBlob((blob) => {
            if (blob) {
                resolve(new File([blob], `${username}_avatar.png`, { type: 'image/png' }));
            }
        }, 'image/png');
    });
};