import { useState } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { Heart, ArrowLeft, Loader2, CheckCircle2, Eye, EyeOff, KeyRound } from 'lucide-react';
import { resetPassword } from '../api/auth';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token') ?? '';

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  if (!token) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center px-4">
        <div className="w-full max-w-sm bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center space-y-4">
          <p className="text-gray-700 font-medium">Invalid or missing reset token.</p>
          <Link to="/forgot-password" className="btn-primary inline-flex">Request a new link</Link>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    try {
      await resetPassword(token, newPassword);
      setSuccess(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Reset link is invalid or has expired.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center px-4">
      <div className="w-full max-w-sm">

        {/* Logo */}
        <div className="flex justify-center mb-8">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary-600">
              <Heart size={20} className="text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">MediCare<span className="text-primary-600">+</span></span>
          </Link>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          {success ? (
            <div className="text-center space-y-4">
              <div className="flex justify-center">
                <div className="h-14 w-14 rounded-full bg-green-100 flex items-center justify-center">
                  <CheckCircle2 size={28} className="text-green-600" />
                </div>
              </div>
              <h1 className="text-xl font-bold text-gray-900">Password reset!</h1>
              <p className="text-sm text-gray-500">Your password has been updated. You can now sign in with your new password.</p>
              <button
                className="btn-primary w-full mt-2"
                onClick={() => navigate('/login')}
              >
                Go to Sign in
              </button>
            </div>
          ) : (
            <>
              <div className="flex justify-center mb-5">
                <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                  <KeyRound size={22} className="text-primary-600" />
                </div>
              </div>
              <h1 className="text-xl font-bold text-gray-900 text-center mb-1">Set new password</h1>
              <p className="text-sm text-gray-500 text-center mb-6">Must be at least 6 characters.</p>

              {error && (
                <div className="mb-4 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="label">New password</label>
                  <div className="relative">
                    <input
                      type={showPassword ? 'text' : 'password'} required
                      className="input pr-10" placeholder="••••••••"
                      value={newPassword} onChange={e => setNewPassword(e.target.value)}
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
                <div>
                  <label className="label">Confirm password</label>
                  <input
                    type="password" required
                    className="input" placeholder="••••••••"
                    value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)}
                  />
                </div>
                <button type="submit" disabled={loading} className="btn-primary w-full py-3">
                  {loading
                    ? <><Loader2 size={16} className="animate-spin" /> Resetting…</>
                    : 'Reset Password'
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
