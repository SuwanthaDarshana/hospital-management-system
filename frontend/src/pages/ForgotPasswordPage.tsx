import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart, ArrowLeft, Loader2, CheckCircle2, Mail } from 'lucide-react';
import { forgotPassword } from '../api/auth';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await forgotPassword(email);
      setSent(true);
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center px-4">
      <div className="w-full max-w-sm">

        <div className="flex justify-center mb-8">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary-600">
              <Heart size={20} className="text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">MediCare<span className="text-primary-600">+</span></span>
          </Link>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          {sent ? (
            <div className="text-center space-y-4">
              <div className="flex justify-center">
                <div className="h-14 w-14 rounded-full bg-green-100 flex items-center justify-center">
                  <CheckCircle2 size={28} className="text-green-600" />
                </div>
              </div>
              <h1 className="text-xl font-bold text-gray-900">Check your email</h1>
              <p className="text-sm text-gray-500 leading-relaxed">
                If an account exists for <span className="font-semibold text-gray-700">{email}</span>,
                we've sent a password reset link. It expires in <strong>15 minutes</strong>.
              </p>
              <p className="text-xs text-gray-400">Don't see it? Check your spam folder.</p>
              <Link to="/login" className="btn-primary w-full mt-2 inline-flex justify-center">
                Back to Sign in
              </Link>
            </div>
          ) : (
            <>
              <div className="flex justify-center mb-5">
                <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                  <Mail size={22} className="text-primary-600" />
                </div>
              </div>
              <h1 className="text-xl font-bold text-gray-900 text-center mb-1">Forgot password?</h1>
              <p className="text-sm text-gray-500 text-center mb-6">
                Enter your email and we'll send you a reset link.
              </p>

              {error && (
                <div className="mb-4 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="label" htmlFor="email">Email address</label>
                  <input
                    id="email" type="email" required
                    className="input" placeholder="you@example.com"
                    value={email} onChange={e => setEmail(e.target.value)}
                  />
                </div>
                <button type="submit" disabled={loading} className="btn-primary w-full py-3">
                  {loading
                    ? <><Loader2 size={16} className="animate-spin" /> Sending…</>
                    : 'Send Reset Link'
                  }
                </button>
              </form>
            </>
          )}
        </div>

        <div className="text-center mt-6">
          <Link to="/login" className="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-primary-600 transition-colors">
            <ArrowLeft size={14} /> Back to Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
