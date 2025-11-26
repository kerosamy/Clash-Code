import DonutChart from "../profile/DonutChart";

interface CategoryItem {
  name: string;
  value: number;
  color: string;
}

interface CategoryDistributionProps {
    categories: CategoryItem[];
    color: string;
}
    
export default function Categories({ categories, color }: CategoryDistributionProps) {
    return (
        <div className="bg-container rounded-lg p-8 flex items-center gap-8 min-w-96">
            <DonutChart categories={categories} color={color} />

            <div className="flex-1 max-h-48 overflow-y-auto space-y-3">
                {categories.map((category, index) => (
                    <div key={index} className="flex items-center gap-3">
                        <div
                            className={`w-4 h-4 rounded-sm flex-shrink-0`}
                        />
                        <span className="text-text text-xl" style={{ color: category.color }}>{category.name} :</span>
                        <span className="text-xl font-semibold" style={{ color: category.color }}>{category.value}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}