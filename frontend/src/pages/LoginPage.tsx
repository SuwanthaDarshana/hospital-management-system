import { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { login } from '../api/auth';
import { Heart, Eye, EyeOff, Loader2, CheckCircle2, ArrowLeft, ShieldCheck, Clock, Users } from 'lucide-react';

const HIGHLIGHTS = [
  { icon: <ShieldCheck size={18} />, label: 'Secure & private records' },
  { icon: <Clock size={18} />,       label: '24/7 access to your health data' },
  { icon: <Users size={18} />,       label: '200+ specialist doctors' },
];

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

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
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

      {/* ── Left panel — image + branding ── */}
      <div className="hidden lg:flex lg:w-5/12 xl:w-1/2 relative flex-col">
        <img
          src="https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=1200&q=80"
          alt="Healthcare"
          className="absolute inset-0 w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-primary-900/88 via-primary-800/75 to-primary-600/55" />

        <div className="relative flex flex-col h-full px-10 py-10">
          <Link to="/" className="flex items-center gap-2.5 w-fit">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/20 backdrop-blur border border-white/30">
              <Heart size={20} className="text-white" />
            </div>
            <span className="text-xl font-bold text-white">MediCare<span className="text-primary-300">+</span></span>
          </Link>

          <div className="flex-1 flex flex-col justify-center">
            <h2 className="text-4xl font-extrabold text-white leading-tight mb-4">
              Welcome Back to<br />Better Health
            </h2>
            <p className="text-primary-200 text-base leading-relaxed mb-10 max-w-sm">
              Sign in to manage your appointments, records, and care — all in one place.
            </p>
            <div className="space-y-4">
              {HIGHLIGHTS.map(h => (
                <div key={h.label} className="flex items-center gap-3 text-white/90 text-sm">
                  <div className="w-8 h-8 rounded-lg bg-white/15 flex items-center justify-center text-primary-300 flex-shrink-0">
                    {h.icon}
                  </div>
                  {h.label}
                </div>
              ))}
            </div>
          </div>

          <div className="border-t border-white/20 pt-6">
            <p className="text-white/70 text-sm italic leading-relaxed">
              "Medicine is not only a science; it is also an art."
            </p>
            <p className="text-primary-300 text-xs mt-1 font-medium">— Paracelsus</p>
          </div>
        </div>
      </div>

      {/* ── Right panel — form ── */}
      <div className="flex-1 flex flex-col bg-gray-50">
        {/* Top bar */}
        <div className="flex items-center justify-between px-6 sm:px-10 py-5 bg-white border-b border-gray-100">
          <Link to="/" className="lg:hidden flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary-600">
              <Heart size={16} className="text-white" />
            </div>
            <span className="font-bold text-gray-900">MediCare<span className="text-primary-600">+</span></span>
          </Link>
          <Link to="/" className="hidden lg:flex items-center gap-1.5 text-sm text-gray-500 hover:text-primary-600 transition-colors">
            <ArrowLeft size={15} /> Back to home
          </Link>
          <p className="text-sm text-gray-500">
            No account?{' '}
            <Link to="/register" className="font-semibold text-primary-600 hover:text-primary-700">Register</Link>
          </p>
        </div>

        {/* Form area */}
        <div className="flex-1 flex items-center justify-center px-6 sm:px-10 py-12">
          <div className="w-full max-w-sm">
            <h1 className="text-2xl font-extrabold text-gray-900 mb-1">Sign in</h1>
            <p className="text-sm text-gray-500 mb-6">Enter your credentials to access your account.</p>

            {justRegistered && (
              <div className="mb-4 rounded-xl bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700 flex items-center gap-2">
                <CheckCircle2 size={16} className="shrink-0" />
                Account created! Sign in with your new credentials.
              </div>
            )}

            {error && (
              <div className="mb-4 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="label" htmlFor="email">Email address</label>
                <input
                  id="email" type="email" required autoComplete="email"
                  className="input" placeholder="you@example.com"
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

              <div className="flex justify-end">
                <Link to="/forgot-password" className="text-xs text-primary-600 hover:text-primary-700 font-medium">
                  Forgot password?
                </Link>
              </div>

              <button type="submit" disabled={loading} className="btn-primary w-full py-3 mt-1">
                {loading ? <><Loader2 size={16} className="animate-spin" /> Signing in…</> : 'Sign in'}
              </button>
            </form>

            <p className="mt-6 text-center text-sm text-gray-500">
              Don't have an account?{' '}
              <Link to="/register" className="font-semibold text-primary-600 hover:text-primary-700">
                Register as patient
              </Link>
            </p>

            <div className="mt-8 rounded-xl bg-primary-50 border border-primary-100 p-4 text-xs text-gray-600">
              <p className="font-semibold text-gray-700 mb-1.5">Demo credentials</p>
              <p>Admin: <span className="font-mono">admin@hospital.com</span> / <span className="font-mono">admin123</span></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
