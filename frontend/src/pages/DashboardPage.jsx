import { useEffect, useMemo, useState } from 'react';
import AddItemForm from '../components/AddItemForm';
import AuthPanel from '../components/AuthPanel';
import DashboardStats from '../components/DashboardStats';
import InventoryTable from '../components/InventoryTable';
import LowStockAlerts from '../components/LowStockAlerts';
import { addItem, deleteItem, getAllItems, getLowStockItems, updateStock } from '../services/api';

function DashboardPage() {
  const [items, setItems] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [error, setError] = useState('');
  const [authenticated, setAuthenticated] = useState(Boolean(localStorage.getItem('inventory_jwt')));
  const [currentUser, setCurrentUser] = useState(localStorage.getItem('inventory_user') || '');

  const refresh = async () => {
    try {
      const [itemsRes, alertsRes] = await Promise.all([getAllItems(), getLowStockItems()]);
      setItems(itemsRes.data);
      setAlerts(alertsRes.data);
      setError('');
    } catch (err) {
      if (err?.response?.status === 401 || err?.response?.status === 403) {
        localStorage.removeItem('inventory_jwt');
        localStorage.removeItem('inventory_user');
        setAuthenticated(false);
        setError('Session expired. Please login again.');
      } else {
        setError('Failed to load inventory data. Ensure backend is running.');
      }
    }
  };

  useEffect(() => {
    if (!authenticated) {
      return;
    }
    refresh();
    const timer = setInterval(refresh, 4000);
    return () => clearInterval(timer);
  }, [authenticated]);

  const stats = useMemo(() => ({
    totalItems: items.length,
    totalStock: items.reduce((acc, item) => acc + item.stockQuantity, 0),
    lowStockCount: alerts.length
  }), [items, alerts]);

  const handleAdd = async (payload) => {
    await addItem(payload);
    refresh();
  };

  const handleDelete = async (id) => {
    await deleteItem(id);
    refresh();
  };

  const handleUpdate = async (id, stockQuantity) => {
    await updateStock(id, { stockQuantity });
    refresh();
  };

  if (!authenticated) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[radial-gradient(circle_at_top,_#c8d8e4_0%,_#f2efe9_45%,_#ffffff_100%)] px-4">
        <AuthPanel
          onAuthenticated={(username) => {
            setCurrentUser(username);
            setAuthenticated(true);
          }}
        />
      </div>
    );
  }

  const isAdmin = currentUser === 'admin';

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top,_#c8d8e4_0%,_#f2efe9_45%,_#ffffff_100%)] px-4 py-8">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
        <header className="rounded-3xl border border-ocean/20 bg-white/70 p-6 shadow-lg backdrop-blur">
          <p className="font-mono text-xs uppercase tracking-[0.35em] text-ocean">HBase + Kafka + Spring Boot</p>
          <h1 className="mt-2 text-3xl font-bold text-ink md:text-4xl">Real-Time Inventory Management</h1>
          <p className="mt-2 max-w-3xl text-sm text-ocean">
            Monitor stock changes, identify low inventory, and track updates continuously from your event-driven data pipeline.
          </p>
          <button
            className="mt-4 rounded-xl bg-ink px-4 py-2 text-sm font-semibold text-white"
            onClick={() => {
              localStorage.removeItem('inventory_jwt');
              localStorage.removeItem('inventory_user');
              setCurrentUser('');
              setAuthenticated(false);
            }}
          >
            Logout
          </button>
        </header>

        {error && <p className="rounded-xl bg-coral px-4 py-2 text-sm font-medium text-white">{error}</p>}

        <DashboardStats {...stats} items={items} isAdmin={isAdmin} />
        <AddItemForm onSubmit={handleAdd} />
        <InventoryTable items={items} onDelete={handleDelete} onUpdate={handleUpdate} />
        <LowStockAlerts alerts={alerts} />
      </div>
    </div>
  );
}

export default DashboardPage;
