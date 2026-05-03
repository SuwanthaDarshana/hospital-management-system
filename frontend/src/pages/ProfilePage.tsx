import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { getDoctorByAuthUserId, updateDoctor, updateDoctorAvailability } from '../api/doctors';
import { getPatientByAuthUserId, updatePatient } from '../api/patients';
import { apiClient } from '../api/client';
import {
  User, Mail, Phone, MapPin, Droplets, Calendar, Shield, Stethoscope,
  Pencil, X, Loader2, CheckCircle, Lock, Clock, CircleDot,
} from 'lucide-react';

type AvailStatus = 'NOT_SET' | 'AVAILABLE' | 'NOT_AVAILABLE';
const AVAIL_CFG: Record<AvailStatus, { label: string; dot: string; badge: string }> = {
  AVAILABLE:     { label: 'Available for appointments', dot: 'text-green-500',  badge: 'bg-green-100 text-green-700' },
  NOT_AVAILABLE: { label: 'Not Available',              dot: 'text-red-500',    badge: 'bg-red-100 text-red-700' },
  NOT_SET:       { label: 'Unavailable',                 dot: 'text-gray-400',   badge: 'bg-gray-100 text-gray-500' },
};
import type { Staff, StaffUpdateRequest, StandardResponse, DoctorUpdateRequest, PatientUpdateRequest } from '../types';

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

function InfoRow({ icon, label, value }: { icon: React.ReactNode; label: string; value?: string | null }) {
  return (
    <div className="flex items-start gap-3">
      <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-gray-100 shrink-0 mt-0.5">
        {icon}
      </div>
      <div>
        <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{label}</p>
        <p className="text-sm text-gray-900 mt-0.5">{value || '—'}</p>
      </div>
    </div>
  );
}

function PasswordSection({ onSave }: { onSave: (pw: string) => void }) {
  const [newPw, setNewPw] = useState('');
  const [confirm, setConfirm] = useState('');
  const [err, setErr] = useState('');
  const [ok, setOk] = useState(false);

  const handle = () => {
    setErr(''); setOk(false);
    if (newPw.length < 6) return setErr('Password must be at least 6 characters.');
    if (newPw !== confirm) return setErr('Passwords do not match.');
    onSave(newPw);
    setNewPw(''); setConfirm('');
    setOk(true);
    setTimeout(() => setOk(false), 3000);
  };

  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-gray-700 flex items-center gap-2">
        <Lock size={14} /> Change Password
      </h3>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <div>
          <label className="label">New password</label>
          <input className="input" type="password" value={newPw} onChange={e => setNewPw(e.target.value)} placeholder="Min. 6 characters" />
        </div>
        <div>
          <label className="label">Confirm password</label>
          <input className="input" type="password" value={confirm} onChange={e => setConfirm(e.target.value)} placeholder="Repeat password" />
        </div>
      </div>
      {err && <p className="text-xs text-red-600">{err}</p>}
      {ok  && <p className="text-xs text-green-600 flex items-center gap-1"><CheckCircle size={12} /> Password will be updated on save.</p>}
      <button type="button" className="btn-secondary text-xs py-1.5" onClick={handle}>Set new password</button>
    </div>
  );
}


// Doctor Profile


