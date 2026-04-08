function LowStockAlerts({ alerts }) {
  return (
    <div className="rounded-2xl border border-coral/30 bg-coral/5 p-5 shadow-sm">
      <h2 className="text-xl font-semibold text-ink">Low Stock Alerts</h2>
      {alerts.length === 0 ? (
        <p className="mt-3 text-sm text-ocean">All inventory levels are healthy.</p>
      ) : (
        <ul className="mt-3 space-y-2 text-sm">
          {alerts.map((item) => (
            <li key={item.itemId} className="rounded-xl border border-coral/30 bg-white px-3 py-2 text-ink">
              <span className="font-semibold">{item.name}</span> ({item.itemId}) is at
              <span className="ml-1 font-mono text-coral">{item.stockQuantity}</span> units.
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default LowStockAlerts;
