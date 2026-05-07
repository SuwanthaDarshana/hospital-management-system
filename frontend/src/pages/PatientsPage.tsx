import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useAuthStore } from '../store/authStore';
import { useRealtimeSync } from '../hooks/useRealtimeSync';
import { getAllPatients, getPatientByAuthUserId, deletePatient, activatePatient } from '../api/patients';
import { getAppointmentsByDoctor } from '../api/appointments';
import { Search, UserRound, Phone } from 'lucide-react';
import type { Patient } from '../types';
import clsx from 'clsx';
import ActiveToggle from '../components/ActiveToggle';

export default function PatientsPage() {
  const [search, setSearch] = useState('');
  const qc = useQueryClient();
  const { user } = useAuthStore();
  const isDoctor = user?.role === 'DOCTOR';

  useRealtimeSync([{ topic: 'patients', invalidate: [['patients'], ['patients-for-doctor']] }]);

  // ADMIN / STAFF — fetch all patients
  const { data: allData, isLoading: allLoading } = useQuery({
    queryKey: ['patients'],
    queryFn: () => getAllPatients().then(r => r.data.data),
    enabled: !isDoctor,
  });

  // DOCTOR — fetch appointments → deduplicate patient IDs → fetch each patient
  const { data: doctorData, isLoading: doctorLoading } = useQuery({
    queryKey: ['patients-for-doctor', user?.authUserId],
    staleTime: 0,
    queryFn: async () => {
      const appointments = await getAppointmentsByDoctor(user!.authUserId).then(r => r.data.data);
      const active = appointments.filter(
        a => a.status === 'CONFIRMED' || a.status === 'COMPLETED'
      );
      const uniqueIds = [...new Set(active.map(a => a.patientAuthUserId))];
      if (uniqueIds.length === 0) return [];

      const results = await Promise.allSettled(
        uniqueIds.map(id => getPatientByAuthUserId(id).then(r => r.data.data))
      );
      return results
        .filter((r): r is PromiseFulfilledResult<Patient> => r.status === 'fulfilled')
        .map(r => r.value);
    },
    enabled: isDoctor,
  });

  const isLoading = isDoctor ? doctorLoading : allLoading;
  const rawPatients: Patient[] = (isDoctor ? doctorData : allData) ?? [];

  const deleteMutation = useMutation({
    mutationFn: (authUserId: number) => deletePatient(authUserId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['patients'] }),
  });

  const activateMutation = useMutation({
    mutationFn: (authUserId: number) => activatePatient(authUserId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['patients'] }),
  });

  const isPending = deleteMutation.isPending || activateMutation.isPending;

  const patients = rawPatients.filter(p => {
    if (!search) return true;
    const s = search.toLowerCase();
    return (
      p.firstName.toLowerCase().includes(s) ||
      p.lastName.toLowerCase().includes(s) ||
      p.email.toLowerCase().includes(s)
    );
  });

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">{isDoctor ? 'My Patients' : 'Patients'}</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          {isDoctor ? 'Patients with a confirmed or completed appointment' : `${patients.length} patient${patients.length !== 1 ? 's' : ''}`}
        </p>
      </div>

      <div className="relative max-w-sm">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input className="input pl-9" placeholder="Search patients…" value={search} onChange={e => setSearch(e.target.value)} />
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
                <th className="th">Patient</th>
                <th className="th">Contact</th>
                <th className="th">Blood</th>
                <th className="th">Gender</th>
                {!isDoctor && <th className="th">Active</th>}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {patients.length === 0 ? (
                <tr><td colSpan={isDoctor ? 4 : 5} className="td text-center py-10 text-gray-400">
                  {isDoctor ? 'No confirmed patients yet.' : 'No patients found.'}
                </td></tr>
              ) : patients.map(p => (
                <tr key={p.id} className={clsx(
                  'transition-colors',
                  p.isActive ? 'hover:bg-gray-50' : 'bg-red-50 hover:bg-red-100',
                )}>
                  <td className="td">
                    <div className="flex items-center gap-3">
                      <div className={clsx(
                        'flex h-8 w-8 items-center justify-center rounded-full shrink-0',
                        p.isActive ? 'bg-purple-100' : 'bg-red-100',
                      )}>
                        <UserRound size={15} className={p.isActive ? 'text-purple-600' : 'text-red-400'} />
                      </div>
                      <div>
                        <p className={clsx('font-medium', p.isActive ? 'text-gray-900' : 'text-red-400 line-through')}>{p.firstName} {p.lastName}</p>
                        <p className={clsx('text-xs', p.isActive ? 'text-gray-400' : 'text-red-300')}>{p.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="td">
                    <div className="flex items-center gap-1 text-gray-500">
                      <Phone size={13} /><span>{p.phone || '—'}</span>
                    </div>
                  </td>
                  <td className="td">
                    <span className="badge bg-red-50 text-red-700">{p.bloodGroup || '—'}</span>
                  </td>
                  <td className="td">{p.gender || '—'}</td>
                  {!isDoctor && (
                    <td className="td">
                      <ActiveToggle
                        checked={p.isActive}
                        disabled={isPending}
                        onChange={() => {
                          if (p.isActive) {
                            deleteMutation.mutate(p.authUserId);
                          } else {
                            activateMutation.mutate(p.authUserId);
                          }
                        }}
                      />
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
