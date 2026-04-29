import type { AppointmentStatus } from '../types';
import clsx from 'clsx';

export default function StatusBadge({ status }: { status: AppointmentStatus }) {
  const map: Record<AppointmentStatus, string> = {
    PENDING:   'badge-pending',
    CONFIRMED: 'badge-confirmed',
    COMPLETED: 'badge-completed',
    CANCELLED: 'badge-cancelled',
  };
  return <span className={clsx('badge', map[status])}>{status}</span>;
}