function DoctorProfile({ authUserId }: { authUserId: number }) {
  const qc = useQueryClient();
  const [editing, setEditing]     = useState(false);
  const [pendingPw, setPendingPw] = useState('');
  const [error, setError]         = useState('');
  const [availError, setAvailError] = useState('');

  const { data: doctor, isLoading } = useQuery({
    queryKey: ['my-doctor', authUserId],
    queryFn: () => getDoctorByAuthUserId(authUserId).then(r => r.data.data),
  });

  const [form, setForm] = useState<DoctorUpdateRequest>({});

  const startEdit = () => {
    setForm({
      phone:          doctor?.phone ?? '',
      specialization: doctor?.specialization ?? '',
      availability:   doctor?.availability ?? '',
    });
    setEditing(true);
    setError('');
    setPendingPw('');
  };

  // General profile update
  const mutation = useMutation({
    mutationFn: () => updateDoctor(authUserId, { ...form, ...(pendingPw ? { password: pendingPw } : {}) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['my-doctor', authUserId] }); setEditing(false); },
    onError: (err: any) => setError(err.response?.data?.message || 'Update failed.'),
  });

  // Dedicated availability status update
  const availMutation = useMutation({
    mutationFn: (status: string) => updateDoctorAvailability(authUserId, status),
    onSuccess: () => {
      setAvailError('');
      qc.invalidateQueries({ queryKey: ['my-doctor', authUserId] });
      qc.invalidateQueries({ queryKey: ['doctors'] });
    },
    onError: (err: any) => setAvailError(err.response?.data?.message || 'Failed to update status.'),
  });

  if (isLoading) return (
    <div className="flex justify-center py-20">
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" />
    </div>
  );

  const set = (k: keyof DoctorUpdateRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  const currentStatus: AvailStatus =
    doctor?.availabilityStatus && AVAIL_CFG[doctor.availabilityStatus as AvailStatus]
      ? (doctor.availabilityStatus as AvailStatus)
      : 'NOT_AVAILABLE';
  const cfg = AVAIL_CFG[currentStatus];

  return (
    <div className="space-y-6">
      {/* Header card */}
      <div className="card flex items-center gap-5">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-primary-100 shrink-0">
          <Stethoscope size={28} className="text-primary-600" />
        </div>
        <div className="flex-1 min-w-0">
          <h2 className="text-lg font-bold text-gray-900 truncate">
            Dr. {doctor?.firstName} {doctor?.lastName}
          </h2>
          <p className="text-sm text-gray-500">{doctor?.specialization || 'General Practitioner'}</p>
          <span className="mt-1 badge bg-primary-100 text-primary-700">DOCTOR</span>
        </div>
        {!editing && (
          <button className="btn-secondary shrink-0" onClick={startEdit}>
            <Pencil size={15} /> Edit Profile
          </button>
        )}
      </div>

      {/* ── Availability Status Card (always visible) ── */}
      <div className="card space-y-4">
        <div className="flex items-center gap-2">
          <CircleDot size={16} className={cfg.dot} />
          <h3 className="font-semibold text-gray-900">Appointment Availability</h3>
        </div>

        <p className="text-sm text-gray-500">
          Control whether patients can book new appointments with you.
          Only <strong>Available</strong> doctors appear in the booking form.
        </p>

        {/* Current status badge */}
        <span className={`inline-flex items-center gap-1.5 text-xs font-semibold rounded-full px-3 py-1.5 ${cfg.badge}`}>
          <CircleDot size={10} />
          {cfg.label}
        </span>

        {/* Instant-save select */}
        <div className="space-y-1.5">
          <label className="label">Change status</label>
          <select
            className="input"
            value={currentStatus}
            disabled={availMutation.isPending}
            onChange={e => availMutation.mutate(e.target.value)}
          >
            <option value="NOT_SET">Unavailable</option>
            <option value="AVAILABLE">Available</option>
            <option value="NOT_AVAILABLE">Not Available</option>
          </select>
          {availMutation.isPending && (
            <p className="text-xs text-gray-400 flex items-center gap-1">
              <Loader2 size={11} className="animate-spin" /> Saving…
            </p>
          )}
          {availMutation.isSuccess && !availError && (
            <p className="text-xs text-green-600 flex items-center gap-1">
              <CheckCircle size={11} /> Status updated successfully
            </p>
          )}
          {availError && <p className="text-xs text-red-500">{availError}</p>}
        </div>
      </div>

      {/* ── Profile info / edit ── */}
      {!editing ? (
        <div className="card grid grid-cols-1 sm:grid-cols-2 gap-5">
          <InfoRow icon={<Mail size={15} className="text-gray-500" />}        label="Email"          value={doctor?.email} />
          <InfoRow icon={<Phone size={15} className="text-gray-500" />}       label="Phone"          value={doctor?.phone} />
          <InfoRow icon={<Stethoscope size={15} className="text-gray-500" />} label="Specialization" value={doctor?.specialization} />
          <InfoRow icon={<Clock size={15} className="text-gray-500" />}       label="Schedule"       value={doctor?.availability || 'Not set'} />
        </div>
      ) : (
        <div className="card space-y-5">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold text-gray-900">Edit Profile</h3>
            <button onClick={() => setEditing(false)} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone ?? ''} onChange={set('phone')} placeholder="0771234567" />
            </div>
            <div>
              <label className="label">Specialization</label>
              <select className="input" value={form.specialization ?? ''} onChange={set('specialization')}>
                <option value="">Select specialization…</option>
                {['Anesthesiology','Cardiology','Dermatology','Emergency Medicine','Endocrinology',
                  'Family Medicine','Gastroenterology','General Practice','General Surgery',
                  'Gynecology','Hematology','Infectious Disease','Internal Medicine','Nephrology',
                  'Neurology','Neurosurgery','Obstetrics & Gynecology','Oncology','Ophthalmology',
                  'Orthopedics','Otolaryngology (ENT)','Pathology','Pediatrics',
                  'Physical Medicine','Psychiatry','Pulmonology','Radiology','Rheumatology',
                  'Urology','Vascular Surgery',
                ].map(s => <option key={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div>
            <label className="label">
              Weekly Schedule
              <span className="text-gray-400 font-normal text-xs ml-1">(JSON format, optional)</span>
            </label>
            <textarea
              className="input resize-none font-mono text-xs" rows={3}
              value={form.availability ?? ''} onChange={set('availability')}
              placeholder='{"monday": "9:00-17:00", "wednesday": "9:00-13:00"}'
            />
          </div>

          <hr className="border-gray-100" />
          <PasswordSection onSave={pw => setPendingPw(pw)} />

          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}

          <div className="flex gap-3">
            <button className="btn-secondary flex-1" onClick={() => setEditing(false)}>Cancel</button>
            <button className="btn-primary flex-1" disabled={mutation.isPending} onClick={() => mutation.mutate()}>
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Saving…</> : 'Save Changes'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}


// Patient Profile

function PatientProfile({ authUserId }: { authUserId: number }) {
  const qc = useQueryClient();
  const [editing, setEditing] = useState(false);
  const [pendingPw, setPendingPw] = useState('');
  const [error, setError] = useState('');

  const { data: patient, isLoading, isError } = useQuery({
    queryKey: ['my-patient', authUserId],
    queryFn: () => getPatientByAuthUserId(authUserId).then(r => r.data.data),
    retry: 3,
    retryDelay: 2000,
  });

  const [form, setForm] = useState<PatientUpdateRequest>({
    firstName: '', lastName: '', email: '', phone: '',
    address: '', gender: '', dateOfBirth: '', bloodGroup: '', isActive: true,
  });

  const startEdit = () => {
    if (!patient) return;
    setForm({
      firstName:   patient.firstName,
      lastName:    patient.lastName,
      email:       patient.email,
      phone:       patient.phone,
      address:     patient.address ?? '',
      gender:      patient.gender ?? '',
      dateOfBirth: patient.dateOfBirth ?? '',
      bloodGroup:  patient.bloodGroup ?? '',
      isActive:    patient.isActive,
    });
    setEditing(true);
    setError('');
    setPendingPw('');
  };

  const mutation = useMutation({
    mutationFn: () => updatePatient(authUserId, { ...form, ...(pendingPw ? { password: pendingPw } : {}) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['my-patient', authUserId] }); setEditing(false); },
    onError: (err: any) => setError(err.response?.data?.message || 'Update failed.'),
  });

  if (isLoading) return <div className="flex justify-center py-20"><div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" /></div>;

  if (isError) return (
    <div className="card text-center py-12 space-y-2">
      <p className="text-sm font-medium text-gray-700">Profile not ready yet</p>
      <p className="text-xs text-gray-400">Your profile is still being set up. Please wait a moment and refresh the page.</p>
    </div>
  );

  const set = (k: keyof PatientUpdateRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="card flex items-center gap-5">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-purple-100 shrink-0">
          <User size={28} className="text-purple-600" />
        </div>
        <div className="flex-1">
          <h2 className="text-lg font-bold text-gray-900">{patient?.firstName} {patient?.lastName}</h2>
          <p className="text-sm text-gray-500">{patient?.email}</p>
          <span className="mt-1 badge bg-purple-100 text-purple-700">PATIENT</span>
        </div>
        {!editing && (
          <button className="btn-secondary" onClick={startEdit}>
            <Pencil size={15} /> Edit Profile
          </button>
        )}
      </div>

      {!editing ? (
        <div className="card grid grid-cols-1 sm:grid-cols-2 gap-5">
          <InfoRow icon={<Mail size={15} className="text-gray-500" />}     label="Email"        value={patient?.email} />
          <InfoRow icon={<Phone size={15} className="text-gray-500" />}    label="Phone"        value={patient?.phone} />
          <InfoRow icon={<User size={15} className="text-gray-500" />}     label="Gender"       value={patient?.gender} />
          <InfoRow icon={<Calendar size={15} className="text-gray-500" />} label="Date of Birth" value={patient?.dateOfBirth} />
          <InfoRow icon={<Droplets size={15} className="text-gray-500" />} label="Blood Group"  value={patient?.bloodGroup} />
          <InfoRow icon={<MapPin size={15} className="text-gray-500" />}   label="Address"      value={patient?.address} />
        </div>
      ) : (
        <div className="card space-y-5">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold text-gray-900">Edit Profile</h3>
            <button onClick={() => setEditing(false)} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">First name *</label>
              <input className="input" required value={form.firstName} onChange={set('firstName')} />
            </div>
            <div>
              <label className="label">Last name *</label>
              <input className="input" required value={form.lastName} onChange={set('lastName')} />
            </div>
          </div>
          <div>
            <label className="label">Email *</label>
            <input className="input" type="email" required value={form.email} onChange={set('email')} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Phone * <span className="font-normal text-gray-400 text-xs">(10 digits)</span></label>
              <input className="input" required value={form.phone} onChange={set('phone')} placeholder="0771234567" />
            </div>
            <div>
              <label className="label">Gender *</label>
              <select className="input" required value={form.gender} onChange={set('gender')}>
                <option value="">Select…</option>
                <option>Male</option><option>Female</option><option>Other</option>
              </select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Date of birth *</label>
              <input className="input" type="date" required value={form.dateOfBirth} onChange={set('dateOfBirth')} />
            </div>
            <div>
              <label className="label">Blood group</label>
              <select className="input" value={form.bloodGroup ?? ''} onChange={set('bloodGroup')}>
                <option value="">Select…</option>
                {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
              </select>
            </div>
          </div>
          <div>
            <label className="label">Address *</label>
            <input className="input" required value={form.address} onChange={set('address')} placeholder="123 Main St, Colombo" />
          </div>

          <hr className="border-gray-100" />
          <PasswordSection onSave={pw => setPendingPw(pw)} />

          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}
          <div className="flex gap-3">
            <button className="btn-secondary flex-1" onClick={() => setEditing(false)}>Cancel</button>
            <button className="btn-primary flex-1" disabled={mutation.isPending} onClick={() => mutation.mutate()}>
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Saving…</> : 'Save Changes'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}


// Staff Profile

function StaffProfile({ authUserId }: { authUserId: number }) {
  const qc = useQueryClient();
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState('');

  const { data: staff, isLoading } = useQuery({
    queryKey: ['my-staff', authUserId],
    queryFn: () =>
      apiClient.get<StandardResponse<Staff>>(`/api/v1/staff/${authUserId}`).then(r => r.data.data),
  });

  const [form, setForm] = useState<StaffUpdateRequest>({
    firstName: '', lastName: '', email: '', phone: '',
    department: '', role: 'STAFF', isActive: true,
    address: '', gender: '', dateOfBirth: '', bloodGroup: '',
  });

  const startEdit = () => {
    if (!staff) return;
    setForm({
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
    setEditing(true);
    setError('');
  };

  const mutation = useMutation({
    mutationFn: () =>
      apiClient.put<StandardResponse<Staff>>(`/api/v1/staff/${authUserId}`, form),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['my-staff', authUserId] }); setEditing(false); },
    onError: (err: any) => setError(err.response?.data?.message || 'Update failed.'),
  });

  if (isLoading) return <div className="flex justify-center py-20"><div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-200 border-t-primary-600" /></div>;

  const set = (k: keyof StaffUpdateRequest) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="space-y-6">
      <div className="card flex items-center gap-5">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-gray-100 shrink-0">
          <User size={28} className="text-gray-600" />
        </div>
        <div className="flex-1">
          <h2 className="text-lg font-bold text-gray-900">{staff?.firstName} {staff?.lastName}</h2>
          <p className="text-sm text-gray-500">{staff?.department}</p>
          <span className="mt-1 badge bg-gray-100 text-gray-700">STAFF</span>
        </div>
        {!editing && (
          <button className="btn-secondary" onClick={startEdit}>
            <Pencil size={15} /> Edit Profile
          </button>
        )}
      </div>

      {!editing ? (
        <div className="card grid grid-cols-1 sm:grid-cols-2 gap-5">
          <InfoRow icon={<Mail size={15} className="text-gray-500" />}     label="Email"       value={staff?.email} />
          <InfoRow icon={<Phone size={15} className="text-gray-500" />}    label="Phone"       value={staff?.phone} />
          <InfoRow icon={<Shield size={15} className="text-gray-500" />}   label="Department"  value={staff?.department} />
          <InfoRow icon={<User size={15} className="text-gray-500" />}     label="Gender"      value={staff?.gender} />
          <InfoRow icon={<Calendar size={15} className="text-gray-500" />} label="Date of Birth" value={staff?.dateOfBirth} />
          <InfoRow icon={<Droplets size={15} className="text-gray-500" />} label="Blood Group" value={staff?.bloodGroup} />
          <InfoRow icon={<MapPin size={15} className="text-gray-500" />}   label="Address"     value={staff?.address} />
        </div>
      ) : (
        <div className="card space-y-5">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold text-gray-900">Edit Profile</h3>
            <button onClick={() => setEditing(false)} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
          </div>

          {/* Read-only admin-controlled fields */}
          <div className="rounded-lg bg-gray-50 border border-gray-200 px-4 py-3 space-y-2">
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Managed by Admin</p>
            <div className="grid grid-cols-2 gap-3 text-sm">
              <div>
                <p className="text-xs text-gray-400">Email</p>
                <p className="text-gray-700 font-medium truncate">{staff?.email}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400">Department</p>
                <p className="text-gray-700 font-medium">{staff?.department || '—'}</p>
              </div>
            </div>
          </div>

          {/* Editable personal fields */}
          <div className="grid grid-cols-2 gap-4">
            <div><label className="label">First name</label><input className="input" required value={form.firstName} onChange={set('firstName')} /></div>
            <div><label className="label">Last name</label><input className="input" required value={form.lastName} onChange={set('lastName')} /></div>
          </div>
          <div><label className="label">Phone</label><input className="input" value={form.phone ?? ''} onChange={set('phone')} placeholder="0771234567" /></div>
          <div><label className="label">Address</label><input className="input" value={form.address ?? ''} onChange={set('address')} placeholder="123 Main St, Colombo" /></div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender ?? ''} onChange={set('gender')}>
                <option value="">Select…</option>
                <option>Male</option><option>Female</option><option>Other</option>
              </select>
            </div>
            <div><label className="label">Date of birth</label><input className="input" type="date" value={form.dateOfBirth ?? ''} onChange={set('dateOfBirth')} /></div>
          </div>
          <div>
            <label className="label">Blood group</label>
            <select className="input" value={form.bloodGroup ?? ''} onChange={set('bloodGroup')}>
              <option value="">Select…</option>
              {['A+','A-','B+','B-','AB+','AB-','O+','O-'].map(g => <option key={g}>{g}</option>)}
            </select>
          </div>

          {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}
          <div className="flex gap-3">
            <button className="btn-secondary flex-1" onClick={() => setEditing(false)}>Cancel</button>
            <button className="btn-primary flex-1" disabled={mutation.isPending} onClick={() => mutation.mutate()}>
              {mutation.isPending ? <><Loader2 size={15} className="animate-spin" /> Saving…</> : 'Save Changes'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}


// Admin — read-only info panel

function AdminProfile() {
  const { user } = useAuthStore();
  return (
    <div className="space-y-6">
      <div className="card flex items-center gap-5">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-primary-100">
          <Shield size={28} className="text-primary-600" />
        </div>
        <div>
          <h2 className="text-lg font-bold text-gray-900">{user?.email}</h2>
          <span className="mt-1 badge bg-primary-100 text-primary-700">ADMIN</span>
        </div>
      </div>
      <div className="card grid grid-cols-1 sm:grid-cols-2 gap-5">
        <InfoRow icon={<Mail size={15} className="text-gray-500" />}   label="Email" value={user?.email} />
        <InfoRow icon={<Shield size={15} className="text-gray-500" />} label="Role"  value="Administrator" />
      </div>
      <div className="card bg-primary-50 border-primary-100">
        <p className="text-sm text-primary-700">
          As an administrator, use the <strong>Doctors</strong> and <strong>Staff</strong> pages to manage team members. Patient records can be managed from the <strong>Patients</strong> page.
        </p>
      </div>
    </div>
  );
}


// Root export


export default function ProfilePage() {
  const { user } = useAuthStore();

  if (!user) return null;

  return (
    <div className="p-6 max-w-2xl space-y-2">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">My Profile</h1>

      {user.role === 'DOCTOR'  && <DoctorProfile authUserId={user.authUserId} />}
      {user.role === 'PATIENT' && <PatientProfile authUserId={user.authUserId} />}
      {user.role === 'STAFF'   && <StaffProfile  authUserId={user.authUserId} />}
      {user.role === 'ADMIN'   && <AdminProfile />}
    </div>
  );
}
