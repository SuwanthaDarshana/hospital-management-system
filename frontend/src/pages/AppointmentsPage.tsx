import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { useRealtimeSync } from '../hooks/useRealtimeSync';
import {
  getAllAppointments, getMyAppointments, getAppointmentsByDoctor,
  cancelAppointment, updateAppointmentStatus, bookAppointment,
} from '../api/appointments';
import { getAllDoctors } from '../api/doctors';
import StatusBadge from '../components/StatusBadge';
import ConfirmModal from '../components/ConfirmModal';
import { Plus, CalendarDays, X, Loader2, CheckCircle2, XCircle, CreditCard } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import type { Appointment, Doctor } from '../types';

function BookModal({ onClose }: { onClose: () => void }) {
  const qc = useQueryClient();
  const { user } = useAuthStore();
  const { data: doctorsData } = useQuery({
    queryKey: ['doctors'],
    queryFn: () => getAllDoctors().then(r => r.data.data),
    staleTime: 0,
  });
  const doctors: Doctor[] = (doctorsData ?? []).filter(d => d.availabilityStatus === 'AVAILABLE');

  const [form, setForm] = useState({
    patientName: '',
    doctorAuthUserId: '',
    appointmentDate: '',
    appointmentTime: '',
    reason: '',
  });

  const mutation = useMutation({
    mutationFn: () => {
      const doc = doctors.find(d => d.authUserId === Number(form.doctorAuthUserId));
      return bookAppointment({
        patientAuthUserId: user!.authUserId,
        patientName: form.patientName,
        doctorAuthUserId: Number(form.doctorAuthUserId),
        doctorName: doc ? `Dr. ${doc.firstName} ${doc.lastName}` : 'Unknown',
        doctorEmail: doc?.email,
        doctorSpecialization: doc?.specialization,
        appointmentDate: form.appointmentDate,
        appointmentTime: form.appointmentTime,
        reason: form.reason,
      });
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['appointments-my'] });
      qc.invalidateQueries({ queryKey: ['appointments-all'] });
      onClose();
    },
  });

  const set = (k: string) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
    setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 className="text-base font-semibold text-gray-900">Book Appointment</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
        </div>
        <form
          className="px-6 py-5 space-y-4"
          onSubmit={e => { e.preventDefault(); mutation.mutate(); }}
        >
          <div className="rounded-lg bg-blue-50 border border-blue-100 px-3 py-2 text-xs text-blue-700">
            Booking as <span className="font-semibold">{user?.email}</span>
          </div>

          <div>
            <label className="label">Your Full Name</label>
            <input className="input" required value={form.patientName} onChange={set('patientName')} placeholder="John Doe" />
          </div>

          <div>
            <label className="label">Select Doctor</label>
            {doctors.length === 0 ? (
              <div className="rounded-lg bg-yellow-50 border border-yellow-200 px-4 py-3 text-sm text-yellow-700">
                No doctors are currently available for appointments. Please check back later.
              </div>
            ) : (
              <select className="input" required value={form.doctorAuthUserId} onChange={set('doctorAuthUserId')}>
                <option value="">Choose a doctor…</option>
                {doctors.map(d => (
                  <option key={d.authUserId} value={d.authUserId}>
                    Dr. {d.firstName} {d.lastName} — {d.specialization}
                  </option>
                ))}
              </select>
            )}
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Date</label>
              <input className="input" required type="date" value={form.appointmentDate} onChange={set('appointmentDate')} min={new Date().toISOString().split('T')[0]} />
            </div>
            <div>
              <label className="label">Time</label>
              <input className="input" required type="time" value={form.appointmentTime} onChange={set('appointmentTime')} />
            </div>
          </div>
          <div>
            <label className="label">Reason</label>
            <textarea className="input resize-none" rows={3} value={form.reason} onChange={set('reason')} placeholder="Describe your symptoms or reason for visit…" />
          </div>

          {mutation.isError && (
            <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">
              {(mutation.error as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Failed to book appointment.'}
            </p>
          )}

          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={mutation.isPending} className="btn-primary flex-1">
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Booking…</> : 'Book Appointment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function AppointmentsPage() {
  const { user } = useAuthStore();
  const qc = useQueryClient();
  const navigate = useNavigate();
  const [showBook, setShowBook] = useState(false);
  const [confirmCancel, setConfirmCancel] = useState<number | null>(null);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const isAdminOrStaff = user?.role === 'ADMIN' || user?.role === 'STAFF';
  const isDoctor = user?.role === 'DOCTOR';

  useRealtimeSync([{
    topic: 'appointments',
    invalidate: [['appointments-all'], ['appointments-my'], ['appointments-doctor']],
  }]);

  const { data, isLoading } = useQuery({
    queryKey: isAdminOrStaff
      ? ['appointments-all']
      : isDoctor
        ? ['appointments-doctor', user?.authUserId]
        : ['appointments-my'],
    queryFn: isAdminOrStaff
      ? () => getAllAppointments().then(r => r.data.data)
      : isDoctor
        ? () => getAppointmentsByDoctor(user!.authUserId).then(r => r.data.data)
        : () => getMyAppointments().then(r => r.data.data),
  });

  const invalidateAll = () => {
    qc.invalidateQueries({ queryKey: ['appointments-all'] });
    qc.invalidateQueries({ queryKey: ['appointments-my'] });
    qc.invalidateQueries({ queryKey: ['appointments-doctor'] });
    qc.invalidateQueries({ queryKey: ['patients-for-doctor'] });
  };

  const cancelMutation = useMutation({
    mutationFn: (id: number) => cancelAppointment(id),
    onSuccess: invalidateAll,
  });

  const confirmMutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) => updateAppointmentStatus(id, status),
    onSuccess: invalidateAll,
  });

  const all: Appointment[] = data ?? [];
  const filtered = filterStatus === 'ALL' ? all : all.filter(a => a.status === filterStatus);

  const statuses = ['ALL', 'PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'];

  return (
    <div className="p-6 space-y-6">
      {showBook && <BookModal onClose={() => setShowBook(false)} />}
      {confirmCancel !== null && (
        <ConfirmModal
          title="Cancel Appointment"
          message="Are you sure you want to cancel this appointment? This action cannot be undone."
          confirmLabel="Yes, Cancel"
          onConfirm={() => { cancelMutation.mutate(confirmCancel); setConfirmCancel(null); }}
          onCancel={() => setConfirmCancel(null)}
        />
      )}

      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Appointments</h1>
          <p className="text-sm text-gray-500 mt-0.5">{filtered.length} record{filtered.length !== 1 ? 's' : ''}</p>
        </div>
        {user?.role === 'PATIENT' && (
          <button className="btn-primary" onClick={() => setShowBook(true)}>
            <Plus size={16} /> Book Appointment
          </button>
        )}
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {statuses.map(s => (
          <button
            key={s}
            onClick={() => setFilterStatus(s)}
            className={`shrink-0 rounded-full px-4 py-1.5 text-xs font-medium transition-colors ${
              filterStatus === s
                ? 'bg-primary-600 text-white'
                : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
            }`}
          >
            {s}
          </button>
        ))}
      </div>

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
                  <th className="th">Patient</th>
                  <th className="th">Doctor</th>
                  <th className="th">Specialization</th>
                  <th className="th">Date</th>
                  <th className="th">Time</th>
                  <th className="th">Reason</th>
                  <th className="th">Status</th>
                  <th className="th">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 bg-white">
                {filtered.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="td text-center py-12 text-gray-400">
                      <CalendarDays size={32} className="mx-auto mb-2 text-gray-200" />
                      No appointments found.
                    </td>
                  </tr>
                ) : filtered.map(a => (
                  <tr key={a.id} className="hover:bg-gray-50 transition-colors">
                    <td className="td font-medium">{a.patientName}</td>
                    <td className="td">{a.doctorName}</td>
                    <td className="td text-gray-500">{a.doctorSpecialization || '—'}</td>
                    <td className="td">{a.appointmentDate}</td>
                    <td className="td">{a.appointmentTime}</td>
                    <td className="td max-w-[180px]">
                      <span className="truncate block text-gray-500">{a.reason || '—'}</span>
                    </td>
                    <td className="td"><StatusBadge status={a.status} /></td>
                    <td className="td">
                      <div className="flex items-center gap-1">
                        {/* Doctor/Admin can confirm */}
                        {(user?.role === 'DOCTOR' || user?.role === 'ADMIN') && a.status === 'PENDING' && (
                          <button
                            title="Confirm"
                            onClick={() => confirmMutation.mutate({ id: a.id, status: 'CONFIRMED' })}
                            className="p-1 text-green-500 hover:text-green-600 transition-colors"
                          >
                            <CheckCircle2 size={16} />
                          </button>
                        )}
                        {/* Doctor/Admin can complete */}
                        {(user?.role === 'DOCTOR' || user?.role === 'ADMIN') && a.status === 'CONFIRMED' && (
                          <button
                            title="Mark Completed"
                            onClick={() => confirmMutation.mutate({ id: a.id, status: 'COMPLETED' })}
                            className="p-1 text-blue-500 hover:text-blue-600 transition-colors"
                          >
                            <CheckCircle2 size={16} />
                          </button>
                        )}
                        {/* Patient can pay for CONFIRMED appointments */}
                        {user?.role === 'PATIENT' && a.status === 'CONFIRMED' && (
                          <button
                            title="Pay Now"
                            onClick={() => navigate('/payments')}
                            className="p-1 text-primary-500 hover:text-primary-700 transition-colors"
                          >
                            <CreditCard size={16} />
                          </button>
                        )}
                        {/* Patient/Admin can cancel */}
                        {(user?.role === 'PATIENT' || user?.role === 'ADMIN') &&
                          (a.status === 'PENDING' || a.status === 'CONFIRMED') && (
                          <button
                            title="Cancel"
                            onClick={() => setConfirmCancel(a.id)}
                            className="p-1 text-red-400 hover:text-red-600 transition-colors"
                          >
                            <XCircle size={16} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
