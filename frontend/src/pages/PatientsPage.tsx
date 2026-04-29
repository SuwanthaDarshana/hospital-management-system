import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAllPatients, deletePatient } from '../api/patients';
import { Search, UserRound, Trash2, Phone } from 'lucide-react';
import type { Patient } from '../types';
import clsx from 'clsx';

export default function PatientsPage() {
  const [search, setSearch] = useState('');
  const qc = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['patients'],
    queryFn: () => getAllPatients().then(r => r.data.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (authUserId: number) => deletePatient(authUserId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['patients'] }),
  });

  const patients: Patient[] = (data ?? []).filter(p => {
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
        <h1 className="text-2xl font-bold text-gray-900">Patients</h1>
        <p className="text-sm text-gray-500 mt-0.5">{patients.length} patient{patients.length !== 1 ? 's' : ''}</p>
      </div>

      {/* Search */}
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
                <th className="th">Status</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {patients.length === 0 ? (
                <tr><td colSpan={6} className="td text-center py-10 text-gray-400">No patients found.</td></tr>
              ) : patients.map(p => (
                <tr key={p.id} className="hover:bg-gray-50 transition-colors">
                  <td className="td">
                    <div className="flex items-center gap-3">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-purple-100 shrink-0">
                        <UserRound size={15} className="text-purple-600" />
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">{p.firstName} {p.lastName}</p>
                        <p className="text-xs text-gray-400">{p.email}</p>
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
                  <td className="td">
                    <span className={clsx('badge', p.isActive ? 'badge-active' : 'badge-inactive')}>
                      {p.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="td">
                    <button
                      onClick={() => window.confirm('Deactivate this patient?') && deleteMutation.mutate(p.authUserId)}
                      className="text-gray-400 hover:text-red-500 transition-colors p-1 rounded"
                    >
                      <Trash2 size={15} />
                    </button>
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
