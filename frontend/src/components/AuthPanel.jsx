import { useState } from 'react';
import { loginUser, registerUser } from '../services/api';

function AuthPanel({ onAuthenticated }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [mode, setMode] = useState('login');
  const [error, setError] = useState('');

  const submit = async (event) => {
    event.preventDefault();
    try {
      const action = mode === 'login' ? loginUser : registerUser;
      const response = await action({ username, password });
      localStorage.setItem('inventory_jwt', response.data.token);
      localStorage.setItem('inventory_user', username);
      setError('');
      onAuthenticated(username);
    } catch (err) {
      setError('Authentication failed. Check credentials and try again.');
    }
  };

  return (
    <div className="mx-auto w-full max-w-md rounded-2xl border border-ocean/30 bg-white p-6 shadow-lg">
      <h2 className="text-2xl font-bold text-ink">Inventory Access</h2>
      <p className="mt-1 text-sm text-ocean">Sign in to access secure inventory operations.</p>
      <form onSubmit={submit} className="mt-4 space-y-3">
        <input
          className="input w-full"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          className="input w-full"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {error && <p className="rounded-lg bg-coral px-3 py-2 text-sm text-white">{error}</p>}
        <button className="w-full rounded-xl bg-ocean px-4 py-2 font-semibold text-white" type="submit">
          {mode === 'login' ? 'Login' : 'Register'}
        </button>
      </form>
      {mode === 'login' && (
        <p className="mt-3 rounded-lg bg-ink/90 px-3 py-2 text-xs text-white">
          Demo admin login: username <span className="font-semibold">admin</span>, password{' '}
          <span className="font-semibold">admin</span>
        </p>
      )}
      <button
        className="mt-3 text-sm font-medium text-ocean underline"
        onClick={() => setMode((prev) => (prev === 'login' ? 'register' : 'login'))}
      >
        Switch to {mode === 'login' ? 'Register' : 'Login'}
      </button>
    </div>
  );
}

export default AuthPanel;
