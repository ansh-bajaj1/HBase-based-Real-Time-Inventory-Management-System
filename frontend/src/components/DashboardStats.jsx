const PIE_COLORS = ['#2f5d62', '#4f7d84', '#7a9e9f', '#9fc0c2', '#c8d8e4', '#e9f1f2'];

function polarToCartesian(center, radius, angleInDegrees) {
  const angleInRadians = ((angleInDegrees - 90) * Math.PI) / 180;
  return {
    x: center + radius * Math.cos(angleInRadians),
    y: center + radius * Math.sin(angleInRadians)
  };
}

function arcPath(center, radius, startAngle, endAngle) {
  const start = polarToCartesian(center, radius, endAngle);
  const end = polarToCartesian(center, radius, startAngle);
  const largeArcFlag = endAngle - startAngle <= 180 ? '0' : '1';
  return `M ${center} ${center} L ${start.x} ${start.y} A ${radius} ${radius} 0 ${largeArcFlag} 0 ${end.x} ${end.y} Z`;
}

function PieChartCard({ title, entries }) {
  const total = entries.reduce((sum, entry) => sum + entry.value, 0);
  let runningAngle = 0;

  return (
    <div className="rounded-2xl border border-ocean/30 bg-white/80 p-5 shadow-sm backdrop-blur">
      <p className="font-mono text-xs uppercase tracking-[0.25em] text-ocean">{title}</p>
      {total === 0 ? (
        <p className="mt-3 text-sm text-ink">No data available yet.</p>
      ) : (
        <div className="mt-4 flex flex-col gap-4 md:flex-row md:items-center">
          <svg viewBox="0 0 120 120" className="h-36 w-36 shrink-0">
            {entries.map((entry, index) => {
              const angle = (entry.value / total) * 360;
              const start = runningAngle;
              const end = runningAngle + angle;
              runningAngle = end;
              return <path key={entry.label} d={arcPath(60, 52, start, end)} fill={PIE_COLORS[index % PIE_COLORS.length]} />;
            })}
            <circle cx="60" cy="60" r="28" fill="white" />
            <text x="60" y="58" textAnchor="middle" className="fill-ink text-[9px] font-semibold uppercase tracking-wider">
              Total
            </text>
            <text x="60" y="70" textAnchor="middle" className="fill-ink text-[12px] font-bold">
              {total}
            </text>
          </svg>

          <div className="space-y-2 text-sm text-ink">
            {entries.map((entry, index) => (
              <div key={entry.label} className="flex items-center gap-2">
                <span
                  className="inline-block h-3 w-3 rounded-full"
                  style={{ backgroundColor: PIE_COLORS[index % PIE_COLORS.length] }}
                />
                <span className="min-w-36">{entry.label}</span>
                <span className="font-semibold">{entry.value}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function DashboardStats({ totalItems, totalStock, lowStockCount, items = [], isAdmin = false }) {
  const cards = [
    { label: 'Total Items', value: totalItems },
    { label: 'Total Stock Units', value: totalStock },
    { label: 'Low Stock Alerts', value: lowStockCount }
  ];

  const categoryCounts = items.reduce((acc, item) => {
    const category = item.category || 'Uncategorized';
    acc[category] = (acc[category] || 0) + 1;
    return acc;
  }, {});

  const categoryEntries = Object.entries(categoryCounts)
    .map(([label, value]) => ({ label, value }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 6);

  const stockHealthEntries = [
    { label: 'Low Stock (<=10)', value: items.filter((item) => item.stockQuantity <= 10).length },
    { label: 'Healthy Stock (>10)', value: items.filter((item) => item.stockQuantity > 10).length }
  ];

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {cards.map((card) => (
          <div key={card.label} className="rounded-2xl border border-ocean/30 bg-white/80 p-5 shadow-sm backdrop-blur">
            <p className="font-mono text-xs uppercase tracking-[0.25em] text-ocean">{card.label}</p>
            <p className="mt-3 text-3xl font-bold text-ink">{card.value}</p>
          </div>
        ))}
      </div>

      {isAdmin && (
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          <PieChartCard title="Admin Analytics - Items by Category" entries={categoryEntries} />
          <PieChartCard title="Admin Analytics - Stock Health" entries={stockHealthEntries} />
        </div>
      )}
    </div>
  );
}

export default DashboardStats;
