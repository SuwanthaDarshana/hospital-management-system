import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '../store/authStore';
import { getAllDoctors } from '../api/doctors';
import { getAllPatients } from '../api/patients';
import { getAllAppointments, getMyAppointments } from '../api/appointments';
import { CalendarDays, Stethoscope, UserRound, TrendingUp, Clock } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import type { Appointment } from '../types';

interface StatCardProps { label: string; value: number | string; icon: React.ReactNode; color: string }

function StatCard({ label, value, icon, color }: StatCardProps) {
  return (
    <div className="card flex items-center gap-4">
      <div className={`flex h-12 w-12 items-center justify-center rounded-xl ${color}`}>
        {icon}
      </div>
      <div>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
        <p className="text-sm text-gray-500">{label}</p>
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const { user } = useAuthStore();
  const isAdminOrStaff = user?.role === 'ADMIN' || user?.role === 'STAFF';

  const { data: doctorsData } = useQuery({
    queryKey: ['doctors'],
    queryFn: () => getAllDoctors().then(r => r.data.data),
    enabled: isAdminOrStaff,
  });

  const { data: patientsData } = useQuery({
    queryKey: ['patients'],
    queryFn: () => getAllPatients().then(r => r.data.data),
    enabled: isAdminOrStaff,
  });

  const { data: allAppointments } = useQuery({
    queryKey: ['appointments-all'],
    queryFn: () => getAllAppointments().then(r => r.data.data),
    enabled: isAdminOrStaff,
  });

  const { data: myAppointments } = useQuery({
    queryKey: ['appointments-my'],
    queryFn: () => getMyAppointments().then(r => r.data.data),
    enabled: !isAdminOrStaff,
  });

  const appointments: Appointment[] = isAdminOrStaff ? (allAppointments ?? []) : (myAppointments ?? []);
  const pending   = appointments.filter(a => a.status === 'PENDING').length;
  const confirmed = appointments.filter(a => a.status === 'CONFIRMED').length;
  const recent    = [...appointments].sort((a, b) => b.id - a.id).slice(0, 5);

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Welcome back, <span className="font-medium text-primary-600">{user?.email}</span>
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {isAdminOrStaff && (
          <>
            <StatCard label="Total Doctors" value={doctorsData?.length ?? '—'} icon={<Stethoscope size={20} className="text-blue-600" />} color="bg-blue-50" />
            <StatCard label="Total Patients" value={patientsData?.length ?? '—'} icon={<UserRound size={20} className="text-purple-600" />} color="bg-purple-50" />
          </>
        )}
        <StatCard label="Pending Appointments"   value={pending}   icon={<Clock size={20} className="text-yellow-600" />} color="bg-yellow-50" />
        <StatCard label="Confirmed Appointments" value={confirmed} icon={<CalendarDays size={20} className="text-green-600" />} color="bg-green-50" />
        {!isAdminOrStaff && (
          <StatCard label="My Appointments" value={appointments.length} icon={<TrendingUp size={20} className="text-primary-600" />} color="bg-primary-50" />
        )}
      </div>

      {/* Recent appointments */}
      <div className="card !p-0 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
          <h2 className="text-base font-semibold text-gray-900">Recent Appointments</h2>
          <CalendarDays size={18} className="text-gray-400" />
        </div>
        {recent.length === 0 ? (
          <div className="px-6 py-10 text-center text-gray-400 text-sm">No appointments yet.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr>
                  <th className="th">Patient</th>
                  <th className="th">Doctor</th>
                  <th className="th">Date</th>
                  <th className="th">Time</th>
                  <th className="th">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 bg-white">
                {recent.map(a => (
                  <tr key={a.id} className="hover:bg-gray-50 transition-colors">
                    <td className="td font-medium">{a.patientName}</td>
                    <td className="td">{a.doctorName}</td>
                    <td className="td">{a.appointmentDate}</td>
                    <td className="td">{a.appointmentTime}</td>
                    <td className="td"><StatusBadge status={a.status} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
