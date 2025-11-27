interface BoardProps<T> {
    data: T[];
    columns: string[];
    onRowClick?: (item: T) => void;
    gridCols?: string;
    renderRow: (item: T, onClick?: () => void) => React.ReactNode;
}

export default function Board<T>({
    data,
    onRowClick,
    columns,
    renderRow,
    gridCols = "grid-cols-[80px_1fr_2fr_100px_100px_60px]",
  }: BoardProps<T>) {
    return (
      <div className="bg-container rounded-lg overflow-hidden">
       <div className={`grid ${gridCols} gap-4 bg-sidebar px-6 py-4 border-b border-sidebar items-center`}>
          {columns.map((col, index) => (
            <span key={index} className="text-orange font-anta text-sm text-center">
              {col}
            </span>
          ))}
        </div>
  
        <div>
          {data.length > 0 ? (
            data.map((item) =>
              renderRow(item, () => onRowClick?.(item))
            )
          ) : (
            <div className="text-center py-12 text-text/50 font-anta">
              No items found
            </div>
          )}
        </div>
      </div>
    );
  }
  

  