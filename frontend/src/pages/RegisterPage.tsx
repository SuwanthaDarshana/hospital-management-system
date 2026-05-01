import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerPatient } from '../api/auth';
import { Heart, Loader2, CheckCircle, ArrowLeft } from 'lucide-react';

const PERKS = [
  'Book appointments with 200+ specialists',
  'Secure digital health records',
  '24/7 emergency support',
  'Real-time appointment reminders',
];

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', phone: '',
    address: '', gender: '', dateOfBirth: '', bloodGroup: '',
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
      const payload = {
        firstName:   form.firstName,
        lastName:    form.lastName,
        email:       form.email,
        password:    form.password,
        phone:       form.phone,
        address:     form.address,
        gender:      form.gender,
        dateOfBirth: form.dateOfBirth,
        ...(form.bloodGroup && { bloodGroup: form.bloodGroup }),
      };
      await registerPatient(payload);
      navigate('/login', { state: { registered: true } });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">

      {/* ── Left panel — image + branding ── */}
      <div className="hidden lg:flex lg:w-5/12 xl:w-1/2 relative flex-col">
        {/* Background image */}
        <img
          src="https://images.unsplash.com/photo-1538108149393-fbbd81895907?w=1200&q=80"
          alt="Hospital"
          className="absolute inset-0 w-full h-full object-cover"
        />
        {/* Gradient overlay */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary-900/85 via-primary-800/75 to-primary-600/60" />

        {/* Content over image */}
        <div className="relative flex flex-col h-full px-10 py-10">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2.5 w-fit">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/20 backdrop-blur border border-white/30">
              <Heart size={20} className="text-white" />
            </div>
            <span className="text-xl font-bold text-white">MediCare<span className="text-primary-300">+</span></span>
          </Link>

          {/* Main message */}
          <div className="flex-1 flex flex-col justify-center">
            <h2 className="text-4xl font-extrabold text-white leading-tight mb-4">
              Your Health Journey<br />Starts Here
            </h2>
            <p className="text-primary-200 text-base leading-relaxed mb-10 max-w-sm">
              Join thousands of patients who manage their care smarter with MediCare+.
            </p>
            <ul className="space-y-3.5">
              {PERKS.map(p => (
                <li key={p} className="flex items-center gap-3 text-white/90 text-sm">
                  <CheckCircle size={18} className="text-primary-300 flex-shrink-0" />
                  {p}
                </li>
              ))}
            </ul>
          </div>

          {/* Bottom quote */}
          <div className="border-t border-white/20 pt-6">
            <p className="text-white/70 text-sm italic leading-relaxed">
              "The greatest medicine of all is to teach people how not to need it."
            </p>
            <p className="text-primary-300 text-xs mt-1 font-medium">— Hippocrates</p>
          </div>
        </div>
      </div>

      {/* ── Right panel — form ── */}
      <div className="flex-1 flex flex-col overflow-y-auto bg-gray-50">
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
            Already registered?{' '}
            <Link to="/login" className="font-semibold text-primary-600 hover:text-primary-700">Sign in</Link>
          </p>
        </div>

        {/* Form area */}
        <div className="flex-1 flex items-start justify-center px-6 sm:px-10 py-10">
          <div className="w-full max-w-lg">
            <div className="mb-8">
              <h1 className="text-2xl font-extrabold text-gray-900 mb-1">Create your account</h1>
              <p className="text-sm text-gray-500">Fill in your details to get started — it only takes a minute.</p>
            </div>

            {error && (
              <div className="mb-5 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Name row */}
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
                <input className="input" type="password" required value={form.password} onChange={set('password')} placeholder="Min. 6 characters" />
              </div>

              {/* Phone + Gender */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="label">Phone</label>
                  <input className="input" required value={form.phone} onChange={set('phone')} placeholder="0712345678" />
                </div>
                <div>
                  <label className="label">Gender</label>
                  <select className="input" required value={form.gender} onChange={set('gender')}>
                    <option value="">Select</option>
                    <option>Male</option>
                    <option>Female</option>
                    <option>Other</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="label">Address</label>
                <input className="input" required value={form.address} onChange={set('address')} placeholder="123 Main St, Colombo" />
              </div>

              {/* DOB + Blood group */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="label">Date of birth</label>
                  <input className="input" type="date" required value={form.dateOfBirth} onChange={set('dateOfBirth')} />
                </div>
                <div>
                  <label className="label">Blood group <span className="text-gray-400 font-normal">(optional)</span></label>
                  <select className="input" value={form.bloodGroup} onChange={set('bloodGroup')}>
                    <option value="">Select</option>
                    {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
                  </select>
                </div>
              </div>

              <button type="submit" disabled={loading} className="btn-primary w-full py-3 mt-2">
                {loading
                  ? <><Loader2 size={16} className="animate-spin" /> Creating account…</>
                  : 'Create account'}
              </button>
            </form>

            <p className="mt-6 text-center text-xs text-gray-400">
              By registering you agree to our{' '}
              <a href="#" className="underline hover:text-primary-600">Terms of Service</a> and{' '}
              <a href="#" className="underline hover:text-primary-600">Privacy Policy</a>.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
