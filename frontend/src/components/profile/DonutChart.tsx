import { enumColors } from "../../utils/colorMapper";

interface CategoryItem {
  name: string;
  value: number;
}

interface DonutChartProps {
  categories: CategoryItem[];
  color: string;
}

export default function DonutChart({ categories, color }: DonutChartProps) {
  const totalValue = categories.reduce((sum, cat) => sum + cat.value, 0);
  const radius = 100;
  const strokeWidth = 50;
  const circumference = 2 * Math.PI * radius;

  let cumulative = 0;
  const segments = categories.map((category) => {
    const percent = category.value / totalValue;
    const dash = percent * circumference;
    const offset = -cumulative;
    // eslint-disable-next-line react-hooks/immutability
    cumulative += dash;
    return { color: enumColors[category.name], dash, offset };
  });

  const size = (radius + strokeWidth) * 2;

  return (
    <div className="relative w-64 h-64 flex-shrink-0">
      <svg className="w-full h-full -rotate-90" viewBox={`0 0 ${size} ${size}`}>
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="#4B5563"
          strokeWidth={strokeWidth}
          className="opacity-50"
        />
        {segments.map((segment, index) => (
          <circle
            key={index}
            cx={size / 2}
            cy={size / 2}
            r={radius}
            fill="none"
            stroke={segment.color}
            strokeWidth={strokeWidth}
            strokeDasharray={`${segment.dash} ${circumference - segment.dash}`}
            strokeDashoffset={segment.offset}
            className="transition-all duration-500 ease-out"
          />
        ))}
      </svg>

      {/* Center label */}
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-2xl font-semibold" style={{ color }}>
          {totalValue}
        </span>
        <div className="text-sm text-text uppercase tracking-widest mt-1 opacity-60">
            Total
        </div>
      </div>
    </div>
  );
}
