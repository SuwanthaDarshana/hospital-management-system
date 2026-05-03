import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAllPayments, refundPayment } from '../api/payments';
import ConfirmModal from '../components/ConfirmModal';
import {
  DollarSign, CreditCard, CheckCircle2, Clock,
  XCircle, RefreshCw, Search, TrendingUp, Receipt,
} from 'lucide-react';
import type { Payment, PaymentStatus } from '../types';

// ── Helpers ───────────────────────────────────────────────────────────────────

const STATUS_STYLES: Record<string, string> = {
  PENDING:    'bg-yellow-50 text-yellow-700 border-yellow-200',
  PROCESSING: 'bg-blue-50 text-blue-700 border-blue-200',
  COMPLETED:  'bg-green-50 text-green-700 border-green-200',
  FAILED:     'bg-red-50 text-red-700 border-red-200',
  REFUNDED:   'bg-gray-100 text-gray-600 border-gray-300',
};

function PaymentStatusBadge({ status }: { status: string }) {
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${STATUS_STYLES[status] ?? 'bg-gray-100 text-gray-600'}`}>
      {status}
    </span>
  );
}

const FILTERS: { label: string; value: string }[] = [
  { label: 'All',        value: 'ALL' },
  { label: 'Completed',  value: 'COMPLETED' },
  { label: 'Pending',    value: 'PENDING' },
  { label: 'Processing', value: 'PROCESSING' },
  { label: 'Refunded',   value: 'REFUNDED' },
  { label: 'Failed',     value: 'FAILED' },
];

// ── Page ─────────────────────────────────────────────────────────────────────
export default function BillingPage() {
  const qc = useQueryClient();
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [confirmRefund, setConfirmRefund] = useState<number | null>(null);

  const { data: payments = [], isLoading } = useQuery({
    queryKey: ['all-payments'],
    queryFn: () => getAllPayments().then(r => r.data.data),
  });

  const refundMutation = useMutation({
    mutationFn: (id: number) => refundPayment(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['all-payments'] }),
  });

  // ── Derived stats ──────────────────────────────────────────────────────────
  const completed  = payments.filter(p => p.status === 'COMPLETED');
  const pending    = payments.filter(p => p.status === 'PENDING' || p.status === 'PROCESSING');
  const refunded   = payments.filter(p => p.status === 'REFUNDED');
  const failed     = payments.filter(p => p.status === 'FAILED');
  const totalRevenue = completed.reduce((sum, p) => sum + p.amount, 0);
  const refundedAmt  = refunded.reduce((sum, p) => sum + p.amount, 0);

  // ── Filtered rows ──────────────────────────────────────────────────────────
  const filtered = payments.filter(p => {
    const matchStatus = statusFilter === 'ALL' || p.status === statusFilter;
    const q = search.toLowerCase();
    const matchSearch = !q
      || p.patientEmail?.toLowerCase().includes(q)
      || String(p.id).includes(q)
      || String(p.appointmentId).includes(q);
    return matchStatus && matchSearch;
  });

  const stats = [
    {
      label: 'Total Revenue',
      value: `$${totalRevenue.toFixed(2)}`,
      sub: `${completed.length} completed payments`,
      icon: <TrendingUp size={20} className="text-green-600" />,
      bg: 'bg-green-100',
    },
    {
      label: 'All Transactions',
      value: payments.length,
      sub: 'across all patients',
      icon: <Receipt size={20} className="text-blue-600" />,
      bg: 'bg-blue-100',
    },
    {
      label: 'Pending',
      value: pending.length,
      sub: 'awaiting payment',
      icon: <Clock size={20} className="text-yellow-600" />,
      bg: 'bg-yellow-100',
    },
    {
      label: 'Refunded',
      value: `$${refundedAmt.toFixed(2)}`,
      sub: `${refunded.length} refund${refunded.length !== 1 ? 's' : ''}`,
      icon: <RefreshCw size={20} className="text-orange-500" />,
      bg: 'bg-orange-100',
    },
    {
      label: 'Failed',
      value: failed.length,
      sub: 'failed transactions',
      icon: <XCircle size={20} className="text-red-500" />,
      bg: 'bg-red-100',
    },
  ];

  return (
    <div className="p-6 space-y-6">

      {confirmRefund !== null && (
        <ConfirmModal
          title="Refund Payment"
          message="Are you sure you want to refund this payment? The patient will be notified and the amount will be returned."
          confirmLabel="Yes, Refund"
          variant="warning"
          onConfirm={() => { refundMutation.mutate(confirmRefund); setConfirmRefund(null); }}
          onCancel={() => setConfirmRefund(null)}
        />
      )}

      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Billing</h1>
        <p className="text-sm text-gray-500 mt-0.5">Manage all patient payments and refunds</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-5 gap-4">
        {stats.map(s => (
          <div key={s.label} className="card flex items-center gap-4">
            <div className={`h-11 w-11 rounded-xl flex items-center justify-center shrink-0 ${s.bg}`}>
              {s.icon}
            </div>
            <div className="min-w-0">
              <p className="text-xs text-gray-500 truncate">{s.label}</p>
              <p className="text-xl font-bold text-gray-900 leading-tight">{s.value}</p>
              <p className="text-xs text-gray-400 truncate">{s.sub}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Toolbar */}
      <div className="flex flex-wrap gap-3 items-center justify-between">
        <div className="flex gap-2 overflow-x-auto pb-1">
          {FILTERS.map(f => (
            <button
              key={f.value}
              onClick={() => setStatusFilter(f.value)}
              className={`shrink-0 rounded-full px-4 py-1.5 text-xs font-medium transition-colors ${
                statusFilter === f.value
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
              }`}
            >
              {f.label}
              {f.value !== 'ALL' && (
                <span className="ml-1.5 opacity-70">
                  ({payments.filter(p => p.status === f.value).length})
                </span>
              )}
            </button>
          ))}
        </div>

        <div className="relative w-full sm:w-64">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            className="input pl-9 text-sm"
            placeholder="Search email, payment ID…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      </div>

      {/* Table */}
      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" />
        </div>
      ) : (
        <div className="card !p-0 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr>
                  <th className="th">Payment ID</th>
                  <th className="th">Patient Email</th>
                  <th className="th">Appointment</th>
                  <th className="th">Amount</th>
                  <th className="th">Status</th>
                  <th className="th">Date</th>
                  <th className="th">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 bg-white">
                {filtered.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="td text-center py-14 text-gray-400">
                      <CreditCard size={32} className="mx-auto mb-2 text-gray-200" />
                      No payments found.
                    </td>
                  </tr>
                ) : filtered.map((p: Payment) => (
                  <tr key={p.id} className="hover:bg-gray-50 transition-colors group">

                    {/* Payment ID */}
                    <td className="td">
                      <span className="font-mono text-xs text-gray-500 bg-gray-100 rounded px-1.5 py-0.5">
                        #{p.id}
                      </span>
                    </td>

                    {/* Patient */}
                    <td className="td">
                      <div className="flex items-center gap-2">
                        <div className="h-7 w-7 rounded-full bg-primary-100 flex items-center justify-center shrink-0">
                          <DollarSign size={13} className="text-primary-600" />
                        </div>
                        <span className="text-sm text-gray-700 truncate max-w-[160px]">
                          {p.patientEmail || '—'}
                        </span>
                      </div>
                    </td>

                    {/* Appointment */}
                    <td className="td text-sm text-gray-500">
                      Appt #{p.appointmentId}
                    </td>

                    {/* Amount */}
                    <td className="td">
                      <span className="font-semibold text-gray-900">${p.amount.toFixed(2)}</span>
                      <span className="ml-1 text-xs text-gray-400">{p.currency}</span>
                    </td>

                    {/* Status */}
                    <td className="td">
                      <PaymentStatusBadge status={p.status} />
                    </td>

                    {/* Date */}
                    <td className="td text-sm text-gray-500">
                      {new Date(p.createdAt).toLocaleDateString('en-US', {
                        year: 'numeric', month: 'short', day: 'numeric',
                      })}
                    </td>

                    {/* Actions */}
                    <td className="td">
                      <div className="flex items-center gap-1">
                        {p.status === 'COMPLETED' && (
                          <button
                            title="Refund payment"
                            onClick={() => setConfirmRefund(p.id)}
                            disabled={refundMutation.isPending}
                            className="flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-xs font-medium text-orange-600 bg-orange-50 hover:bg-orange-100 border border-orange-200 transition-colors disabled:opacity-50"
                          >
                            <RefreshCw size={12} />
                            Refund
                          </button>
                        )}
                        {p.status === 'REFUNDED' && (
                          <span className="flex items-center gap-1 text-xs text-gray-400">
                            <CheckCircle2 size={13} /> Refunded
                          </span>
                        )}
                      </div>
                    </td>

                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Footer count */}
          <div className="px-4 py-3 border-t border-gray-100 bg-gray-50 text-xs text-gray-500">
            Showing {filtered.length} of {payments.length} transaction{payments.length !== 1 ? 's' : ''}
          </div>
        </div>
      )}
    </div>
  );
}
