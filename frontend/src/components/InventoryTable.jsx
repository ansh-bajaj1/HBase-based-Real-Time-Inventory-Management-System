function InventoryTable({ items, onDelete, onUpdate }) {
  return (
    <div className="rounded-2xl border border-ocean/30 bg-white p-5 shadow-sm">
      <h2 className="text-xl font-semibold text-ink">Inventory Table</h2>
      <div className="mt-4 overflow-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-mist text-ocean">
              <th className="pb-2">Item ID</th>
              <th className="pb-2">Name</th>
              <th className="pb-2">Category</th>
              <th className="pb-2">Price</th>
              <th className="pb-2">Stock</th>
              <th className="pb-2">Last Updated</th>
              <th className="pb-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.itemId} className="border-b border-mist/80 text-ink">
                <td className="py-2 font-mono">{item.itemId}</td>
                <td className="py-2">{item.name}</td>
                <td className="py-2">{item.category}</td>
                <td className="py-2">${item.price}</td>
                <td className={`py-2 font-semibold ${item.lowStock ? 'text-coral' : 'text-ocean'}`}>{item.stockQuantity}</td>
                <td className="py-2">{new Date(item.lastUpdated).toLocaleString()}</td>
                <td className="py-2">
                  <div className="flex gap-2">
                    <button
                      onClick={() => {
                        const value = prompt('Enter new stock quantity', item.stockQuantity);
                        if (value !== null && value !== '') {
                          onUpdate(item.itemId, Number(value));
                        }
                      }}
                      className="rounded-lg bg-mist px-3 py-1 text-xs font-semibold text-ink"
                    >
                      Update
                    </button>
                    <button
                      onClick={() => onDelete(item.itemId)}
                      className="rounded-lg bg-coral px-3 py-1 text-xs font-semibold text-white"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default InventoryTable;
