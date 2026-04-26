
export default function CounterCard({
  count
}: {count: number}) {
    return (
        <div className="flex items-center gap-4 bg-sidebar/20 border border-white/5 rounded-xl px-6 py-3 backdrop-blur-sm">
          <div className="flex flex-col items-end">
            <span className="text-2xl font-anta text-orange leading-none">
              {count}
            </span>
            <span className="text-[10px] font-anta text-text/60 uppercase tracking-widest">
              Pending
            </span>
          </div>
          <div className="h-8 w-[1px] bg-white/10" />
          <div className="h-2 w-2 rounded-full bg-orange animate-pulse shadow-[0_0_10px_rgba(249,115,22,0.5)]" />
        </div>
    )
}