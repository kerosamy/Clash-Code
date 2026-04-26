export const generateInitialImage = (username?: string, color?: string): Promise<File> => {
  return new Promise((resolve, reject) => {
    try {
      const size = 512;
      const canvas = document.createElement('canvas');
      canvas.width = size;
      canvas.height = size;
      const ctx = canvas.getContext('2d');
      if (!ctx) throw new Error("Could not get canvas context");

      const finalUsername = username && username.length > 0 ? username : "User";
      const finalColor = color || "#999999";

      const hexToRgbaWithAlpha20 = (hex: string) => {
        const cleanHex = hex.replace('#', '');
        const r = parseInt(cleanHex.slice(0, 2), 16);
        const g = parseInt(cleanHex.slice(2, 4), 16);
        const b = parseInt(cleanHex.slice(4, 6), 16);
        return `rgba(${r}, ${g}, ${b}, ${32 / 255})`;
      };

      ctx.fillStyle = hexToRgbaWithAlpha20(finalColor);
      ctx.fillRect(0, 0, size, size);

      const initial = finalUsername.charAt(0).toUpperCase();
      ctx.font = 'bold 260px "Anta", sans-serif';
      ctx.fillStyle = finalColor;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(initial, size / 2, size / 2);

      canvas.toBlob((blob) => {
        if (blob) {
          resolve(new File([blob], `${finalUsername}_avatar.png`, { type: 'image/png' }));
        } else {
          const emptyCanvas = document.createElement('canvas');
          emptyCanvas.width = size;
          emptyCanvas.height = size;
          emptyCanvas.toBlob((emptyBlob) => {
            if (emptyBlob) {
              resolve(new File([emptyBlob], 'default_avatar.png', { type: 'image/png' }));
            } else {
              reject(new Error('Failed to generate avatar'));
            }
          }, 'image/png');
        }
      }, 'image/png');

    } catch (err) {
      reject(err);
    }
  });
};
