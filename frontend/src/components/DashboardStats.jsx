function DashboardStats({ totalItems, totalStock, lowStockCount }) {
  const cards = [
    { label: 'Total Items', value: totalItems },
    { label: 'Total Stock Units', value: totalStock },
    { label: 'Low Stock Alerts', value: lowStockCount }
  ];

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
      {cards.map((card) => (
        <div key={card.label} className="rounded-2xl border border-ocean/30 bg-white/80 p-5 shadow-sm backdrop-blur">
          <p className="font-mono text-xs uppercase tracking-[0.25em] text-ocean">{card.label}</p>
          <p className="mt-3 text-3xl font-bold text-ink">{card.value}</p>
        </div>
      ))}
    </div>
  );
}

export default DashboardStats;
