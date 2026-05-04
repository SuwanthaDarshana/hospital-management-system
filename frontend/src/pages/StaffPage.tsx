import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { apiClient } from '../api/client';
import { registerStaff } from '../api/auth';
import { Users, Search, Trash2, Plus, X, Loader2, Pencil } from 'lucide-react';
import type { Staff, RegisterStaffRequest, StaffUpdateRequest, StandardResponse } from '../types';
import clsx from 'clsx';
import ConfirmModal from '../components/ConfirmModal';

const getStaff = () =>
  apiClient.get<StandardResponse<Staff[]>>('/api/v1/staff').then(r => r.data.data);

const updateStaff = (authUserId: number, data: StaffUpdateRequest) =>
  apiClient.put<StandardResponse<Staff>>(`/api/v1/staff/${authUserId}`, data);

const deactivateStaff = (authUserId: number) =>
  apiClient.delete(`/api/v1/staff/${authUserId}`);

const DEPARTMENTS = ['RECEPTION', 'PHARMACY', 'LAB', 'NURSING', 'ADMIN', 'BILLING', 'RADIOLOGY'];

const DEPT_COLORS: Record<string, string> = {
  RECEPTION: 'bg-blue-100 text-blue-700',
  PHARMACY:  'bg-green-100 text-green-700',
  LAB:       'bg-purple-100 text-purple-700',
  NURSING:   'bg-pink-100 text-pink-700',
  ADMIN:     'bg-orange-100 text-orange-700',
  BILLING:   'bg-cyan-100 text-cyan-700',
  RADIOLOGY: 'bg-indigo-100 text-indigo-700',
};

