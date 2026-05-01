import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { getAllDoctors, updateDoctor } from '../api/doctors';
import { registerDoctor } from '../api/auth';
import {
  Search, Stethoscope, Phone, Mail, Plus, X, Loader2, Pencil, Clock,
} from 'lucide-react';
import type { Doctor, RegisterDoctorRequest, DoctorUpdateRequest } from '../types';

// Add Doctor Modal (Admin only) 
function AddDoctorModal({ onClose }: { onClose: () => void }) {
  const qc = useQueryClient();
  const [form, setForm] = useState<RegisterDoctorRequest>({
    firstName: '', lastName: '', email: '', password: '', phone: '', specialization: '',
  });
  const [error, setError] = useState('');

  const mutation = useMutation({
    mutationFn: () => registerDoctor(form),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['doctors'] }); onClose(); },
    onError: (err: unknown) => setError((err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Registration failed.'),
  });

  const set = (k: keyof RegisterDoctorRequest) =>
    (e: React.ChangeEvent<HTMLInputElement>) => setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 className="text-base font-semibold text-gray-900">Register New Doctor</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
        </div>
        <form className="px-6 py-5 space-y-4" onSubmit={e => { e.preventDefault(); setError(''); mutation.mutate(); }}>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">First name *</label>
              <input className="input" required value={form.firstName} onChange={set('firstName')} placeholder="John" />
            </div>
            <div>
              <label className="label">Last name *</label>
              <input className="input" required value={form.lastName} onChange={set('lastName')} placeholder="Smith" />
            </div>
          </div>
          <div>
            <label className="label">Email *</label>
            <input className="input" type="email" required value={form.email} onChange={set('email')} placeholder="dr.smith@hospital.com" />
          </div>
          <div>
            <label className="label">Password *</label>
            <input className="input" type="password" required minLength={6} value={form.password} onChange={set('password')} placeholder="Min. 6 characters" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Phone *</label>
              <input className="input" required value={form.phone} onChange={set('phone')} placeholder="0771234567" />
            </div>
            <div>
              <label className="label">Specialization *</label>
              <input className="input" required value={form.specialization} onChange={set('specialization')} placeholder="Cardiology" />
            </div>
          </div>
          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}
          <div className="flex gap-3 pt-1">
            <button type="button" onClick={onClose} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={mutation.isPending} className="btn-primary flex-1">
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Registering…</> : 'Register Doctor'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// Edit Doctor Modal (Admin: all fields | Doctor: own profile limited fields)
function EditDoctorModal({ doctor, onClose }: { doctor: Doctor; onClose: () => void }) {
  const { user } = useAuthStore();
  const qc = useQueryClient();
  const isAdmin = user?.role === 'ADMIN';

  const [form, setForm] = useState<DoctorUpdateRequest>({
    firstName: doctor.firstName,
    lastName:  doctor.lastName,
    email:     doctor.email,
    phone:     doctor.phone ?? '',
    specialization: doctor.specialization ?? '',
    availability:   doctor.availability ?? '',
    password: '',
  });
  const [error, setError] = useState('');

  const mutation = useMutation({
    mutationFn: () => {
      // Only send non-empty password
      const payload: DoctorUpdateRequest = { ...form };
      if (!payload.password) delete payload.password;
      return updateDoctor(doctor.authUserId, payload);
    },
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['doctors'] }); onClose(); },
    onError: (err: unknown) => setError((err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Update failed.'),
  });

  const set = (k: keyof DoctorUpdateRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4">
      <div className="w-full max-w-lg bg-white rounded-2xl shadow-xl">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <div>
            <h3 className="text-base font-semibold text-gray-900">
              Edit Dr. {doctor.firstName} {doctor.lastName}
            </h3>
            {!isAdmin && (
              <p className="text-xs text-gray-400 mt-0.5">You can edit phone, specialization, availability and password</p>
            )}
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
        </div>
        <form className="px-6 py-5 space-y-4" onSubmit={e => { e.preventDefault(); setError(''); mutation.mutate(); }}>
          {/* Admin-only fields */}
          {isAdmin && (
            <>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="label">First name</label>
                  <input className="input" value={form.firstName ?? ''} onChange={set('firstName')} />
                </div>
                <div>
                  <label className="label">Last name</label>
                  <input className="input" value={form.lastName ?? ''} onChange={set('lastName')} />
                </div>
              </div>
              <div>
                <label className="label">Email</label>
                <input className="input" type="email" value={form.email ?? ''} onChange={set('email')} />
              </div>
            </>
          )}

          {/* Admin + Doctor fields */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone ?? ''} onChange={set('phone')} placeholder="0771234567" />
            </div>
            <div>
              <label className="label">Specialization</label>
              <input className="input" value={form.specialization ?? ''} onChange={set('specialization')} placeholder="Cardiology" />
            </div>
          </div>
          <div>
            <label className="label">Availability (JSON)</label>
            <textarea
              className="input resize-none font-mono text-xs" rows={3}
              value={form.availability ?? ''}
              onChange={set('availability')}
              placeholder='{"monday": "9:00-17:00", "tuesday": "9:00-17:00"}'
            />
          </div>
          <div>
            <label className="label">New password <span className="text-gray-400 font-normal">(leave blank to keep current)</span></label>
            <input className="input" type="password" minLength={6} value={form.password ?? ''} onChange={set('password')} placeholder="Min. 6 characters" />
          </div>

          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}
          <div className="flex gap-3 pt-1">
            <button type="button" onClick={onClose} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={mutation.isPending} className="btn-primary flex-1">
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Saving…</> : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// Main Page 
export default function DoctorsPage() {
  const { user } = useAuthStore();
  const isAdmin = user?.role === 'ADMIN';
  const isDoctor = user?.role === 'DOCTOR';

  const [search, setSearch] = useState('');
  const [showAdd, setShowAdd] = useState(false);
  const [editTarget, setEditTarget] = useState<Doctor | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['doctors'],
    queryFn: () => getAllDoctors().then(r => r.data.data),
  });

  const allDoctors: Doctor[] = data ?? [];

  const q = search.trim().toLowerCase();
  const doctors = q
    ? allDoctors.filter(d =>
        `${d.firstName} ${d.lastName}`.toLowerCase().includes(q) ||
        d.email?.toLowerCase().includes(q) ||
        d.specialization?.toLowerCase().includes(q)
      )
    : allDoctors;

  const canEdit = (doc: Doctor) =>
    isAdmin || (isDoctor && doc.email === user?.email);

  return (
    <div className="p-6 space-y-6">
      {showAdd    && <AddDoctorModal onClose={() => setShowAdd(false)} />}
      {editTarget && <EditDoctorModal doctor={editTarget} onClose={() => setEditTarget(null)} />}

      {/* Header */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Doctors</h1>
          <p className="text-sm text-gray-500 mt-0.5">{doctors.length} doctor{doctors.length !== 1 ? 's' : ''} registered</p>
        </div>
        {isAdmin && (
          <button className="btn-primary" onClick={() => setShowAdd(true)}>
            <Plus size={16} /> Add Doctor
          </button>
        )}
      </div>

      {/* Search */}
      <div className="relative max-w-sm">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          className="input pl-9"
          placeholder="Search by name, specialization or email…"
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      {/* Cards */}
      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" />
        </div>
      ) : doctors.length === 0 ? (
        <div className="card text-center py-16 text-gray-400">No doctors found.</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {doctors.map(doc => (
            <div key={doc.id} className="card hover:shadow-md transition-shadow group">
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-4 flex-1 min-w-0">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary-100 shrink-0">
                    <Stethoscope size={22} className="text-primary-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-gray-900 truncate">
                      Dr. {doc.firstName} {doc.lastName}
                    </p>
                    <span className="inline-block mt-1 text-xs font-medium text-primary-600 bg-primary-50 rounded-full px-2 py-0.5">
                      {doc.specialization || 'General'}
                    </span>
                  </div>
                </div>
                {canEdit(doc) && (
                  <button
                    onClick={() => setEditTarget(doc)}
                    className="opacity-0 group-hover:opacity-100 ml-2 p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 transition-all"
                    title="Edit"
                  >
                    <Pencil size={15} />
                  </button>
                )}
              </div>

              <div className="mt-4 space-y-1.5 text-sm text-gray-600">
                {doc.email && (
                  <div className="flex items-center gap-2">
                    <Mail size={13} className="text-gray-400 shrink-0" />
                    <span className="truncate">{doc.email}</span>
                  </div>
                )}
                {doc.phone && (
                  <div className="flex items-center gap-2">
                    <Phone size={13} className="text-gray-400 shrink-0" />
                    <span>{doc.phone}</span>
                  </div>
                )}
                {doc.availability && (
                  <div className="flex items-start gap-2 mt-2">
                    <Clock size={13} className="text-gray-400 shrink-0 mt-0.5" />
                    <span className="text-xs text-gray-400 font-mono truncate">{doc.availability}</span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
