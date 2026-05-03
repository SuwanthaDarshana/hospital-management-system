import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { getPaymentsByPatient, createPaymentIntent, confirmPayment } from '../api/payments';
import { getMyAppointments } from '../api/appointments';
import {
  CreditCard, X, Loader2, CheckCircle2, AlertCircle,
  Receipt, DollarSign,
} from 'lucide-react';
import type { Payment, Appointment } from '../types';

// ── Status badge ──────────────────────────────────────────────────────────────
const STATUS_STYLES: Record<string, string> = {
  PENDING:    'bg-yellow-50 text-yellow-700 border-yellow-200',
  PROCESSING: 'bg-blue-50 text-blue-700 border-blue-200',
  COMPLETED:  'bg-green-50 text-green-700 border-green-200',
  FAILED:     'bg-red-50 text-red-700 border-red-200',
  REFUNDED:   'bg-gray-100 text-gray-600 border-gray-200',
};

function PaymentStatusBadge({ status }: { status: string }) {
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${STATUS_STYLES[status] ?? 'bg-gray-100 text-gray-600'}`}>
      {status}
    </span>
  );
}

// ── Pay Now modal ─────────────────────────────────────────────────────────────
function PayModal({
  appointment,
  patientId,
  patientEmail,
  onClose,
}: {
  appointment: Appointment;
  patientId: number;
  patientEmail: string;
  onClose: () => void;
}) {
  const qc = useQueryClient();
  const [amount, setAmount] = useState('50.00');
  const [step, setStep] = useState<'form' | 'processing' | 'success' | 'error'>('form');
  const [errorMsg, setErrorMsg] = useState('');
  const [completedPayment, setCompletedPayment] = useState<Payment | null>(null);

  const pay = async () => {
    setStep('processing');
    try {
      const intentRes = await createPaymentIntent({
        appointmentId: appointment.id,
        patientId,
        patientEmail,
        amount: parseFloat(amount),
        currency: 'USD',
        description: `Appointment with ${appointment.doctorName} on ${appointment.appointmentDate}`,
      });

      const intent = intentRes.data.data;
      const confirmRes = await confirmPayment({ paymentIntentId: intent.stripePaymentIntentId });
      setCompletedPayment(confirmRes.data.data);
      setStep('success');
      qc.invalidateQueries({ queryKey: ['payments', patientId] });
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Payment failed. Please try again.');
      setStep('error');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 className="text-base font-semibold text-gray-900 flex items-center gap-2">
            <CreditCard size={18} className="text-primary-600" /> Process Payment
          </h3>
          {step !== 'processing' && (
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
          )}
        </div>

        <div className="px-6 py-5">
          {step === 'form' && (
            <div className="space-y-4">
              <div className="rounded-xl bg-gray-50 border border-gray-100 p-4 space-y-1.5 text-sm">
                <p className="font-medium text-gray-900">{appointment.doctorName}</p>
                <p className="text-gray-500">{appointment.doctorSpecialization}</p>
                <p className="text-gray-500">{appointment.appointmentDate} at {appointment.appointmentTime}</p>
              </div>
              <div>
                <label className="label">Consultation Fee (USD)</label>
                <div className="relative">
                  <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 text-sm">$</span>
                  <input
                    type="number" min="0.01" step="0.01"
                    className="input pl-7"
                    value={amount}
                    onChange={e => setAmount(e.target.value)}
                  />
                </div>
              </div>
              <div className="rounded-xl bg-blue-50 border border-blue-100 px-4 py-3 text-xs text-blue-700">
                <strong>Mock payment</strong> — no real charge will be made.
              </div>
              <div className="flex gap-3 pt-1">
                <button onClick={onClose} className="btn-secondary flex-1">Cancel</button>
                <button onClick={pay} className="btn-primary flex-1">
                  Pay ${parseFloat(amount || '0').toFixed(2)}
                </button>
              </div>
            </div>
          )}

          {step === 'processing' && (
            <div className="py-8 flex flex-col items-center gap-4 text-center">
              <Loader2 size={40} className="animate-spin text-primary-500" />
              <p className="font-medium text-gray-700">Processing payment…</p>
              <p className="text-sm text-gray-400">Please wait, do not close this window.</p>
            </div>
          )}

          {step === 'success' && completedPayment && (
            <div className="space-y-4">
              <div className="flex flex-col items-center gap-2 py-4">
                <div className="h-14 w-14 rounded-full bg-green-100 flex items-center justify-center">
                  <CheckCircle2 size={28} className="text-green-600" />
                </div>
                <h4 className="font-bold text-lg text-gray-900">Payment Successful</h4>
                <p className="text-sm text-gray-500">A receipt has been sent to your email.</p>
              </div>
              <div className="rounded-xl bg-gray-50 border border-gray-100 p-4 space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">Payment ID</span>
                  <span className="font-mono text-gray-800 text-xs">{completedPayment.id}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Amount</span>
                  <span className="font-semibold text-gray-900">${completedPayment.amount.toFixed(2)} {completedPayment.currency}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Status</span>
                  <PaymentStatusBadge status={completedPayment.status} />
                </div>
              </div>
              <button onClick={onClose} className="btn-primary w-full">Done</button>
            </div>
          )}

          {step === 'error' && (
            <div className="space-y-4">
              <div className="flex flex-col items-center gap-2 py-4">
                <div className="h-14 w-14 rounded-full bg-red-100 flex items-center justify-center">
                  <AlertCircle size={28} className="text-red-500" />
                </div>
                <h4 className="font-bold text-lg text-gray-900">Payment Failed</h4>
                <p className="text-sm text-red-600">{errorMsg}</p>
              </div>
              <div className="flex gap-3">
                <button onClick={onClose} className="btn-secondary flex-1">Close</button>
                <button onClick={() => setStep('form')} className="btn-primary flex-1">Try Again</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ── Main page ─────────────────────────────────────────────────────────────────
export default function PaymentsPage() {
  const { user } = useAuthStore();
  const qc = useQueryClient();
  const [payingAppointment, setPayingAppointment] = useState<Appointment | null>(null);

  const { data: payments = [], isLoading } = useQuery({
    queryKey: ['payments', user?.authUserId],
    queryFn: () => getPaymentsByPatient(user!.authUserId).then(r => r.data.data),
    enabled: !!user?.authUserId,
  });

  const { data: appointments = [] } = useQuery({
    queryKey: ['appointments-my'],
    queryFn: () => getMyAppointments().then(r => r.data.data),
    enabled: user?.role === 'PATIENT',
  });

  // CONFIRMED appointments that don't yet have a COMPLETED payment
  const paidAppointmentIds = new Set(
    payments.filter(p => p.status === 'COMPLETED').map(p => p.appointmentId)
  );
  const unpaidConfirmed = (appointments as Appointment[]).filter(
    a => a.status === 'CONFIRMED' && !paidAppointmentIds.has(a.id)
  );

  const completedPayments = payments.filter(p => p.status === 'COMPLETED');
  const totalSpent = completedPayments.reduce((sum, p) => sum + p.amount, 0);

  return (
    <div className="p-6 space-y-6">
      {payingAppointment && (
        <PayModal
          appointment={payingAppointment}
          patientId={user!.authUserId}
          patientEmail={user!.email}
          onClose={() => setPayingAppointment(null)}
        />
      )}

      <div>
        <h1 className="text-2xl font-bold text-gray-900">Payments</h1>
        <p className="text-sm text-gray-500 mt-0.5">Manage your appointment payments</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card flex items-center gap-4">
          <div className="h-10 w-10 rounded-xl bg-green-100 flex items-center justify-center shrink-0">
            <DollarSign size={20} className="text-green-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Total Paid</p>
            <p className="text-xl font-bold text-gray-900">${totalSpent.toFixed(2)}</p>
          </div>
        </div>
        <div className="card flex items-center gap-4">
          <div className="h-10 w-10 rounded-xl bg-blue-100 flex items-center justify-center shrink-0">
            <Receipt size={20} className="text-blue-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Completed Payments</p>
            <p className="text-xl font-bold text-gray-900">{completedPayments.length}</p>
          </div>
        </div>
        <div className="card flex items-center gap-4">
          <div className="h-10 w-10 rounded-xl bg-yellow-100 flex items-center justify-center shrink-0">
            <CreditCard size={20} className="text-yellow-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Awaiting Payment</p>
            <p className="text-xl font-bold text-gray-900">{unpaidConfirmed.length}</p>
          </div>
        </div>
      </div>

      {/* Pending payments section */}
      {unpaidConfirmed.length > 0 && (
        <div className="space-y-3">
          <h2 className="text-base font-semibold text-gray-800">Awaiting Payment</h2>
          <div className="space-y-2">
            {unpaidConfirmed.map(a => (
              <div key={a.id} className="card flex items-center justify-between gap-4 flex-wrap">
                <div className="space-y-0.5">
                  <p className="font-medium text-gray-900 text-sm">{a.doctorName}</p>
                  <p className="text-xs text-gray-500">{a.doctorSpecialization} · {a.appointmentDate} at {a.appointmentTime}</p>
                </div>
                <button
                  onClick={() => setPayingAppointment(a)}
                  className="btn-primary text-sm shrink-0"
                >
                  <CreditCard size={15} /> Pay Now
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Payment history */}
      <div className="space-y-3">
        <h2 className="text-base font-semibold text-gray-800">Payment History</h2>
        {isLoading ? (
          <div className="flex justify-center py-12">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" />
          </div>
        ) : payments.length === 0 ? (
          <div className="card text-center py-14 text-gray-400">
            <CreditCard size={32} className="mx-auto mb-2 text-gray-200" />
            <p>No payments yet.</p>
          </div>
        ) : (
          <div className="card !p-0 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full">
                <thead>
                  <tr>
                    <th className="th">ID</th>
                    <th className="th">Appointment</th>
                    <th className="th">Amount</th>
                    <th className="th">Status</th>
                    <th className="th">Date</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 bg-white">
                  {payments.map((p: Payment) => (
                    <tr key={p.id} className="hover:bg-gray-50 transition-colors">
                      <td className="td font-mono text-xs text-gray-500">#{p.id}</td>
                      <td className="td text-sm">Appt #{p.appointmentId}</td>
                      <td className="td font-semibold">${p.amount.toFixed(2)} <span className="text-xs text-gray-400">{p.currency}</span></td>
                      <td className="td"><PaymentStatusBadge status={p.status} /></td>
                      <td className="td text-sm text-gray-500">{new Date(p.createdAt).toLocaleDateString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
