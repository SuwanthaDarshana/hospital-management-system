import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerPatient } from '../api/auth';
import { Hospital, Loader2 } from 'lucide-react';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', phone: '',
    gender: '', dateOfBirth: '', bloodGroup: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (k: string) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
    setForm(f => ({ ...f, [k]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await registerPatient(form);
      navigate('/login');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="w-full max-w-lg">
        <div className="flex justify-center mb-6">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary-600">
            <Hospital size={24} className="text-white" />
          </div>
        </div>
        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-1">Create patient account</h2>
          <p className="text-sm text-gray-500 mb-6">Fill in your details to register</p>

          {error && (
            <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">First name</label>
                <input className="input" required value={form.firstName} onChange={set('firstName')} placeholder="John" />
              </div>
              <div>
                <label className="label">Last name</label>
                <input className="input" required value={form.lastName} onChange={set('lastName')} placeholder="Doe" />
              </div>
            </div>
            <div>
              <label className="label">Email address</label>
              <input className="input" type="email" required value={form.email} onChange={set('email')} placeholder="john@example.com" />
            </div>
            <div>
              <label className="label">Password</label>
              <input className="input" type="password" required value={form.password} onChange={set('password')} placeholder="Min. 8 characters" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Phone</label>
                <input className="input" value={form.phone} onChange={set('phone')} placeholder="0712345678" />
              </div>
              <div>
                <label className="label">Gender</label>
                <select className="input" value={form.gender} onChange={set('gender')}>
                  <option value="">Select</option>
                  <option>Male</option>
                  <option>Female</option>
                  <option>Other</option>
                </select>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Date of birth</label>
                <input className="input" type="date" value={form.dateOfBirth} onChange={set('dateOfBirth')} />
              </div>
              <div>
                <label className="label">Blood group</label>
                <select className="input" value={form.bloodGroup} onChange={set('bloodGroup')}>
                  <option value="">Select</option>
                  {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
                </select>
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn-primary w-full">
              {loading ? <><Loader2 size={16} className="animate-spin" /> Creating account…</> : 'Create account'}
            </button>
          </form>

          <p className="mt-4 text-center text-sm text-gray-500">
            Already have an account?{' '}
            <Link to="/login" className="font-medium text-primary-600 hover:text-primary-700">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