// ─── Add Staff Modal (Admin only) ────────────────────────────────────────────
function AddStaffModal({ onClose }: { onClose: () => void }) {
  const qc = useQueryClient();
  const [form, setForm] = useState<RegisterStaffRequest>({
    firstName: '', lastName: '', email: '', password: '', phone: '',
    role: 'STAFF', department: '', address: '', gender: '', dateOfBirth: '', bloodGroup: '',
  });
  const [error, setError] = useState('');

  const mutation = useMutation({
    mutationFn: () => registerStaff({ ...form, role: 'STAFF' }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['staff'] }); onClose(); },
    onError: (err: any) => setError(err.response?.data?.message || 'Registration failed.'),
  });

  const set = (k: keyof RegisterStaffRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4 py-6">
      <div className="w-full max-w-lg bg-white rounded-2xl shadow-xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
          <h3 className="text-base font-semibold text-gray-900">Register New Staff</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
        </div>
        <form
          className="px-6 py-5 space-y-4 overflow-y-auto"
          onSubmit={e => { e.preventDefault(); setError(''); mutation.mutate(); }}
        >
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">First name *</label>
              <input className="input" required value={form.firstName} onChange={set('firstName')} placeholder="Jane" />
            </div>
            <div>
              <label className="label">Last name *</label>
              <input className="input" required value={form.lastName} onChange={set('lastName')} placeholder="Doe" />
            </div>
          </div>
          <div>
            <label className="label">Email *</label>
            <input className="input" type="email" required value={form.email} onChange={set('email')} placeholder="jane.doe@hospital.com" />
          </div>
          <div>
            <label className="label">Password *</label>
            <input className="input" type="password" required minLength={6} value={form.password} onChange={set('password')} placeholder="Min. 6 characters" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone ?? ''} onChange={set('phone')} placeholder="0771234567" />
            </div>
            <div>
              <label className="label">Department *</label>
              <select className="input" required value={form.department} onChange={set('department')}>
                <option value="">Select…</option>
                {DEPARTMENTS.map(d => <option key={d}>{d}</option>)}
              </select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender ?? ''} onChange={set('gender')}>
                <option value="">Select…</option>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
              </select>
            </div>
            <div>
              <label className="label">Date of birth</label>
              <input className="input" type="date" value={form.dateOfBirth ?? ''} onChange={set('dateOfBirth')} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Blood group</label>
              <select className="input" value={form.bloodGroup ?? ''} onChange={set('bloodGroup')}>
                <option value="">Select…</option>
                {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
              </select>
            </div>
            <div className="col-span-2">
              <label className="label">Address</label>
              <input className="input" value={form.address ?? ''} onChange={set('address')} placeholder="123 Main St, Colombo" />
            </div>
          </div>

          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}

          <div className="flex gap-3 pt-1">
            <button type="button" onClick={onClose} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={mutation.isPending} className="btn-primary flex-1">
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Registering…</> : 'Register Staff'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ─── Edit Staff Modal ────────────────────────────────────────────────────────
function EditStaffModal({ staff, isAdmin, onClose }: { staff: Staff; isAdmin: boolean; onClose: () => void }) {
  const qc = useQueryClient();
  const [form, setForm] = useState<StaffUpdateRequest>({
    firstName:   staff.firstName,
    lastName:    staff.lastName,
    email:       staff.email,
    phone:       staff.phone ?? '',
    department:  staff.department ?? '',
    role:        staff.role ?? 'STAFF',
    isActive:    staff.isActive,
    address:     staff.address ?? '',
    gender:      staff.gender ?? '',
    dateOfBirth: staff.dateOfBirth ?? '',
    bloodGroup:  staff.bloodGroup ?? '',
  });
  const [error, setError] = useState('');

  const mutation = useMutation({
    mutationFn: () => {
      const payload: StaffUpdateRequest = {
        ...form,
        phone:       form.phone       || null,
        address:     form.address     || null,
        gender:      form.gender      || null,
        dateOfBirth: form.dateOfBirth || null,
        bloodGroup:  form.bloodGroup  || null,
      };
      return updateStaff(staff.authUserId, payload);
    },
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['staff'] }); onClose(); },
    onError: (err: any) => setError(err.response?.data?.message || 'Update failed.'),
  });

  const set = (k: keyof StaffUpdateRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4 py-6">
      <div className="w-full max-w-lg bg-white rounded-2xl shadow-xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
          <div>
            <h3 className="text-base font-semibold text-gray-900">Edit {staff.firstName} {staff.lastName}</h3>
            {!isAdmin && (
              <p className="text-xs text-gray-400 mt-0.5">You can update your personal and contact details</p>
            )}
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
        </div>
        <form
          className="px-6 py-5 space-y-4 overflow-y-auto"
          onSubmit={e => { e.preventDefault(); setError(''); mutation.mutate(); }}
        >
          {/* ── Admin-only fields ── */}
          {isAdmin && (
            <>
              <div className="rounded-lg bg-amber-50 border border-amber-200 px-3 py-2 text-xs text-amber-700 font-medium">
                Admin fields — changes sync to the auth service
              </div>
              <div>
                <label className="label">Email</label>
                <input className="input" type="email" value={form.email} onChange={set('email')} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="label">Department</label>
                  <select className="input" value={form.department ?? ''} onChange={set('department')}>
                    <option value="">Select…</option>
                    {DEPARTMENTS.map(d => <option key={d}>{d}</option>)}
                  </select>
                </div>
                <div>
                  <label className="label">Status</label>
                  <select className="input" value={form.isActive ? 'true' : 'false'}
                    onChange={e => setForm(f => ({ ...f, isActive: e.target.value === 'true' }))}>
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </div>
              </div>
              <div className="border-t border-gray-100 pt-2" />
            </>
          )}

          {/* ── Fields editable by admin and staff ── */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">First name</label>
              <input className="input" required value={form.firstName} onChange={set('firstName')} />
            </div>
            <div>
              <label className="label">Last name</label>
              <input className="input" required value={form.lastName} onChange={set('lastName')} />
            </div>
          </div>
          <div>
            <label className="label">Phone</label>
            <input className="input" value={form.phone ?? ''} onChange={set('phone')} placeholder="0771234567" />
          </div>
          <div>
            <label className="label">Address</label>
            <input className="input" value={form.address ?? ''} onChange={set('address')} placeholder="123 Main St, Colombo" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender ?? ''} onChange={set('gender')}>
                <option value="">Select…</option>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
              </select>
            </div>
            <div>
              <label className="label">Date of birth</label>
              <input className="input" type="date" value={form.dateOfBirth ?? ''} onChange={set('dateOfBirth')} />
            </div>
          </div>
          <div>
            <label className="label">Blood group</label>
            <select className="input" value={form.bloodGroup ?? ''} onChange={set('bloodGroup')}>
              <option value="">Select…</option>
              {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
            </select>
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

// ─── Main Page ───────────────────────────────────────────────────────────────
export default function StaffPage() {
  const { user } = useAuthStore();
  const isAdmin = user?.role === 'ADMIN';
  const qc = useQueryClient();
  const [search, setSearch] = useState('');
  const [showAdd, setShowAdd] = useState(false);
  const [editTarget, setEditTarget] = useState<Staff | null>(null);
  const [confirmDeactivate, setConfirmDeactivate] = useState<number | null>(null);

  const { data, isLoading } = useQuery({ queryKey: ['staff'], queryFn: getStaff });

  const deleteMutation = useMutation({
    mutationFn: (authUserId: number) => deactivateStaff(authUserId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['staff'] }),
  });

  const staff: Staff[] = (data ?? []).filter(s => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      s.firstName.toLowerCase().includes(q) ||
      s.lastName.toLowerCase().includes(q) ||
      s.department?.toLowerCase().includes(q)
    );
  });

  const canEdit = (s: Staff) =>
    isAdmin || (user?.role === 'STAFF' && s.email === user?.email);

  return (
    <div className="p-6 space-y-6">
      {showAdd    && <AddStaffModal onClose={() => setShowAdd(false)} />}
      {editTarget && <EditStaffModal staff={editTarget} isAdmin={isAdmin} onClose={() => setEditTarget(null)} />}
      {confirmDeactivate !== null && (
        <ConfirmModal
          title="Deactivate Staff Member"
          message="Are you sure you want to deactivate this staff member? They will lose access to the system."
          confirmLabel="Deactivate"
          onConfirm={() => { deleteMutation.mutate(confirmDeactivate); setConfirmDeactivate(null); }}
          onCancel={() => setConfirmDeactivate(null)}
        />
      )}

      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Staff</h1>
          <p className="text-sm text-gray-500 mt-0.5">{staff.length} staff member{staff.length !== 1 ? 's' : ''}</p>
        </div>
        {isAdmin && (
          <button className="btn-primary" onClick={() => setShowAdd(true)}>
            <Plus size={16} /> Add Staff
          </button>
        )}
      </div>

      <div className="relative max-w-sm">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input className="input pl-9" placeholder="Search by name or department…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" />
        </div>
      ) : (
        <div className="card !p-0 overflow-hidden">
          <table className="min-w-full">
            <thead>
              <tr>
                <th className="th">Name</th>
                <th className="th">Email</th>
                <th className="th">Phone</th>
                <th className="th">Department</th>
                <th className="th">Status</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {staff.length === 0 ? (
                <tr><td colSpan={6} className="td text-center py-10 text-gray-400">No staff found.</td></tr>
              ) : staff.map(s => (
                <tr key={s.id} className={clsx(
                  'transition-colors group',
                  s.isActive ? 'hover:bg-gray-50' : 'bg-red-50 hover:bg-red-100',
                )}>
                  <td className="td">
                    <div className="flex items-center gap-3">
                      <div className={clsx(
                        'flex h-8 w-8 items-center justify-center rounded-full shrink-0',
                        s.isActive ? 'bg-gray-100' : 'bg-red-100',
                      )}>
                        <Users size={15} className={s.isActive ? 'text-gray-500' : 'text-red-400'} />
                      </div>
                      <p className={clsx('font-medium', s.isActive ? 'text-gray-900' : 'text-red-400 line-through')}>{s.firstName} {s.lastName}</p>
                    </div>
                  </td>
                  <td className={clsx('td', !s.isActive && 'text-red-300')}>{s.email}</td>
                  <td className={clsx('td', !s.isActive && 'text-red-300')}>{s.phone || '—'}</td>
                  <td className="td">
                    <span className={clsx('badge', DEPT_COLORS[s.department] ?? 'bg-gray-100 text-gray-600')}>
                      {s.department || '—'}
                    </span>
                  </td>
                  <td className="td">
                    <span className={clsx('badge', s.isActive ? 'badge-active' : 'badge-inactive')}>
                      {s.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="td">
                    <div className="flex items-center gap-1">
                      {canEdit(s) && (
                        <button
                          onClick={() => setEditTarget(s)}
                          className="p-1.5 rounded text-gray-400 hover:text-primary-600 hover:bg-primary-50 transition-colors"
                          title="Edit"
                        >
                          <Pencil size={14} />
                        </button>
                      )}
                      {isAdmin && (
                        <button
                          onClick={() => setConfirmDeactivate(s.authUserId)}
                          className="p-1.5 rounded text-gray-400 hover:text-red-500 transition-colors"
                          title="Deactivate"
                        >
                          <Trash2 size={14} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
