import DonutChart from "../profile/DonutChart";
import { enumColors } from "../../utils/colorMapper";

interface CategoryItem {
  name: string;
  value: number;
}

interface CategoryDistributionProps {
    categories: CategoryItem[];
    color: string;
}
    
export default function Categories({ categories, color }: CategoryDistributionProps) {
    // Calculate total to check for empty state
    const totalSolved = categories.reduce((sum, cat) => sum + cat.value, 0);

    return (
        <div className="bg-container rounded-lg p-6 flex items-center gap-6 min-w-96 pr-10">
            <DonutChart categories={categories} color={color} />

            <div className="w-px h-56 bg-gray-400"></div>

            <div className="flex-1 max-h-56 overflow-y-auto custom-scroll space-y-3">
                {totalSolved > 0 ? (
                    categories.map((category, index) => (
                        <div key={index} className="flex items-center gap-3">
                            <div className="w-4 h-4 rounded-sm flex-shrink-0" />
                            <span className="text-text text-xl" style={{ color: enumColors[category.name] }}>
                                {category.name} :
                            </span>
                            <span className="text-xl font-semibold" style={{ color: enumColors[category.name] }}>
                                {category.value}
                            </span>
                        </div>
                    ))
                ) : (
                    <div className="h-full flex items-center justify-center">
                        <span className="text-gray-400 text-xl italic font-medium">
                            No problems solved
                        </span>
                    </div>
                )}
            </div>
        </div>
    ); 
}