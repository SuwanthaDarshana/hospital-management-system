import { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { login } from '../api/auth';
import { Hospital, Eye, EyeOff, Loader2, CheckCircle2 } from 'lucide-react';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { setAuth } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const justRegistered = (location.state as any)?.registered === true;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await login({ email, password });
      if (data.success) {
        setAuth(
          { email: data.data.email, role: data.data.role as any, authUserId: Number(data.data.id) },
          { accessToken: data.data.accessToken, refreshToken: data.data.refreshToken }
        );
        navigate('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-primary-900 flex-col items-center justify-center p-12 text-white">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-primary-600 mb-6">
          <Hospital size={32} />
        </div>
        <h1 className="text-4xl font-bold mb-3">MediCare HMS</h1>
        <p className="text-primary-300 text-center text-lg max-w-sm">
          A modern hospital management system for seamless patient care and operations.
        </p>
        <div className="mt-12 grid grid-cols-2 gap-4 w-full max-w-sm">
          {[
            ['Appointments', 'Book & manage patient visits'],
            ['Doctors', 'Manage specialist profiles'],
            ['Patients', 'Complete health records'],
            ['Staff', 'Coordinate your team'],
          ].map(([title, desc]) => (
            <div key={title} className="rounded-xl bg-primary-800/60 p-4">
              <p className="font-semibold text-sm">{title}</p>
              <p className="text-primary-400 text-xs mt-1">{desc}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Right panel */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-12 bg-gray-50">
        <div className="w-full max-w-sm">
          <div className="lg:hidden flex justify-center mb-8">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary-600">
              <Hospital size={24} className="text-white" />
            </div>
          </div>

          <h2 className="text-2xl font-bold text-gray-900">Welcome back</h2>
          <p className="mt-1 text-sm text-gray-500">Sign in to your account</p>

          {justRegistered && (
            <div className="mt-4 rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700 flex items-center gap-2">
              <CheckCircle2 size={16} className="shrink-0" />
              Account created! Sign in with your new credentials.
            </div>
          )}

          {error && (
            <div className="mt-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <div>
              <label className="label" htmlFor="email">Email address</label>
              <input
                id="email" type="email" required autoComplete="email"
                className="input" placeholder="you@hospital.com"
                value={email} onChange={e => setEmail(e.target.value)}
              />
            </div>
            <div>
              <label className="label" htmlFor="password">Password</label>
              <div className="relative">
                <input
                  id="password" type={showPassword ? 'text' : 'password'} required
                  className="input pr-10" placeholder="••••••••"
                  value={password} onChange={e => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600"
                  onClick={() => setShowPassword(v => !v)}
                >
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn-primary w-full mt-2">
              {loading ? <><Loader2 size={16} className="animate-spin" />Signing in…</> : 'Sign in'}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-gray-500">
            Don't have an account?{' '}
            <Link to="/register" className="font-medium text-primary-600 hover:text-primary-700">
              Register as patient
            </Link>
          </p>

          <div className="mt-8 rounded-lg bg-primary-50 border border-primary-100 p-4 text-xs text-gray-600">
            <p className="font-semibold text-gray-700 mb-2">Demo credentials</p>
            <p>Admin: <span className="font-mono">admin@hospital.com</span> / <span className="font-mono">admin123</span></p>
          </div>
        </div>
      </div>
    </div>
  );
}
