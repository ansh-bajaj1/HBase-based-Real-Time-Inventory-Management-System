import { useState } from 'react';

const initialForm = {
  itemId: '',
  name: '',
  category: '',
  price: '',
  stockQuantity: ''
};

function AddItemForm({ onSubmit }) {
  const [form, setForm] = useState(initialForm);

  const handleChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    await onSubmit({
      ...form,
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity)
    });
    setForm(initialForm);
  };

  return (
    <form onSubmit={handleSubmit} className="rounded-2xl border border-ocean/30 bg-white p-5 shadow-sm">
      <h2 className="text-xl font-semibold text-ink">Add Inventory Item</h2>
      <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-2">
        <input name="itemId" value={form.itemId} onChange={handleChange} placeholder="Item ID" className="input" required />
        <input name="name" value={form.name} onChange={handleChange} placeholder="Name" className="input" required />
        <input name="category" value={form.category} onChange={handleChange} placeholder="Category" className="input" required />
        <input name="price" type="number" step="0.01" value={form.price} onChange={handleChange} placeholder="Price" className="input" required />
        <input name="stockQuantity" type="number" value={form.stockQuantity} onChange={handleChange} placeholder="Stock Quantity" className="input md:col-span-2" required />
      </div>
      <button type="submit" className="mt-4 rounded-xl bg-ocean px-5 py-2 text-sm font-semibold text-white transition hover:bg-ink">
        Add Item
      </button>
    </form>
  );
}

export default AddItemForm;
